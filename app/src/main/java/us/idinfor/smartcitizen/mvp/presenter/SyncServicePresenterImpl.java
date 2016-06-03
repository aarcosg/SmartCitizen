package us.idinfor.smartcitizen.mvp.presenter;

import android.util.Log;

import java.util.Calendar;
import java.util.Date;

import javax.inject.Inject;

import rx.subscriptions.CompositeSubscription;
import us.idinfor.smartcitizen.data.api.hermes.entity.User;
import us.idinfor.smartcitizen.interactor.SyncServiceInteractor;
import us.idinfor.smartcitizen.mvp.view.SyncServiceView;
import us.idinfor.smartcitizen.mvp.view.View;

public class SyncServicePresenterImpl implements SyncServicePresenter{

    private static final String TAG = SyncServicePresenterImpl.class.getCanonicalName();

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
                    , throwable -> Log.e(TAG,"Error @queryPeriodicLocations: " + throwable.getLocalizedMessage())
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
                    , throwable -> Log.e(TAG,"Error @queryPeriodicActivities: " + throwable.getLocalizedMessage())
        ));
    }

    @Override
    public void queryPeriodicSteps() {
        mSubscriptions.add(this.mSyncServiceInteractor.queryStepsToGoogleFit(
                this.mSyncServiceInteractor.getLastStepsTimeSentFromPreferences(),new Date().getTime())
                .subscribe(
                        steps ->  {
                            if(!steps.isEmpty()){
                                this.mSyncServiceInteractor.uploadPeriodicSteps(steps);
                            }
                        }
                        , throwable -> Log.e(TAG,"Error @queryPeriodicSteps: " + throwable.getLocalizedMessage())
                ));
    }

    @Override
    public void queryPeriodicDistances() {
        mSubscriptions.add(this.mSyncServiceInteractor.queryDistancesToGoogleFit(
                this.mSyncServiceInteractor.getLastDistanceTimeSentFromPreferences(),new Date().getTime())
                .subscribe(
                        distances ->  {
                            if(!distances.isEmpty()){
                                this.mSyncServiceInteractor.uploadPeriodicDistances(distances);
                            }
                        }
                        , throwable -> Log.e(TAG,"Error @queryPeriodicDistances: " + throwable.getLocalizedMessage())
                ));
    }

    @Override
    public void queryPeriodicCaloriesExpended() {
        mSubscriptions.add(this.mSyncServiceInteractor.queryCaloriesExpendedToGoogleFit(
                this.mSyncServiceInteractor.getLastCaloriesExpendedTimeSentFromPreferences(),new Date().getTime())
                .subscribe(
                        caloriesExpendedList ->  {
                            if(!caloriesExpendedList.isEmpty()){
                                this.mSyncServiceInteractor.uploadPeriodicCaloriesExpended(caloriesExpendedList);
                            }
                        }
                        , throwable -> Log.e(TAG,"Error @queryPeriodicCaloriesExpended: " + throwable.getLocalizedMessage())
                ));
    }

    @Override
    public void queryPeriodicHeartRates() {
        mSubscriptions.add(this.mSyncServiceInteractor.queryHeartRateSamplesToGoogleFit(
                this.mSyncServiceInteractor.getLastHeartRateTimeSentFromPreferences(),new Date().getTime())
                .subscribe(
                        heartRates ->  {
                            if(!heartRates.isEmpty()){
                                this.mSyncServiceInteractor.uploadPeriodicHeartRateSample(heartRates);
                            }
                        }
                        , throwable -> Log.e(TAG,"Error @queryPeriodicHeartRates: " + throwable.getLocalizedMessage())
                ));
    }

    @Override
    public void queryPeriodicSleep() {
        mSubscriptions.add(this.mSyncServiceInteractor.querySleepActivityToGoogleFit(
                this.mSyncServiceInteractor.getLastSleepTimeSentFromPreferences(),new Date().getTime())
                .subscribe(
                        sleepActivities -> {
                            if(!sleepActivities.isEmpty()){
                                this.mSyncServiceInteractor.uploadPeriodicSleep(sleepActivities);
                            }
                        }
                        , throwable -> Log.e(TAG,"Error @queryPeriodicSleep: " + throwable.getLocalizedMessage())
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
                            , throwable -> Log.e(TAG,"Error @queryFullDayData: " + throwable.getLocalizedMessage())
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
                            , throwable -> Log.e(TAG,"Error @queryFullDayData: " + throwable.getLocalizedMessage())
                    )
            );

            mSubscriptions.add(this.mSyncServiceInteractor.queryStepsToGoogleFit(
                    lastDay.getTimeInMillis(),now.getTimeInMillis())
                    .subscribe(
                            steps ->  {
                                if(!steps.isEmpty()){
                                    mSyncServiceInteractor.uploadFullDaySteps(steps);
                                }
                            }
                            , throwable -> Log.e(TAG,"Error @queryFullDayData: " + throwable.getLocalizedMessage())
                    )
            );

            mSubscriptions.add(this.mSyncServiceInteractor.queryDistancesToGoogleFit(
                    lastDay.getTimeInMillis(),now.getTimeInMillis())
                    .subscribe(
                            distances ->  {
                                if(!distances.isEmpty()){
                                    mSyncServiceInteractor.uploadFullDayDistances(distances);
                                }
                            }
                            , throwable -> Log.e(TAG,"Error @queryFullDayData: " + throwable.getLocalizedMessage())
                    )
            );

            mSubscriptions.add(this.mSyncServiceInteractor.queryCaloriesExpendedToGoogleFit(
                    lastDay.getTimeInMillis(),now.getTimeInMillis())
                    .subscribe(
                            caloriesExpended ->  {
                                if(!caloriesExpended.isEmpty()){
                                    mSyncServiceInteractor.uploadFullDayCaloriesExpended(caloriesExpended);
                                }
                            }
                            , throwable -> Log.e(TAG,"Error @queryFullDayData: " + throwable.getLocalizedMessage())
                    )
            );

            mSubscriptions.add(this.mSyncServiceInteractor.queryHeartRateSamplesToGoogleFit(
                    lastDay.getTimeInMillis(),now.getTimeInMillis())
                    .subscribe(
                            heartRates ->  {
                                if(!heartRates.isEmpty()){
                                    mSyncServiceInteractor.uploadFullDayHeartRates(heartRates);
                                }
                            }
                            , throwable -> Log.e(TAG,"Error @queryFullDayData: " + throwable.getLocalizedMessage())
                    )
            );

            mSyncServiceInteractor.saveLastDayDataSentInPreferences();
        }

    }
}