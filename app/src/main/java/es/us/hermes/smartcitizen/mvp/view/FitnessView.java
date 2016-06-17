package es.us.hermes.smartcitizen.mvp.view;

import com.google.android.gms.maps.GoogleMap;

import es.us.hermes.smartcitizen.data.api.google.fit.ActivityDetails;

public interface FitnessView extends View {

    void showProgressDialog();
    void hideProgressDialog();
    void setupDateView();
    void setupMapView();
    void onGoogleMapReady(GoogleMap googleMap);
    void showAppPermissionsRequiredErrorMessage();
    void hideAppPermissionsRequiredErrorMessage();
    void bindActivityDetails(ActivityDetails activityDetails);
    void showGoogleFitErrorMessage();
}