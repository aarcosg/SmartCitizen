package es.us.hermes.smartcitizen;

import android.content.Context;
import android.support.multidex.MultiDexApplication;

import es.us.hermes.smartcitizen.di.components.ApplicationComponent;
import es.us.hermes.smartcitizen.di.components.DaggerApplicationComponent;
import es.us.hermes.smartcitizen.di.modules.ApplicationModule;
import es.us.hermes.smartcitizen.di.modules.NetworkModule;

public class SmartCitizenApplication extends MultiDexApplication {

    private static final String TAG = SmartCitizenApplication.class.getCanonicalName();

    private ApplicationComponent mApplicationComponent;

    public SmartCitizenApplication(){
        super();
    }

    public static SmartCitizenApplication get(Context context){
        return (SmartCitizenApplication) context.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.initializeInjector();
    }

    private void initializeInjector() {
        mApplicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .networkModule(new NetworkModule())
                .build();
    }

    public ApplicationComponent getApplicationComponent() {
        return this.mApplicationComponent;
    }
}
