package es.us.hermes.smartcitizen.data.api.google.fit;


import android.content.Context;
import android.util.Log;

import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessStatusCodes;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.maps.model.LatLng;
import com.patloew.rxfit.RxFit;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Single;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import es.us.hermes.smartcitizen.Constants;
import es.us.hermes.smartcitizen.utils.Utils;
import es.us.hermes.smartcitizen.data.api.google.fit.entity.ActivitySummaryFit;
import es.us.hermes.smartcitizen.data.api.google.fit.entity.CaloriesExpendedFit;
import es.us.hermes.smartcitizen.data.api.google.fit.entity.DistanceDeltaFit;
import es.us.hermes.smartcitizen.data.api.google.fit.entity.HeartRateSummaryFit;
import es.us.hermes.smartcitizen.data.api.google.fit.entity.LocationBoundingBoxFit;
import es.us.hermes.smartcitizen.data.api.google.fit.entity.StepCountDeltaFit;

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

    public static void subscribeFitnessData(Context context){
        for(DataType dataType : recordingDataTypes){
            Single<Status> statusObservable = RxFit.Recording.subscribe(dataType)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
            statusObservable.subscribe(
                    status ->  handleSubscriptionStatus(status,dataType),
                    throwable -> handleSubscriptionException(context,throwable,statusObservable,dataType)
            );
        }
    }

    private static void handleSubscriptionStatus(Status status, DataType dataType){
        if (status.isSuccess()) {
            if (status.getStatusCode() == FitnessStatusCodes.SUCCESS_ALREADY_SUBSCRIBED) {
                //no-op
                //Log.i(TAG, "Existing subscription for " + dataType.getName() + " detected.");
            } else {
                Log.i(TAG, "Successfully subscribed!: " + dataType.getName());
            }
        } else {
            Log.i(TAG, "There was a problem subscribing: " + dataType.getName());
        }
    }

    private static void handleSubscriptionException(Context context, Throwable throwable,
        Single<Status> observable, DataType dataType){
            if(throwable instanceof SecurityException){
                Utils.requestMandatoryAppPermissions(context).subscribe(granted -> {
                    observable.subscribe(
                            status -> handleSubscriptionStatus(status,dataType)
                    );
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

    public static ActivityDetails getActivityDetailsFromBucket(Bucket bucket) {
        ActivityDetails activityDetails = new ActivityDetails();
        List<ActivitySummaryFit> activitiesSummary = new ArrayList<>();
        // Single bucket expected
        for (DataSet dataSet : bucket.getDataSets()) {
            if (dataSet.getDataType().equals(DataType.AGGREGATE_STEP_COUNT_DELTA)) {
                for (DataPoint dp : dataSet.getDataPoints()) {
                    if (dp.getDataType().equals(DataType.AGGREGATE_STEP_COUNT_DELTA)) {
                        StepCountDeltaFit stepCount = new StepCountDeltaFit(
                                dp.getValue(Field.FIELD_STEPS).asInt(),
                                dp.getStartTime(TimeUnit.MILLISECONDS),
                                dp.getEndTime(TimeUnit.MILLISECONDS)
                        );
                        activityDetails.setStepCountDelta(stepCount);
                    }
                }
            }
            if (dataSet.getDataType().equals(DataType.AGGREGATE_DISTANCE_DELTA)) {
                for (DataPoint dp : dataSet.getDataPoints()) {
                    if (dp.getDataType().equals(DataType.AGGREGATE_DISTANCE_DELTA)) {
                        DistanceDeltaFit distanceDelta = new DistanceDeltaFit(
                                dp.getValue(Field.FIELD_DISTANCE).asFloat() / 1000,
                                dp.getStartTime(TimeUnit.MILLISECONDS),
                                dp.getEndTime(TimeUnit.MILLISECONDS)
                        );
                        activityDetails.setDistanceDelta(distanceDelta);
                    }
                }
            }
            if (dataSet.getDataType().equals(DataType.AGGREGATE_CALORIES_EXPENDED)) {
                for (DataPoint dp : dataSet.getDataPoints()) {
                    if (dp.getDataType().equals(DataType.AGGREGATE_CALORIES_EXPENDED)) {
                        CaloriesExpendedFit caloriesExpended = new CaloriesExpendedFit(
                                dp.getValue(Field.FIELD_CALORIES).asFloat(),
                                dp.getStartTime(TimeUnit.MILLISECONDS),
                                dp.getEndTime(TimeUnit.MILLISECONDS)
                        );
                        activityDetails.setCaloriesExpended(caloriesExpended);
                    }
                }
            }
            if (dataSet.getDataType().equals(DataType.AGGREGATE_ACTIVITY_SUMMARY)) {
                for (DataPoint dp : dataSet.getDataPoints()) {
                    if (dp.getDataType().equals(DataType.AGGREGATE_ACTIVITY_SUMMARY)) {
                        ActivitySummaryFit activity = new ActivitySummaryFit(
                                dp.getValue(Field.FIELD_ACTIVITY).asActivity(),
                                dp.getValue(Field.FIELD_DURATION).asInt() / 1000 / 60,
                                dp.getValue(Field.FIELD_NUM_SEGMENTS).asInt(),
                                dp.getStartTime(TimeUnit.MILLISECONDS),
                                dp.getEndTime(TimeUnit.MILLISECONDS)
                        );
                        activitiesSummary.add(activity);
                    }
                }
                activityDetails.setActivitiesSummary(activitiesSummary);
            }
            if (dataSet.getDataType().equals(DataType.AGGREGATE_LOCATION_BOUNDING_BOX)) {
                Float latitudeSW, longitudeSW, latitudeNE, longitudeNE;
                for (DataPoint dp : dataSet.getDataPoints()) {
                    if (dp.getDataType().equals(DataType.AGGREGATE_LOCATION_BOUNDING_BOX)) {

                        latitudeSW = dp.getValue(Field.FIELD_LOW_LATITUDE).asFloat();
                        longitudeSW = dp.getValue(Field.FIELD_LOW_LONGITUDE).asFloat();

                        latitudeNE = dp.getValue(Field.FIELD_HIGH_LATITUDE).asFloat();
                        longitudeNE = dp.getValue(Field.FIELD_HIGH_LONGITUDE).asFloat();

                        LocationBoundingBoxFit locationBoundingBox = new LocationBoundingBoxFit(
                                latitudeSW,
                                longitudeSW,
                                latitudeNE,
                                longitudeNE,
                                dp.getStartTime(TimeUnit.MILLISECONDS),
                                dp.getEndTime(TimeUnit.MILLISECONDS)
                        );
                        activityDetails.setLocationBoundingBox(locationBoundingBox);
                    }
                }

            }
            if (dataSet.getDataType().equals(DataType.AGGREGATE_HEART_RATE_SUMMARY)) {
                for (DataPoint dp : dataSet.getDataPoints()) {
                    if (dp.getDataType().equals(DataType.AGGREGATE_HEART_RATE_SUMMARY)) {
                        HeartRateSummaryFit heartRateSummary = new HeartRateSummaryFit(
                                dp.getValue(Field.FIELD_AVERAGE).asFloat(),
                                dp.getValue(Field.FIELD_MIN).asFloat(),
                                dp.getValue(Field.FIELD_MAX).asFloat(),
                                dp.getStartTime(TimeUnit.MILLISECONDS),
                                dp.getEndTime(TimeUnit.MILLISECONDS)
                        );
                        activityDetails.setHeartRateSummary(heartRateSummary);
                    }
                }
            }
        }
        return activityDetails;
    }

    public static List<ActivityDetails> getActivityDetailsListFromBuckets(List<Bucket> buckets){
        List<ActivityDetails> activities = new ArrayList<>();
        for (Bucket bucket : buckets) {
            ActivityDetails currentActivity = new ActivityDetails();
            for (DataSet dataSet : bucket.getDataSets()) {
                if (dataSet.getDataType().equals(DataType.AGGREGATE_STEP_COUNT_DELTA)) {
                    for (DataPoint dp : dataSet.getDataPoints()) {
                        if (dp.getDataType().equals(DataType.AGGREGATE_STEP_COUNT_DELTA)) {
                            StepCountDeltaFit stepCount = new StepCountDeltaFit(
                                    dp.getValue(Field.FIELD_STEPS).asInt(),
                                    dp.getStartTime(TimeUnit.MILLISECONDS),
                                    dp.getEndTime(TimeUnit.MILLISECONDS)
                            );
                            currentActivity.setStepCountDelta(stepCount);
                        }
                    }
                }

                if (dataSet.getDataType().equals(DataType.AGGREGATE_DISTANCE_DELTA)) {
                    for (DataPoint dp : dataSet.getDataPoints()) {
                        if (dp.getDataType().equals(DataType.AGGREGATE_DISTANCE_DELTA)) {
                            DistanceDeltaFit distanceDelta = new DistanceDeltaFit(
                                    dp.getValue(Field.FIELD_DISTANCE).asFloat() / 1000,
                                    dp.getStartTime(TimeUnit.MILLISECONDS),
                                    dp.getEndTime(TimeUnit.MILLISECONDS)
                            );
                            currentActivity.setDistanceDelta(distanceDelta);
                        }
                    }
                }

                if (dataSet.getDataType().equals(DataType.AGGREGATE_CALORIES_EXPENDED)) {
                    for (DataPoint dp : dataSet.getDataPoints()) {
                        if (dp.getDataType().equals(DataType.AGGREGATE_CALORIES_EXPENDED)) {
                            CaloriesExpendedFit caloriesExpended = new CaloriesExpendedFit(
                                    dp.getValue(Field.FIELD_CALORIES).asFloat(),
                                    dp.getStartTime(TimeUnit.MILLISECONDS),
                                    dp.getEndTime(TimeUnit.MILLISECONDS)
                            );
                            currentActivity.setCaloriesExpended(caloriesExpended);
                        }
                    }
                }

                if (dataSet.getDataType().equals(DataType.AGGREGATE_ACTIVITY_SUMMARY)) {
                    for (DataPoint dp : dataSet.getDataPoints()) {
                        if (dp.getDataType().equals(DataType.AGGREGATE_ACTIVITY_SUMMARY)) {
                            ActivitySummaryFit activitySummary = new ActivitySummaryFit(
                                    dp.getValue(Field.FIELD_ACTIVITY).asActivity(),
                                    dp.getValue(Field.FIELD_DURATION).asInt() / 1000 / 60,
                                    dp.getValue(Field.FIELD_NUM_SEGMENTS).asInt(),
                                    dp.getStartTime(TimeUnit.MILLISECONDS),
                                    dp.getEndTime(TimeUnit.MILLISECONDS)
                            );
                            if(activitySummary.getName().startsWith(Constants.ACTIVITY_SLEEP_PREFIX)){
                                activitySummary.setName(Constants.ACTIVITY_SLEEP_PREFIX);
                            }
                            currentActivity.setActivitySummary(activitySummary);
                        }
                    }
                }

                if (dataSet.getDataType().equals(DataType.AGGREGATE_LOCATION_BOUNDING_BOX)) {
                    Float latitudeSW, longitudeSW, latitudeNE, longitudeNE;
                    for (DataPoint dp : dataSet.getDataPoints()) {
                        if (dp.getDataType().equals(DataType.AGGREGATE_LOCATION_BOUNDING_BOX)) {

                            latitudeSW = dp.getValue(Field.FIELD_LOW_LATITUDE).asFloat();
                            longitudeSW = dp.getValue(Field.FIELD_LOW_LONGITUDE).asFloat();

                            latitudeNE = dp.getValue(Field.FIELD_HIGH_LATITUDE).asFloat();
                            longitudeNE = dp.getValue(Field.FIELD_HIGH_LONGITUDE).asFloat();

                            LocationBoundingBoxFit locationBoundingBox = new LocationBoundingBoxFit(
                                    latitudeSW,
                                    longitudeSW,
                                    latitudeNE,
                                    longitudeNE,
                                    dp.getStartTime(TimeUnit.MILLISECONDS),
                                    dp.getEndTime(TimeUnit.MILLISECONDS)
                            );
                            currentActivity.setLocationBoundingBox(locationBoundingBox);
                        }
                    }
                }
            }

            if(!activities.isEmpty() && currentActivity.getActivitySummary().getName().equals(Constants.ACTIVITY_SLEEP_PREFIX)){
                //Join sleep activities
                ActivityDetails lastActivity = activities.get(activities.size() - 1);
                if (lastActivity.getActivitySummary().getName().equals(Constants.ACTIVITY_SLEEP_PREFIX)) {
                    lastActivity.getActivitySummary().setEndTime(currentActivity.getActivitySummary().getEndTime());
                    lastActivity.getStepCountDelta().setSteps(
                            lastActivity.getStepCountDelta().getSteps() + currentActivity.getStepCountDelta().getSteps());
                    lastActivity.getCaloriesExpended().setCalories(
                            lastActivity.getCaloriesExpended().getCalories() + currentActivity.getCaloriesExpended().getCalories());
                    lastActivity.getDistanceDelta().setDistance(
                            lastActivity.getDistanceDelta().getDistance() + currentActivity.getDistanceDelta().getDistance());
                    activities.set(activities.size() - 1, lastActivity);
                } else {
                    activities.add(currentActivity);
                }
            }else{
                activities.add(currentActivity);
            }
        }
        return activities;
    }

    public static List<LatLng> getPointListFromDataSets(List<DataSet> dataSets) {
        List<LatLng> locations = new ArrayList<>();
        for(DataSet dataSet : dataSets){
            if (dataSet.getDataType().equals(DataType.TYPE_LOCATION_SAMPLE)) {
                for (DataPoint dp : dataSet.getDataPoints()) {
                    if (dp.getDataType().equals(DataType.TYPE_LOCATION_SAMPLE)) {
                        LatLng point = new LatLng(
                                dp.getValue(Field.FIELD_LATITUDE).asFloat(),
                                dp.getValue(Field.FIELD_LONGITUDE).asFloat());
                        locations.add(point);
                    }
                }
            }
        }
        return locations;
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
