package es.us.hermes.smartcitizen.mvp.presenter;

import android.util.Log;

import com.google.android.gms.maps.SupportMapFragment;
import com.sdoward.rxgooglemap.MapObservableProvider;

import javax.inject.Inject;

import es.us.hermes.smartcitizen.interactor.FitnessInteractor;
import es.us.hermes.smartcitizen.mvp.view.FitnessView;
import es.us.hermes.smartcitizen.mvp.view.View;
import rx.subscriptions.CompositeSubscription;

public class FitnessPresenterImpl implements FitnessPresenter{

    private static final String TAG = FitnessPresenterImpl.class.getCanonicalName();

    private FitnessView mFitnessView;
    private final FitnessInteractor mFitnessInteractor;
    private CompositeSubscription mSubscriptions =  new CompositeSubscription();

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
        this.mFitnessView.setTimeRange();
        this.mFitnessView.setupDateView();
        this.mFitnessView.setupMapView();
        this.mFitnessView.setupNavArrowIcons();
    }

    @Override
    public void onPause() {
        if(!this.mSubscriptions.isUnsubscribed()){
            this.mSubscriptions.unsubscribe();
        }
    }

    @Override
    public void onResume() {
        mSubscriptions = new CompositeSubscription();
        this.mFitnessInteractor.initGoogleFitApi();
        this.mFitnessInteractor.subscribeUserToGoogleFit();
    }

    @Override
    public void initGoogleMap(SupportMapFragment mapFragment) {
        MapObservableProvider mapObservableProvider = new MapObservableProvider(mapFragment);
        this.mSubscriptions.add(mapObservableProvider.getMapReadyObservable()
                .subscribe(googleMap -> this.mFitnessView.onGoogleMapReady(googleMap)));
    }

    @Override
    public void queryFitnessData(long statTime, long endTime){
        this.mFitnessView.showProgressDialog();
        this.mSubscriptions.add(this.mFitnessInteractor.getGoogleFitQueryResponse(statTime, endTime)
                .subscribe(
                        activityDetails -> this.mFitnessView.bindActivityDetails(activityDetails),
                        throwable -> {
                            handleException(throwable);
                            this.mFitnessView.hideProgressDialog();
                        },
                        () -> this.mFitnessView.hideProgressDialog()
                ));
    }

    private void handleException(Throwable throwable) {
        if(throwable instanceof SecurityException){
            Log.w(TAG,"App permissions required");
        }else{
            this.mFitnessView.showGoogleFitErrorMessage();
        }
    }

}