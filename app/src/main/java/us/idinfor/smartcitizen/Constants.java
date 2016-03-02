package us.idinfor.smartcitizen;


import android.content.Context;
import android.content.res.Resources;

import com.google.android.gms.location.DetectedActivity;

public class Constants {

    public static final String PACKAGE_NAME = "us.idinfor.smartcitizen";
    public static final String GOOGLE_APPENGINE_URL = "https://hermes-smart-citizen.appspot.com/_ah/api/";
    public static final String GOOGLE_APPENGINE_APP_NAME = "hermes-smart-citizen";
    public static final String GCM_SENDER_ID = "208008499190";
    public static final String PROPERTY_DEVICE_ID = "device_id";
    public static final String PROPERTY_GCM_ID = "gcm_id";
    public static final String PROPERTY_APP_VERSION = "app_version";
    public static final String PROPERTY_DRAWER_LEARNED = "drawer_learned";
    public static final String PROPERTY_LAST_DETECTED_ACTIVITY = "last_detected_activity";
    public static final String PROPERTY_LAST_LATITUDE = "last_latitude";
    public static final String PROPERTY_LAST_LONGITUDE = "last_longitude";
    public static final String PROPERTY_ACTIVITY_UPDATES = "activity_updates";
    public static final String PROPERTY_LOCATION_UPDATES = "location_updates";
    public static final String PROPERTY_USER_NAME = "user_name";
    public static final String PROPERTY_RECORD_DATA = "record_data";
    public static final String PROPERTY_SYNC_DATA_HERMES = "sync_data_hermes";
    public static final String PROPERTY_LAST_LOCATION_TIME_SENT = "last_location_time_sent";
    public static final String PROPERTY_LAST_ACTIVITY_TIME_SENT = "last_activity_time_sent";

    public static final String EXTRA_DETECTED_CONTEXTS = PACKAGE_NAME + ".EXTRA_DETECTED_CONTEXTS";
    public static final String EXTRA_LOCATION = PACKAGE_NAME + ".EXTRA_LOCATION";
    public static final String EXTRA_LATITUDE = PACKAGE_NAME + ".EXTRA_LATITUDE";
    public static final String EXTRA_LONGITUDE = PACKAGE_NAME + ".EXTRA_LONGITUDE";
    public static final String EXTRA_ID = PACKAGE_NAME + ".EXTRA_ID";
    public static final String EXTRA_RESULT = PACKAGE_NAME + ".EXTRA_RESULT";
    public static final String EXTRA_ADDRESS = PACKAGE_NAME + ".EXTRA_ADDRESS";
    public static final String EXTRA_DETECTED_CONTEXT = PACKAGE_NAME + ".EXTRA_DETECTED_CONTEXT";
    public static final String EXTRA_DETECTED_CONTEXT_CONFIDENCE = PACKAGE_NAME + ".EXTRA_DETECTED_CONTEXT_CONFIDENCE";
    public static final String EXTRA_NOTIFICATION = PACKAGE_NAME + ".EXTRA_NOTIFICATION";

    public static final int PLAY_SERVICES_RESOLUTION_REQUEST  = 9000;
    public static final long ACTIVITY_DETECTION_INTERVAL_IN_MILLISECONDS = 30*1000;
    public static final long LOCATION_REQUEST_INTERVAL_IN_MILLISECONDS = 60*1000;
    public static final long LOCATION_REQUEST_FASTEST_INTERVAL_IN_MILLISECONDS = 60*1000;
    public static final int SUCCESS_RESULT = 0;
    public static final int FAILURE_RESULT = 1;
    public static final int MIN_DETECTED_CONFIDENCE = 70;
    public static final double DEFAULT_LATITUDE = 37.359954;
    public static final double DEFAULT_LONGITUDE = -5.987395;

    public static final String ACTION_START_ACTIVITY_RECOGNITION = PACKAGE_NAME + ".ACTION_START_ACTIVITY_RECOGNITION";
    public static final String ACTION_STOP_ACTIVITY_RECOGNITION = PACKAGE_NAME + ".ACTION_STOP_ACTIVITY_RECOGNITION";
    public static final String ACTION_ACTIVITY_RECOGNITION_RESULT = PACKAGE_NAME + ".ACTION_ACTIVITY_RECOGNITION_RESULT";
    public static final String ACTION_LOCATION_CHANGED_RESULT = PACKAGE_NAME + ".ACTION_LOCATION_CHANGED_RESULT";
    public static final String ACTION_QUERY_ALL =  Constants.PACKAGE_NAME + ".ACTION_QUERY_ALL";

    public static final String DATABASE_NAME = "smartcitizen-db";

    public static final int RANGE_DAY = 0;
    public static final int RANGE_WEEK = 1;
    public static final int RANGE_MONTH = 2;
    public static final int RANGE_YEAR = 3;

    public static final String ACTIVITY_SLEEP_PREFIX = "sleep";

    public static final int SIGNUP_RESOLUTION_REQUEST = 100;



    /**
     * List of DetectedActivity types that we monitor in this sample.
     */
    public static final int[] MONITORED_ACTIVITIES = {
            DetectedActivity.STILL,
            DetectedActivity.ON_FOOT,
            DetectedActivity.WALKING,
            DetectedActivity.RUNNING,
            DetectedActivity.ON_BICYCLE,
            DetectedActivity.IN_VEHICLE,
            DetectedActivity.TILTING,
            DetectedActivity.UNKNOWN
    };

    /**
     * Returns a human readable String corresponding to a detected activity type.
     */
    public static String getActivityString(Context context, int detectedActivityType) {
        Resources resources = context.getResources();
        switch(detectedActivityType) {
            case DetectedActivity.IN_VEHICLE:
                return resources.getString(R.string.in_vehicle);
            case DetectedActivity.ON_BICYCLE:
                return resources.getString(R.string.on_bicycle);
            case DetectedActivity.WALKING:
                return resources.getString(R.string.walking);
            case DetectedActivity.RUNNING:
                return resources.getString(R.string.running);
            case DetectedActivity.ON_FOOT:
                return resources.getString(R.string.on_foot);
            case DetectedActivity.STILL:
                return resources.getString(R.string.still);
            case DetectedActivity.TILTING:
                return resources.getString(R.string.tilting);
            case DetectedActivity.UNKNOWN:
                return resources.getString(R.string.unknown);
            default:
                return resources.getString(R.string.other);
        }
    }
}
