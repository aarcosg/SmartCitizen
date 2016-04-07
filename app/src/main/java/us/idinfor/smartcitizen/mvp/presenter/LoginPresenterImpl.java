package us.idinfor.smartcitizen.mvp.presenter;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;

import javax.inject.Inject;

import rx.Subscription;
import us.idinfor.smartcitizen.interactor.LoginInteractor;
import us.idinfor.smartcitizen.mvp.view.LoginView;
import us.idinfor.smartcitizen.mvp.view.View;

public class LoginPresenterImpl implements LoginPresenter{

    private LoginView mLoginView;
    private final LoginInteractor mLoginInteractor;
    private Subscription mSubscription;

    @Inject
    public LoginPresenterImpl(LoginInteractor loginInteractor){
        this.mLoginInteractor = loginInteractor;
    }

    @Override
    public void setView(View v) {
        mLoginView = (LoginView) v;
    }

    @Override
    public void showLoadingView(){
        this.mLoginView.showProgressDialog();
    }

    @Override
    public void hideLoadingView(){
        this.mLoginView.hideProgressDialog();
    }

    @Override
    public void handleGoogleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess() && result.getSignInAccount() != null) {
            // Signed in successfully, register user in Smart Citizen backend.
            GoogleSignInAccount account = result.getSignInAccount();
            this.mLoginView.showProgressDialog();
            this.mLoginInteractor.loginOrRegisterUserInHermes(account);
        } else {
            // Signed out
            this.mLoginView.showGoogleSignInError();
        }
    }

}