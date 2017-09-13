package es.us.hermes.smartcitizen.mvp.presenter;

import javax.inject.Inject;

import es.us.hermes.smartcitizen.interactor.ActivityTimelineInteractor;
import es.us.hermes.smartcitizen.mvp.view.ActivityTimelineView;
import es.us.hermes.smartcitizen.mvp.view.View;
import rx.Subscription;
import rx.subscriptions.Subscriptions;

public class ActivityTimelinePresenterImpl implements ActivityTimelinePresenter{

    private ActivityTimelineView mActivityTimelineView;
    private final ActivityTimelineInteractor mActivityTimelineInteractor;
    private Subscription mSubscription = Subscriptions.empty();

    @Inject
    public ActivityTimelinePresenterImpl(ActivityTimelineInteractor activityTimelineInteractor){
        this.mActivityTimelineInteractor = activityTimelineInteractor;
    }

    @Override
    public void setView(View v) {
        mActivityTimelineView = (ActivityTimelineView) v;
    }

    @Override
    public void onCreateView() {
        this.mActivityTimelineView.setTimeRange();
        this.mActivityTimelineView.setupAdapter();
        this.mActivityTimelineView.setupRecyclerView();
    }

    @Override
    public void onPause() {
        if(!this.mSubscription.isUnsubscribed()){
            this.mSubscription.unsubscribe();
        }
    }

    @Override
    public void queryFitnessData(long statTime, long endTime) {
        this.mSubscription.unsubscribe();
        this.mActivityTimelineView.showProgressDialog();
        this.mSubscription = this.mActivityTimelineInteractor.getGoogleFitQueryResponse(statTime, endTime)
                .subscribe(
                        activityDetailsList -> this.mActivityTimelineView.bindActivitiesList(activityDetailsList),
                        throwable -> {
                            this.mActivityTimelineView.showGoogleFitErrorMessage();
                            this.mActivityTimelineView.hideProgressDialog();
                        },
                        () -> this.mActivityTimelineView.hideProgressDialog()
                );
    }
}