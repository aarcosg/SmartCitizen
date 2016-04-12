package us.idinfor.smartcitizen.mvp.presenter;

import com.google.android.gms.maps.SupportMapFragment;
import com.sdoward.rxgooglemap.MapObservableProvider;

import javax.inject.Inject;

import rx.Subscription;
import rx.subscriptions.Subscriptions;
import us.idinfor.smartcitizen.Constants;
import us.idinfor.smartcitizen.interactor.FitnessInteractor;
import us.idinfor.smartcitizen.mvp.view.FitnessView;
import us.idinfor.smartcitizen.mvp.view.View;

public class FitnessPresenterImpl implements FitnessPresenter{

    private FitnessView mFitnessView;
    private final FitnessInteractor mFitnessInteractor;
    private Subscription mGoogleMapSuscription = Subscriptions.empty();
    private Subscription mGoogleFitSuscription = Subscriptions.empty();

    @Inject
    public FitnessPresenterImpl(FitnessInteractor fitnessInteractor){
        this.mFitnessInteractor = fitnessInteractor;
    }

    @Override
    public void setView(View v) {
        mFitnessView = (FitnessView) v;
    }

    @Override
    public void onCreate() {
        this.mFitnessInteractor.initGoogleFitApi();
    }

    @Override
    public void onViewCreated() {
        this.mFitnessView.setupDateView();
        this.mFitnessView.setupMapView();
    }

    @Override
    public void onPause() {
        if(!this.mGoogleMapSuscription.isUnsubscribed()){
            this.mGoogleMapSuscription.unsubscribe();
        }
        if(!this.mGoogleFitSuscription.isUnsubscribed()){
            this.mGoogleFitSuscription.unsubscribe();
        }
    }

    @Override
    public void onResume() {
        this.queryGoogleFit(Constants.RANGE_DAY);
    }

    @Override
    public void initGoogleMap(SupportMapFragment mapFragment) {
        MapObservableProvider mapObservableProvider = new MapObservableProvider(mapFragment);
        this.mGoogleMapSuscription = mapObservableProvider.getMapReadyObservable()
                .subscribe(googleMap -> this.mFitnessView.onGoogleMapReady(googleMap));
    }

    @Override
    public void queryGoogleFit(int timeRange){
        this.mGoogleFitSuscription.unsubscribe();
        this.mFitnessView.showProgressDialog();
        this.mGoogleFitSuscription = this.mFitnessInteractor.getGoogleFitQueryResponse(timeRange)
            .subscribe(
                activityDetails -> this.mFitnessView.bindActivityDetails(activityDetails),
                throwable -> {
                    handleException(throwable);
                    this.mFitnessView.hideProgressDialog();
                },
                () -> this.mFitnessView.hideProgressDialog()
            );
    }

    private void handleException(Throwable throwable) {
        if(throwable instanceof SecurityException){
            this.mFitnessView.requestMandatoryAppPermissions();
            this.mFitnessView.showAppPermissionsRequiredErrorMessage();
        }else{
            this.mFitnessView.showGoogleFitErrorMessage();
        }
    }

}