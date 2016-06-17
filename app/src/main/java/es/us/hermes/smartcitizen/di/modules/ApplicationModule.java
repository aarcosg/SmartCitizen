package es.us.hermes.smartcitizen.di.modules;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;

import dagger.Module;
import dagger.Provides;
import es.us.hermes.smartcitizen.BuildConfig;
import es.us.hermes.smartcitizen.Constants;
import es.us.hermes.smartcitizen.SmartCitizenApplication;
import es.us.hermes.smartcitizen.data.api.google.fit.GoogleFitHelper;
import es.us.hermes.smartcitizen.di.scopes.PerApp;
import io.fabric.sdk.android.Fabric;

@Module
public class ApplicationModule {

    private final SmartCitizenApplication mApplication;

    public ApplicationModule(SmartCitizenApplication application) {
        this.mApplication = application;

        if(BuildConfig.DEBUG){
            //LeakCanary.install(this.mApplication);
        }else{
            Fabric.with(this.mApplication, new Crashlytics(), new Answers());
        }

        if(!TextUtils.isEmpty(provideDefaultSharedPreferences().getString(Constants.PROPERTY_USER_NAME, ""))){
            GoogleFitHelper.initFitApi(provideApplicationContext());
        }
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
}
