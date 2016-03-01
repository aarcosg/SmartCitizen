package us.idinfor.smartcitizen.fragment;


import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.maps.android.SphericalUtil;
import com.viewpagerindicator.CirclePageIndicator;

import org.greenrobot.eventbus.Subscribe;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import us.idinfor.smartcitizen.Constants;
import us.idinfor.smartcitizen.GoogleFitService;
import us.idinfor.smartcitizen.R;
import us.idinfor.smartcitizen.Utils;
import us.idinfor.smartcitizen.activity.ActivityDetailsActivity;
import us.idinfor.smartcitizen.activity.LocationDetailsActivity;
import us.idinfor.smartcitizen.adapter.ActivityDurationPagerAdapter;
import us.idinfor.smartcitizen.event.FitBucketsResultEvent;
import us.idinfor.smartcitizen.event.GoogleApiClientConnectedEvent;
import us.idinfor.smartcitizen.model.ActivityDetails;
import us.idinfor.smartcitizen.model.fit.ActivitySummaryFit;
import us.idinfor.smartcitizen.model.fit.CaloriesExpendedFit;
import us.idinfor.smartcitizen.model.fit.DistanceDeltaFit;
import us.idinfor.smartcitizen.model.HeartRateSummary;
import us.idinfor.smartcitizen.model.fit.LocationBoundingBoxFit;
import us.idinfor.smartcitizen.model.fit.StepCountDeltaFit;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FitnessFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FitnessFragment extends BaseGoogleFitFragment implements OnMapReadyCallback {

    private static final String TAG = FitnessFragment.class.getCanonicalName();

    @Bind(R.id.progressBar)
    ProgressBar mProgressBar;
    @Bind(R.id.stepsCounter)
    TextView mStepsCounter;
    @Bind(R.id.distanceCounter)
    TextView mDistanceCounter;
    @Bind(R.id.caloriesCounter)
    TextView mCaloriesCounter;
    @Bind(R.id.timePager)
    ViewPager mTimePager;
    @Bind(R.id.timePagerIndicator)
    CirclePageIndicator mTimePagerIndicator;
    @Bind(R.id.heartRateCounter)
    TextView mHeartRateCounter;
    @Bind(R.id.date)
    TextView mDate;
    @Bind(R.id.activityDetailsBtn)
    Button mActivityDetailsBtn;
    @Bind(R.id.locationDetailsBtn)
    Button mLocationDetailsBtn;
    @Bind(R.id.stepsProgress)
    ProgressBar mStepsProgress;
    @Bind(R.id.distanceProgress)
    ProgressBar mDistanceProgress;
    @Bind(R.id.caloriesProgress)
    ProgressBar mCaloriesProgress;

    private SharedPreferences prefs;
    private GoogleMap mMap;

    private PolygonOptions boundingBoxPolygon;
    private LatLng boundingBoxCenter;
    private LatLngBounds bounds;

    List<ActivitySummaryFit> activities = new ArrayList<>();
    private ActivityDetails activityDetails;

    public static FitnessFragment newInstance() {
        FitnessFragment fragment = new FitnessFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public FitnessFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = Utils.getSharedPreferences(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fitness, container, false);
        ButterKnife.bind(this, view);

        mDate.setText(DateUtils.formatDateTime(getContext(),
                System.currentTimeMillis(),
                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_WEEKDAY | DateUtils.FORMAT_ABBREV_MONTH));

        if (mMap == null) {
            SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(FitnessFragment.this);
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (fitHelper.getGoogleApiClient().isConnected()) {
            queryGoogleFit(Constants.RANGE_DAY);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick(R.id.locationDetailsBtn)
    public void openLocationDetailsActivity() {
        LocationDetailsActivity.launch(getActivity());
    }

    @OnClick(R.id.activityDetailsBtn)
    public void openActivityDetailsActivity() {
        ActivityDetailsActivity.launch(getActivity());
    }


    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        mMap.getUiSettings().setAllGesturesEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    protected DataReadRequest.Builder buildFitQuery() {
        DataReadRequest.Builder builder = new DataReadRequest.Builder()
                .aggregate(DataType.TYPE_LOCATION_SAMPLE, DataType.AGGREGATE_LOCATION_BOUNDING_BOX)
                .aggregate(DataType.TYPE_ACTIVITY_SEGMENT, DataType.AGGREGATE_ACTIVITY_SUMMARY)
                .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
                .aggregate(DataType.TYPE_HEART_RATE_BPM, DataType.AGGREGATE_HEART_RATE_SUMMARY)
                .aggregate(DataType.TYPE_CALORIES_EXPENDED, DataType.AGGREGATE_CALORIES_EXPENDED)
                .aggregate(DataType.TYPE_DISTANCE_DELTA, DataType.AGGREGATE_DISTANCE_DELTA)
                .bucketByTime(1, TimeUnit.DAYS);
        return builder;
    }

    @Override
    protected void queryGoogleFit(int timeRange) {
        mProgressBar.setVisibility(View.VISIBLE);
        fitHelper.queryFitnessData(
                Utils.getStartTimeRange(timeRange),
                new Date().getTime(),
                buildFitQuery(),
                GoogleFitService.QUERY_DEFAULT);
    }

    @Subscribe
    public void onEvent(GoogleApiClientConnectedEvent event) {
        queryGoogleFit(Constants.RANGE_DAY);
    }

    @Subscribe
    public void onEvent(FitBucketsResultEvent event) {
        activityDetails = new ActivityDetails();
        // Single bucket expected
        for (DataSet dataSet : event.getBuckets().get(0).getDataSets()) {
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
                if (activities == null) {
                    activities = new ArrayList<ActivitySummaryFit>();
                } else {
                    activities.clear();
                }
                for (DataPoint dp : dataSet.getDataPoints()) {
                    if (dp.getDataType().equals(DataType.AGGREGATE_ACTIVITY_SUMMARY)) {
                        ActivitySummaryFit activity = new ActivitySummaryFit(
                                dp.getValue(Field.FIELD_ACTIVITY).asActivity(),
                                dp.getValue(Field.FIELD_DURATION).asInt() / 1000 / 60,
                                dp.getValue(Field.FIELD_NUM_SEGMENTS).asInt(),
                                dp.getStartTime(TimeUnit.MILLISECONDS),
                                dp.getEndTime(TimeUnit.MILLISECONDS)
                        );
                        activities.add(activity);
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
                        activityDetails.setLocationBoundingBox(locationBoundingBox);
                    }
                }

            }
            if (dataSet.getDataType().equals(DataType.AGGREGATE_HEART_RATE_SUMMARY)) {
                for (DataPoint dp : dataSet.getDataPoints()) {
                    if (dp.getDataType().equals(DataType.AGGREGATE_HEART_RATE_SUMMARY)) {
                        HeartRateSummary heartRateSummary = new HeartRateSummary(
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
        updateUI();
    }

    private void updateUI() {
        NumberFormat df = DecimalFormat.getInstance();
        df.setMaximumFractionDigits(2);

        if (activityDetails.getStepCountDelta() != null) {
            Integer steps = activityDetails.getStepCountDelta().getSteps();
            Double stepsGoal = Double.valueOf(getString(R.string.default_steps_goal));
            Integer stepsProgress = (int)(steps * 100 / stepsGoal);
            mStepsCounter.setText(steps.toString());
            mStepsProgress.setProgressDrawable(Utils.getFitnessProgresBarDrawable(getActivity(),stepsProgress));
            mStepsProgress.setProgress(stepsProgress > 100 ? 100 : stepsProgress);

        }

        if (activityDetails.getDistanceDelta() != null) {
            Float distance = activityDetails.getDistanceDelta().getDistance();
            Float distanceGoal = Float.valueOf(getString(R.string.default_distance_goal));
            Integer distanceProgress = (int)(distance * 100 / distanceGoal);
            mDistanceCounter.setText(df.format(distance));
            mDistanceProgress.setProgressDrawable(Utils.getFitnessProgresBarDrawable(getActivity(),distanceProgress));
            mDistanceProgress.setProgress(distanceProgress > 100 ? 100 : distanceProgress);
        }

        if (activityDetails.getCaloriesExpended() != null) {
            Float calories = activityDetails.getCaloriesExpended().getCalories();
            Float caloriesGoal = Float.valueOf(getString(R.string.default_calories_goal));
            Integer caloriesProgress = (int)(calories * 100 / caloriesGoal);
            mCaloriesCounter.setText(df.format(calories));
            mCaloriesProgress.setProgressDrawable(Utils.getFitnessProgresBarDrawable(getActivity(),caloriesProgress));
            mCaloriesProgress.setProgress(caloriesProgress > 100 ? 100 : caloriesProgress);

        }

        if (activities != null && !activities.isEmpty()) {
            mTimePager.setAdapter(new ActivityDurationPagerAdapter(this.getContext(), activities));
            mTimePagerIndicator.setViewPager(mTimePager);
        }

        if (activityDetails.getHeartRateSummary() != null) {
            mHeartRateCounter.setText(Integer.valueOf(activityDetails.getHeartRateSummary().getAverage().intValue()).toString());
        }

        if (activityDetails.getLocationBoundingBox() != null) {
            boundingBoxPolygon = new PolygonOptions()
                    .add(activityDetails.getLocationBoundingBox().getLocationNE(), //ne
                            new LatLng(activityDetails.getLocationBoundingBox().getLatitudeNE(), activityDetails.getLocationBoundingBox().getLongitudeSW()),
                            activityDetails.getLocationBoundingBox().getLocationSW(), //sw
                            new LatLng(activityDetails.getLocationBoundingBox().getLatitudeSW(), activityDetails.getLocationBoundingBox().getLongitudeNE()))
                    .strokeColor(Color.argb(180, 0, 150, 136))
                    .fillColor(Color.argb(110, 0, 150, 136))
                    .strokeWidth(5)
                    .geodesic(true);

            LatLngBounds.Builder boundsBuilder = LatLngBounds.builder();
            for (LatLng point : boundingBoxPolygon.getPoints()) {
                boundsBuilder.include(point);
            }
            bounds = boundsBuilder.build();
            boundingBoxCenter = SphericalUtil.interpolate(activityDetails.getLocationBoundingBox().getLocationSW(), activityDetails.getLocationBoundingBox().getLocationNE(), 0.5);
            if (mMap != null) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 12));
                mMap.moveCamera(CameraUpdateFactory.zoomOut());
                mMap.addPolygon(boundingBoxPolygon);
            }
        }
        mProgressBar.setVisibility(View.GONE);
    }
}
