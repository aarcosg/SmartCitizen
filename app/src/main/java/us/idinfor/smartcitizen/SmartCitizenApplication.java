package us.idinfor.smartcitizen;

import android.app.Application;

public class SmartCitizenApplication extends Application {
    private static final String TAG = SmartCitizenApplication.class.getCanonicalName();

    public SmartCitizenApplication(){
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //ActivityRecognitionService.actionStartActivityRecognition(this);
    }
}
