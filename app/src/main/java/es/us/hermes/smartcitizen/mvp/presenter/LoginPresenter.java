package es.us.hermes.smartcitizen.mvp.presenter;


import com.google.android.gms.auth.api.signin.GoogleSignInResult;

import es.us.hermes.smartcitizen.mvp.model.hermes.User;

public interface LoginPresenter extends Presenter {

    void handleGoogleSignInResult(GoogleSignInResult result);
    void onUserLogged(User user);
    void onLoginException(Throwable throwable);

}
