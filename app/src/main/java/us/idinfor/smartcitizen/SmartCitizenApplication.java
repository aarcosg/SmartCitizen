package us.idinfor.smartcitizen;

import android.support.multidex.MultiDexApplication;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

public class SmartCitizenApplication extends MultiDexApplication {
    private static final String TAG = SmartCitizenApplication.class.getCanonicalName();

    public SmartCitizenApplication(){
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
    }
}
