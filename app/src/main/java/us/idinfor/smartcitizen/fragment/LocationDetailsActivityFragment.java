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

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import us.idinfor.smartcitizen.Constants;
import us.idinfor.smartcitizen.R;
import us.idinfor.smartcitizen.Utils;

public class LocationDetailsActivityFragment extends BaseGoogleFitFragment implements OnMapReadyCallback {

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

    @Override
    public void onConnected(Bundle bundle) {
        super.onConnected(bundle);

        launchQuery(Utils.getStartTimeRange(Constants.RANGE_DAY), new Date().getTime());

    }

    @Override
    protected DataReadRequest queryFitnessData(long startTime, long endTime) {
        DataReadRequest.Builder readRequestBuilder;

        readRequestBuilder = new DataReadRequest.Builder()
                .read(DataType.TYPE_LOCATION_SAMPLE);

        DataReadRequest readRequest = readRequestBuilder
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build();

        return readRequest;
    }

    @Override
    protected void dumpDataSet(DataSet dataSet) {
        super.dumpDataSet(dataSet);
        //mProgressBar.setVisibility(View.VISIBLE);
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
       // mProgressBar.setVisibility(View.GONE);
    }

    public void updateMapData(int timeRange){
        launchQuery(Utils.getStartTimeRange(timeRange), new Date().getTime());
    }

}
