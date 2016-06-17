package es.us.hermes.smartcitizen.interactor;

import android.content.SharedPreferences;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import es.us.hermes.smartcitizen.Constants;
import es.us.hermes.smartcitizen.data.api.hermes.HermesCitizenApi;
import es.us.hermes.smartcitizen.data.api.hermes.entity.User;
import es.us.hermes.smartcitizen.data.api.hermes.exception.UnknownErrorHermesException;
import es.us.hermes.smartcitizen.data.api.hermes.exception.UserAlreadyExistsErrorHermesException;
import es.us.hermes.smartcitizen.data.api.hermes.exception.UserNotRegisteredErrorHermesException;
import es.us.hermes.smartcitizen.utils.RxNetwork;

public class LoginInteractorImpl implements LoginInteractor {

    private static final String TAG = LoginInteractorImpl.class.getCanonicalName();

    private final RxNetwork mRxNetwork;
    private final HermesCitizenApi mHermesCitizenApi;
    private final SharedPreferences mPrefs;

    @Inject
    public LoginInteractorImpl(RxNetwork rxNetwork, HermesCitizenApi hermesCitizenApi, SharedPreferences prefs){
        this.mRxNetwork = rxNetwork;
        this.mHermesCitizenApi =  hermesCitizenApi;
        this.mPrefs = prefs;
    }

    //@RxLogObservable
    @Override
    public Observable<User> loginOrRegisterUserInHermes(final GoogleSignInAccount account) {
        String email = account.getEmail();
        return mRxNetwork.checkInternetConnection().andThen(
                Observable.create(subscriber -> {
                    mHermesCitizenApi.existsUser(email)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(booleanResponse -> {
                            if (booleanResponse.body()) {
                                // User exists
                                subscriber.onNext(new User(email, null));
                                subscriber.onCompleted();
                            } else {
                                // User doesn't exist, sign up user
                                mHermesCitizenApi.registerUser(new User(email, Constants.DEFAULT_PASSWORD))
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(response -> {
                                            if (response.body() == HermesCitizenApi.RESPONSE_OK) {
                                                subscriber.onNext(new User(email, null));
                                                subscriber.onCompleted();
                                            } else {
                                                subscriber.onError(getHermesException(response.body()));
                                            }
                                        },
                                        throwable -> subscriber.onError(new UnknownErrorHermesException()));
                            }
                        },
                        throwable -> subscriber.onError(new UnknownErrorHermesException()));
            })
        );
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