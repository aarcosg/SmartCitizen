package us.idinfor.smartcitizen.di.modules;

import android.content.Context;
import android.support.annotation.Nullable;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.fitness.Fitness;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import us.idinfor.smartcitizen.activity.BaseActivity;
import us.idinfor.smartcitizen.di.scopes.PerActivity;
import us.idinfor.smartcitizen.di.scopes.PerApp;

@Module
public class GoogleApiClientModule {

    public final static String NAME_GOOGLEAPICLIENT_SIGNIN = "NAME_GOOGLEAPICLIENT_SIGNIN";
    public final static String NAME_GOOGLEAPICLIENT_FIT_AUTO = "NAME_GOOGLEAPICLIENT_FIT_AUTO";
    public final static String NAME_GOOGLEAPICLIENT_FIT_MANUAL = "NAME_GOOGLEAPICLIENT_FIT_MANUAL";

    private GoogleApiClient.OnConnectionFailedListener connectionFailedListener;
    private GoogleApiClient.ConnectionCallbacks connectionCallbacks;

    public GoogleApiClientModule(
            GoogleApiClient.OnConnectionFailedListener connectionFailedListener,
            @Nullable GoogleApiClient.ConnectionCallbacks connectionCallbacks) {

        connectionFailedListener = connectionFailedListener;
        connectionCallbacks = connectionCallbacks;
    }

    @PerActivity
    @Provides
    public GoogleSignInOptions provideGoogleSignInOptions() {
        return new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
    }

    @Named(NAME_GOOGLEAPICLIENT_SIGNIN)
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

    @Named(NAME_GOOGLEAPICLIENT_FIT_AUTO)
    @PerApp
    @Provides
    public GoogleApiClient providesGoogleApiClientFitAuto(BaseActivity baseActivity) {
        return new GoogleApiClient
                .Builder(baseActivity)
                .enableAutoManage(baseActivity, connectionFailedListener)
                .addApi(Fitness.HISTORY_API)
                .addApi(Fitness.RECORDING_API)
                .addScope(new Scope(Scopes.FITNESS_LOCATION_READ_WRITE))
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                .addScope(new Scope(Scopes.FITNESS_BODY_READ_WRITE))
                .build();
    }

    @Named(NAME_GOOGLEAPICLIENT_FIT_MANUAL)
    @PerApp
    @Provides
    public GoogleApiClient providesGoogleApiClientFitManual(Context context) {
        return new GoogleApiClient
                .Builder(context)
                .addApi(Fitness.HISTORY_API)
                .addApi(Fitness.RECORDING_API)
                .addScope(new Scope(Scopes.FITNESS_LOCATION_READ_WRITE))
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                .addScope(new Scope(Scopes.FITNESS_BODY_READ_WRITE))
                .addConnectionCallbacks(connectionCallbacks)
                .addOnConnectionFailedListener(connectionFailedListener)
                .build();
    }
}