package us.idinfor.smartcitizen.service;


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

import us.idinfor.smartcitizen.data.api.google.fit.entity.ActivitySegmentFit;
import us.idinfor.smartcitizen.data.api.google.fit.entity.LocationSampleFit;

public class SyncServiceUtils {

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

}
