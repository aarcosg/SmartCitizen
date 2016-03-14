package us.idinfor.smartcitizen.fragment;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import us.idinfor.smartcitizen.Constants;
import us.idinfor.smartcitizen.R;
import us.idinfor.smartcitizen.Utils;
import us.idinfor.smartcitizen.activity.MainActivity;
import us.idinfor.smartcitizen.asynctask.UserLoginOrRegisterAsyncTask;
import us.idinfor.smartcitizen.hermes.HermesCitizenApi;

public class LoginActivityFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = LoginActivityFragment.class.getCanonicalName();
    private static final int REQUEST_SIGN_IN = 2001;

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginOrRegisterAsyncTask mAuthTask = null;

    @Bind(R.id.signinBtn)
    SignInButton mSigninBtn;

    private ProgressDialog mProgressDialog;
    private GoogleApiClient mGoogleApiClient;
    private GoogleSignInOptions mGoogleSignInOptions;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGoogleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .enableAutoManage(getActivity() /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, mGoogleSignInOptions)
                .build();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.bind(this, view);
        mSigninBtn.setSize(SignInButton.SIZE_WIDE);
        mSigninBtn.setColorScheme(SignInButton.COLOR_LIGHT);
        mSigninBtn.setScopes(mGoogleSignInOptions.getScopeArray());
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

    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if(status.isSuccess()) {
                            Log.d(TAG,"User signed out from Google");
                        }
                    }
                });
    }

    private void revokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if(status.isSuccess()) {
                            Log.d(TAG,"Revoked Google access");
                        }
                    }
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
        mAuthTask = new UserLoginOrRegisterAsyncTask(email, Constants.DEFAULT_PASSWORD) {
            @Override
            protected void onPostExecute(Integer result) {
                mAuthTask = null;
                hideProgressDialog();
                switch (result) {
                    case HermesCitizenApi.RESPONSE_OK:
                        Log.i(TAG, "User registered or logged successfully");
                        Toast.makeText(getActivity(), "User signed up", Toast.LENGTH_LONG).show();
                        SharedPreferences prefs = Utils.getSharedPreferences(getActivity());
                        prefs.edit()
                                .putString(Constants.PROPERTY_USER_NAME, email)
                                .apply();
                        MainActivity.launch(getActivity());
                        getActivity().finish();
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

            @Override
            protected void onCancelled() {
                mAuthTask = null;
                hideProgressDialog();
            }

        };
        showProgressDialog();
        mAuthTask.execute((Void) null);
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
            mProgressDialog.hide();
        }
    }
}
