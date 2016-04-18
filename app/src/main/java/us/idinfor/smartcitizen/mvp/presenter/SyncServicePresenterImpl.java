package us.idinfor.smartcitizen.mvp.presenter;

import javax.inject.Inject;

import rx.subscriptions.CompositeSubscription;
import us.idinfor.smartcitizen.data.api.hermes.entity.User;
import us.idinfor.smartcitizen.interactor.SyncServiceInteractor;
import us.idinfor.smartcitizen.mvp.view.SyncServiceView;
import us.idinfor.smartcitizen.mvp.view.View;

public class SyncServicePresenterImpl implements SyncServicePresenter{

    private SyncServiceView mSyncServiceView;
    private final SyncServiceInteractor mSyncServiceInteractor;
    private CompositeSubscription mSubscriptions = new CompositeSubscription();

    @Inject
    public SyncServicePresenterImpl(SyncServiceInteractor syncServiceInteractor){
        this.mSyncServiceInteractor = syncServiceInteractor;
    }

    @Override
    public void setView(View v) {
        mSyncServiceView = (SyncServiceView) v;
    }

    @Override
    public void onPause() {
        if(!this.mSubscriptions.isUnsubscribed()){
            this.mSubscriptions.unsubscribe();
        }
    }

    @Override
    public void onDestroy() {
        onPause();
    }

    @Override
    public User getUser() {
        return this.mSyncServiceInteractor.getUserFromPreferences();
    }

    @Override
    public void queryLocations() {
        mSubscriptions.add(this.mSyncServiceInteractor.queryLocationsToGoogleFit().subscribe(
            locations ->  {
                if(!locations.isEmpty()){
                    this.mSyncServiceInteractor.uploadLocations(locations);
                }
            }
        ));
    }

    @Override
    public void queryActivities() {
        mSubscriptions.add(this.mSyncServiceInteractor.queryActivitiesToGoogleFit().subscribe(
            activities -> {
                if(!activities.isEmpty()){
                    this.mSyncServiceInteractor.uploadActivities(activities);
                }
            }
        ));
    }
}