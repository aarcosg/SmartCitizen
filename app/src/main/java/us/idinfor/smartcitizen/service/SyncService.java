package us.idinfor.smartcitizen.service;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import java.util.Calendar;

import javax.inject.Inject;

import us.idinfor.smartcitizen.Constants;
import us.idinfor.smartcitizen.SmartCitizenApplication;
import us.idinfor.smartcitizen.data.api.google.fit.GoogleFitHelper;
import us.idinfor.smartcitizen.data.api.hermes.entity.User;
import us.idinfor.smartcitizen.mvp.presenter.SyncServicePresenter;
import us.idinfor.smartcitizen.mvp.view.SyncServiceView;

public class SyncService extends Service implements SyncServiceView {

    private static final String TAG = SyncService.class.getCanonicalName();

    @Inject
    Context mContext;
    @Inject
    SyncServicePresenter mSyncServicePresenter;

    private PendingIntent mSyncPendingIntent;

    public SyncService(){}

    @Inject
    public SyncService(Context context, SyncServicePresenter syncServicePresenter){
        this.mContext = context;
        this.mSyncServicePresenter = syncServicePresenter;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.initializeInjector();
        GoogleFitHelper.initFitApi(this.getApplicationContext());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mSyncServicePresenter.onDestroy();
    }

    private void initializeInjector() {
        SmartCitizenApplication.get(this.getApplicationContext()).getApplicationComponent().inject(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG,"@onStartCommand");
        checkUser();
        if(intent != null){
            this.mSyncServicePresenter.queryLocations();
            this.mSyncServicePresenter.queryActivities();
        }
        return START_STICKY;
    }

    public void start(){
        Log.i(TAG,"@start");
        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        Calendar startTime = Calendar.getInstance();
        startTime.add(Calendar.MINUTE, Constants.SYNC_INTERVAL_IN_MINUTES); // First time
        long frequency = Constants.SYNC_INTERVAL_IN_MINUTES * 60 * 1000;
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, startTime.getTimeInMillis(), frequency, getSyncPendingIntent());
    }

    public void stop(){
        Log.i(TAG,"@stop");
        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(getSyncPendingIntent());
    }

    public boolean isRunning() {
        Log.i(TAG,"@isRunning");
        ActivityManager manager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (SyncService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void checkUser(){
        User user = this.mSyncServicePresenter.getUser();
        if(TextUtils.isEmpty(user.getEmail())){
            this.stop();
            this.stopSelf();
        }
    }

    private PendingIntent getSyncPendingIntent(){
        if(mSyncPendingIntent == null){
            Intent intent = new Intent(mContext, SyncService.class);
            mSyncPendingIntent = PendingIntent.getService(
                    mContext, 0,
                    intent, 0);
        }
        return mSyncPendingIntent;
    }
}
