package us.idinfor.smartcitizen.di.modules;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.crashlytics.android.Crashlytics;

import dagger.Module;
import dagger.Provides;
import io.fabric.sdk.android.Fabric;
import us.idinfor.smartcitizen.BuildConfig;
import us.idinfor.smartcitizen.Constants;
import us.idinfor.smartcitizen.data.api.google.fit.GoogleFitHelper;
import us.idinfor.smartcitizen.SmartCitizenApplication;
import us.idinfor.smartcitizen.di.scopes.PerApp;

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

        if(!TextUtils.isEmpty(provideDefaultSharedPreferences().getString(Constants.PROPERTY_USER_NAME, ""))){
            GoogleFitHelper.initFitApi(provideApplicationContext());
        }
    }

    @Provides
    @PerApp
    public Context provideApplicationContext() {
        return this.application;
    }

    @Provides
    @PerApp
    public SharedPreferences provideDefaultSharedPreferences() {
        return PreferenceManager
                .getDefaultSharedPreferences(this.application);
    }
}
