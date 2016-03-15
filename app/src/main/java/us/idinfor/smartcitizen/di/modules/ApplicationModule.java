package us.idinfor.smartcitizen.di.modules;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.crashlytics.android.Crashlytics;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.fabric.sdk.android.Fabric;
import us.idinfor.smartcitizen.BuildConfig;
import us.idinfor.smartcitizen.SmartCitizenApplication;

@Module
public class ApplicationModule {
    private final SmartCitizenApplication application;

    public ApplicationModule(SmartCitizenApplication application) {
        this.application = application;

        if(BuildConfig.DEBUG){
            //debug libs initialization
        }else{
            Fabric.with(this.application, new Crashlytics());
        }
    }

    @Provides
    @Singleton
    Context provideApplicationContext() {
        return this.application;
    }

    @Provides
    @Singleton
    SharedPreferences provideDefaultSharedPreferences() {
        return PreferenceManager
                .getDefaultSharedPreferences(this.application);
    }
}
