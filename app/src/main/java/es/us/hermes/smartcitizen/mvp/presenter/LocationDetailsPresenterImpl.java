package es.us.hermes.smartcitizen.mvp.presenter;


import com.google.android.gms.maps.SupportMapFragment;
import com.sdoward.rxgooglemap.MapObservableProvider;

import javax.inject.Inject;

import rx.Subscription;
import rx.subscriptions.Subscriptions;
import es.us.hermes.smartcitizen.interactor.LocationDetailsInteractor;
import es.us.hermes.smartcitizen.mvp.view.LocationDetailsView;
import es.us.hermes.smartcitizen.mvp.view.View;

public class LocationDetailsPresenterImpl implements LocationDetailsPresenter {

    private LocationDetailsView mLocationDetailsView;
    private final LocationDetailsInteractor mLocationDetailsInteractor;
    private Subscription mGoogleMapSubscription = Subscriptions.empty();
    private Subscription mGoogleFitSubscription = Subscriptions.empty();

    @Inject
    public LocationDetailsPresenterImpl(LocationDetailsInteractor locationDetailsInteractor){
        this.mLocationDetailsInteractor = locationDetailsInteractor;
    }

    @Override
    public void setView(View v) {
        mLocationDetailsView = (LocationDetailsView) v;
    }

    @Override
    public void onCreateView() {
        this.mLocationDetailsView.setTimeRange();
        this.mLocationDetailsView.setupToolbar();
        this.mLocationDetailsView.setupMapView();
        this.mLocationDetailsView.setupSpinner();
    }

    @Override
    public void onPause() {
        if(!this.mGoogleMapSubscription.isUnsubscribed()){
            this.mGoogleMapSubscription.unsubscribe();
        }
        if(!this.mGoogleFitSubscription.isUnsubscribed()){
            this.mGoogleFitSubscription.unsubscribe();
        }
    }

    @Override
    public void initGoogleMap(SupportMapFragment mapFragment) {
        MapObservableProvider mapObservableProvider = new MapObservableProvider(mapFragment);
        this.mGoogleMapSubscription = mapObservableProvider.getMapReadyObservable()
            .subscribe(googleMap -> this.mLocationDetailsView.onGoogleMapReady(googleMap));
    }

    @Override
    public void queryFitnessData(long statTime, long endTime) {
        this.mGoogleFitSubscription.unsubscribe();
        this.mLocationDetailsView.showProgressDialog();
        this.mGoogleFitSubscription = this.mLocationDetailsInteractor.getGoogleFitQueryResponse(statTime, endTime)
            .subscribe(
                locations -> this.mLocationDetailsView.bindLocations(locations),
                throwable -> {
                    this.mLocationDetailsView.showGoogleFitErrorMessage();
                    this.mLocationDetailsView.hideProgressDialog();
                },
                () -> this.mLocationDetailsView.hideProgressDialog()
            );
    }

}
