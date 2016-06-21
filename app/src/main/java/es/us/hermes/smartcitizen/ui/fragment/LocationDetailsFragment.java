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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.heatmaps.HeatmapTileProvider;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnItemSelected;
import es.us.hermes.smartcitizen.Constants;
import es.us.hermes.smartcitizen.R;
import es.us.hermes.smartcitizen.di.components.LocationDetailsComponent;
import es.us.hermes.smartcitizen.mvp.presenter.LocationDetailsPresenter;
import es.us.hermes.smartcitizen.mvp.view.LocationDetailsView;
import es.us.hermes.smartcitizen.ui.activity.LocationDetailsActivity;

public class LocationDetailsFragment extends BaseFragment implements LocationDetailsView {

    private static final String TAG = LocationDetailsFragment.class.getCanonicalName();

    @Inject
    LocationDetailsPresenter mLocationDetailsPresenter;

    @Bind(R.id.toolbarSpinner)
    AppCompatSpinner mToolbarSpinner;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.progressBar)
    ProgressBar mProgressBar;


    private GoogleMap mMap;
    private HeatmapTileProvider mProvider;
    private TileOverlay mOverlay;

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
        this.mLocationDetailsPresenter.queryGoogleFit(mToolbarSpinner.getSelectedItemPosition());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
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
                        v -> this.mLocationDetailsPresenter.queryGoogleFit(Constants.RANGE_DAY))
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
    public void setTimeRange(int position){
        this.mLocationDetailsPresenter.queryGoogleFit(position);
    }
}
