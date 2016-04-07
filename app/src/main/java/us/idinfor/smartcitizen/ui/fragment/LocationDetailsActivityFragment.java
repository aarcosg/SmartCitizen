package us.idinfor.smartcitizen.ui.fragment;

import android.os.Bundle;
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
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.sdoward.rxgooglemap.MapObservableProvider;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import us.idinfor.smartcitizen.R;
import us.idinfor.smartcitizen.Utils;

public class LocationDetailsActivityFragment extends BaseFragment {

    private static final String TAG = LocationDetailsActivityFragment.class.getCanonicalName();

    @Bind(R.id.progressBar)
    ProgressBar mProgressBar;

    private GoogleMap mMap;
    private ArrayList<LatLng> locations;
    private HeatmapTileProvider mProvider;
    private TileOverlay mOverlay;

    private SupportMapFragment mMapFragment;
    private MapObservableProvider mMapObservableProvider;

    public LocationDetailsActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_location_details, container, false);
        ButterKnife.bind(this, view);
        initMapView();
        /*subscribeToFragment(((LocationDetailsActivity)getActivity()).getTimeRange()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(timeRange -> queryGoogleFit(timeRange))
        );*/
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        //queryGoogleFit(Constants.RANGE_DAY);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    private void initMapView() {
        mMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mMapObservableProvider = new MapObservableProvider(mMapFragment);
        /*subscribeToFragment(mMapObservableProvider.getMapReadyObservable()
                .subscribe(googleMap -> {
                    mMap = googleMap;
                    if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                        mMap.setMyLocationEnabled(true);
                    }
                })
        );*/
    }

    protected DataReadRequest.Builder buildFitDataReadRequest(){
        DataReadRequest.Builder builder = new DataReadRequest.Builder()
                .read(DataType.TYPE_LOCATION_SAMPLE);
        return builder;
    }

    protected void queryGoogleFit(int timeRange) {
        mProgressBar.setVisibility(View.VISIBLE);

        DataReadRequest.Builder dataReadRequestBuilder = buildFitDataReadRequest();

        dataReadRequestBuilder.setTimeRange(Utils.getStartTimeRange(timeRange),new Date().getTime(), TimeUnit.MILLISECONDS);

        DataReadRequest dataReadRequest = dataReadRequestBuilder.build();
        DataReadRequest dataReadRequestServer = dataReadRequestBuilder.enableServerQueries().build();

        /*subscribeToFragment(RxFit.History.read(dataReadRequestServer)
                .doOnError(throwable -> {
                    if(throwable instanceof StatusException && ((StatusException)throwable).getStatus().getStatusCode() == CommonStatusCodes.TIMEOUT) {
                        Log.e(TAG, "Timeout on server query request");
                    }
                })
                .compose(new RxFit.OnExceptionResumeNext.Single<>(RxFit.History.read(dataReadRequest)))
                .flatMapObservable(dataReadResult -> Observable.from(dataReadResult.getDataSets()))
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(dataSet -> {
                    processFitDataSet(dataSet);
                }, e -> {
                    mProgressBar.setVisibility(View.GONE);
                    Log.e(TAG, "Error reading fitness data", e);
                    Snackbar.make(mProgressBar.getRootView(), "Error getting Fit data", Snackbar.LENGTH_LONG)
                            .setAction("Retry", v -> queryGoogleFit(timeRange))
                            .show();

                }, () -> {
                    mProgressBar.setVisibility(View.GONE);
                    updateUI();
                })
        );*/
    }

    private void processFitDataSet(DataSet dataSet) {
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

   /* @Subscribe
    public void onEvent(TimeRangeSelectedEvent event){
        queryGoogleFit(event.getTimeRange());
    }
*/

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
    }

    /*@Override
    protected void injectActivityComponent() {
        getBaseActivity().getActivityComponent().inject(this);
    }*/
}
