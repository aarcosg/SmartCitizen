package us.idinfor.smartcitizen.interactor;

import android.content.SharedPreferences;

import com.fernandocejas.frodo.annotation.RxLogObservable;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import us.idinfor.smartcitizen.Constants;
import us.idinfor.smartcitizen.data.api.hermes.HermesCitizenApi;
import us.idinfor.smartcitizen.data.api.hermes.entity.User;
import us.idinfor.smartcitizen.data.api.hermes.exception.UnknownErrorHermesException;
import us.idinfor.smartcitizen.data.api.hermes.exception.UserAlreadyExistsErrorHermesException;
import us.idinfor.smartcitizen.data.api.hermes.exception.UserNotRegisteredErrorHermesException;

public class LoginInteractorImpl implements LoginInteractor {

    private static final String TAG = LoginInteractorImpl.class.getCanonicalName();

    private final HermesCitizenApi mHermesCitizenApi;
    private final SharedPreferences mPrefs;

    @Inject
    public LoginInteractorImpl(HermesCitizenApi hermesCitizenApi, SharedPreferences prefs){
        this.mHermesCitizenApi =  hermesCitizenApi;
        this.mPrefs = prefs;
    }

    @RxLogObservable
    @Override
    public Observable<User> loginOrRegisterUserInHermes(final GoogleSignInAccount account) {
        return Observable.create(subscriber -> {
            mHermesCitizenApi.existsUser(account.getEmail())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext(booleanResponse -> {
                        if(booleanResponse.body()) {
                            // User exists
                            subscriber.onNext(new User(account.getEmail(),null));
                            subscriber.onCompleted();
                        }else{
                            // User doesn't exist, sign up user
                            mHermesCitizenApi.registerUser(
                                new User(account.getEmail(), Constants.DEFAULT_PASSWORD))
                                .doOnNext(integerResponse -> {
                                    if (integerResponse.body() == HermesCitizenApi.RESPONSE_OK) {
                                        subscriber.onNext(new User(account.getEmail(), null));
                                        subscriber.onCompleted();
                                    } else {
                                        subscriber.onError(getHermesException(integerResponse.body()));
                                    }
                                });
                        }
                    })
                    .doOnError(throwable -> subscriber.onError(new UnknownErrorHermesException()))
                    .subscribe();
        });
    }

    @Override
    public void saveLoggedUserInPreferences(User user) {
        mPrefs.edit()
                .putString(Constants.PROPERTY_USER_NAME, user.getEmail())
                .commit();
    }

    private Exception getHermesException (Integer apiResultCode){
        Exception exception = new UnknownErrorHermesException();
        switch (apiResultCode){
            case HermesCitizenApi.RESPONSE_ERROR_USER_EXISTS:
                exception = new UserAlreadyExistsErrorHermesException();
            break;
            case HermesCitizenApi.RESPONSE_ERROR_USER_NOT_REGISTERED:
                exception = new UserNotRegisteredErrorHermesException();
            break;
        }
        return exception;
    }

}