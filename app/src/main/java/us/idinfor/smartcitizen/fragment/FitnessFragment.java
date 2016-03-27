package us.idinfor.smartcitizen.fragment;


import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.maps.android.SphericalUtil;
import com.patloew.rxfit.RxFit;
import com.patloew.rxfit.StatusException;
import com.sdoward.rxgooglemap.MapObservableProvider;
import com.viewpagerindicator.CirclePageIndicator;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import us.idinfor.smartcitizen.Constants;
import us.idinfor.smartcitizen.R;
import us.idinfor.smartcitizen.Utils;
import us.idinfor.smartcitizen.activity.ActivityDetailsActivity;
import us.idinfor.smartcitizen.activity.LocationDetailsActivity;
import us.idinfor.smartcitizen.adapter.ActivityDurationPagerAdapter;
import us.idinfor.smartcitizen.model.entities.ActivityDetails;
import us.idinfor.smartcitizen.model.entities.fit.ActivitySummaryFit;
import us.idinfor.smartcitizen.model.entities.fit.CaloriesExpendedFit;
import us.idinfor.smartcitizen.model.entities.fit.DistanceDeltaFit;
import us.idinfor.smartcitizen.model.entities.fit.HeartRateSummaryFit;
import us.idinfor.smartcitizen.model.entities.fit.LocationBoundingBoxFit;
import us.idinfor.smartcitizen.model.entities.fit.StepCountDeltaFit;

public class FitnessFragment extends BaseFragment {

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

    private GoogleMap mMap;
    private PolygonOptions mBoundingBoxPolygon;
    private LatLngBounds mBounds;

    private SupportMapFragment mMapFragment;
    private MapObservableProvider mMapObservableProvider;

    private List<ActivitySummaryFit> mActivitiesSummary = new ArrayList<>();
    private ActivityDetails mActivityDetails;

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
    }

    @Override
    protected void injectActivityComponent() {
        getBaseActivity().getActivityComponent().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fitness, container, false);
        ButterKnife.bind(this, view);

        mDate.setText(DateUtils.formatDateTime(getContext(),
                System.currentTimeMillis(),
                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_WEEKDAY | DateUtils.FORMAT_ABBREV_MONTH));

        mMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mMapObservableProvider = new MapObservableProvider(mMapFragment);
        subscribeToFragment(mMapObservableProvider.getMapReadyObservable()
                .subscribe(googleMap -> {
                    mMap = googleMap;
                    mMap.getUiSettings().setAllGesturesEnabled(false);
                    mMap.getUiSettings().setZoomControlsEnabled(true);
                    if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        mMap.setMyLocationEnabled(true);
                    }
                    if (mBoundingBoxPolygon != null && mBounds != null){
                        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(mBounds, 12));
                        mMap.moveCamera(CameraUpdateFactory.zoomOut());
                        mMap.addPolygon(mBoundingBoxPolygon);
                    }
                })
        );

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        queryGoogleFit(Constants.RANGE_DAY);
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


    private DataReadRequest.Builder buildFitDataReadRequest() {
        DataReadRequest.Builder builder = new DataReadRequest.Builder()
                .aggregate(DataType.TYPE_LOCATION_SAMPLE, DataType.AGGREGATE_LOCATION_BOUNDING_BOX)
                .aggregate(DataType.TYPE_ACTIVITY_SEGMENT, DataType.AGGREGATE_ACTIVITY_SUMMARY)
                .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
                .aggregate(DataType.TYPE_HEART_RATE_BPM, DataType.AGGREGATE_HEART_RATE_SUMMARY)
                .aggregate(DataType.TYPE_CALORIES_EXPENDED, DataType.AGGREGATE_CALORIES_EXPENDED)
                .aggregate(DataType.TYPE_DISTANCE_DELTA, DataType.AGGREGATE_DISTANCE_DELTA);
        return builder;
    }

    private void queryGoogleFit(int timeRange) {
        mProgressBar.setVisibility(View.VISIBLE);
        DataReadRequest.Builder dataReadRequestBuilder = buildFitDataReadRequest();

        dataReadRequestBuilder.setTimeRange(Utils.getStartTimeRange(timeRange),new Date().getTime(),TimeUnit.MILLISECONDS);
        dataReadRequestBuilder.bucketByTime(1, TimeUnit.DAYS);

        DataReadRequest dataReadRequest = dataReadRequestBuilder.build();
        DataReadRequest dataReadRequestServer = dataReadRequestBuilder.enableServerQueries().build();

        mActivityDetails = new ActivityDetails();

        subscribeToFragment(RxFit.History.read(dataReadRequestServer)
                .doOnError(throwable -> {
                    if(throwable instanceof StatusException && ((StatusException)throwable).getStatus().getStatusCode() == CommonStatusCodes.TIMEOUT) {
                        Log.e(TAG, "Timeout on server query request");
                    }
                })
                .compose(new RxFit.OnExceptionResumeNext.Single<>(RxFit.History.read(dataReadRequest)))
                .flatMapObservable(dataReadResult -> Observable.from(dataReadResult.getBuckets()))
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(bucket -> {
                    processFitBucket(bucket);
                }, e -> {
                    mProgressBar.setVisibility(View.GONE);
                    Log.e(TAG, "Error reading fitness data", e);
                    Snackbar.make(mProgressBar.getRootView(), "Error getting Fit data", Snackbar.LENGTH_INDEFINITE)
                            .setAction("Retry", v -> queryGoogleFit(timeRange))
                            .show();

                }, () -> {
                    mProgressBar.setVisibility(View.GONE);
                    updateUI();
                })
        );
    }

    public void processFitBucket(Bucket bucket) {
        mActivityDetails = new ActivityDetails();
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
                        mActivityDetails.setStepCountDelta(stepCount);
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
                        mActivityDetails.setDistanceDelta(distanceDelta);
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
                        mActivityDetails.setCaloriesExpended(caloriesExpended);
                    }
                }
            }
            if (dataSet.getDataType().equals(DataType.AGGREGATE_ACTIVITY_SUMMARY)) {
                if (mActivitiesSummary == null) {
                    mActivitiesSummary = new ArrayList<ActivitySummaryFit>();
                } else {
                    mActivitiesSummary.clear();
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
                        mActivitiesSummary.add(activity);
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
                        mActivityDetails.setLocationBoundingBox(locationBoundingBox);
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
                        mActivityDetails.setHeartRateSummary(heartRateSummary);
                    }
                }
            }
        }
        updateUI();
    }

    private void updateUI() {
        NumberFormat df = DecimalFormat.getInstance();
        df.setMaximumFractionDigits(2);

        if (mActivityDetails.getStepCountDelta() != null) {
            Integer steps = mActivityDetails.getStepCountDelta().getSteps();
            Double stepsGoal = Double.valueOf(getString(R.string.default_steps_goal));
            Integer stepsProgress = (int)(steps * 100 / stepsGoal);
            mStepsCounter.setText(steps.toString());
            mStepsProgress.setProgressDrawable(Utils.getFitnessProgressBarDrawable(getActivity(),stepsProgress));
            mStepsProgress.setProgress(stepsProgress > 100 ? 100 : stepsProgress);

        }

        if (mActivityDetails.getDistanceDelta() != null) {
            Float distance = mActivityDetails.getDistanceDelta().getDistance();
            Float distanceGoal = Float.valueOf(getString(R.string.default_distance_goal));
            Integer distanceProgress = (int)(distance * 100 / distanceGoal);
            mDistanceCounter.setText(df.format(distance));
            mDistanceProgress.setProgressDrawable(Utils.getFitnessProgressBarDrawable(getActivity(),distanceProgress));
            mDistanceProgress.setProgress(distanceProgress > 100 ? 100 : distanceProgress);
        }

        if (mActivityDetails.getCaloriesExpended() != null) {
            Float calories = mActivityDetails.getCaloriesExpended().getCalories();
            Float caloriesGoal = Float.valueOf(getString(R.string.default_calories_goal));
            Integer caloriesProgress = (int)(calories * 100 / caloriesGoal);
            mCaloriesCounter.setText(df.format(calories));
            mCaloriesProgress.setProgressDrawable(Utils.getFitnessProgressBarDrawable(getActivity(),caloriesProgress));
            mCaloriesProgress.setProgress(caloriesProgress > 100 ? 100 : caloriesProgress);

        }

        if (mActivitiesSummary != null && !mActivitiesSummary.isEmpty()) {
            mTimePager.setAdapter(new ActivityDurationPagerAdapter(this.getContext(), mActivitiesSummary));
            mTimePagerIndicator.setViewPager(mTimePager);
        }

        if (mActivityDetails.getHeartRateSummary() != null) {
            mHeartRateCounter.setText(Integer.valueOf(mActivityDetails.getHeartRateSummary().getAverage().intValue()).toString());
        }

        if (mActivityDetails.getLocationBoundingBox() != null) {
            mBoundingBoxPolygon = new PolygonOptions()
                    .add(mActivityDetails.getLocationBoundingBox().getLocationNE(), //ne
                            new LatLng(mActivityDetails.getLocationBoundingBox().getLatitudeNE(), mActivityDetails.getLocationBoundingBox().getLongitudeSW()),
                            mActivityDetails.getLocationBoundingBox().getLocationSW(), //sw
                            new LatLng(mActivityDetails.getLocationBoundingBox().getLatitudeSW(), mActivityDetails.getLocationBoundingBox().getLongitudeNE()))
                    .strokeColor(Color.argb(180, 0, 150, 136))
                    .fillColor(Color.argb(110, 0, 150, 136))
                    .strokeWidth(5)
                    .geodesic(true);

            LatLngBounds.Builder boundsBuilder = LatLngBounds.builder();
            for (LatLng point : mBoundingBoxPolygon.getPoints()) {
                boundsBuilder.include(point);
            }
            mBounds = boundsBuilder.build();
            SphericalUtil.interpolate(mActivityDetails.getLocationBoundingBox().getLocationSW(), mActivityDetails.getLocationBoundingBox().getLocationNE(), 0.5);
            if (mMap != null) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(mBounds, 12));
                mMap.moveCamera(CameraUpdateFactory.zoomOut());
                mMap.addPolygon(mBoundingBoxPolygon);
            }
        }
        mProgressBar.setVisibility(View.GONE);
    }
}
