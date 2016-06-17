package es.us.hermes.smartcitizen.mvp.presenter;


import com.google.android.gms.maps.SupportMapFragment;

public interface LocationDetailsPresenter extends Presenter {

    void onCreateView();
    void initGoogleMap(SupportMapFragment mMapFragment);
    void queryGoogleFit(int timeRange);

}
