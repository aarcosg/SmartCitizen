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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import us.idinfor.smartcitizen.Constants;
import us.idinfor.smartcitizen.hermes.HermesCitizenApi;
import us.idinfor.smartcitizen.R;
import us.idinfor.smartcitizen.Utils;
import us.idinfor.smartcitizen.asynctask.UserRegisterAsyncTask;

public class SignupActivity extends BaseActivity {

    private static final String TAG = SignupActivity.class.getCanonicalName();
    @Bind(R.id.signupProgressBar)
    ProgressBar mSignupProgressBar;
    @Bind(R.id.userEdit)
    EditText mUserEdit;
    @Bind(R.id.passwordEdit)
    EditText mPasswordEdit;
    @Bind(R.id.signupBtn)
    Button mSignupBtn;
    @Bind(R.id.signupForm)
    ScrollView mSignupForm;

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserRegisterAsyncTask mAuthTask = null;


    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = Utils.getSharedPreferences(this);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);
        buildActionBarToolbar(getString(R.string.title_activity_signup), true);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    @OnClick(R.id.signupBtn)
    public void attemptSignup() {

        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mUserEdit.setError(null);
        mPasswordEdit.setError(null);

        // Store values at the time of the login attempt.
        final String username = mUserEdit.getText().toString();
        final String password = mPasswordEdit.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid email address.
        if (TextUtils.isEmpty(username)) {
            mUserEdit.setError(getString(R.string.error_field_required));
            focusView = mUserEdit;
            cancel = true;
        } else if(!username.matches("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$")){
            mUserEdit.setError(getString(R.string.error_email_pattern));
            focusView = mUserEdit;
            cancel = true;
        }

        // Check for a valid password
        if (TextUtils.isEmpty(password)) {
            mPasswordEdit.setError(getString(R.string.error_field_required));
            focusView = mPasswordEdit;
            cancel = true;
        } else if(!password.matches("^[a-zA-Z0-9]*$")){
            mPasswordEdit.setError(getString(R.string.error_password_pattern));
            focusView = mPasswordEdit;
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
            mAuthTask = new UserRegisterAsyncTask(username,password) {
                @Override
                protected void onPostExecute(Integer result) {
                    mAuthTask = null;
                    showProgress(false);
                    switch (result) {
                        case HermesCitizenApi.RESPONSE_OK:
                            Log.i(TAG,"User registered successfully");
                            Toast.makeText(getApplicationContext(), "User signed up", Toast.LENGTH_LONG).show();
                            prefs.edit()
                                    .putString(Constants.PROPERTY_USER_NAME, username)
                                    .apply();
                            Intent returnIntent = new Intent();
                            setResult(Activity.RESULT_OK,returnIntent);
                            finish();
                            break;
                        case HermesCitizenApi.RESPONSE_ERROR_USER_EXISTS:
                            Log.e(TAG,"Error: User already taken");
                            Toast.makeText(getApplicationContext(), "Error: User already taken", Toast.LENGTH_LONG).show();
                            break;
                        case HermesCitizenApi.RESPONSE_ERROR_USER_NOT_REGISTERED:
                            Log.e(TAG,"Error: User not registered");
                            Toast.makeText(getApplicationContext(), "Error: User not registered", Toast.LENGTH_LONG).show();
                            break;
                        default:
                            Log.e(TAG,"Unknown error");
                            Toast.makeText(getApplicationContext(), "Unknown error", Toast.LENGTH_LONG).show();
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

            mSignupForm.setVisibility(show ? View.GONE : View.VISIBLE);
            mSignupForm.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mSignupForm.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mSignupProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            mSignupProgressBar.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mSignupProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mSignupProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            mSignupForm.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    public static void launch(Activity activity) {
        Intent intent = new Intent(activity, SignupActivity.class);
        ActivityCompat.startActivityForResult(activity, intent, Constants.SIGNUP_RESOLUTION_REQUEST, null);
    }
}

