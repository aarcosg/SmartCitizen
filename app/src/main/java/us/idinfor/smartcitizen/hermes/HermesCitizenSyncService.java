package us.idinfor.smartcitizen.hermes;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.fitness.FitnessActivities;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import us.idinfor.smartcitizen.Constants;
import us.idinfor.smartcitizen.GoogleFitApi;
import us.idinfor.smartcitizen.Utils;
import us.idinfor.smartcitizen.event.FitBucketsResultEvent;
import us.idinfor.smartcitizen.event.FitDataSetsResultEvent;
import us.idinfor.smartcitizen.model.fit.ActivitySegmentFit;
import us.idinfor.smartcitizen.model.fit.LocationSampleFit;

public class HermesCitizenSyncService extends Service {

    private static final String TAG = HermesCitizenSyncService.class.getCanonicalName();

    private static final String ACTION_QUERY_LOCATIONS = Constants.PACKAGE_NAME + "ACTION_QUERY_LOCATIONS";
    private static final String ACTION_QUERY_ACTIVITIES =  Constants.PACKAGE_NAME + "ACTION_QUERY_ACTIVITIES";
    private static final String ACTION_UPLOAD_LOCATIONS = Constants.PACKAGE_NAME + "ACTION_UPLOAD_LOCATIONS";
    private static final String ACTION_UPLOAD_ACTIVITIES =  Constants.PACKAGE_NAME + "ACTION_UPLOAD_ACTIVITIES";

    private static final String EXTRA_USERNAME =  Constants.PACKAGE_NAME + "EXTRA_USERNAME";
    private static final String EXTRA_ITEMS =  Constants.PACKAGE_NAME + "EXTRA_ITEMS";

    private static PendingIntent syncPI;

    public HermesCitizenSyncService() {}

    public static void startActionQueryAll(Context context) {
        Intent intent = new Intent(context, HermesCitizenSyncService.class);
        intent.setAction(Constants.ACTION_QUERY_ALL);
        context.startService(intent);
    }

    public static void startActionQueryLocations(Context context) {
        Intent intent = new Intent(context, HermesCitizenSyncService.class);
        intent.setAction(ACTION_QUERY_LOCATIONS);
        context.startService(intent);
    }

    public static void startActionQueryActivities(Context context) {
        Intent intent = new Intent(context, HermesCitizenSyncService.class);
        intent.setAction(ACTION_QUERY_ACTIVITIES);
        context.startService(intent);
    }

    public static void startActionUploadLocations(Context context, String username, List<LocationSampleFit> items) {
        Intent intent = new Intent(context, HermesCitizenSyncService.class);
        intent.setAction(ACTION_UPLOAD_LOCATIONS);
        intent.putExtra(EXTRA_USERNAME, username);
        intent.putParcelableArrayListExtra(EXTRA_ITEMS, (ArrayList<LocationSampleFit>)items);
        context.startService(intent);
    }

    public static void startActionUploadActivities(Context context, String username, List<ActivitySegmentFit> items) {
        Intent intent = new Intent(context, HermesCitizenSyncService.class);
        intent.setAction(ACTION_UPLOAD_ACTIVITIES);
        intent.putExtra(EXTRA_USERNAME, username);
        intent.putParcelableArrayListExtra(EXTRA_ITEMS, (ArrayList<ActivitySegmentFit>)items);
        context.startService(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if(!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void onDestroy() {
        if(EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().unregister(this);
        }
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            final String action = intent.getAction();
            Log.i(TAG,"@onStartCommand:" + action);
            if (ACTION_QUERY_LOCATIONS.equals(action)){
                HermesCitizenSyncUtils.queryLocationsFit(getApplicationContext());
            } else if (ACTION_QUERY_ACTIVITIES.equals(action)) {
                HermesCitizenSyncUtils.queryActivitiesFit(getApplicationContext());
            } else if (Constants.ACTION_QUERY_ALL.equals(action)) {
                HermesCitizenSyncUtils.queryLocationsFit(getApplicationContext());
                HermesCitizenSyncUtils.queryActivitiesFit(getApplicationContext());
            } else if (ACTION_UPLOAD_LOCATIONS.equals(action)) {
                final String username = intent.getStringExtra(EXTRA_USERNAME);
                final List<LocationSampleFit> items = intent.getParcelableArrayListExtra(EXTRA_ITEMS);
                handleActionUploadLocations(username, items);
            } else if (ACTION_UPLOAD_ACTIVITIES.equals(action)) {
                final String username = intent.getStringExtra(EXTRA_USERNAME);
                final List<ActivitySegmentFit> items = intent.getParcelableArrayListExtra(EXTRA_ITEMS);
                handleActionUploadActivities(username, items);
            }
        }
        return START_STICKY;
    }

    private void handleActionUploadLocations(String username, List<LocationSampleFit> items) {
        handleApiResult(ACTION_UPLOAD_LOCATIONS, HermesCitizenApi.uploadLocations(username,items));
    }

    private void handleActionUploadActivities(String username, List<ActivitySegmentFit> items) {
        handleApiResult(ACTION_UPLOAD_ACTIVITIES, HermesCitizenApi.uploadActivities(username,items));
    }

    private void handleApiResult(String action, Integer result) {
        Log.i(TAG,"@handleApiResult");
        SharedPreferences prefs = Utils.getSharedPreferences(getApplicationContext());
        switch (result) {
            case HermesCitizenApi.RESPONSE_OK:
                Log.i(TAG,"Data uploaded successfully");
                long endTime = new Date().getTime();
                if (ACTION_UPLOAD_LOCATIONS.equals(action)) {
                    prefs.edit().putLong(Constants.PROPERTY_LAST_LOCATION_TIME_SENT,endTime).apply();
                } else if (ACTION_UPLOAD_ACTIVITIES.equals(action)) {
                    prefs.edit().putLong(Constants.PROPERTY_LAST_ACTIVITY_TIME_SENT,endTime).apply();
                }
                break;
            case HermesCitizenApi.RESPONSE_ERROR_USER_NOT_FOUND:
                Log.e(TAG,"Error: User not found");
                prefs.edit().remove(Constants.PROPERTY_USER_NAME).apply();
                stopSelf();
                break;
            case HermesCitizenApi.RESPONSE_ERROR_DATA_NOT_UPLOADED:
                Log.e(TAG,"Error: Data not uploaded");
                break;
            default:
                Log.e(TAG,"Unknown error");
        }
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onFitDataResult(FitDataSetsResultEvent result){
        switch (result.getQueryType()){
            case GoogleFitApi.QUERY_LOCATIONS_HERMES:
                Log.i(TAG,"FitDataSetsResultEvent@GoogleFitService.QUERY_LOCATIONS_HERMES");
                String username = Utils.getSharedPreferences(getApplicationContext()).getString(Constants.PROPERTY_USER_NAME,"");
                List<LocationSampleFit> locations = HermesCitizenSyncUtils.dataSetsToLocationSampleList(result.getDataSets());
                if(!locations.isEmpty()){
                    handleActionUploadLocations(username, locations);
                }
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onFitDataResult(FitBucketsResultEvent result){
        switch (result.getQueryType()){
            case GoogleFitApi.QUERY_ACTIVITIES_HERMES:
                Log.i(TAG,"FitBucketsResultEvent@GoogleFitService.QUERY_ACTIVITIES_HERMES");
                String username = Utils.getSharedPreferences(getApplicationContext()).getString(Constants.PROPERTY_USER_NAME,"");
                List<ActivitySegmentFit> activities = HermesCitizenSyncUtils.bucketsToActivitySegmentList(result.getBuckets());
                if(!activities.isEmpty() &&
                        (activities.size() > 1 ||
                                (activities.size() == 1 && !activities.get(0).getName().equals(FitnessActivities.UNKNOWN)))){
                    handleActionUploadActivities(username, activities);
                }

                break;
        }
    }

    public static void startSync(Context context){
        Log.i(TAG,"@startSync");
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, Constants.HERMES_SYNC_INTERVAL_IN_MINUTES); // First time
        long frequency = Constants.HERMES_SYNC_INTERVAL_IN_MINUTES * 60 * 1000;

        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), frequency, getSyncPI(context));

        Utils.getSharedPreferences(context).edit().putBoolean(Constants.PROPERTY_SYNC_DATA_HERMES,true).apply();
    }

    public static void stopSync(Context context){
        Log.i(TAG,"@stopSync");
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(getSyncPI(context));
        Utils.getSharedPreferences(context).edit().putBoolean(Constants.PROPERTY_SYNC_DATA_HERMES,false).apply();
    }

    private static PendingIntent getSyncPI(Context context) {
        if (syncPI == null) {
            Intent syncService = new Intent(context, HermesCitizenSyncService.class);
            syncService.setAction(Constants.ACTION_QUERY_ALL);
            syncPI = PendingIntent.getService(context,  0, syncService, 0);
        }
        return syncPI;
    }
}
