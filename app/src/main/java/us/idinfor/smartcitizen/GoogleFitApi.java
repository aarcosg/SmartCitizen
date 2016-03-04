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
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResult;

import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.TimeUnit;

import us.idinfor.smartcitizen.event.ConnectionResultEvent;
import us.idinfor.smartcitizen.event.FitBucketsResultEvent;
import us.idinfor.smartcitizen.event.FitDataSetsResultEvent;
import us.idinfor.smartcitizen.event.GoogleApiClientConnectedEvent;

public class GoogleFitApi implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = GoogleFitApi.class.getCanonicalName();
    public static final int QUERY_DEFAULT = 0;
    public static final int QUERY_LOCATIONS_HERMES = 1;
    public static final int QUERY_ACTIVITIES_HERMES = 2;
    private GoogleApiClient mGoogleApiClient = null;
    private Context mContext;
    private static GoogleFitApi instance = null;

    protected GoogleFitApi(Context context){
        this.mContext = context;
        buildFitnessClient();
    }

    public static GoogleFitApi getInstance(Context context){
        if(instance == null){
            instance = new GoogleFitApi(context);
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
        subscribeFitnessData();
        EventBus.getDefault().post(new GoogleApiClientConnectedEvent());
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
        if (connectionResult.getErrorCode() == FitnessStatusCodes.NEEDS_OAUTH_PERMISSIONS ||
                connectionResult.getErrorCode() == ConnectionResult.SIGN_IN_REQUIRED) {
            EventBus.getDefault().post(new ConnectionResultEvent(connectionResult));
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
        Fitness.RecordingApi.subscribe(mGoogleApiClient, DataType.TYPE_CALORIES_EXPENDED)
                .setResultCallback(new SubscribeCallback(DataType.TYPE_CALORIES_EXPENDED));
    }

    public void unsubscribeFitnessData(){
        Fitness.RecordingApi.unsubscribe(mGoogleApiClient, DataType.TYPE_ACTIVITY_SAMPLE)
                .setResultCallback(new UnsubscribeCallback(DataType.TYPE_ACTIVITY_SAMPLE));
        Fitness.RecordingApi.unsubscribe(mGoogleApiClient, DataType.TYPE_LOCATION_SAMPLE)
                .setResultCallback(new UnsubscribeCallback(DataType.TYPE_LOCATION_SAMPLE));
        Fitness.RecordingApi.unsubscribe(mGoogleApiClient, DataType.TYPE_STEP_COUNT_DELTA)
                .setResultCallback(new UnsubscribeCallback(DataType.TYPE_STEP_COUNT_DELTA));
        Fitness.RecordingApi.unsubscribe(mGoogleApiClient, DataType.TYPE_CALORIES_EXPENDED)
                .setResultCallback(new UnsubscribeCallback(DataType.TYPE_CALORIES_EXPENDED));
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
                EventBus.getDefault().post(new FitBucketsResultEvent(queryType,dataReadResult.getBuckets()));
            } else if (dataReadResult.getDataSets().size() > 0) {
                Log.i(TAG, "Number of returned DataSets is: "
                        + dataReadResult.getDataSets().size());
                EventBus.getDefault().post(new FitDataSetsResultEvent(queryType,dataReadResult.getDataSets()));
            }
        }
    }

    public void queryFitnessData(long startTime, long endTime, DataReadRequest.Builder builder, int queryType){
        DataReadRequest request = builder.setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build();
        // Invoke the History API to fetch the data with the query and await the result of
        // the read request.
        Log.i(TAG,"History API read data");
        Fitness.HistoryApi.readData(mGoogleApiClient, request).setResultCallback(new DataReadResultCallback(queryType));
    }
}
