package us.idinfor.smartcitizen.fragment;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.Lazy;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import us.idinfor.smartcitizen.Constants;
import us.idinfor.smartcitizen.R;
import us.idinfor.smartcitizen.activity.MainActivity;
import us.idinfor.smartcitizen.asynctask.UserLoginOrRegisterAsyncTask;
import us.idinfor.smartcitizen.di.components.DaggerGoogleApiClientComponent;
import us.idinfor.smartcitizen.di.components.GoogleApiClientComponent;
import us.idinfor.smartcitizen.di.modules.GoogleApiClientModule;
import us.idinfor.smartcitizen.hermes.HermesCitizenApi;
import us.idinfor.smartcitizen.hermes.HermesCitizenApi_old;
import us.idinfor.smartcitizen.hermes.User;

public class LoginActivityFragment extends BaseFragment implements GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = LoginActivityFragment.class.getCanonicalName();
    private static final int REQUEST_SIGN_IN = 2001;

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginOrRegisterAsyncTask mAuthTask = null;

    @Bind(R.id.signinBtn)
    SignInButton mSignInBtn;

    @Inject
    @Named(GoogleApiClientModule.NAME_GOOGLEAPICLIENT_SIGNIN)
    GoogleApiClient mGoogleApiClient;
    @Inject
    HermesCitizenApi mHermesCitizenApi;
    @Inject
    Lazy<SharedPreferences> prefs;

    private ProgressDialog mProgressDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initDependencies();
    }

    private void initDependencies() {
        GoogleApiClientComponent googleApiClientComponent = DaggerGoogleApiClientComponent
                .builder()
                .applicationComponent(getBaseActivity().getApplicationComponent())
                .baseActivityModule(getBaseActivity().getBaseActivityModule())
                .googleApiClientModule(new GoogleApiClientModule(this,null))
                .build();
        injectComponent(googleApiClientComponent);
    }

    private void injectComponent(GoogleApiClientComponent googleApiClientComponent) {
        googleApiClientComponent.inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.bind(this, view);
        customizeSignInBtn();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    @OnClick(R.id.signinBtn)
    public void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, REQUEST_SIGN_IN);
    }

    private void customizeSignInBtn(){
        mSignInBtn.setSize(SignInButton.SIZE_WIDE);
        mSignInBtn.setColorScheme(SignInButton.COLOR_LIGHT);
    }

    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(status -> {
           if(status.isSuccess())
               Log.d(TAG,"User signed out from Google");
        });
    }

    private void revokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(status -> {
            if(status.isSuccess())
                Log.d(TAG,"Revoked Google access");
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == REQUEST_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess() && result.getSignInAccount() != null) {
            // Signed in successfully, register user in Smart Citizen backend.
            GoogleSignInAccount acct = result.getSignInAccount();
            loginOrRegisterUserInHermes(acct.getEmail());
        } else {
            // Signed out
            hideProgressDialog();
            Toast.makeText(getActivity(),
                    getString(R.string.common_google_play_services_sign_in_failed_text),
                    Toast.LENGTH_LONG)
                    .show();
        }
    }

    private void loginOrRegisterUserInHermes(final String email) {
        showProgressDialog();
        mHermesCitizenApi.existsUser(email)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(booleanResponse -> {
                    if(booleanResponse.body()) {
                        // User exists
                        doLoginUser(email);
                    }else{
                        mHermesCitizenApi.registerUser(
                                new User(email, Constants.DEFAULT_PASSWORD))
                                .doOnNext(integerResponse -> {
                                    doRegisterUser(integerResponse.body(),email);
                                })
                                .doOnError(throwable -> {
                                    Toast.makeText(getContext(),throwable.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                                })
                                .doOnTerminate(() -> hideProgressDialog())
                                .subscribe();
                    }
                })
                .doOnError(throwable -> {
                    Toast.makeText(getContext(),throwable.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                })
                .doOnTerminate(() -> hideProgressDialog())
                .subscribe();
    }

    private void doLoginUser(String email){
        Log.i(TAG, "User registered or logged successfully");
        Toast.makeText(getContext(), "User signed up", Toast.LENGTH_LONG).show();
        prefs.get()
                .edit()
                .putString(Constants.PROPERTY_USER_NAME, email)
                .commit();
        MainActivity.launch(getActivity());
        hideProgressDialog();
        getActivity().finish();
    }

    private void doRegisterUser(Integer result, String email){
        switch (result) {
            case HermesCitizenApi_old.RESPONSE_OK:
                doLoginUser(email);
                break;
            case HermesCitizenApi_old.RESPONSE_ERROR_USER_EXISTS:
                Log.e(TAG, "Error: User already taken");
                Toast.makeText(getActivity(), "Error: User already taken", Toast.LENGTH_LONG).show();
                signOut();
                revokeAccess();
                break;
            case HermesCitizenApi_old.RESPONSE_ERROR_USER_NOT_REGISTERED:
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
    
    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }
        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }


}
