package us.idinfor.smartcitizen.mvp.presenter;


import com.google.android.gms.auth.api.signin.GoogleSignInResult;

public interface LoginPresenter extends Presenter {

    void showLoadingView();
    void hideLoadingView();
    void handleGoogleSignInResult(GoogleSignInResult result);

}
