package us.idinfor.smartcitizen.mvp.view;

import com.google.android.gms.maps.GoogleMap;

import us.idinfor.smartcitizen.data.api.google.fit.ActivityDetails;

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