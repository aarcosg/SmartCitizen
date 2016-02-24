package us.idinfor.smartcitizen;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessStatusCodes;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResult;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import us.idinfor.smartcitizen.model.ActivitySampleFit;
import us.idinfor.smartcitizen.model.LocationSampleFit;

public class GoogleFitHelper implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = GoogleFitHelper.class.getCanonicalName();
    public static final String EVENT_GOOGLEAPICLIENT_READY = "event_googleapiclient_ready";
    public static final String EXTRA_FAILED_STATUS_CODE = "extra_failed_status_code";
    public static final String EXTRA_FAILED_INTENT = "extra_failed_intent";
    public static final int QUERY_DEFAULT = 0;
    public static final int QUERY_LOCATIONS = 1;
    public static final int QUERY_ACTIVITIES = 2;
    private GoogleApiClient mGoogleApiClient = null;
    private Context mContext;
    private static GoogleFitHelper instance = null;

    protected GoogleFitHelper(Context context){
        this.mContext = context;
        buildFitnessClient();
    }

    public static GoogleFitHelper getInstance(Context context){
        if(instance == null){
            instance = new GoogleFitHelper(context);
        }
        return instance;
    }

    private void buildFitnessClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addApi(Fitness.HISTORY_API)
                .addApi(Fitness.RECORDING_API)
                .addScope(new Scope(Scopes.FITNESS_LOCATION_READ_WRITE))
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                .addScope(new Scope(Scopes.FITNESS_BODY_READ_WRITE))
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    public GoogleApiClient getGoogleApiClient() {
        return mGoogleApiClient;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "GoogleApiClient connected");
        EventBus.getDefault().post(new MessageEvent(EVENT_GOOGLEAPICLIENT_READY));
    }

    @Override
    public void onConnectionSuspended(int i) {
        if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_NETWORK_LOST) {
            Log.i(TAG, "Connection lost.  Cause: Network Lost.");
        } else if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED) {
            Log.i(TAG, "Connection lost.  Reason: Service Disconnected");
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG, "Connection failed. Cause: " + connectionResult.toString());
        if (connectionResult.getErrorCode() == FitnessStatusCodes.NEEDS_OAUTH_PERMISSIONS) {
            EventBus.getDefault().post(connectionResult);
        }else if (!connectionResult.hasResolution()) {
            GoogleApiAvailability.getInstance().showErrorNotification(mContext,connectionResult.getErrorCode());
        }
    }

    public void subscribeFitnessData(){
        Fitness.RecordingApi.subscribe(mGoogleApiClient, DataType.TYPE_ACTIVITY_SAMPLE)
                .setResultCallback(new SubscribeCallback(DataType.TYPE_ACTIVITY_SAMPLE));
        Fitness.RecordingApi.subscribe(mGoogleApiClient, DataType.TYPE_LOCATION_SAMPLE)
                .setResultCallback(new SubscribeCallback(DataType.TYPE_LOCATION_SAMPLE));
        Fitness.RecordingApi.subscribe(mGoogleApiClient, DataType.TYPE_STEP_COUNT_DELTA)
                .setResultCallback(new SubscribeCallback(DataType.TYPE_STEP_COUNT_DELTA));
    }

    public void unsubscribeFitnessData(){
        Fitness.RecordingApi.unsubscribe(mGoogleApiClient, DataType.TYPE_ACTIVITY_SAMPLE)
                .setResultCallback(new UnsubscribeCallback(DataType.TYPE_ACTIVITY_SAMPLE));
        Fitness.RecordingApi.unsubscribe(mGoogleApiClient, DataType.TYPE_LOCATION_SAMPLE)
                .setResultCallback(new UnsubscribeCallback(DataType.TYPE_LOCATION_SAMPLE));
        Fitness.RecordingApi.unsubscribe(mGoogleApiClient, DataType.TYPE_STEP_COUNT_DELTA)
                .setResultCallback(new UnsubscribeCallback(DataType.TYPE_STEP_COUNT_DELTA));
    }

    private class SubscribeCallback implements ResultCallback<Status>{

        private DataType dataType;

        public SubscribeCallback(DataType dataType){
            this.dataType = dataType;
        }

        @Override
        public void onResult(@NonNull Status status) {
            if (status.isSuccess()) {
                if (status.getStatusCode() == FitnessStatusCodes.SUCCESS_ALREADY_SUBSCRIBED) {
                    Log.i(TAG, "Existing subscription for " + dataType.getName() + " detected.");
                } else {
                    Log.i(TAG, "Successfully subscribed!: " + dataType.getName());
                }
            } else {
                Log.i(TAG, "There was a problem subscribing: " + dataType.getName());
            }
        }
    }

    private class UnsubscribeCallback implements ResultCallback<Status>{

        private DataType dataType;

        public UnsubscribeCallback(DataType dataType){
            this.dataType = dataType;
        }

        @Override
        public void onResult(@NonNull Status status) {
            if (status.isSuccess()) {
                Log.i(TAG, "Successfully unsubscribed for data type: " + dataType.getName());
            } else {
                // Subscription not removed
                Log.i(TAG, "Failed to unsubscribe for data type: " + dataType.getName());
            }
        }
    }

    private class DataReadResultCallback implements ResultCallback<DataReadResult>{

        private int queryType;

        public DataReadResultCallback(int queryType){
            this.queryType = queryType;
        }

        @Override
        public void onResult(@NonNull DataReadResult dataReadResult) {
            Log.i(TAG,"History API onResult");
            // If the DataReadRequest object specified aggregated data, dataReadResult will be returned
            // as buckets containing DataSets, instead of just DataSets.
            if (dataReadResult.getBuckets().size() > 0) {
                Log.i(TAG, "Number of returned buckets of DataSets is: "
                        + dataReadResult.getBuckets().size());
                EventBus.getDefault().post(dataReadResult.getBuckets());
            } else if (dataReadResult.getDataSets().size() > 0) {
                Log.i(TAG, "Number of returned DataSets is: "
                        + dataReadResult.getDataSets().size());
                switch (queryType){
                    case QUERY_DEFAULT:
                        EventBus.getDefault().post(dataReadResult.getDataSets());
                        break;
                    case QUERY_LOCATIONS:
                        EventBus.getDefault().post(convertToLocationSampleList(dataReadResult.getDataSets()));
                        break;
                    case QUERY_ACTIVITIES:
                        EventBus.getDefault().post(convertToActivitySampleList(dataReadResult.getDataSets()));
                        break;
                }
            }
        }
    }

    public void queryFitnessData(long startTime, long endTime, DataReadRequest.Builder builder){
        DataReadRequest request = builder.setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build();
        // Invoke the History API to fetch the data with the query and await the result of
        // the read request.
        Log.i(TAG,"History API read data");
        Fitness.HistoryApi.readData(mGoogleApiClient, request).setResultCallback(new DataReadResultCallback(QUERY_DEFAULT));
    }

    public void queryFitnessData(long startTime, long endTime, DataReadRequest.Builder builder, int queryType){
        DataReadRequest request = builder.setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build();
        // Invoke the History API to fetch the data with the query and await the result of
        // the read request.
        Log.i(TAG,"History API read data");
        Fitness.HistoryApi.readData(mGoogleApiClient, request).setResultCallback(new DataReadResultCallback(queryType));
    }

    private List<LocationSampleFit> convertToLocationSampleList(List<DataSet> dataSets){
        List<LocationSampleFit> locations = null;
        for (DataSet dataSet : dataSets) {
            if (dataSet.getDataType().equals(DataType.TYPE_LOCATION_SAMPLE)) {
                locations = new ArrayList<>();
                for (DataPoint dp : dataSet.getDataPoints()) {
                    if (dp.getDataType().equals(DataType.TYPE_LOCATION_SAMPLE)) {
                        LocationSampleFit location = new LocationSampleFit(
                                dp.getValue(Field.FIELD_LATITUDE).asFloat(),
                                dp.getValue(Field.FIELD_LONGITUDE).asFloat(),
                                dp.getValue(Field.FIELD_ACCURACY).asFloat(),
                                dp.getStartTime(TimeUnit.MILLISECONDS),
                                dp.getEndTime(TimeUnit.MILLISECONDS)
                        );
                        locations.add(location);
                    }
                }
            }
        }
        return locations;
    }

    private List<ActivitySampleFit> convertToActivitySampleList(List<DataSet> dataSets){
        List<ActivitySampleFit> activities = null;
        for (DataSet dataSet : dataSets) {
            if (dataSet.getDataType().equals(DataType.TYPE_ACTIVITY_SAMPLE)) {
                activities = new ArrayList<>();
                for (DataPoint dp : dataSet.getDataPoints()) {
                    if (dp.getDataType().equals(DataType.TYPE_ACTIVITY_SAMPLE)) {
                        ActivitySampleFit activity = new ActivitySampleFit(
                                dp.getValue(Field.FIELD_ACTIVITY).asActivity(),
                                dp.getValue(Field.FIELD_CONFIDENCE).asFloat(),
                                dp.getStartTime(TimeUnit.MILLISECONDS),
                                dp.getEndTime(TimeUnit.MILLISECONDS)
                        );
                        activities.add(activity);
                    }
                }
            }
        }
        return activities;
    }
}
