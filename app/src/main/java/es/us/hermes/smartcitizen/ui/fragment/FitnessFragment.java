package es.us.hermes.smartcitizen.ui.fragment;


import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.maps.android.SphericalUtil;
import com.viewpagerindicator.CirclePageIndicator;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.us.hermes.smartcitizen.Constants;
import es.us.hermes.smartcitizen.R;
import es.us.hermes.smartcitizen.data.api.google.fit.ActivityDetails;
import es.us.hermes.smartcitizen.di.components.MainComponent;
import es.us.hermes.smartcitizen.mvp.presenter.FitnessPresenter;
import es.us.hermes.smartcitizen.mvp.view.FitnessView;
import es.us.hermes.smartcitizen.ui.activity.ActivityTimelineActivity;
import es.us.hermes.smartcitizen.ui.activity.LocationDetailsActivity;
import es.us.hermes.smartcitizen.ui.adapter.ActivityDurationPagerAdapter;
import es.us.hermes.smartcitizen.utils.Utils;

public class FitnessFragment extends BaseFragment implements FitnessView {

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

    @Inject
    FitnessPresenter mFitnessPresenter;

    private GoogleMap mMap;
    private PolygonOptions mBoundingBoxPolygon;
    private LatLngBounds mBounds;
    private ActivityDetails mActivityDetails;
    private Snackbar mPermissionsSnackbar;

    public static FitnessFragment newInstance() {
        FitnessFragment fragment = new FitnessFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public FitnessFragment() {
        setRetainInstance(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getComponent(MainComponent.class).inject(this);
        this.mFitnessPresenter.setView(this);
        this.mFitnessPresenter.onCreate();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View fragmentView = inflater.inflate(R.layout.fragment_fitness, container, false);
        ButterKnife.bind(this, fragmentView);
        this.mFitnessPresenter.onCreateView();
        return fragmentView;
    }

    @Override
    public void onPause() {
        super.onPause();
        this.mFitnessPresenter.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        this.mFitnessPresenter.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void setupDateView() {
        mDate.setText(DateUtils.formatDateTime(getContext(),
                System.currentTimeMillis(),
                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_WEEKDAY | DateUtils.FORMAT_ABBREV_MONTH));
    }

    @Override
    public void setupMapView() {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        this.mFitnessPresenter.initGoogleMap(mapFragment);
    }

    @Override
    public void onGoogleMapReady(GoogleMap googleMap) {
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
                        v -> this.mFitnessPresenter.queryGoogleFit(Constants.RANGE_DAY))
                .show();
    }

    @Override
    public void showAppPermissionsRequiredErrorMessage() {
        mPermissionsSnackbar = Snackbar.make(mProgressBar.getRootView(),
                getString(R.string.exception_message_permissions_required),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(R.string.retry),
                        v -> this.mFitnessPresenter.requestAppPermissions());
        mPermissionsSnackbar.show();
    }

    @Override
    public void hideAppPermissionsRequiredErrorMessage(){
        if(mPermissionsSnackbar != null && mPermissionsSnackbar.isShown()){
            mPermissionsSnackbar.dismiss();
        }
    }

    @Override
    public void bindActivityDetails(ActivityDetails activityDetails) {
        NumberFormat df = DecimalFormat.getInstance();
        df.setMaximumFractionDigits(2);

        mActivityDetails = activityDetails;

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

        if (mActivityDetails.getActivitiesSummary() != null && !mActivityDetails.getActivitiesSummary().isEmpty()) {
            mTimePager.setAdapter(new ActivityDurationPagerAdapter(this.getContext(), mActivityDetails.getActivitiesSummary()));
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
    }

    @OnClick(R.id.locationDetailsBtn)
    public void openLocationDetailsActivity() {
        LocationDetailsActivity.launch(getActivity());
    }

    @OnClick(R.id.activityDetailsBtn)
    public void openActivityDetailsActivity() {
        ActivityTimelineActivity.launch(getActivity());
    }

}
