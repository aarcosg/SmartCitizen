package us.idinfor.smartcitizen.di.modules;

import android.app.Activity;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;

import dagger.Module;
import dagger.Provides;
import us.idinfor.smartcitizen.di.scopes.PerActivity;

@Module
public class GoogleApiClientSignInModule {

    private final GoogleApiClient.OnConnectionFailedListener mConnectionFailedListener;

    public GoogleApiClientSignInModule(
                           GoogleApiClient.OnConnectionFailedListener connectionFailedListener) {
        this.mConnectionFailedListener = connectionFailedListener;
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
    public GoogleApiClient providesGoogleApiClientSignIn(Activity activity,
            GoogleSignInOptions googleSignInOptions) {
        return new GoogleApiClient
                .Builder(activity)
                .enableAutoManage((FragmentActivity)activity, this.mConnectionFailedListener)
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build();
    }
}