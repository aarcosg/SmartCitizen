package us.idinfor.smartcitizen.fragment;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.heatmaps.HeatmapTileProvider;

import java.util.ArrayList;
import java.util.List;

import us.idinfor.smartcitizen.Constants;
import us.idinfor.smartcitizen.R;
import us.idinfor.smartcitizen.Utils;
import us.idinfor.smartcitizen.asynctask.LoadLocationsAsyncTask;
import us.idinfor.smartcitizen.backend.contextApi.model.Context;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LocationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LocationFragment extends Fragment implements OnMapReadyCallback {

    private static final String TAG = LocationFragment.class.getCanonicalName();

    private SharedPreferences prefs;
    private GoogleMap mMap;
    private ArrayList<LatLng> locations;
    private HeatmapTileProvider mProvider;
    private TileOverlay mOverlay;

    public static LocationFragment newInstance() {
        LocationFragment fragment = new LocationFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public LocationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = Utils.getSharedPreferences(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_location, container, false);
        if(mMap == null){
            SupportMapFragment mapFragment = (SupportMapFragment)getChildFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }

        return view;
    }


    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        LatLng lastLocation = new LatLng(
                prefs.getFloat(Constants.PROPERTY_LAST_LATITUDE,(float)Constants.DEFAULT_LATITUDE),
                prefs.getFloat(Constants.PROPERTY_LAST_LONGITUDE,(float)Constants.DEFAULT_LONGITUDE));

        mMap.setMyLocationEnabled(true);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastLocation, 13));

        new LoadLocationsAsyncTask(prefs.getLong(Constants.PROPERTY_DEVICE_ID,0L)){
            @Override
            protected void onPostExecute(List<Context> contexts) {
                super.onPostExecute(contexts);
                if(contexts != null && !contexts.isEmpty()){
                    locations = new ArrayList<LatLng>();
                    for(Context c : contexts){
                        locations.add(new LatLng(c.getLocation().getLatitude(),c.getLocation().getLongitude()));
                    }
                    if(mProvider == null){
                        mProvider = new HeatmapTileProvider.Builder().data(locations).build();
                        mOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
                    }else{
                        mProvider.setData(locations);
                        mOverlay.clearTileCache();
                    }
                }
            }
        }.execute();

    }
}
