package us.idinfor.smartcitizen;

import android.support.multidex.MultiDexApplication;

public class SmartCitizenApplication extends MultiDexApplication {
    private static final String TAG = SmartCitizenApplication.class.getCanonicalName();

    public SmartCitizenApplication(){
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
