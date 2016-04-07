package us.idinfor.smartcitizen.ui.fragment;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import us.idinfor.smartcitizen.R;
import us.idinfor.smartcitizen.di.components.LoginComponent;
import us.idinfor.smartcitizen.mvp.presenter.LoginPresenter;
import us.idinfor.smartcitizen.mvp.view.LoginView;

public class LoginFragment extends BaseFragment implements LoginView{

    private static final String TAG = LoginFragment.class.getCanonicalName();
    private static final int REQUEST_SIGN_IN = 2001;

    @Inject
    LoginPresenter mLoginPresenter;
    @Inject
    GoogleApiClient mGoogleApiClient;

    @Bind(R.id.signInGoogleBtn)
    SignInButton mSignInGoogleBtn;
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
        ButterKnife.bind(this, fragmentView);
        customizeSignInGoogleBtn();
        return fragmentView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.mLoginPresenter.setView(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
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
    public void showGoogleSignInError() {
        Snackbar.make(mSignInGoogleBtn.getRootView(),
                getString(R.string.common_google_play_services_sign_in_failed_text),
                Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.retry), v -> onClickSignInGoogleButton())
                .show();
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
