package us.idinfor.smartcitizen.mvp.presenter;

import javax.inject.Inject;

import rx.Subscription;
import rx.subscriptions.Subscriptions;
import us.idinfor.smartcitizen.Constants;
import us.idinfor.smartcitizen.interactor.ActivityTimelineInteractor;
import us.idinfor.smartcitizen.mvp.view.ActivityTimelineView;
import us.idinfor.smartcitizen.mvp.view.View;

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
    public void onResume() {
        this.queryGoogleFit(Constants.RANGE_DAY);
    }

    @Override
    public void queryGoogleFit(int timeRange) {
        this.mSubscription.unsubscribe();
        this.mActivityTimelineView.showProgressDialog();
        this.mSubscription = this.mActivityTimelineInteractor.getGoogleFitQueryResponse(timeRange)
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