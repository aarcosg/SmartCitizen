package es.us.hermes.smartcitizen.mvp.presenter;


import com.google.android.gms.maps.SupportMapFragment;

public interface FitnessPresenter extends Presenter {

    void onCreateView();
    void onResume();
    void initGoogleMap(SupportMapFragment mapFragment);
    void queryGoogleFit(int timeRange);

}
