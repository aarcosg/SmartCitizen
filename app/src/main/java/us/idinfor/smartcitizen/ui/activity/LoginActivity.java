package us.idinfor.smartcitizen.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import us.idinfor.smartcitizen.R;
import us.idinfor.smartcitizen.di.HasComponent;
import us.idinfor.smartcitizen.di.components.DaggerLoginComponent;
import us.idinfor.smartcitizen.di.components.LoginComponent;
import us.idinfor.smartcitizen.di.modules.GoogleApiClientSignInModule;
import us.idinfor.smartcitizen.ui.fragment.LoginFragment;


public class LoginActivity extends BaseActivity implements HasComponent<LoginComponent>{

    private static final String TAG = LoginActivity.class.getCanonicalName();

    private LoginComponent mLoginComponent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        this.initializeInjector();
        if(savedInstanceState == null){
            addFragment(R.id.fragment_container, new LoginFragment());
        }
    }

    private void initializeInjector() {
        this.mLoginComponent = DaggerLoginComponent.builder()
                .applicationComponent(getApplicationComponent())
                .activityModule(getActivityModule())
                .googleApiClientSignInModule(new GoogleApiClientSignInModule(connectionResult ->
                        Log.e(TAG, "onConnectionFailed:" + connectionResult)
                ))
                .build();
    }

    @Override
    public LoginComponent getComponent() {
        return mLoginComponent;
    }

    public static void launch(Activity activity) {
        Intent intent = new Intent(activity, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        ActivityCompat.startActivity(activity, intent, null);
    }
}



