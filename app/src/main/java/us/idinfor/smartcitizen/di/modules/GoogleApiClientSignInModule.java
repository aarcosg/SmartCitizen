package us.idinfor.smartcitizen.di.modules;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;

import dagger.Module;
import dagger.Provides;
import us.idinfor.smartcitizen.activity.BaseActivity;
import us.idinfor.smartcitizen.di.scopes.PerActivity;

@Module (includes = ActivityModule.class)
public class GoogleApiClientSignInModule {

    private GoogleApiClient.OnConnectionFailedListener connectionFailedListener;

    public GoogleApiClientSignInModule(
            GoogleApiClient.OnConnectionFailedListener connectionFailedListener) {
        connectionFailedListener = connectionFailedListener;
    }

    @PerActivity
    @Provides
    public GoogleSignInOptions provideGoogleSignInOptions() {
        return new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
    }

    @PerActivity
    @Provides
    public GoogleApiClient providesGoogleApiClientSignIn(BaseActivity baseActivity,
                                                   GoogleSignInOptions googleSignInOptions) {
        return new GoogleApiClient
                .Builder(baseActivity)
                .enableAutoManage(baseActivity, connectionFailedListener)
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build();
    }
}