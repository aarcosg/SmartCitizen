package es.us.hermes.smartcitizen.ui.fragment;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import es.us.hermes.smartcitizen.R;
import es.us.hermes.smartcitizen.di.components.LoginComponent;
import es.us.hermes.smartcitizen.exception.ErrorMessageFactory;
import es.us.hermes.smartcitizen.mvp.presenter.LoginPresenter;
import es.us.hermes.smartcitizen.mvp.view.LoginView;
import es.us.hermes.smartcitizen.ui.activity.MainActivity;

public class LoginFragment extends BaseFragment implements LoginView{

    private static final String TAG = LoginFragment.class.getCanonicalName();
    private static final int REQUEST_SIGN_IN = 2001;

    @Inject
    LoginPresenter mLoginPresenter;
    @Inject
    GoogleApiClient mGoogleApiClient;

    @BindView(R.id.signInGoogleBtn)
    SignInButton mSignInGoogleBtn;

    private Unbinder mUnbinder;
    private ProgressDialog mProgressDialog;

    public LoginFragment(){
        setRetainInstance(true);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getComponent(LoginComponent.class).inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View fragmentView = inflater.inflate(R.layout.fragment_login, container, false);
        mUnbinder = ButterKnife.bind(this, fragmentView);
        customizeSignInGoogleBtn();
        this.mLoginPresenter.setView(this);
        return fragmentView;
    }

    @Override
    public void onPause() {
        super.onPause();
        this.mLoginPresenter.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
       if(mUnbinder != null){
           mUnbinder.unbind();
       }
    }

    @Override
    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }
        mProgressDialog.show();
    }

    @Override
    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void showGoogleSignInErrorMessage() {
        Snackbar.make(mSignInGoogleBtn.getRootView(),
                getString(R.string.common_google_play_services_sign_in_failed_text),
                Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.retry), v -> onClickSignInGoogleButton())
                .show();
    }

    @Override
    public void showLoginSuccessMessage() {
        Toast.makeText(getContext(),
                getString(R.string.user_signed_up),
                Toast.LENGTH_LONG)
                .show();
    }

    @Override
    public void navigateToMainScreen() {
        MainActivity.launch(getActivity());
    }

    @Override
    public void finishActivity() {
        getActivity().finish();
    }

    @Override
    public void showErrorMessage(Throwable throwable) {
        Snackbar.make(mSignInGoogleBtn.getRootView(),
                ErrorMessageFactory.create(getContext(),throwable),
                Snackbar.LENGTH_LONG)
                .show();
    }

    @Override
    public void doGoogleSignOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(status -> {
            if(status.isSuccess()){
                Log.d(TAG,"User signed out from Google");
            }
        });
    }

    @Override
    public void doGoogleRevokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(status -> {
            if(status.isSuccess()){
                Log.d(TAG,"Revoked Google access");
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == REQUEST_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            this.mLoginPresenter.handleGoogleSignInResult(result);

        }
    }

    @OnClick(R.id.signInGoogleBtn)
    public void onClickSignInGoogleButton() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, REQUEST_SIGN_IN);
    }

    private void customizeSignInGoogleBtn(){
        mSignInGoogleBtn.setSize(SignInButton.SIZE_WIDE);
        mSignInGoogleBtn.setColorScheme(SignInButton.COLOR_LIGHT);
    }

}
