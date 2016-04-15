package us.idinfor.smartcitizen;


public class Constants {

    public static final String PACKAGE_NAME = "us.idinfor.smartcitizen";

    public static final String PROPERTY_APP_VERSION = "app_version";
    public static final String PROPERTY_DRAWER_LEARNED = "drawer_learned";
    public static final String PROPERTY_USER_NAME = "user_name";
    public static final String PROPERTY_RECORD_DATA = "record_data";
    public static final String PROPERTY_SYNC_DATA_HERMES = "sync_data_hermes";
    public static final String PROPERTY_LAST_LOCATION_TIME_SENT = "last_location_time_sent";
    public static final String PROPERTY_LAST_ACTIVITY_TIME_SENT = "last_activity_time_sent";

    public static final int PLAY_SERVICES_RESOLUTION_REQUEST  = 9000;
    public static final String ACTIVITY_SLEEP_PREFIX = "sleep";
    public static final int SIGNUP_RESOLUTION_REQUEST = 100;
    public static final int HERMES_SYNC_INTERVAL_IN_MINUTES = 15;
    public static final int SYNC_INTERVAL_IN_MINUTES = 3;

    public static final String ACTION_QUERY_ALL =  Constants.PACKAGE_NAME + ".ACTION_QUERY_ALL";

    public static final int RANGE_DAY = 0;
    public static final int RANGE_WEEK = 1;
    public static final int RANGE_MONTH = 2;
    public static final int RANGE_YEAR = 3;

    public static final String DEFAULT_PASSWORD = "123456";
}
