package us.idinfor.smartcitizen.mvp.presenter;

import java.util.Calendar;
import java.util.Date;

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
    public void queryPeriodicLocations() {
        mSubscriptions.add(this.mSyncServiceInteractor.queryLocationsToGoogleFit(
                this.mSyncServiceInteractor.getLastLocationTimeSentFromPreferences(),new Date().getTime())
                .subscribe(
                    locations ->  {
                        if(!locations.isEmpty()){
                            this.mSyncServiceInteractor.uploadPeriodicLocations(locations);
                        }
                    }
        ));
    }

    @Override
    public void queryPeriodicActivities() {
        mSubscriptions.add(this.mSyncServiceInteractor.queryActivitiesToGoogleFit(
                this.mSyncServiceInteractor.getLastActivityTimeSentFromPreferences(),new Date().getTime())
                .subscribe(
                    activities -> {
                        if(!activities.isEmpty()){
                            this.mSyncServiceInteractor.uploadPeriodicActivities(activities);
                        }
                    }
        ));
    }

    @Override
    public void queryFullDayData(){
        Calendar now = Calendar.getInstance();
        Calendar lastDay = mSyncServiceInteractor.getLastDayDataSentFromPreferences();
        if(lastDay.get(Calendar.DAY_OF_YEAR) < now.get(Calendar.DAY_OF_YEAR)){
            lastDay.set(Calendar.HOUR_OF_DAY,0);
            lastDay.set(Calendar.MINUTE,0);
            lastDay.set(Calendar.SECOND,0);
            now.add(Calendar.DAY_OF_YEAR, -1);
            now.set(Calendar.HOUR_OF_DAY,23);
            now.set(Calendar.MINUTE,59);
            now.set(Calendar.SECOND,59);

            mSubscriptions.add(this.mSyncServiceInteractor.queryLocationsToGoogleFit(
                    lastDay.getTimeInMillis(),now.getTimeInMillis())
                    .subscribe(
                            locations ->  {
                                if(!locations.isEmpty()){
                                    mSyncServiceInteractor.uploadFullDayLocations(locations);
                                }
                            }
                    )
            );

            mSubscriptions.add(this.mSyncServiceInteractor.queryActivitiesToGoogleFit(
                    lastDay.getTimeInMillis(),now.getTimeInMillis())
                    .subscribe(
                            activities ->  {
                                if(!activities.isEmpty()){
                                    mSyncServiceInteractor.uploadFullDayActivities(activities);
                                }
                            }
                    )
            );
            mSyncServiceInteractor.saveLastDayDataSentInPreferences();
        }

    }
}