package us.idinfor.smartcitizen.fragment;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataReadRequest;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import us.idinfor.smartcitizen.Constants;
import us.idinfor.smartcitizen.R;
import us.idinfor.smartcitizen.Utils;
import us.idinfor.smartcitizen.adapter.ActivitySegmentAdapter;
import us.idinfor.smartcitizen.model.ActivitySegmentDetails;
import us.idinfor.smartcitizen.model.ActivitySegmentFit;
import us.idinfor.smartcitizen.model.ActivitySummaryFit;
import us.idinfor.smartcitizen.model.CaloriesExpendedFit;
import us.idinfor.smartcitizen.model.DistanceDeltaFit;
import us.idinfor.smartcitizen.model.LocationBoundingBoxFit;
import us.idinfor.smartcitizen.model.StepCountDeltaFit;


public class ActivityDetailsActivityFragment extends BaseGoogleFitFragment {

    private static final String TAG = ActivityDetailsActivityFragment.class.getCanonicalName();

    @Bind(R.id.progressBar)
    ProgressBar mpProgressBar;
    @Bind(R.id.activitiesRecyclerView)
    RecyclerView mActivitiesRecyclerView;

    ActivitySegmentAdapter adapter;

    //private ArrayList<ActivitySegmentFit> activities;
    private ArrayList<ActivitySegmentDetails> activities;

    public ActivityDetailsActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_activity_details, container, false);
        ButterKnife.bind(this, view);
        mActivitiesRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mActivitiesRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mActivitiesRecyclerView.setHasFixedSize(true);
        activities = new ArrayList<ActivitySegmentDetails>();
        adapter = new ActivitySegmentDetailsAdapter(activities);
        mActivitiesRecyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        activities.clear();
    }

    @Override
    protected DataReadRequest queryFitnessData(long startTime, long endTime) {
        DataReadRequest.Builder readRequestBuilder;

        readRequestBuilder = new DataReadRequest.Builder()
                //.read(DataType.TYPE_ACTIVITY_SEGMENT);
                //.read(DataType.TYPE_LOCATION_SAMPLE);
                .aggregate(DataType.TYPE_LOCATION_SAMPLE, DataType.AGGREGATE_LOCATION_BOUNDING_BOX)
                .aggregate(DataType.TYPE_ACTIVITY_SEGMENT, DataType.AGGREGATE_ACTIVITY_SUMMARY)
                .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
                .aggregate(DataType.TYPE_HEART_RATE_BPM, DataType.AGGREGATE_HEART_RATE_SUMMARY)
                .aggregate(DataType.TYPE_CALORIES_EXPENDED, DataType.AGGREGATE_CALORIES_EXPENDED)
                .aggregate(DataType.TYPE_DISTANCE_DELTA, DataType.AGGREGATE_DISTANCE_DELTA);

        DataReadRequest readRequest = readRequestBuilder
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .bucketByActivitySegment(1,TimeUnit.MINUTES)
                .build();

        return readRequest;
    }

    @Override
    public void onConnected(Bundle bundle) {
        super.onConnected(bundle);
        launchQuery(Utils.getStartTimeRange(Constants.RANGE_DAY), new Date().getTime());
    }

    @Override
    protected void dumpDataSet(DataSet dataSet) {
        super.dumpDataSet(dataSet);
        NumberFormat df = DecimalFormat.getInstance();
        df.setMaximumFractionDigits(2);

        if (dataSet.getDataType().equals(DataType.AGGREGATE_STEP_COUNT_DELTA)) {
            for (DataPoint dp : dataSet.getDataPoints()) {
                if (dp.getDataType().equals(DataType.AGGREGATE_STEP_COUNT_DELTA)) {
                    StepCountDeltaFit stepCount = new StepCountDeltaFit(
                            dp.getValue(Field.FIELD_STEPS).asInt(),
                            dp.getStartTime(TimeUnit.SECONDS),
                            dp.getEndTime(TimeUnit.SECONDS)
                    );
                }
            }
        }

        if (dataSet.getDataType().equals(DataType.AGGREGATE_DISTANCE_DELTA)) {
            for (DataPoint dp : dataSet.getDataPoints()) {
                if (dp.getDataType().equals(DataType.AGGREGATE_DISTANCE_DELTA)) {
                    DistanceDeltaFit distanceDelta = new DistanceDeltaFit(
                            dp.getValue(Field.FIELD_DISTANCE).asFloat() / 1000,
                            dp.getStartTime(TimeUnit.SECONDS),
                            dp.getEndTime(TimeUnit.SECONDS)
                    );
                }
            }
        }

        if (dataSet.getDataType().equals(DataType.AGGREGATE_CALORIES_EXPENDED)) {
            for (DataPoint dp : dataSet.getDataPoints()) {
                if (dp.getDataType().equals(DataType.AGGREGATE_CALORIES_EXPENDED)) {
                    CaloriesExpendedFit caloriesExpended = new CaloriesExpendedFit(
                            dp.getValue(Field.FIELD_CALORIES).asFloat(),
                            dp.getStartTime(TimeUnit.SECONDS),
                            dp.getEndTime(TimeUnit.SECONDS)
                    );
                }
            }
        }

        if (dataSet.getDataType().equals(DataType.AGGREGATE_ACTIVITY_SUMMARY)) {
            for (DataPoint dp : dataSet.getDataPoints()) {
                if (dp.getDataType().equals(DataType.AGGREGATE_ACTIVITY_SUMMARY)) {
                    ActivitySummaryFit activity = new ActivitySummaryFit(
                            dp.getValue(Field.FIELD_ACTIVITY).asInt(),
                            dp.getValue(Field.FIELD_ACTIVITY).asActivity(),
                            dp.getValue(Field.FIELD_DURATION).asInt() / 1000 / 60,
                            dp.getValue(Field.FIELD_NUM_SEGMENTS).asInt()
                    );
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
                            dp.getStartTime(TimeUnit.SECONDS),
                            dp.getEndTime(TimeUnit.SECONDS)
                    );
                }
            }
        }


        /*if (dataSet.getDataType().equals(DataType.TYPE_ACTIVITY_SEGMENT)) {
            for (DataPoint dp : dataSet.getDataPoints()) {
                if (dp.getDataType().equals(DataType.TYPE_ACTIVITY_SEGMENT)) {
                    ActivitySegmentFit current = new ActivitySegmentFit(
                            dp.getValue(Field.FIELD_ACTIVITY).asInt(),
                            dp.getValue(Field.FIELD_ACTIVITY).asActivity(),
                            dp.getStartTime(TimeUnit.MILLISECONDS),
                            dp.getEndTime(TimeUnit.MILLISECONDS)
                    );
                    if (!activities.isEmpty()) {
                        ActivitySegmentFit last = activities.get(activities.size() - 1);
                        if (last.getId() == current.getId()) {
                            last.setEndTime(current.getEndTime());
                            activities.set(activities.size() - 1, last);
                        } else {
                            activities.add(current);
                        }
                    } else {
                        activities.add(current);
                    }
                }
            }
            if(!activities.isEmpty()){
                Collections.reverse(activities);
                adapter.notifyDataSetChanged();
                //adapter.addAll(activities);
            }
        }*/
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
