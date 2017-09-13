package es.us.hermes.smartcitizen.ui.fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.heatmaps.HeatmapTileProvider;

import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnItemSelected;
import es.us.hermes.smartcitizen.R;
import es.us.hermes.smartcitizen.di.components.LocationDetailsComponent;
import es.us.hermes.smartcitizen.mvp.presenter.LocationDetailsPresenter;
import es.us.hermes.smartcitizen.mvp.view.LocationDetailsView;
import es.us.hermes.smartcitizen.ui.activity.LocationDetailsActivity;

public class LocationDetailsFragment extends BaseFragment implements LocationDetailsView {

    private static final String TAG = LocationDetailsFragment.class.getCanonicalName();
    private static final String ARG_RANGE_START_TIME = "ARG_RANGE_START_TIME";
    private static final String ARG_RANGE_END_TIME = "ARG_RANGE_END_TIME";
    private static final int RANGE_DAY = 0;
    private static final int RANGE_WEEK = 1;
    private static final int RANGE_MONTH = 2;

    @Inject
    LocationDetailsPresenter mLocationDetailsPresenter;
    @Inject
    Tracker mTracker;

    @Bind(R.id.toolbarSpinner)
    AppCompatSpinner mSpinner;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.progressBar)
    ProgressBar mProgressBar;

    private long mDayRangeStartTime;
    private long mDayRangeEndTime;
    private long mWeekRangeStartTime;
    private long mWeekRangeEndTime;
    private long mMonthRangeStartTime;
    private long mMonthRangeEndTime;
    private GoogleMap mMap;
    private HeatmapTileProvider mProvider;
    private TileOverlay mOverlay;

    public static LocationDetailsFragment newInstance(long rangeStartTime, long rangeEndTime) {
        LocationDetailsFragment fragment = new LocationDetailsFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_RANGE_START_TIME, rangeStartTime);
        args.putLong(ARG_RANGE_END_TIME, rangeEndTime);
        fragment.setArguments(args);
        return fragment;
    }


    public LocationDetailsFragment() {
        setRetainInstance(true);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getComponent(LocationDetailsComponent.class).inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        final View fragmentView = inflater.inflate(R.layout.fragment_location_details, container, false);
         ButterKnife.bind(this, fragmentView);
        this.mLocationDetailsPresenter.setView(this);
        this.mLocationDetailsPresenter.onCreateView();
        return fragmentView;
    }

    @Override
    public void onPause() {
        super.onPause();
        this.mLocationDetailsPresenter.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mTracker.setScreenName(LocationDetailsFragment.class.getName());
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        onTimeRangeSelected(mSpinner.getSelectedItemPosition());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void setTimeRange() {
        if (getArguments() != null && !getArguments().isEmpty()) {
            mDayRangeStartTime = getArguments().getLong(ARG_RANGE_START_TIME);
            mDayRangeEndTime = getArguments().getLong(ARG_RANGE_END_TIME);

            Calendar startTime = Calendar.getInstance();
            startTime.setFirstDayOfWeek(Calendar.MONDAY);
            startTime.setTimeInMillis(mDayRangeStartTime);
            Calendar endTime = Calendar.getInstance();
            endTime.setFirstDayOfWeek(Calendar.MONDAY);
            endTime.setTimeInMillis(mDayRangeEndTime);

            startTime.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
            mWeekRangeStartTime = startTime.getTimeInMillis();
            endTime.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
            mWeekRangeEndTime = endTime.getTimeInMillis();

            startTime.set(Calendar.DAY_OF_MONTH, 1);
            mMonthRangeStartTime = startTime.getTimeInMillis();
            endTime.set(Calendar.DAY_OF_MONTH, endTime.getActualMaximum(Calendar.DAY_OF_MONTH));
            mMonthRangeEndTime = endTime.getTimeInMillis();
        }
    }

    @Override
    public void setupSpinner(){
        String dayFormatted = DateUtils.formatDateTime(getContext(),
                mDayRangeStartTime,
                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_WEEKDAY | DateUtils.FORMAT_ABBREV_MONTH);

        String weekFormatted = DateUtils.formatDateRange(getContext(),
                mWeekRangeStartTime,
                mWeekRangeEndTime,
                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_WEEKDAY | DateUtils.FORMAT_ABBREV_MONTH);

        String monthFormatted = DateUtils.formatDateRange(getContext(),
                mMonthRangeStartTime,
                mMonthRangeEndTime,
                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_WEEKDAY | DateUtils.FORMAT_ABBREV_MONTH);

        String spinnerEntries[] = {dayFormatted, weekFormatted, monthFormatted};

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getContext(), R.layout.spinner_item, spinnerEntries);
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        mSpinner.setAdapter(spinnerArrayAdapter);
    }

    @Override
    public void setupToolbar() {
        ((LocationDetailsActivity)getActivity()).setSupportActionBar(mToolbar);
        ActionBar actionBar = ((LocationDetailsActivity)getActivity()).getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void setupMapView() {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        this.mLocationDetailsPresenter.initGoogleMap(mapFragment);
    }

    @Override
    public void onGoogleMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public void showGoogleFitErrorMessage() {
        Snackbar.make(mProgressBar.getRootView(),
                getString(R.string.exception_message_google_fit_query),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(R.string.retry),
                        v -> onTimeRangeSelected(mSpinner.getSelectedItemPosition()))
                .show();
    }

    @Override
    public void bindLocations(List<LatLng> locations) {
        if(mMap != null && locations != null && !locations.isEmpty()){
            if(mProvider == null){
                mProvider = new HeatmapTileProvider.Builder().data(locations).build();
                mOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
            }else{
                mProvider.setData(locations);
                mOverlay.clearTileCache();
            }
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locations.get(0), 10));
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


    @OnItemSelected(R.id.toolbarSpinner)
    public void onTimeRangeSelected(int position){
        switch (position){
            case RANGE_DAY:
                this.mLocationDetailsPresenter.queryFitnessData(mDayRangeStartTime, mDayRangeEndTime);
                break;
            case RANGE_WEEK:
                this.mLocationDetailsPresenter.queryFitnessData(mWeekRangeStartTime, mWeekRangeEndTime);
                break;
            case RANGE_MONTH:
                this.mLocationDetailsPresenter.queryFitnessData(mMonthRangeStartTime, mMonthRangeEndTime);
                break;
        }

    }

}
