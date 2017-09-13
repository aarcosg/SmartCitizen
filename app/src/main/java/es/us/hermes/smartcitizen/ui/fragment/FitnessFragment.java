package es.us.hermes.smartcitizen.ui.fragment;


import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.fitness.FitnessActivities;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolygonOptions;
import com.viewpagerindicator.CirclePageIndicator;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Collections;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.us.hermes.smartcitizen.R;
import es.us.hermes.smartcitizen.SmartCitizenApplication;
import es.us.hermes.smartcitizen.di.components.DaggerFitnessComponent;
import es.us.hermes.smartcitizen.di.components.FitnessComponent;
import es.us.hermes.smartcitizen.di.modules.FitnessModule;
import es.us.hermes.smartcitizen.mvp.model.ActivityDetails;
import es.us.hermes.smartcitizen.mvp.model.google.fit.ActivitySummaryFit;
import es.us.hermes.smartcitizen.mvp.presenter.FitnessPresenter;
import es.us.hermes.smartcitizen.mvp.view.FitnessView;
import es.us.hermes.smartcitizen.ui.activity.ActivityTimelineActivity;
import es.us.hermes.smartcitizen.ui.activity.LocationDetailsActivity;
import es.us.hermes.smartcitizen.ui.adapter.ActivityDurationPagerAdapter;
import es.us.hermes.smartcitizen.utils.Utils;

public class FitnessFragment extends BaseFragment implements FitnessView {

    private static final String TAG = FitnessFragment.class.getCanonicalName();
    private static final String ARG_DAY = "ARG_DAY";
    private static final String ARG_MONTH = "ARG_MONTH";
    private static final String ARG_YEAR = "ARG_YEAR";
    private static final LatLng MADRID = new LatLng(40, -3.3);

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
    @Bind(R.id.leftArrowIcon)
    ImageView mLeftArrowIcon;
    @Bind(R.id.rightArrowIcon)
    ImageView mRightArrowIcon;

    @Inject
    FitnessPresenter mFitnessPresenter;

    private SupportMapFragment mMapFragment;
    private PolygonOptions mBoundingBoxPolygon;
    private LatLngBounds mBounds;
    private ActivityDetails mActivityDetails;
    private Integer mDay;
    private Integer mMonth;
    private Integer mYear;
    private long mRangeStartTime;
    private long mRangeEndTime;

    public static FitnessFragment newInstance(int day, int month, int year) {
        FitnessFragment fragment = new FitnessFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_DAY, day);
        args.putInt(ARG_MONTH, month);
        args.putInt(ARG_YEAR, year);
        fragment.setArguments(args);
        return fragment;
    }

    public FitnessFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeInjector();
        this.mFitnessPresenter.setView(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View fragmentView = inflater.inflate(R.layout.fragment_fitness, container, false);
        ButterKnife.bind(this, fragmentView);
        this.mFitnessPresenter.onCreateView();
        return fragmentView;
    }

    @Override
    public void onResume() {
        super.onResume();
        this.mFitnessPresenter.onResume();
        this.mFitnessPresenter.queryFitnessData(mRangeStartTime, mRangeEndTime);
    }

    @Override
    public void onPause() {
        super.onPause();
        this.mFitnessPresenter.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        try {
            FragmentTransaction ft = getChildFragmentManager().beginTransaction();
            ft.remove(mMapFragment);
            ft.commitAllowingStateLoss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setTimeRange() {
        Calendar startTime = Calendar.getInstance();
        Calendar endTime = Calendar.getInstance();
        if (getArguments() != null && !getArguments().isEmpty()) {
            mDay = getArguments().getInt(ARG_DAY);
            mMonth = getArguments().getInt(ARG_MONTH);
            mYear = getArguments().getInt(ARG_YEAR);
            startTime = Calendar.getInstance();
            startTime.set(Calendar.DAY_OF_MONTH, mDay);
            startTime.set(Calendar.MONTH, mMonth);
            startTime.set(Calendar.YEAR, mYear);
            startTime.set(Calendar.HOUR_OF_DAY, 0);
            startTime.set(Calendar.MINUTE, 0);
            startTime.set(Calendar.SECOND, 0);

            endTime = (Calendar) startTime.clone();
            endTime.set(Calendar.HOUR_OF_DAY, 23);
            endTime.set(Calendar.MINUTE, 59);
            endTime.set(Calendar.SECOND, 59);
        }
        mRangeStartTime = startTime.getTimeInMillis();
        mRangeEndTime = endTime.getTimeInMillis();
    }

    @Override
    public void setupDateView() {
        mDate.setText(DateUtils.formatDateTime(getContext(),
                mRangeStartTime,
                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_WEEKDAY | DateUtils.FORMAT_ABBREV_MONTH));
    }

    @Override
    public void setupMapView() {
        mMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
    }

    @Override
    public void setupNavArrowIcons() {
        Calendar today = Calendar.getInstance();
        Calendar startTime = Calendar.getInstance();
        startTime.setTimeInMillis(mRangeStartTime);
        if (startTime.get(Calendar.DAY_OF_MONTH) == startTime.getActualMaximum(Calendar.DAY_OF_MONTH) ||
                (today.get(Calendar.DAY_OF_MONTH) == startTime.get(Calendar.DAY_OF_MONTH)
                && today.get(Calendar.MONTH) == startTime.get(Calendar.MONTH)
                && today.get(Calendar.YEAR) == startTime.get(Calendar.YEAR))) {
            mLeftArrowIcon.setVisibility(View.VISIBLE);
            mRightArrowIcon.setVisibility(View.GONE);
        } else if (mDay == 1) {
            mLeftArrowIcon.setVisibility(View.GONE);
            mRightArrowIcon.setVisibility(View.VISIBLE);
        } else {
            mLeftArrowIcon.setVisibility(View.VISIBLE);
            mRightArrowIcon.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onGoogleMapReady(GoogleMap googleMap) {
        googleMap.getUiSettings().setAllGesturesEnabled(false);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
        }
        if (mBoundingBoxPolygon != null && mBounds != null) {
            googleMap.addPolygon(mBoundingBoxPolygon);
            googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(mBounds, 12));
            googleMap.moveCamera(CameraUpdateFactory.zoomOut());
        } else {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(MADRID, 6));
        }
    }

    @Override
    public void showProgressDialog() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgressDialog() {
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void showGoogleFitErrorMessage() {
        Snackbar.make(mProgressBar.getRootView(),
                getString(R.string.exception_message_google_fit_query),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(R.string.retry),
                        v -> this.mFitnessPresenter.queryFitnessData(mRangeStartTime, mRangeEndTime))
                .show();
    }

    @Override
    public void bindActivityDetails(ActivityDetails activityDetails) {
        NumberFormat df = DecimalFormat.getInstance();
        df.setMaximumFractionDigits(2);

        mActivityDetails = activityDetails;

        if (mActivityDetails.getStepCountDelta() != null) {
            Integer steps = mActivityDetails.getStepCountDelta().getSteps();
            Double stepsGoal = Double.valueOf(getString(R.string.default_steps_goal));
            Integer stepsProgress = (int) (steps * 100 / stepsGoal);
            mStepsCounter.setText(steps.toString());
            mStepsProgress.setProgressDrawable(Utils.getFitnessProgressBarDrawable(getActivity(), stepsProgress));
            mStepsProgress.setProgress(stepsProgress > 100 ? 100 : stepsProgress);

        }

        if (mActivityDetails.getDistanceDelta() != null) {
            Float distance = mActivityDetails.getDistanceDelta().getDistance();
            Float distanceGoal = Float.valueOf(getString(R.string.default_distance_goal));
            Integer distanceProgress = (int) (distance * 100 / distanceGoal);
            mDistanceCounter.setText(df.format(distance));
            mDistanceProgress.setProgressDrawable(Utils.getFitnessProgressBarDrawable(getActivity(), distanceProgress));
            mDistanceProgress.setProgress(distanceProgress > 100 ? 100 : distanceProgress);
        }

        if (mActivityDetails.getCaloriesExpended() != null) {
            Float calories = mActivityDetails.getCaloriesExpended().getCalories();
            Float caloriesGoal = Float.valueOf(getString(R.string.default_calories_goal));
            Integer caloriesProgress = (int) (calories * 100 / caloriesGoal);
            mCaloriesCounter.setText(df.format(calories));
            mCaloriesProgress.setProgressDrawable(Utils.getFitnessProgressBarDrawable(getActivity(), caloriesProgress));
            mCaloriesProgress.setProgress(caloriesProgress > 100 ? 100 : caloriesProgress);

        }

        if (mActivityDetails.getActivitiesSummary() != null) {
            if (!mActivityDetails.getActivitiesSummary().isEmpty()) {
                mTimePager.setAdapter(new ActivityDurationPagerAdapter(this.getContext(), mActivityDetails.getActivitiesSummary()));
            } else {
                mTimePager.setAdapter(new ActivityDurationPagerAdapter(this.getContext(), Collections.singletonList(
                        new ActivitySummaryFit(FitnessActivities.UNKNOWN, 0, 0)
                )));
            }
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
            this.mFitnessPresenter.initGoogleMap(mMapFragment);
        }
    }

    @OnClick(R.id.locationDetailsBtn)
    public void openLocationDetailsActivity() {
        LocationDetailsActivity.launch(getActivity(), mRangeStartTime, mRangeEndTime);
    }

    @OnClick(R.id.activityDetailsBtn)
    public void openActivityDetailsActivity() {
        ActivityTimelineActivity.launch(getActivity(), mRangeStartTime, mRangeEndTime);
    }

    private void initializeInjector() {
        FitnessComponent fitnessComponent = DaggerFitnessComponent.builder()
                .applicationComponent(SmartCitizenApplication.get(getContext()).getApplicationComponent())
                .fitnessModule(new FitnessModule())
                .build();
        fitnessComponent.inject(this);
    }
}
