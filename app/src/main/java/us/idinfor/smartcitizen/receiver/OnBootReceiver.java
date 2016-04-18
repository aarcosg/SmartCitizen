package us.idinfor.smartcitizen.receiver;


import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import javax.inject.Inject;

import us.idinfor.smartcitizen.SmartCitizenApplication;
import us.idinfor.smartcitizen.service.SyncService;

public class OnBootReceiver extends WakefulBroadcastReceiver {

    private static final String TAG = OnBootReceiver.class.getCanonicalName();

    @Inject
    SyncService mSyncService;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "onReceive");
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            this.initializeInjector(context);
            mSyncService.start();
        }
    }

    private void initializeInjector(Context context) {
        SmartCitizenApplication.get(context).getApplicationComponent().inject(this);
    }
}
