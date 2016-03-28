package us.idinfor.smartcitizen.data.api.google.fit;


import android.content.Context;
import android.util.Log;

import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessStatusCodes;
import com.google.android.gms.fitness.data.DataType;
import com.patloew.rxfit.RxFit;

import java.util.concurrent.TimeUnit;

public class GoogleFitHelper {

    private static final String TAG = GoogleFitHelper.class.getCanonicalName();

    public static final int QUERY_DEFAULT = 0;
    public static final int QUERY_LOCATIONS_HERMES = 1;
    public static final int QUERY_ACTIVITIES_HERMES = 2;

    private static final DataType[] recordingDataTypes = {
            DataType.TYPE_ACTIVITY_SAMPLE,
            DataType.TYPE_LOCATION_SAMPLE,
            DataType.TYPE_STEP_COUNT_DELTA,
            DataType.TYPE_CALORIES_EXPENDED,
    };

    public static void initFitApi(Context context){
        RxFit.init(
                context,
                new Api[] {Fitness.HISTORY_API, Fitness.RECORDING_API},
                new Scope[] {
                        new Scope(Scopes.FITNESS_LOCATION_READ_WRITE),
                        new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE),
                        new Scope(Scopes.FITNESS_BODY_READ_WRITE)
                }
        );
        RxFit.setDefaultTimeout(15, TimeUnit.SECONDS);
    }

    public static void subscribeFitnessData(){
        for(DataType dataType : recordingDataTypes){
            RxFit.Recording.subscribe(dataType)
                    .subscribe(status -> {
                        if (status.isSuccess()) {
                            if (status.getStatusCode() == FitnessStatusCodes.SUCCESS_ALREADY_SUBSCRIBED) {
                                Log.i(TAG, "Existing subscription for " + dataType.getName() + " detected.");
                            } else {
                                Log.i(TAG, "Successfully subscribed!: " + dataType.getName());
                            }
                        } else {
                            Log.i(TAG, "There was a problem subscribing: " + dataType.getName());
                        }
                    });
        }
    }

    public static void unsubscribeFitnessData(){
        for(DataType dataType : recordingDataTypes){
            RxFit.Recording.unsubscribe(dataType)
                    .subscribe(status -> {
                        if (status.isSuccess()) {
                            Log.i(TAG, "Successfully unsubscribed for data type: " + dataType.getName());
                        } else {
                            // Subscription not removed
                            Log.i(TAG, "Failed to unsubscribe for data type: " + dataType.getName());
                        }
                    });
        }
    }

    /*private class DataReadResultCallback implements ResultCallback<DataReadResult>{

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
    }*/

    /*public void queryFitnessData(long startTime, long endTime, DataReadRequest.Builder builder, int queryType){
        DataReadRequest request = builder.setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build();
        // Invoke the History API to fetch the data with the query and await the result of
        // the read request.
        Log.i(TAG,"History API read data");
        Fitness.HistoryApi.readData(mGoogleApiClient, request).setResultCallback(new DataReadResultCallback(queryType));
    }*/

}
