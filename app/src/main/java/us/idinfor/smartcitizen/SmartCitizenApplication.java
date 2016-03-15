package us.idinfor.smartcitizen;

import android.support.multidex.MultiDexApplication;

import us.idinfor.smartcitizen.di.components.ApplicationComponent;
import us.idinfor.smartcitizen.di.components.DaggerApplicationComponent;
import us.idinfor.smartcitizen.di.modules.ApplicationModule;
import us.idinfor.smartcitizen.di.modules.NetworkModule;

public class SmartCitizenApplication extends MultiDexApplication {

    private static final String TAG = SmartCitizenApplication.class.getCanonicalName();

    private static ApplicationComponent applicationComponent;

    public SmartCitizenApplication(){
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initializeInjector();
        //Fabric.with(this, new Crashlytics());
    }

    private void initializeInjector() {
        applicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .networkModule(new NetworkModule())
                .build();
    }

    public static ApplicationComponent getApplicationComponent() {
        return applicationComponent;
    }
}
