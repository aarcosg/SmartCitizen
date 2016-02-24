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

public class GoogleFitHelper implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = GoogleFitHelper.class.getCanonicalName();
    public static final String EVENT_GOOGLEAPICLIENT_READY ="event_googleapiclient_ready";
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
        //subscribeFitnessData();
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
        if (!connectionResult.hasResolution()) {
            GoogleApiAvailability.getInstance().showErrorNotification(mContext,connectionResult.getErrorCode());
        }
    }

    public void subscribeFitnessData(){
        Fitness.RecordingApi.subscribe(mGoogleApiClient, DataType.TYPE_ACTIVITY_SAMPLE)
                .setResultCallback(new SubscribeCallback(DataType.TYPE_ACTIVITY_SAMPLE));
        Fitness.RecordingApi.subscribe(mGoogleApiClient, DataType.TYPE_LOCATION_SAMPLE)
                .setResultCallback(new SubscribeCallback(DataType.TYPE_LOCATION_SAMPLE));
    }

    public void unsubscribeFitnessData(){
        Fitness.RecordingApi.unsubscribe(mGoogleApiClient, DataType.TYPE_ACTIVITY_SAMPLE)
                .setResultCallback(new SubscribeCallback(DataType.TYPE_ACTIVITY_SAMPLE));
        Fitness.RecordingApi.unsubscribe(mGoogleApiClient, DataType.TYPE_LOCATION_SAMPLE)
                .setResultCallback(new SubscribeCallback(DataType.TYPE_LOCATION_SAMPLE));
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

        @Override
        public void onResult(@NonNull DataReadResult dataReadResult) {
            Log.i(TAG,"History API onResult");
            // If the DataReadRequest object specified aggregated data, dataReadResult will be returned
            // as buckets containing DataSets, instead of just DataSets.
            if (dataReadResult.getBuckets().size() > 0) {
                Log.i(TAG, "Number of returned buckets of DataSets is: "
                        + dataReadResult.getBuckets().size());
                EventBus.getDefault().post(dataReadResult.getBuckets());
               // dumpBuckets(dataReadResult.getBuckets());
               // onFitResultDumped();

            } else if (dataReadResult.getDataSets().size() > 0) {
                Log.i(TAG, "Number of returned DataSets is: "
                        + dataReadResult.getDataSets().size());
                EventBus.getDefault().post(dataReadResult.getDataSets());
               // dumpDataSets(dataReadResult.getDataSets());
               // onFitResultDumped();

            }
        }
    }

    public void queryFitnessData(long startTime, long endTime, DataReadRequest.Builder builder){
        DataReadRequest request = builder.setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build();
        // Invoke the History API to fetch the data with the query and await the result of
        // the read request.
        Log.i(TAG,"History API read data");
        Fitness.HistoryApi.readData(mGoogleApiClient, request).setResultCallback(new DataReadResultCallback());
    }
}
