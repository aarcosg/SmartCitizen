package us.idinfor.smartcitizen.service;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.util.List;

import us.idinfor.smartcitizen.Constants;
import us.idinfor.smartcitizen.Utils;
import us.idinfor.smartcitizen.asynctask.AddDetectedContextAsyncTask;

public class ActivityRecognitionService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        ResultCallback<Status> {

    private static final String TAG = ActivityRecognitionService.class.getCanonicalName();

    private GoogleApiClient mGoogleApiClient;
    private String mCurrentContext;
    private Long deviceId;
    private PendingIntent mActivityRecognitionPI;
    private PendingIntent mLocationChangedPI;
    private boolean startRecognition;
    private boolean stopRecognition;
    private SharedPreferences prefs;
    private LocationRequest mLocationRequest;
    private Location mLastLocation;
    private boolean sendContext;

    public ActivityRecognitionService() {}

    public static void actionStartActivityRecognition(Context context) {
        Log.d(TAG,"@actionStartActivityRecognition");
        Intent intent = new Intent(context, ActivityRecognitionService.class);
        intent.setAction(Constants.ACTION_START_ACTIVITY_RECOGNITION);
        context.startService(intent);
    }

    public static void actionStopActivityRecognition(Context context){
        Log.d(TAG,"@actionStopActivityRecognition");
        Intent intent = new Intent(context, ActivityRecognitionService.class);
        intent.setAction(Constants.ACTION_STOP_ACTIVITY_RECOGNITION);
        context.startService(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            Log.i(TAG, "onStartCommand" + intent.getAction());
            final String action = intent.getAction();
            if (Constants.ACTION_START_ACTIVITY_RECOGNITION.equals(action)) {
                handleActionStartActivityRecognition();
            }else if(Constants.ACTION_ACTIVITY_RECOGNITION_RESULT.equals(action) && ActivityRecognitionResult.hasResult(intent)){
                handleActionActivityRecognitionResult(ActivityRecognitionResult.extractResult(intent));
            }else if(Constants.ACTION_STOP_ACTIVITY_RECOGNITION.equals(action)){
                handleActionStopActivityRecognition();
            }else if(Constants.ACTION_LOCATION_CHANGED_RESULT.equals(action) && LocationResult.hasResult(intent)){
                handleActionLocationChangedResult(LocationResult.extractResult(intent));
            }
        }
        return START_STICKY;
    }



    private void handleActionStartActivityRecognition(){
        Log.d(TAG,"@handleActionStartActivityRecognition");
        if (mGoogleApiClient == null) {
            buildGoogleApiClient();
        }
        if (!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }
        if (mGoogleApiClient.isConnected()) {
            createLocationRequest();
            startLocationUpdates();
            startActivityRecognition();
        } else {
            startRecognition = true;
        }
    }

    private void handleActionActivityRecognitionResult(ActivityRecognitionResult result){
        Log.d(TAG,"@handleActionActivityRecognitionResult");
        Log.i(TAG, result.toString());
        DetectedActivity detectedActivity = result.getMostProbableActivity();
        mCurrentContext = Constants.getActivityString(getApplicationContext(),detectedActivity.getType());
        int confidence = detectedActivity.getConfidence();
        Log.d(TAG, "Detected context=" + mCurrentContext + " Confidence=" + confidence);

        // Get the list of the probable activities associated with the current state of the
        // device. Each activity is associated with a confidence level, which is an int between
        // 0 and 100.
        List<DetectedActivity> detectedActivities = result.getProbableActivities();

        // Log each activity.
        Log.i(TAG, "activities detected");
        for (DetectedActivity da: detectedActivities) {
            Log.i(TAG, Constants.getActivityString(
                            getApplicationContext(),
                            da.getType()) + " " + da.getConfidence() + "%"
            );
        }

        if(confidence >= Constants.MIN_DETECTED_CONFIDENCE) {
            prefs = Utils.getSharedPreferences(this);
            deviceId = prefs.getLong(Constants.PROPERTY_DEVICE_ID, 0L);
            if (deviceId > 0L) {
                Log.d(TAG,"Save last detected activity on prefs");
                prefs.edit()
                        .putString(Constants.PROPERTY_LAST_DETECTED_ACTIVITY, mCurrentContext)
                        .apply();
            }
        }
    }

    private void handleActionLocationChangedResult(LocationResult locationResult) {
        mLastLocation = locationResult.getLastLocation();
        prefs = Utils.getSharedPreferences(this);
        deviceId = prefs.getLong(Constants.PROPERTY_DEVICE_ID, 0L);
        if (deviceId > 0L) {
            sendContext = true;
            if (mGoogleApiClient == null) {
                Log.d(TAG, "googleapiclient = null");
                buildGoogleApiClient();
                if (!mGoogleApiClient.isConnected()) {
                    mGoogleApiClient.connect();
                }
            } else {
                Log.d(TAG, "googleapiclient != null");
                if (!mGoogleApiClient.isConnected()) {
                    mGoogleApiClient.connect();
                }
                sendContextToBackend();
            }
        }
    }

    private void handleActionStopActivityRecognition(){
        Log.d(TAG,"@handleActionStopActivityRecognition");
        if(mGoogleApiClient == null) {
            buildGoogleApiClient();
        }
        if (!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }
        if (mGoogleApiClient.isConnected()) {
            stopLocationUpdates();
            stopActivityRecognition();
        } else {
            stopRecognition = true;
        }
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(Constants.LOCATION_REQUEST_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(Constants.LOCATION_REQUEST_FASTEST_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
    }

    /**
     * Builds a GoogleApiClient. Uses the {@code #addApi} method to request the
     * ActivityRecognition API.
     */
    protected synchronized void buildGoogleApiClient() {
        Log.d(TAG, "Building GoogleApiClient");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(ActivityRecognition.API)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "Google Play Services connected");
        if (startRecognition) {
            createLocationRequest();
            startLocationUpdates();
            startActivityRecognition();
            startRecognition = false;
        }
        if(stopRecognition){
            stopLocationUpdates();
            stopActivityRecognition();
            stopRecognition = false;
        }

    }

    @Override
    public void onConnectionSuspended(int i) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.w(TAG, "Google Play Services connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.w(TAG, "Google Play Services connection failed");
    }

    /**
     * Gets a PendingIntent to be sent for each activity detection.
     */
    private PendingIntent getActivityRecognitionPendingIntent() {
        if (mActivityRecognitionPI == null) {
            Intent intent = new Intent(this, ActivityRecognitionService.class);
            intent.setAction(Constants.ACTION_ACTIVITY_RECOGNITION_RESULT);
            mActivityRecognitionPI = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
        return mActivityRecognitionPI;
    }

    /**
     * Gets a PendingIntent to be sent for each location update.
     */
    private PendingIntent getLocationChangedPendingIntent() {
        if (mLocationChangedPI == null) {
            Intent intent = new Intent(this, ActivityRecognitionService.class);
            intent.setAction(Constants.ACTION_LOCATION_CHANGED_RESULT);
            mLocationChangedPI = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
        return mLocationChangedPI;
    }

    private void startActivityRecognition() {
        //if(!isActivityRecognitionRunning()){
            Log.i(TAG, "startActivityRecognition");
            ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(
                    mGoogleApiClient,
                    Constants.ACTIVITY_DETECTION_INTERVAL_IN_MILLISECONDS,
                    getActivityRecognitionPendingIntent()
            ).setResultCallback(this);
        //}

    }

    private void stopActivityRecognition() {
        //if(isActivityRecognitionRunning()){
            Log.i(TAG, "stoptActivityRecognition");
            ActivityRecognition.ActivityRecognitionApi.removeActivityUpdates(
                    mGoogleApiClient,
                    getActivityRecognitionPendingIntent()
            ).setResultCallback(this);
            stopSelf();
        //}
    }

    private void startLocationUpdates() {
        //if(!isLocationUpdatesRunning()){
            Log.i(TAG, "startLocationUpdates");
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient,
                    mLocationRequest,
                    getLocationChangedPendingIntent());
        //}
    }

    private void stopLocationUpdates(){
        //if(isLocationUpdatesRunning()){
            Log.i(TAG, "stopLocationUpdates");
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient,
                    getLocationChangedPendingIntent());
        //}
    }


    /**
     * Retrieves the boolean from SharedPreferences that tracks whether we are requesting activity
     * updates.
     */
    private boolean isActivityRecognitionRunning() {
        return Utils.getSharedPreferences(this)
                .getBoolean(Constants.PROPERTY_ACTIVITY_UPDATES, false);
    }

    /**
     * Sets the boolean in SharedPreferences that tracks whether we are requesting activity
     * updates.
     */
    private void setActivityRecognitionRunning(boolean requestingUpdates) {
        Utils.getSharedPreferences(this)
                .edit()
                .putBoolean(Constants.PROPERTY_ACTIVITY_UPDATES, requestingUpdates)
                .commit();
    }

    /**
     * Retrieves the boolean from SharedPreferences that tracks whether we are requesting activity
     * updates.
     */
    private boolean isLocationUpdatesRunning() {
        return Utils.getSharedPreferences(this)
                .getBoolean(Constants.PROPERTY_ACTIVITY_UPDATES, false);
    }

    /**
     * Sets the boolean in SharedPreferences that tracks whether we are requesting activity
     * updates.
     */
    private void setLocationUpdatesRunning(boolean requestingUpdates) {
        Utils.getSharedPreferences(this)
                .edit()
                .putBoolean(Constants.PROPERTY_ACTIVITY_UPDATES, requestingUpdates)
                .commit();
    }

    private void sendContextToBackend(){
        mCurrentContext = prefs.getString(Constants.PROPERTY_LAST_DETECTED_ACTIVITY,"");
        if(!TextUtils.isEmpty(mCurrentContext)){
            Log.d(TAG, "Send new context to backend: " + mCurrentContext);
            if(mLastLocation != null){
                Log.d(TAG, "Location !=null: Lat=" + mLastLocation.getLatitude() + " Lon=" + mLastLocation.getLongitude());
            }
            new AddDetectedContextAsyncTask(deviceId,mCurrentContext,mLastLocation){
                @Override
                protected void onPostExecute(us.idinfor.smartcitizen.backend.contextApi.model.Context context) {
                    if(context != null){
                        sendContext = false;
                        Log.d(TAG, "New context saved in datastore: " + context.getContext());
                    }
                }
            }.execute();
        }
    }


    @Override
    public void onResult(Status status) {
        if (status.isSuccess()) {
            // Toggle the status of activity updates requested, and save in shared preferences.
           /* boolean requestingUpdates = !isActivityRecognitionRunning();
            setActivityRecognitionRunning(requestingUpdates);
            setLocationUpdatesRunning(requestingUpdates);
            Log.d(TAG, requestingUpdates ? "Activity updates added" : "Activity updates removed");*/
        } else {
            Log.e(TAG, "Error adding or removing activity detection: " + status.getStatusMessage());
        }
    }

}
