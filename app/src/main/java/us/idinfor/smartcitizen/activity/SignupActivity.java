package us.idinfor.smartcitizen.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import us.idinfor.smartcitizen.Constants;
import us.idinfor.smartcitizen.R;
import us.idinfor.smartcitizen.Utils;
import us.idinfor.smartcitizen.asynctask.UserLoginAsyncTask;

/**
 * A login screen that offers login via email/password.
 */
public class SignupActivity extends BaseActivity {

    private static final String TAG = SignupActivity.class.getCanonicalName();

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginAsyncTask mAuthTask = null;

    @Bind(R.id.loginProgressBar)
    ProgressBar mLoginProgressBar;
    @Bind(R.id.userEdit)
    EditText mUserEdit;
    @Bind(R.id.loginBtn)
    Button mLoginBtn;
    @Bind(R.id.signupBtn)
    Button mSignupBtn;
    @Bind(R.id.loginForm)
    ScrollView mLoginForm;

    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!Utils.checkPlayServices(this)) {
            finish();
        }
        prefs = Utils.getSharedPreferences(this);
        if (!TextUtils.isEmpty(prefs.getString(Constants.PROPERTY_USER_NAME, ""))) {
            MainActivity.launch(this);
            finish();
        }
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        buildActionBarToolbar(getString(R.string.title_activity_login), false);
    }

    @OnClick(R.id.signupBtn)
    public void openSignup(){
        SignupActivity.launch(this);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    @OnClick(R.id.loginBtn)
    public void attemptLogin() {

        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mUserEdit.setError(null);

        // Store values at the time of the login attempt.
        final String username = mUserEdit.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid email address.
        if (TextUtils.isEmpty(username)) {
            mUserEdit.setError(getString(R.string.error_field_required));
            focusView = mUserEdit;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginAsyncTask(username) {
                @Override
                protected void onPostExecute(Boolean exists) {
                    mAuthTask = null;
                    showProgress(false);
                    if (exists) {
                        prefs.edit()
                                .putString(Constants.PROPERTY_USER_NAME, username)
                                .apply();
                        //MainActivity.launch(LoginActivity.this);
                        finish();
                    } else {
                        mUserEdit.setError(getString(R.string.error_user_not_found));
                        mUserEdit.requestFocus();
                    }
                }

                @Override
                protected void onCancelled() {
                    mAuthTask = null;
                    showProgress(false);
                }
            };
            mAuthTask.execute((Void) null);
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginForm.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginForm.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginForm.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mLoginProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginProgressBar.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mLoginProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginForm.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    public static void launch(Activity activity) {
        Intent intent = new Intent(activity, SignupActivity.class);
        ActivityCompat.startActivity(activity, intent, null);
    }
}

