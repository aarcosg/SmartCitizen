package es.us.hermes.smartcitizen.mvp.view;

import com.google.android.gms.maps.GoogleMap;

import es.us.hermes.smartcitizen.mvp.model.ActivityDetails;

public interface FitnessView extends View {

    void showProgressDialog();
    void hideProgressDialog();
    void setupDateView();
    void setupMapView();
    void onGoogleMapReady(GoogleMap googleMap);
    void bindActivityDetails(ActivityDetails activityDetails);
    void showGoogleFitErrorMessage();
    void setTimeRange();
    void setupNavArrowIcons();

}