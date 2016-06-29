package es.us.hermes.smartcitizen.mvp.presenter;

import android.util.Log;

import com.google.android.gms.maps.SupportMapFragment;
import com.sdoward.rxgooglemap.MapObservableProvider;

import javax.inject.Inject;

import es.us.hermes.smartcitizen.Constants;
import es.us.hermes.smartcitizen.interactor.FitnessInteractor;
import es.us.hermes.smartcitizen.mvp.view.FitnessView;
import es.us.hermes.smartcitizen.mvp.view.View;
import rx.Subscription;
import rx.subscriptions.Subscriptions;

public class FitnessPresenterImpl implements FitnessPresenter{

    private static final String TAG = FitnessPresenterImpl.class.getCanonicalName();

    private FitnessView mFitnessView;
    private final FitnessInteractor mFitnessInteractor;
    private Subscription mGoogleMapSubscription = Subscriptions.empty();
    private Subscription mGoogleFitSubscription = Subscriptions.empty();
    private Subscription mAppPermissionsSubscription = Subscriptions.empty();

    @Inject
    public FitnessPresenterImpl(FitnessInteractor fitnessInteractor){
        this.mFitnessInteractor = fitnessInteractor;
    }

    @Override
    public void setView(View v) {
        mFitnessView = (FitnessView) v;
    }

    @Override
    public void onCreateView() {
        this.mFitnessView.setupDateView();
        this.mFitnessView.setupMapView();
    }

    @Override
    public void onPause() {
        if(!this.mGoogleMapSubscription.isUnsubscribed()){
            this.mGoogleMapSubscription.unsubscribe();
        }
        if(!this.mGoogleFitSubscription.isUnsubscribed()){
            this.mGoogleFitSubscription.unsubscribe();
        }
        if(!this.mAppPermissionsSubscription.isUnsubscribed()){
            this.mAppPermissionsSubscription.unsubscribe();
        }
    }

    @Override
    public void onResume() {
        this.mFitnessInteractor.initGoogleFitApi();
        this.mFitnessInteractor.subscribeUserToGoogleFit();
        this.queryGoogleFit(Constants.RANGE_DAY);
    }

    @Override
    public void initGoogleMap(SupportMapFragment mapFragment) {
        MapObservableProvider mapObservableProvider = new MapObservableProvider(mapFragment);
        this.mGoogleMapSubscription = mapObservableProvider.getMapReadyObservable()
                .subscribe(googleMap -> this.mFitnessView.onGoogleMapReady(googleMap));
    }

    @Override
    public void queryGoogleFit(int timeRange){
        this.mGoogleFitSubscription.unsubscribe();
        this.mFitnessView.showProgressDialog();
        this.mGoogleFitSubscription = this.mFitnessInteractor.getGoogleFitQueryResponse(timeRange)
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
            Log.w(TAG,"App permissions required");
        }else{
            this.mFitnessView.showGoogleFitErrorMessage();
        }
    }

}