package es.us.hermes.smartcitizen.service;


import com.google.android.gms.fitness.FitnessActivities;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import es.us.hermes.smartcitizen.mvp.model.google.fit.ActivitySegmentFit;
import es.us.hermes.smartcitizen.mvp.model.google.fit.CaloriesExpendedFit;
import es.us.hermes.smartcitizen.mvp.model.google.fit.DistanceDeltaFit;
import es.us.hermes.smartcitizen.mvp.model.google.fit.HeartRateSampleFit;
import es.us.hermes.smartcitizen.mvp.model.google.fit.LocationSampleFit;
import es.us.hermes.smartcitizen.mvp.model.google.fit.StepCountDeltaFit;

public class SyncServiceUtils {

    public static String getHash(String stringToHash){
        String hashedEmail = "";
        MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.reset();
            hashedEmail =  bin2hex(messageDigest.digest(stringToHash.getBytes()));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return hashedEmail;
    }

    public static String bin2hex(byte[] data) {
        return String.format("%0" + (data.length*2) + "x", new BigInteger(1, data));
    }

    public static List<LocationSampleFit> getLocationListFromDataSets(List<DataSet> dataSets){
        List<LocationSampleFit> locations = new ArrayList<>();
        for (DataSet dataSet : dataSets) {
            if (dataSet.getDataType().equals(DataType.TYPE_LOCATION_SAMPLE)) {
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

        // Set end time of current location as start time of next location.
        if(!locations.isEmpty()){
            for(int i = 0; i < locations.size() - 1 ; i++){
                LocationSampleFit current = locations.get(i);
                LocationSampleFit next = locations.get(i+1);
                current.setEndTime(next.getStartTime());
                locations.set(i,current);
            }
            locations.get(locations.size() - 1).setEndTime(new Date().getTime());
        }
        return locations;
    }

    public static List<ActivitySegmentFit> getActivitySegmentListFromBuckets(List<Bucket> buckets){

        List<ActivitySegmentFit> activities = new ArrayList<>();
        for (Bucket bucket : buckets) {
            for (DataSet dataSet : bucket.getDataSets()) {
                if (dataSet.getDataType().equals(DataType.AGGREGATE_ACTIVITY_SUMMARY)) {
                    for (DataPoint dp : dataSet.getDataPoints()) {
                        if (dp.getDataType().equals(DataType.AGGREGATE_ACTIVITY_SUMMARY)) {
                            ActivitySegmentFit activity = new ActivitySegmentFit(
                                    dp.getValue(Field.FIELD_ACTIVITY).asActivity(),
                                    dp.getStartTime(TimeUnit.MILLISECONDS),
                                    dp.getEndTime(TimeUnit.MILLISECONDS)
                            );
                            activities.add(activity);
                        }
                    }
                }
            }
        }

        if(!activities.isEmpty() && activities.size() > 1){
            ActivitySegmentFit lastActivity = activities.get(activities.size() - 1);
            if(lastActivity.getName().equals(FitnessActivities.UNKNOWN) ){
                activities.get(activities.size() - 2).setEndTime(lastActivity.getEndTime());
                activities.remove(activities.size() - 1);
            }
        }

        return activities;
    }

    public static List<StepCountDeltaFit> getStepsListFromDataSets(List<DataSet> dataSets){
        List<StepCountDeltaFit> stepsList = new ArrayList<>();
        for (DataSet dataSet : dataSets) {
            if (dataSet.getDataType().equals(DataType.TYPE_STEP_COUNT_DELTA)) {
                for (DataPoint dp : dataSet.getDataPoints()) {
                    if (dp.getDataType().equals(DataType.TYPE_STEP_COUNT_DELTA)) {
                        StepCountDeltaFit step = new StepCountDeltaFit(
                                dp.getValue(Field.FIELD_STEPS).asInt(),
                                dp.getStartTime(TimeUnit.MILLISECONDS),
                                dp.getEndTime(TimeUnit.MILLISECONDS)
                        );
                        stepsList.add(step);
                    }
                }
            }
        }

        return stepsList;
    }

    public static List<DistanceDeltaFit> getDistanceListFromDataSets(List<DataSet> dataSets){
        List<DistanceDeltaFit> distances = new ArrayList<>();
        for (DataSet dataSet : dataSets) {
            if (dataSet.getDataType().equals(DataType.TYPE_DISTANCE_DELTA)) {
                for (DataPoint dp : dataSet.getDataPoints()) {
                    if (dp.getDataType().equals(DataType.TYPE_DISTANCE_DELTA)) {
                        DistanceDeltaFit distance = new DistanceDeltaFit(
                                dp.getValue(Field.FIELD_DISTANCE).asFloat(),
                                dp.getStartTime(TimeUnit.MILLISECONDS),
                                dp.getEndTime(TimeUnit.MILLISECONDS)
                        );
                        distances.add(distance);
                    }
                }
            }
        }

        return distances;
    }

    public static List<CaloriesExpendedFit> getCaloriesExpendedListFromDataSets(List<DataSet> dataSets){
        List<CaloriesExpendedFit> caloriesExpendedList = new ArrayList<>();
        for (DataSet dataSet : dataSets) {
            if (dataSet.getDataType().equals(DataType.TYPE_CALORIES_EXPENDED)) {
                for (DataPoint dp : dataSet.getDataPoints()) {
                    if (dp.getDataType().equals(DataType.TYPE_CALORIES_EXPENDED)) {
                        CaloriesExpendedFit caloriesExpended = new CaloriesExpendedFit(
                                dp.getValue(Field.FIELD_CALORIES).asFloat(),
                                dp.getStartTime(TimeUnit.MILLISECONDS),
                                dp.getEndTime(TimeUnit.MILLISECONDS)
                        );
                        caloriesExpendedList.add(caloriesExpended);
                    }
                }
            }
        }

        return caloriesExpendedList;
    }

    public static List<HeartRateSampleFit> getHeartRateSampleListFromDataSets(List<DataSet> dataSets){
        List<HeartRateSampleFit> heartRates = new ArrayList<>();
        for (DataSet dataSet : dataSets) {
            if (dataSet.getDataType().equals(DataType.TYPE_HEART_RATE_BPM)) {
                for (DataPoint dp : dataSet.getDataPoints()) {
                    if (dp.getDataType().equals(DataType.TYPE_HEART_RATE_BPM)) {
                        HeartRateSampleFit heartRate = new HeartRateSampleFit(
                                dp.getValue(Field.FIELD_BPM).asFloat(),
                                dp.getStartTime(TimeUnit.MILLISECONDS),
                                dp.getEndTime(TimeUnit.MILLISECONDS)
                        );
                        heartRates.add(heartRate);
                    }
                }
            }
        }

        return heartRates;
    }

    public static List<ActivitySegmentFit> getSleepActivityListFromBuckets(List<Bucket> buckets){

        List<ActivitySegmentFit> activities = new ArrayList<>();
        for (Bucket bucket : buckets) {
            for (DataSet dataSet : bucket.getDataSets()) {
                if (dataSet.getDataType().equals(DataType.AGGREGATE_ACTIVITY_SUMMARY)) {
                    for (DataPoint dp : dataSet.getDataPoints()) {
                        if (dp.getDataType().equals(DataType.AGGREGATE_ACTIVITY_SUMMARY)) {
                            String activityName = dp.getValue(Field.FIELD_ACTIVITY).asActivity();
                            if(activityName.equals(FitnessActivities.SLEEP)
                                    || activityName.equals(FitnessActivities.SLEEP_AWAKE)
                                    || activityName.equals(FitnessActivities.SLEEP_DEEP)
                                    || activityName.equals(FitnessActivities.SLEEP_LIGHT)
                                    || activityName.equals(FitnessActivities.SLEEP_REM)){
                                ActivitySegmentFit activity = new ActivitySegmentFit(
                                        activityName,
                                        dp.getStartTime(TimeUnit.MILLISECONDS),
                                        dp.getEndTime(TimeUnit.MILLISECONDS)
                                );
                                activities.add(activity);
                            }
                        }
                    }
                }
            }
        }

        return activities;
    }

}
