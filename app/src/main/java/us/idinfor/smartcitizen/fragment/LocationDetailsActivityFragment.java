package us.idinfor.smartcitizen.fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

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
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.heatmaps.HeatmapTileProvider;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import us.idinfor.smartcitizen.Constants;
import us.idinfor.smartcitizen.R;
import us.idinfor.smartcitizen.event.FitDataSetsResultEvent;
import us.idinfor.smartcitizen.event.GoogleApiClientConnectedEvent;
import us.idinfor.smartcitizen.event.TimeRangeSelectedEvent;

public class LocationDetailsActivityFragment extends BaseFragment implements OnMapReadyCallback {

    private static final String TAG = LocationDetailsActivityFragment.class.getCanonicalName();

    @Bind(R.id.progressBar)
    ProgressBar mProgressBar;

    private GoogleMap mMap;
    private ArrayList<LatLng> locations;
    private HeatmapTileProvider mProvider;
    private TileOverlay mOverlay;

    public LocationDetailsActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_location_details, container, false);
        if (mMap == null) {
            SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            mMap.setMyLocationEnabled(true);
        }
    }

    protected DataReadRequest.Builder buildFitQuery(){
        DataReadRequest.Builder builder = new DataReadRequest.Builder()
                .read(DataType.TYPE_LOCATION_SAMPLE);
        return builder;
    }

    protected void queryGoogleFit(int timeRange) {
       /* mProgressBar.setVisibility(View.VISIBLE);
        fitHelper.queryFitnessData(
                Utils.getStartTimeRange(timeRange),
                new Date().getTime(),
                buildFitQuery(),
                GoogleFitApi.QUERY_DEFAULT);*/
    }

    @Subscribe
    public void onEvent(TimeRangeSelectedEvent event){
        queryGoogleFit(event.getTimeRange());
    }

    @Subscribe
    public void onEvent(GoogleApiClientConnectedEvent event){
        queryGoogleFit(Constants.RANGE_DAY);
    }

    @Subscribe
    public void onEvent(FitDataSetsResultEvent event){
        for (DataSet dataSet : event.getDataSets()) {
            if (dataSet.getDataType().equals(DataType.TYPE_LOCATION_SAMPLE)) {
                if(locations == null){
                    locations = new ArrayList<LatLng>();
                }
                for (DataPoint dp : dataSet.getDataPoints()) {
                    if (dp.getDataType().equals(DataType.TYPE_LOCATION_SAMPLE)) {
                        LatLng point = new LatLng(
                                dp.getValue(Field.FIELD_LATITUDE).asFloat(),
                                dp.getValue(Field.FIELD_LONGITUDE).asFloat());
                        locations.add(point);
                    }
                }
            }
        }
        updateUI();
    }

    private void updateUI(){
        if(mMap != null && locations != null && !locations.isEmpty()){
            if(mProvider == null){
                mProvider = new HeatmapTileProvider.Builder().data(locations).build();
                mOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
            }else{
                mProvider.setData(locations);
                mOverlay.clearTileCache();
            }
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locations.get(0), 10));
            locations.clear();
        }

        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    protected void injectActivityComponent() {
        getBaseActivity().getActivityComponent().inject(this);
    }
}
