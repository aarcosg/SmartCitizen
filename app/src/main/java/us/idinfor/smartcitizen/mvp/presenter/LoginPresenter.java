package us.idinfor.smartcitizen.mvp.presenter;


import com.google.android.gms.auth.api.signin.GoogleSignInResult;

import us.idinfor.smartcitizen.data.api.hermes.entity.User;

public interface LoginPresenter extends Presenter {

    void handleGoogleSignInResult(GoogleSignInResult result);
    void onUserLogged(User user);
    void onLoginException(Throwable throwable);

}
