package us.idinfor.smartcitizen.receiver;


import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import us.idinfor.smartcitizen.service.ActivityRecognitionService;

public class OnBootReceiver extends WakefulBroadcastReceiver {

    private static final String TAG = OnBootReceiver.class.getCanonicalName();
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "onReceive");
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            //Start activity recognition
            ActivityRecognitionService.actionStartActivityRecognition(context.getApplicationContext());
        }
    }
}
