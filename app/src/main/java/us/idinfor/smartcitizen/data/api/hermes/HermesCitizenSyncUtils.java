package us.idinfor.smartcitizen.data.api.hermes;


import android.content.Context;

import com.google.android.gms.fitness.FitnessActivities;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import us.idinfor.smartcitizen.model.entities.fit.ActivitySegmentFit;
import us.idinfor.smartcitizen.model.entities.fit.LocationSampleFit;

public class HermesCitizenSyncUtils {

    private static final String TAG = HermesCitizenSyncUtils.class.getCanonicalName();
    private static final String DATE_FORMAT = "yyyy.MM.dd HH:mm:ss";

    public static void queryLocationsFit(Context context){
        /*SharedPreferences prefs = Utils.getSharedPreferences(context);
        GoogleFitApi fitHelper = GoogleFitApi.getInstance(context);
        long startTime = prefs.getLong(Constants.PROPERTY_LAST_LOCATION_TIME_SENT,Utils.getStartTimeRange(Constants.RANGE_DAY));
        long endTime = new Date().getTime();
        DataReadRequest.Builder builder = new DataReadRequest.Builder()
                .read(DataType.TYPE_LOCATION_SAMPLE);
        fitHelper.queryFitnessData(
                startTime,
                endTime,
                builder,
                GoogleFitApi.QUERY_LOCATIONS_HERMES);*/
    }

    public static void queryActivitiesFit(Context context){
        /*SharedPreferences prefs = Utils.getSharedPreferences(context);
        GoogleFitApi fitHelper = GoogleFitApi.getInstance(context);
        long startTime = prefs.getLong(Constants.PROPERTY_LAST_ACTIVITY_TIME_SENT,Utils.getStartTimeRange(Constants.RANGE_DAY));
        long endTime = new Date().getTime();
        DataReadRequest.Builder builder = new DataReadRequest.Builder()
                .aggregate(DataType.TYPE_ACTIVITY_SEGMENT,DataType.AGGREGATE_ACTIVITY_SUMMARY)
                .bucketByActivitySegment(1,TimeUnit.MINUTES);
        fitHelper.queryFitnessData(
                startTime,
                endTime,
                builder,
                GoogleFitApi.QUERY_ACTIVITIES_HERMES);*/
    }

    public static List<LocationSampleFit> dataSetsToLocationSampleList(List<DataSet> dataSets){
        /*SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT,Locale.getDefault());
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
        }*/

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

        // Set end time of current location as start time of next location.
        if(locations != null && !locations.isEmpty()){
            for(int i = 0; i < locations.size() - 1 ; i++){
                LocationSampleFit current = locations.get(i);
                LocationSampleFit next = locations.get(i+1);
                current.setEndTime(next.getStartTime());
                locations.set(i,current);
                /*Log.i(TAG, "\tStart: " + dateFormat.format(current.getStartTime()));
                Log.i(TAG, "\tEnd: " + dateFormat.format(current.getEndTime()));
                Log.i(TAG, "\tStartMillis: " + current.getStartTime());
                Log.i(TAG, "\tEndMillis: " + current.getEndTime());
                Log.i(TAG, "\tLatitude: " + current.getLatitude());
                Log.i(TAG, "\tLongitude: " + current.getLongitude());*/
            }
            locations.get(locations.size() - 1).setEndTime(new Date().getTime());
        }
        return locations;
    }

    public static List<ActivitySegmentFit> bucketsToActivitySegmentList(List<Bucket> buckets){
        /*SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
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
        }*/

        List<ActivitySegmentFit> activities = activities = new ArrayList<>();
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
}
