package es.us.hermes.smartcitizen.mvp.view;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public interface LocationDetailsView extends View {

    void setupMapView();
    void onGoogleMapReady(GoogleMap googleMap);
    void showProgressDialog();
    void hideProgressDialog();
    void setupToolbar();
    void bindLocations(List<LatLng> pointList);
    void showGoogleFitErrorMessage();

}