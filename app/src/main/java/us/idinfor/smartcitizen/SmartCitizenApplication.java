package us.idinfor.smartcitizen;

import android.content.Context;
import android.support.multidex.MultiDexApplication;

import us.idinfor.smartcitizen.di.components.ApplicationComponent;
import us.idinfor.smartcitizen.di.components.DaggerApplicationComponent;
import us.idinfor.smartcitizen.di.modules.ApplicationModule;
import us.idinfor.smartcitizen.di.modules.NetworkModule;

public class SmartCitizenApplication extends MultiDexApplication {

    private static final String TAG = SmartCitizenApplication.class.getCanonicalName();

    private ApplicationComponent applicationComponent;
    private ApplicationModule applicationModule;

    public SmartCitizenApplication(){
        super();
    }

    public static SmartCitizenApplication get(Context context){
        return (SmartCitizenApplication) context.getApplicationContext();
    }


    @Override
    public void onCreate() {
        super.onCreate();
        initApplicationComponent();
        //Fabric.with(this, new Crashlytics());
    }

    private void initApplicationComponent() {
        applicationModule = new ApplicationModule(this);
        applicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(applicationModule)
                .networkModule(new NetworkModule())
                .build();
    }

    public ApplicationComponent getApplicationComponent() {
        return applicationComponent;
    }

    public ApplicationModule getApplicationModule(){
        return applicationModule;
    }
}
