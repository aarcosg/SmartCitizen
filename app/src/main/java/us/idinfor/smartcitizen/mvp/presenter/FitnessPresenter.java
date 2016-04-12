package us.idinfor.smartcitizen.mvp.presenter;


import com.google.android.gms.maps.SupportMapFragment;

public interface FitnessPresenter extends Presenter {

    void onCreate();
    void onViewCreated();
    void onResume();
    void initGoogleMap(SupportMapFragment mapFragment);
    void queryGoogleFit(int timeRange);
}
