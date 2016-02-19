package us.idinfor.smartcitizen.activity;


import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Value;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.request.DataSourcesRequest;
import com.google.android.gms.fitness.request.OnDataPointListener;
import com.google.android.gms.fitness.request.SensorRequest;
import com.google.android.gms.fitness.result.DataReadResult;
import com.google.android.gms.fitness.result.DataSourcesResult;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import us.idinfor.smartcitizen.R;

public class FitnessActivity extends BaseActivity{

    private static final String TAG = FitnessActivity.class.getCanonicalName();

    /**
     *  Track whether an authorization activity is stacking over the current activity, i.e. when
     *  a known auth error is being resolved, such as showing the account chooser or presenting a
     *  consent dialog. This avoids common duplications as might happen on screen rotations, etc.
     */
    private static final String AUTH_PENDING = "auth_state_pending";
    private static final int REQUEST_OAUTH = 1;
    private static final String DATE_FORMAT = "yyyy.MM.dd HH:mm:ss";
    private boolean authInProgress = false;

    private GoogleApiClient mGoogleApiClient = null;

    // Need to hold a reference to this listener, as it's passed into the "unregister"
    // method in order to stop all sensors from sending data to this listener.
    private OnDataPointListener mListener;

    private TextView logTV;
    private StringBuffer logBuffer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fitness);
        buildActionBarToolbar(getString(R.string.title_activity_fitness), true);
        if(savedInstanceState != null){
            authInProgress = savedInstanceState.getBoolean(AUTH_PENDING);
        }

        logTV = (TextView) findViewById(R.id.log_text);
        logBuffer = new StringBuffer();

        buildFitnessClient();
        //buildFitnessHistoryClient();

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e(TAG, "Connecting");
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mGoogleApiClient.isConnected()){
            mGoogleApiClient.disconnect();
        }
    }

    /**
     *  Build a {@link GoogleApiClient} that will authenticate the user and allow the application
     *  to connect to Fitness APIs. The scopes included should match the scopes your app needs
     *  (see documentation for details). Authentication will occasionally fail intentionally,
     *  and in those cases, there will be a known resolution, which the OnConnectionFailedListener()
     *  can address. Examples of this include the user never having signed in before, or having
     *  multiple accounts on the device and needing to specify which account to use, etc.
     */
    private void buildFitnessClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Fitness.SENSORS_API)
                .addScope(new Scope(Scopes.FITNESS_LOCATION_READ))
                .addScope(new Scope((Scopes.FITNESS_ACTIVITY_READ)))
                .addScope(new Scope((Scopes.FITNESS_BODY_READ)))
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {

                                            @Override
                                            public void onConnected(Bundle bundle) {
                                                Log.e(TAG, "Google Api client connected");
                                                findFitnessDataSources();
                                            }

                                            @Override
                                            public void onConnectionSuspended(int i) {
                                                if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_NETWORK_LOST) {
                                                    Log.i(TAG, "Connection lost.  Cause: Network Lost.");
                                                } else if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED) {
                                                    Log.i(TAG, "Connection lost.  Reason: Service Disconnected");
                                                }
                                            }
                                        }
                )
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult connectionResult) {
                        Log.e(TAG, "Connection failed. Cause: " + connectionResult.toString());
                        if (!connectionResult.hasResolution()) {
                            GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(),
                                    FitnessActivity.this, 0).show();
                        }
                        /* The failure has a resolution. Resolve it.
                        * Called typically when the app is not yet authorized, and an
                        * authorization dialog is displayed to the user.
                        * */
                        if (!authInProgress) {
                            Log.e(TAG, "Attempting to resolve failed connection");
                            authInProgress = true;
                            try {
                                connectionResult.startResolutionForResult(FitnessActivity.this,REQUEST_OAUTH);
                            } catch (IntentSender.SendIntentException e) {
                                Log.e(TAG,"Exception while starting resolution activity");
                            }
                        }
                    }
                })
                .build();
    }

    /**
     * Find available data sources and attempt to register on a specific {@link DataType}.
     * If the application cares about a data type but doesn't care about the source of the data,
     * this can be skipped entirely, instead calling
     *     {@link com.google.android.gms.fitness.SensorsApi
     *     #register(GoogleApiClient, SensorRequest, DataSourceListener)},
     * where the {@link SensorRequest} contains the desired data type.
     */
    private void findFitnessDataSources() {
        Fitness.SensorsApi.findDataSources(mGoogleApiClient,
                new DataSourcesRequest.Builder()
                .setDataTypes(
                        DataType.TYPE_LOCATION_SAMPLE,
                        DataType.TYPE_ACTIVITY_SAMPLE)
                .setDataSourceTypes(
                        DataSource.TYPE_RAW,
                        DataSource.TYPE_DERIVED)
                .build())
                .setResultCallback(new ResultCallback<DataSourcesResult>() {
                    @Override
                    public void onResult(DataSourcesResult dataSourcesResult) {
                        Log.e(TAG, "Result: " + dataSourcesResult.getStatus().toString());
                        for (DataSource dataSource : dataSourcesResult.getDataSources()) {
                            Log.e(TAG, "Data source found: " + dataSource.toString());
                            Log.e(TAG, "Data Source type: " + dataSource.getDataType().getName());

                            final DataType dataType = dataSource.getDataType();
                            if (mListener == null &&
                                    (dataType.equals(DataType.TYPE_LOCATION_SAMPLE) ||
                                            dataType.equals(DataType.TYPE_ACTIVITY_SAMPLE))) {
                                registerFitnessDataListener(dataSource, dataType);
                            }

                           /* //Let's register a listener to receive Activity data!
                            if (dataSource.getDataType().equals(DataType.TYPE_LOCATION_SAMPLE) && mListener == null) {
                                Log.e(TAG, "Data source for TYPE_LOCATION_SAMPLE found!  Registering.");
                                registerFitnessDataListener(dataSource, DataType.TYPE_LOCATION_SAMPLE);
                            }*/


                        }
                    }
                });
        ;

    }

    /**
     * Register a listener with the Sensors API for the provided {@link DataSource} and
     * {@link DataType} combo.
     */
    private void registerFitnessDataListener(DataSource dataSource, DataType dataType) {
        mListener = new OnDataPointListener() {
            @Override
            public void onDataPoint(DataPoint dataPoint) {
                Log.e(TAG,"onDataPoint");
                for (Field field : dataPoint.getDataType().getFields()) {
                    Value val = dataPoint.getValue(field);
                    Log.e(TAG, "Detected DataPoint field: " + field.getName());
                    Log.e(TAG, "Detected DataPoint value: " + val);
                    logTV.setText("Detected DataPoint field: " + field.getName() + "\n");
                    logTV.setText("Detected DataPoint value: " + val + "\n");
                }
            }
        };

        Fitness.SensorsApi.add(
                mGoogleApiClient,
                new SensorRequest.Builder()
                        .setDataSource(dataSource) // Optional but recommended for custom data sets.
                        .setDataType(dataType) // Can't be omitted.
                        .setSamplingRate(30, TimeUnit.SECONDS)
                        .build(),
                mListener)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if (status.isSuccess()) {
                            Log.e(TAG, "Listener registered!");
                        } else {
                            Log.e(TAG, "Listener not registered.");
                        }
                    }
                });

    }

    /**
     * Unregister the listener with the Sensors API.
     */
    private void unregisterFitnessDataListener() {
        if (mListener == null) {
            // This code only activates one listener at a time.  If there's no listener, there's
            // nothing to unregister.
            return;
        }

        // Waiting isn't actually necessary as the unregister call will complete regardless,
        // even if called from within onStop, but a callback can still be added in order to
        // inspect the results.
        Fitness.SensorsApi.remove(
                mGoogleApiClient,
                mListener)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if (status.isSuccess()) {
                            Log.e(TAG, "Listener was removed!");
                        } else {
                            Log.e(TAG, "Listener was not removed.");
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_OAUTH){
            authInProgress = false;
            if(resultCode == RESULT_OK){
                if(!mGoogleApiClient.isConnecting() && !mGoogleApiClient.isConnected()){
                    mGoogleApiClient.connect();
                }
            }
        }
    }

    private void buildFitnessHistoryClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Fitness.HISTORY_API)
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                .addScope(new Scope(Scopes.FITNESS_BODY_READ_WRITE))
                .addScope(new Scope(Scopes.FITNESS_LOCATION_READ_WRITE))
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {

                                            @Override
                                            public void onConnected(Bundle bundle) {
                                                Log.e(TAG, "Google Api client connected");
                                                DataReadRequest readRequest = queryFitnessData();
                                                // Invoke the History API to fetch the data with the query and await the result of
                                                // the read request.
                                                Log.e(TAG,"History API read data");
                                               Fitness.HistoryApi.readData(mGoogleApiClient, readRequest).setResultCallback(new ResultCallback<DataReadResult>() {
                                                   @Override
                                                   public void onResult(DataReadResult dataReadResult) {
                                                       Log.e(TAG,"History API onResult");
                                                       // For the sake of the sample, we'll print the data so we can see what we just added.
                                                       // In general, logging fitness information should be avoided for privacy reasons.
                                                       printData(dataReadResult);
                                                   }
                                               });

                                            }

                                            @Override
                                            public void onConnectionSuspended(int i) {
                                                if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_NETWORK_LOST) {
                                                    Log.i(TAG, "Connection lost.  Cause: Network Lost.");
                                                } else if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED) {
                                                    Log.i(TAG, "Connection lost.  Reason: Service Disconnected");
                                                }
                                            }
                                        }
                )
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult connectionResult) {
                        Log.e(TAG, "Connection failed. Cause: " + connectionResult.toString());
                        if (!connectionResult.hasResolution()) {
                            GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(),
                                    FitnessActivity.this, 0).show();
                        }
                        /* The failure has a resolution. Resolve it.
                        * Called typically when the app is not yet authorized, and an
                        * authorization dialog is displayed to the user.
                        * */
                        if (!authInProgress) {
                            Log.e(TAG, "Attempting to resolve failed connection");
                            authInProgress = true;
                            try {
                                connectionResult.startResolutionForResult(FitnessActivity.this,REQUEST_OAUTH);
                            } catch (IntentSender.SendIntentException e) {
                                Log.e(TAG,"Exception while starting resolution activity");
                            }
                        }
                    }
                })
                .build();
    }

    /**
     * Return a {@link DataReadRequest} for all step count changes in the past week.
     */
    private DataReadRequest queryFitnessData() {
        // [START build_read_data_request]
        // Setting a start and end date using a range of 1 week before this moment.
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.WEEK_OF_YEAR, -1);
        //cal.add(Calendar.DAY_OF_YEAR, -1);
        long startTime = cal.getTimeInMillis();

        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        Log.i(TAG, "Range Start: " + dateFormat.format(startTime));
        Log.i(TAG, "Range End: " + dateFormat.format(endTime));

        DataReadRequest readRequest = new DataReadRequest.Builder()
                // The data request can specify multiple data types to return, effectively
                // combining multiple data queries into one call.
                // In this example, it's very unlikely that the request is for several hundred
                // datapoints each consisting of a few steps and a timestamp.  The more likely
                // scenario is wanting to see how many steps were walked per day, for 7 days.

                //.read(DataType.TYPE_ACTIVITY_SEGMENT)
                //.read(DataType.TYPE_ACTIVITY_SAMPLE)
                .aggregate(DataType.TYPE_LOCATION_SAMPLE, DataType.AGGREGATE_LOCATION_BOUNDING_BOX)
                .aggregate(DataType.TYPE_ACTIVITY_SEGMENT, DataType.AGGREGATE_ACTIVITY_SUMMARY)
                .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
                .aggregate(DataType.TYPE_HEART_RATE_BPM,DataType.AGGREGATE_HEART_RATE_SUMMARY)
                .aggregate(DataType.TYPE_CALORIES_EXPENDED,DataType.AGGREGATE_CALORIES_EXPENDED)
                .aggregate(DataType.TYPE_DISTANCE_DELTA, DataType.AGGREGATE_DISTANCE_DELTA)

                        // Analogous to a "Group By" in SQL, defines how data should be aggregated.
                        // bucketByTime allows for a time span, whereas bucketBySession would allow
                        // bucketing by "sessions", which would need to be defined in code.
                .bucketByTime(1, TimeUnit.DAYS)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build();

        return readRequest;
    }

    /**
     * Log a record of the query result. It's possible to get more constrained data sets by
     * specifying a data source or data type, but for demonstrative purposes here's how one would
     * dump all the data. In this sample, logging also prints to the device screen, so we can see
     * what the query returns, but your app should not log fitness information as a privacy
     * consideration. A better option would be to dump the data you receive to a local data
     * directory to avoid exposing it to other applications.
     */
    private void printData(DataReadResult dataReadResult) {
        // [START parse_read_data_result]
        // If the DataReadRequest object specified aggregated data, dataReadResult will be returned
        // as buckets containing DataSets, instead of just DataSets.
        if (dataReadResult.getBuckets().size() > 0) {
            Log.i(TAG, "Number of returned buckets of DataSets is: "
                    + dataReadResult.getBuckets().size());
            logBuffer.append("Number of returned buckets of DataSets is: ").append(dataReadResult.getBuckets().size());
            for (Bucket bucket : dataReadResult.getBuckets()) {
                List<DataSet> dataSets = bucket.getDataSets();
                for (DataSet dataSet : dataSets) {
                    dumpDataSet(dataSet);
                }
            }
        } else if (dataReadResult.getDataSets().size() > 0) {
            Log.i(TAG, "Number of returned DataSets is: "
                    + dataReadResult.getDataSets().size());
            for (DataSet dataSet : dataReadResult.getDataSets()) {
                dumpDataSet(dataSet);
            }
        }
        // [END parse_read_data_result]
    }

    // [START parse_dataset]
    private void dumpDataSet(DataSet dataSet) {
        Log.i(TAG, "Data returned for Data type: " + dataSet.getDataType().getName());
        logBuffer.append("\nData returned for Data type: ").append(dataSet.getDataType().getName());
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);

        for (DataPoint dp : dataSet.getDataPoints()) {
            Log.i(TAG, "Data point:");
            logBuffer.append("\n\nData point:");
            Log.i(TAG, "\tType: " + dp.getDataType().getName());
            logBuffer.append("\n\tType: ").append(dp.getDataType().getName());
            Log.i(TAG, "\tStart: " + dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
            logBuffer.append("\n\tStart: ").append(dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
            Log.i(TAG, "\tEnd: " + dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)));
            logBuffer.append("\n\tEnd: ").append(dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)));
            for(Field field : dp.getDataType().getFields()) {
                Log.i(TAG, "\tField: " + field.getName());
                logBuffer.append("\n\t\tField: ").append(field.getName());
                if(field.getName().equalsIgnoreCase("activity")){
                    Log.i(TAG," Value: " + dp.getValue(field).asActivity());
                    logBuffer.append("\n\t\tValue: ").append(dp.getValue(field).asActivity());
                }else if(field.getName().equalsIgnoreCase("duration")){
                    Log.i(TAG," Value: " + dp.getValue(field).asInt() / 1000 / 60 + " minutes");
                    logBuffer.append("\n\t\tValue: ").append(dp.getValue(field).asInt() / 1000 / 60).append(" minutes");
                }else{
                    Log.i(TAG," Value: " + dp.getValue(field));
                    logBuffer.append("\n\t\tValue: ").append(dp.getValue(field));
                }
            }
        }
        logTV.setText(logBuffer.toString());
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(AUTH_PENDING, authInProgress);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_fitness, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_unregister_listener) {
            unregisterFitnessDataListener();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static void launch(Activity activity){
        Intent intent = new Intent(activity,FitnessActivity.class);
        ActivityCompat.startActivity(activity,intent,null);
    }

}
