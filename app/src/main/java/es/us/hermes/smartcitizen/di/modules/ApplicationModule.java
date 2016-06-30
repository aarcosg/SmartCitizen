package es.us.hermes.smartcitizen.di.modules;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import dagger.Module;
import dagger.Provides;
import es.us.hermes.smartcitizen.BuildConfig;
import es.us.hermes.smartcitizen.Constants;
import es.us.hermes.smartcitizen.R;
import es.us.hermes.smartcitizen.SmartCitizenApplication;
import es.us.hermes.smartcitizen.data.api.google.fit.GoogleFitHelper;
import es.us.hermes.smartcitizen.di.scopes.PerApp;
import io.fabric.sdk.android.Fabric;

@Module
public class ApplicationModule {

    private final SmartCitizenApplication mApplication;
    private final Tracker mTracker;

    public ApplicationModule(SmartCitizenApplication application) {
        this.mApplication = application;
        GoogleAnalytics analytics = GoogleAnalytics.getInstance(this.mApplication);

        if(BuildConfig.DEBUG){
            //LeakCanary.install(this.mApplication);
            analytics.setDryRun(true);
        }else{
            Fabric.with(this.mApplication, new Crashlytics(), new Answers());
        }

        if(!TextUtils.isEmpty(provideDefaultSharedPreferences().getString(Constants.PROPERTY_USER_NAME, ""))){
            GoogleFitHelper.initFitApi(provideApplicationContext());
        }

        this.mTracker = analytics.newTracker(R.xml.global_tracker);
    }

    @Provides
    @PerApp
    public Context provideApplicationContext() {
        return this.mApplication;
    }

    @Provides
    @PerApp
    public SharedPreferences provideDefaultSharedPreferences() {
        return PreferenceManager
                .getDefaultSharedPreferences(this.mApplication);
    }

    @Provides
    @PerApp
    public Tracker provideGATracker() {
        return this.mTracker;
    }
}
