package us.idinfor.smartcitizen;

import android.support.multidex.MultiDexApplication;

import us.idinfor.smartcitizen.di.components.ApplicationComponent;
import us.idinfor.smartcitizen.di.components.DaggerApplicationComponent;
import us.idinfor.smartcitizen.di.components.DaggerNetworkComponent;
import us.idinfor.smartcitizen.di.components.NetworkComponent;
import us.idinfor.smartcitizen.di.modules.ApplicationModule;
import us.idinfor.smartcitizen.di.modules.NetworkModule;

public class SmartCitizenApplication extends MultiDexApplication {

    private static final String TAG = SmartCitizenApplication.class.getCanonicalName();

    private ApplicationComponent applicationComponent;
    private NetworkComponent networkComponent;

    public SmartCitizenApplication(){
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.initializeInjectors();
        //Fabric.with(this, new Crashlytics());
    }

    private void initializeInjectors() {
        this.applicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .build();
        this.networkComponent = DaggerNetworkComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .networkModule(new NetworkModule())
                .build();

    }

    public ApplicationComponent getApplicationComponent() {
        return this.applicationComponent;
    }

    public NetworkComponent getNetworkComponent(){
        return this.networkComponent;
    }
}
