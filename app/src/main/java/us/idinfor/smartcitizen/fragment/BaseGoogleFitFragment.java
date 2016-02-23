package us.idinfor.smartcitizen.fragment;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.request.OnDataPointListener;
import com.google.android.gms.fitness.result.DataReadResult;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.TimeUnit;


public abstract class BaseGoogleFitFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,  ResultCallback<DataReadResult> {

    private static final String TAG = BaseGoogleFitFragment.class.getCanonicalName();
    private static final String AUTH_PENDING = "auth_state_pending";
    private static final int REQUEST_OAUTH = 1;
    private static final String DATE_FORMAT = "yyyy.MM.dd HH:mm:ss";
    private static final boolean LOGGER = false;

    private boolean authInProgress = false;
    protected GoogleApiClient mGoogleApiClient = null;

    private OnDataPointListener mListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null){
            authInProgress = savedInstanceState.getBoolean(AUTH_PENDING);
        }
        buildFitnessHistoryClient();
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.e(TAG, "Connecting");
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        if(mGoogleApiClient.isConnected()){
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(AUTH_PENDING, authInProgress);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_OAUTH){
            authInProgress = false;
            if(resultCode == android.app.Activity.RESULT_OK){
                if(!mGoogleApiClient.isConnecting() && !mGoogleApiClient.isConnected()){
                    mGoogleApiClient.connect();
                }
            }
        }
    }

    private void buildFitnessHistoryClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addApi(Fitness.HISTORY_API)
                .addScope(new Scope(Scopes.FITNESS_LOCATION_READ_WRITE))
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                .addScope(new Scope(Scopes.FITNESS_BODY_READ_WRITE))
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.e(TAG, "Google Api client connected");
        /*Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.WEEK_OF_YEAR, -1);
        //cal.add(Calendar.DAY_OF_YEAR, -1);
        long startTime = cal.getTimeInMillis();

        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        Log.i(TAG, "Range Start: " + dateFormat.format(startTime));
        Log.i(TAG, "Range End: " + dateFormat.format(endTime));
        DataReadRequest readRequest = queryFitnessData(startTime,endTime,true);
        // Invoke the History API to fetch the data with the query and await the result of
        // the read request.
        Log.e(TAG,"History API read data");
        Fitness.HistoryApi.readData(mGoogleApiClient, readRequest).setResultCallback(this);
        */
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
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG, "Connection failed. Cause: " + connectionResult.toString());
        if (!connectionResult.hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(),
                    getActivity(), 0).show();
        }
                        /* The failure has a resolution. Resolve it.
                        * Called typically when the app is not yet authorized, and an
                        * authorization dialog is displayed to the user.
                        * */
        if (!authInProgress) {
            Log.e(TAG, "Attempting to resolve failed connection");
            authInProgress = true;
            try {
                connectionResult.startResolutionForResult(getActivity(),REQUEST_OAUTH);
            } catch (IntentSender.SendIntentException e) {
                Log.e(TAG,"Exception while starting resolution activity");
            }
        }
    }

    @Override
    public void onResult(DataReadResult dataReadResult) {
        Log.e(TAG,"History API onResult");
        // If the DataReadRequest object specified aggregated data, dataReadResult will be returned
        // as buckets containing DataSets, instead of just DataSets.
        if (dataReadResult.getBuckets().size() > 0) {
            Log.i(TAG, "Number of returned buckets of DataSets is: "
                    + dataReadResult.getBuckets().size());
            dumpBuckets(dataReadResult.getBuckets());
            onFitResultDumped();
            /*for (Bucket bucket : dataReadResult.getBuckets()) {
                List<DataSet> dataSets = bucket.getDataSets();
                for (DataSet dataSet : dataSets) {
                    dumpDataSet(dataSet);
                }
            }*/

        } else if (dataReadResult.getDataSets().size() > 0) {
            Log.i(TAG, "Number of returned DataSets is: "
                    + dataReadResult.getDataSets().size());
            dumpDataSets(dataReadResult.getDataSets());
            onFitResultDumped();
            /*for (DataSet dataSet : dataReadResult.getDataSets()) {
                dumpDataSet(dataSet);
            }*/

        }
    }

    /**
     * Return a {@link DataReadRequest} for all step count changes in the past week.
     */
    protected abstract DataReadRequest queryFitnessData(long startTime, long endTime); //{
        // [START build_read_data_request]
        // Setting a start and end date using a range of 1 week before this moment.
        /*Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.WEEK_OF_YEAR, -1);
        //cal.add(Calendar.DAY_OF_YEAR, -1);
        long startTime = cal.getTimeInMillis();

        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        Log.i(TAG, "Range Start: " + dateFormat.format(startTime));
        Log.i(TAG, "Range End: " + dateFormat.format(endTime));*/
        /*DataReadRequest.Builder readRequestBuilder;
            readRequestBuilder = new DataReadRequest.Builder()
                    .aggregate(DataType.TYPE_LOCATION_SAMPLE, DataType.AGGREGATE_LOCATION_BOUNDING_BOX)
                    .aggregate(DataType.TYPE_ACTIVITY_SEGMENT, DataType.AGGREGATE_ACTIVITY_SUMMARY)
                    .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
                    .aggregate(DataType.TYPE_HEART_RATE_BPM,DataType.AGGREGATE_HEART_RATE_SUMMARY)
                    .aggregate(DataType.TYPE_CALORIES_EXPENDED,DataType.AGGREGATE_CALORIES_EXPENDED)
                    .aggregate(DataType.TYPE_DISTANCE_DELTA, DataType.AGGREGATE_DISTANCE_DELTA)

                    .bucketByTime(1, TimeUnit.DAYS);

            readRequestBuilder = new DataReadRequest.Builder()
                    //.read(DataType.TYPE_ACTIVITY_SEGMENT)
                    .read(DataType.TYPE_ACTIVITY_SAMPLE);


        DataReadRequest readRequest = readRequestBuilder
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build();

        return readRequest;
    }*/

    /**
     * Log a record of the query result. It's possible to get more constrained data sets by
     * specifying a data source or data type, but for demonstrative purposes here's how one would
     * dump all the data. In this sample, logging also prints to the device screen, so we can see
     * what the query returns, but your app should not log fitness information as a privacy
     * consideration. A better option would be to dump the data you receive to a local data
     * directory to avoid exposing it to other applications.
     */
    /*private void printData(DataReadResult dataReadResult) {
        // [START parse_read_data_result]
        // If the DataReadRequest object specified aggregated data, dataReadResult will be returned
        // as buckets containing DataSets, instead of just DataSets.
        if (dataReadResult.getBuckets().size() > 0) {
            Log.i(TAG, "Number of returned buckets of DataSets is: "
                    + dataReadResult.getBuckets().size());
            for (Bucket bucket : dataReadResult.getBuckets()) {
                List<DataSet> dataSets = bucket.getDataSets();
                for (DataSet dataSet : dataSets) {
                    dumpDataSet(dataSet);
                }
            }
            onDataSetDumped();
        } else if (dataReadResult.getDataSets().size() > 0) {
            Log.i(TAG, "Number of returned DataSets is: "
                    + dataReadResult.getDataSets().size());
            for (DataSet dataSet : dataReadResult.getDataSets()) {
                dumpDataSet(dataSet);
            }
            onDataSetDumped();
        }
        // [END parse_read_data_result]
    }*/

    // [START parse_dataset]
    protected  void dumpDataSets(List<DataSet> dataSets) {
        if(LOGGER){
            SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
            for (DataSet dataSet : dataSets) {
                Log.i(TAG, "Data returned for Data type: " + dataSet.getDataType().getName());
                for (DataPoint dp : dataSet.getDataPoints()) {
                    Log.i(TAG, "Data point:");
                    Log.i(TAG, "\tType: " + dp.getDataType().getName());
                    Log.i(TAG, "\tStart: " + dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
                    Log.i(TAG, "\tEnd: " + dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)));
                    for (Field field : dp.getDataType().getFields()) {
                        Log.i(TAG, "\tField: " + field.getName());
                        if (field.getName().equalsIgnoreCase("activity")) {
                            Log.i(TAG, " Value: " + dp.getValue(field).asActivity());
                        } else if (field.getName().equalsIgnoreCase("duration")) {
                            Log.i(TAG, " Value: " + dp.getValue(field).asInt() / 1000 / 60 + " minutes");
                        } else {
                            Log.i(TAG, " Value: " + dp.getValue(field));
                        }
                    }
                }
            }
        }
    }

    protected  void dumpBuckets(List<Bucket> buckets) {
        if(LOGGER){
            SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
            for (Bucket bucket : buckets) {
                for (DataSet dataSet : bucket.getDataSets()) {
                    Log.i(TAG, "Data returned for Data type: " + dataSet.getDataType().getName());
                    for (DataPoint dp : dataSet.getDataPoints()) {
                        Log.i(TAG, "Data point:");
                        Log.i(TAG, "\tType: " + dp.getDataType().getName());
                        Log.i(TAG, "\tStart: " + dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
                        Log.i(TAG, "\tEnd: " + dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)));
                        for (Field field : dp.getDataType().getFields()) {
                            Log.i(TAG, "\tField: " + field.getName());
                            if (field.getName().equalsIgnoreCase("activity")) {
                                Log.i(TAG, " Value: " + dp.getValue(field).asActivity());
                            } else if (field.getName().equalsIgnoreCase("duration")) {
                                Log.i(TAG, " Value: " + dp.getValue(field).asInt() / 1000 / 60 + " minutes");
                            } else {
                                Log.i(TAG, " Value: " + dp.getValue(field));
                            }
                        }
                    }
                }
            }
        }
    }

    protected abstract void onFitResultDumped();

    protected void launchQuery(long startTime, long endTime){
        DataReadRequest readRequest = queryFitnessData(startTime,endTime);
        // Invoke the History API to fetch the data with the query and await the result of
        // the read request.
        Log.e(TAG,"History API read data");
        Fitness.HistoryApi.readData(mGoogleApiClient, readRequest).setResultCallback(this);
    }

}
