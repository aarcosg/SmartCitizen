package us.idinfor.smartcitizen.di.modules;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;

import dagger.Module;
import dagger.Provides;
import us.idinfor.smartcitizen.activity.BaseActivity;
import us.idinfor.smartcitizen.di.scopes.PerActivity;

@Module
public class GoogleApiClientModule {

    private GoogleApiClient.OnConnectionFailedListener mConnectionFailedListener;
    private GoogleApiClient.ConnectionCallbacks mConnectionCallbacks;

    public GoogleApiClientModule(
            GoogleApiClient.OnConnectionFailedListener connectionFailedListener,
            GoogleApiClient.ConnectionCallbacks connectionCallbacks) {
        mConnectionFailedListener = connectionFailedListener;
        mConnectionCallbacks = connectionCallbacks;
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
                .enableAutoManage(baseActivity, mConnectionFailedListener)
                .addConnectionCallbacks(mConnectionCallbacks)
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build();
    }
}