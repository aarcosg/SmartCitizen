package us.idinfor.smartcitizen.interactor;

import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import javax.inject.Inject;

import dagger.Lazy;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import us.idinfor.smartcitizen.Constants;
import us.idinfor.smartcitizen.data.api.google.fit.GoogleFitHelper;
import us.idinfor.smartcitizen.data.api.hermes.HermesCitizenApi;
import us.idinfor.smartcitizen.data.api.hermes.entity.User;
import us.idinfor.smartcitizen.ui.activity.MainActivity;

public class LoginInteractorImpl implements LoginInteractor {

    private static final String TAG = LoginInteractorImpl.class.getCanonicalName();

    @Inject
    HermesCitizenApi mHermesCitizenApi;
    @Inject
    Lazy<SharedPreferences> mPrefs;

    @Inject
    public LoginInteractorImpl(){}

    @Override
    public void loginOrRegisterUserInHermes(GoogleSignInAccount account) {
        mHermesCitizenApi.existsUser(account.getEmail())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(booleanResponse -> {
                    if(booleanResponse.body()) {
                        // User exists
                        doLoginUser(account.getEmail());
                        return Observable.empty();
                    }else{
                        // User doesn't exist, sign up user
                        return mHermesCitizenApi.registerUser(
                                new User(account.getEmail(), Constants.DEFAULT_PASSWORD))
                                .doOnNext(integerResponse -> {
                                    doRegisterUser(integerResponse.body(),account.getEmail());
                                });
                    }
                })
                .doOnError(throwable ->
                        Toast.makeText(getContext(),throwable.getLocalizedMessage(),Toast.LENGTH_LONG).show()
                )
                .doOnTerminate(() -> hideProgressDialog())
                .subscribe();
    }

    private void doLoginUser(String email){
        Log.i(TAG, "User registered or logged successfully");
        Toast.makeText(getContext(), "User signed up", Toast.LENGTH_LONG).show();
        mPrefs.get()
                .edit()
                .putString(Constants.PROPERTY_USER_NAME, email)
                .commit();
        GoogleFitHelper.initFitApi(getBaseActivity().getApplicationContext());
        MainActivity.launch(getActivity());
        getActivity().finish();
    }

    private void doRegisterUser(Integer result, String email){
        switch (result) {
            case HermesCitizenApi.RESPONSE_OK:
                doLoginUser(email);
                break;
            case HermesCitizenApi.RESPONSE_ERROR_USER_EXISTS:
                Log.e(TAG, "Error: User already taken");
                Toast.makeText(getActivity(), "Error: User already taken", Toast.LENGTH_LONG).show();
                signOut();
                revokeAccess();
                break;
            case HermesCitizenApi.RESPONSE_ERROR_USER_NOT_REGISTERED:
                Log.e(TAG, "Error: User not registered");
                Toast.makeText(getActivity(), "Error: User not registered", Toast.LENGTH_LONG).show();
                signOut();
                revokeAccess();
                break;
            default:
                Log.e(TAG, "Unknown error");
                Toast.makeText(getActivity(), "Unknown error", Toast.LENGTH_LONG).show();
                signOut();
                revokeAccess();
        }
    }
}