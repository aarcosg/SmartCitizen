package us.idinfor.smartcitizen.interactor;

import android.content.SharedPreferences;
import android.util.Log;

import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.patloew.rxfit.RxFit;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import us.idinfor.smartcitizen.Constants;
import us.idinfor.smartcitizen.data.api.google.fit.entity.ActivitySegmentFit;
import us.idinfor.smartcitizen.data.api.google.fit.entity.CaloriesExpendedFit;
import us.idinfor.smartcitizen.data.api.google.fit.entity.DistanceDeltaFit;
import us.idinfor.smartcitizen.data.api.google.fit.entity.HeartRateSampleFit;
import us.idinfor.smartcitizen.data.api.google.fit.entity.LocationSampleFit;
import us.idinfor.smartcitizen.data.api.google.fit.entity.StepCountDeltaFit;
import us.idinfor.smartcitizen.data.api.hermes.HermesCitizenApi;
import us.idinfor.smartcitizen.data.api.hermes.entity.ItemsList;
import us.idinfor.smartcitizen.data.api.hermes.entity.User;
import us.idinfor.smartcitizen.data.api.ztreamy.ZtreamyApi;
import us.idinfor.smartcitizen.data.api.ztreamy.entity.Event;
import us.idinfor.smartcitizen.service.SyncServiceUtils;
import us.idinfor.smartcitizen.utils.RxNetwork;
import us.idinfor.smartcitizen.utils.Utils;

public class SyncServiceInteractorImpl implements SyncServiceInteractor {

    private static final String TAG = SyncServiceInteractorImpl.class.getCanonicalName();

    private final SharedPreferences mPrefs;
    private final RxNetwork mRxNetwork;
    private final HermesCitizenApi mHermesCitizenApi;
    private final ZtreamyApi mZtreamyApi;

    @Inject
    public SyncServiceInteractorImpl(
            SharedPreferences sharedPreferences,
            RxNetwork rxNetwork,
            HermesCitizenApi hermesCitizenApi,
            ZtreamyApi ztreamyApi){
        this.mPrefs = sharedPreferences;
        this.mRxNetwork = rxNetwork;
        this.mHermesCitizenApi = hermesCitizenApi;
        this.mZtreamyApi = ztreamyApi;
    }

    @Override
    public User getUserFromPreferences() {
        return new User(this.mPrefs.getString(Constants.PROPERTY_USER_NAME,""), null);
    }

    @Override
    public Calendar getLastDayDataSentFromPreferences(){
        Calendar defaultLastDay = Calendar.getInstance();
        defaultLastDay.add(Calendar.DAY_OF_YEAR, -1);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(mPrefs.getLong(Constants.PROPERTY_LAST_DAY_DATA_SENT, defaultLastDay.getTimeInMillis()));
        return calendar;
    }

    @Override
    public void saveLastDayDataSentInPreferences(){
        mPrefs.edit().putLong(Constants.PROPERTY_LAST_DAY_DATA_SENT,new Date().getTime()).apply();
    }

    @Override
    public long getLastLocationTimeSentFromPreferences(){
        return mPrefs.getLong(Constants.PROPERTY_LAST_LOCATION_TIME_SENT, Utils.getStartTimeRange(Constants.RANGE_DAY));
    }

    @Override
    public long getLastActivityTimeSentFromPreferences(){
        return mPrefs.getLong(Constants.PROPERTY_LAST_ACTIVITY_TIME_SENT,Utils.getStartTimeRange(Constants.RANGE_DAY));
    }

    @Override
    public long getLastStepsTimeSentFromPreferences(){
        return mPrefs.getLong(Constants.PROPERTY_LAST_STEPS_TIME_SENT,Utils.getStartTimeRange(Constants.RANGE_DAY));
    }

    @Override
    public long getLastDistanceTimeSentFromPreferences(){
        return mPrefs.getLong(Constants.PROPERTY_LAST_DISTANCE_TIME_SENT,Utils.getStartTimeRange(Constants.RANGE_DAY));
    }

    @Override
    public long getLastCaloriesExpendedTimeSentFromPreferences(){
        return mPrefs.getLong(Constants.PROPERTY_LAST_CALORIES_EXPENDED_TIME_SENT,Utils.getStartTimeRange(Constants.RANGE_DAY));
    }

    @Override
    public long getLastHeartRateTimeSentFromPreferences(){
        return mPrefs.getLong(Constants.PROPERTY_LAST_HEART_RATE_TIME_SENT,Utils.getStartTimeRange(Constants.RANGE_DAY));
    }

    @Override
    public long getLastSleepTimeSentFromPreferences(){
        return mPrefs.getLong(Constants.PROPERTY_LAST_SLEEP_TIME_SENT, Utils.getStartTimeRange(Constants.RANGE_DAY));
    }

    @Override
    public DataReadRequest.Builder buildGoogleFitLocationsRequest() {
        DataReadRequest.Builder builder = new DataReadRequest.Builder()
                .read(DataType.TYPE_LOCATION_SAMPLE);
        return builder;
    }

    @Override
    public DataReadRequest.Builder buildGoogleFitActivitiesRequest() {
        DataReadRequest.Builder builder = new DataReadRequest.Builder()
                .aggregate(DataType.TYPE_ACTIVITY_SEGMENT,DataType.AGGREGATE_ACTIVITY_SUMMARY);
        return builder;
    }

    @Override
    public DataReadRequest.Builder buildGoogleFitStepsRequest() {
        DataReadRequest.Builder builder = new DataReadRequest.Builder()
                .read(DataType.TYPE_STEP_COUNT_DELTA);
        return builder;
    }

    @Override
    public DataReadRequest.Builder buildGoogleFitDistancesRequest() {
        DataReadRequest.Builder builder = new DataReadRequest.Builder()
                .read(DataType.TYPE_DISTANCE_DELTA);
        return builder;
    }

    @Override
    public DataReadRequest.Builder buildGoogleFitCaloriesExpendedRequest() {
        DataReadRequest.Builder builder = new DataReadRequest.Builder()
                .read(DataType.TYPE_CALORIES_EXPENDED);
        return builder;
    }

    @Override
    public DataReadRequest.Builder buildGoogleFitHeartRateSamplesRequest() {
        DataReadRequest.Builder builder = new DataReadRequest.Builder()
                .read(DataType.TYPE_HEART_RATE_BPM);
        return builder;
    }

    //@RxLogObservable
    @Override
    public Observable<List<LocationSampleFit>> queryLocationsToGoogleFit(long startTime, long endTime) {

        DataReadRequest.Builder dataReadRequestBuilder = buildGoogleFitLocationsRequest();

        dataReadRequestBuilder.setTimeRange(startTime,endTime, TimeUnit.MILLISECONDS);

        DataReadRequest dataReadRequest = dataReadRequestBuilder.build();
        DataReadRequest dataReadRequestServer = dataReadRequestBuilder.enableServerQueries().build();

        return RxFit.checkConnection()
                .andThen(RxFit.History.read(dataReadRequestServer)
                        .compose(new RxFit.OnExceptionResumeNext.Single<>(RxFit.History.read(dataReadRequest)))
                        .flatMapObservable(dataReadResult -> Observable.just(
                                SyncServiceUtils.getLocationListFromDataSets(dataReadResult.getDataSets())
                        ))
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread()));
    }

    //@RxLogObservable
    @Override
    public Observable<List<ActivitySegmentFit>> queryActivitiesToGoogleFit(long startTime, long endTime) {

        DataReadRequest.Builder dataReadRequestBuilder = buildGoogleFitActivitiesRequest();
        
        dataReadRequestBuilder
                .setTimeRange(startTime,endTime, TimeUnit.MILLISECONDS)
                .bucketByActivitySegment(1,TimeUnit.MINUTES);

        DataReadRequest dataReadRequest = dataReadRequestBuilder.build();
        DataReadRequest dataReadRequestServer = dataReadRequestBuilder.enableServerQueries().build();

        return RxFit.checkConnection()
                .andThen(RxFit.History.read(dataReadRequestServer)
                        .compose(new RxFit.OnExceptionResumeNext.Single<>(RxFit.History.read(dataReadRequest)))
                        .flatMapObservable(dataReadResult -> Observable.just(
                                SyncServiceUtils.getActivitySegmentListFromBuckets(dataReadResult.getBuckets())
                        ))
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread()));
    }

    //@RxLogObservable
    @Override
    public Observable<List<StepCountDeltaFit>> queryStepsToGoogleFit(long startTime, long endTime) {

        DataReadRequest.Builder dataReadRequestBuilder = buildGoogleFitStepsRequest();

        dataReadRequestBuilder.setTimeRange(startTime,endTime, TimeUnit.MILLISECONDS);

        DataReadRequest dataReadRequest = dataReadRequestBuilder.build();
        DataReadRequest dataReadRequestServer = dataReadRequestBuilder.enableServerQueries().build();

        return RxFit.checkConnection()
                .andThen(RxFit.History.read(dataReadRequestServer)
                        .compose(new RxFit.OnExceptionResumeNext.Single<>(RxFit.History.read(dataReadRequest)))
                        .flatMapObservable(dataReadResult -> Observable.just(
                                SyncServiceUtils.getStepsListFromDataSets(dataReadResult.getDataSets())
                        ))
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread()));
    }

    //@RxLogObservable
    @Override
    public Observable<List<DistanceDeltaFit>> queryDistancesToGoogleFit(long startTime, long endTime) {

        DataReadRequest.Builder dataReadRequestBuilder = buildGoogleFitDistancesRequest();

        dataReadRequestBuilder.setTimeRange(startTime,endTime, TimeUnit.MILLISECONDS);

        DataReadRequest dataReadRequest = dataReadRequestBuilder.build();
        DataReadRequest dataReadRequestServer = dataReadRequestBuilder.enableServerQueries().build();

        return RxFit.checkConnection()
                .andThen(RxFit.History.read(dataReadRequestServer)
                        .compose(new RxFit.OnExceptionResumeNext.Single<>(RxFit.History.read(dataReadRequest)))
                        .flatMapObservable(dataReadResult -> Observable.just(
                                SyncServiceUtils.getDistanceListFromDataSets(dataReadResult.getDataSets())
                        ))
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread()));
    }

    //@RxLogObservable
    @Override
    public Observable<List<CaloriesExpendedFit>> queryCaloriesExpendedToGoogleFit(long startTime, long endTime) {

        DataReadRequest.Builder dataReadRequestBuilder = buildGoogleFitCaloriesExpendedRequest();

        dataReadRequestBuilder.setTimeRange(startTime,endTime, TimeUnit.MILLISECONDS);

        DataReadRequest dataReadRequest = dataReadRequestBuilder.build();
        DataReadRequest dataReadRequestServer = dataReadRequestBuilder.enableServerQueries().build();

        return RxFit.checkConnection()
                .andThen(RxFit.History.read(dataReadRequestServer)
                        .compose(new RxFit.OnExceptionResumeNext.Single<>(RxFit.History.read(dataReadRequest)))
                        .flatMapObservable(dataReadResult -> Observable.just(
                                SyncServiceUtils.getCaloriesExpendedListFromDataSets(dataReadResult.getDataSets())
                        ))
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread()));
    }

    //@RxLogObservable
    @Override
    public Observable<List<HeartRateSampleFit>> queryHeartRateSamplesToGoogleFit(long startTime, long endTime) {

        DataReadRequest.Builder dataReadRequestBuilder = buildGoogleFitHeartRateSamplesRequest();

        dataReadRequestBuilder.setTimeRange(startTime,endTime, TimeUnit.MILLISECONDS);

        DataReadRequest dataReadRequest = dataReadRequestBuilder.build();
        DataReadRequest dataReadRequestServer = dataReadRequestBuilder.enableServerQueries().build();

        return RxFit.checkConnection()
                .andThen(RxFit.History.read(dataReadRequestServer)
                        .compose(new RxFit.OnExceptionResumeNext.Single<>(RxFit.History.read(dataReadRequest)))
                        .flatMapObservable(dataReadResult -> Observable.just(
                                SyncServiceUtils.getHeartRateSampleListFromDataSets(dataReadResult.getDataSets())
                        ))
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread()));
    }

    @Override
    public Observable<List<ActivitySegmentFit>> querySleepActivityToGoogleFit(long startTime, long endTime) {

        DataReadRequest.Builder dataReadRequestBuilder = buildGoogleFitActivitiesRequest();

        dataReadRequestBuilder
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .bucketByActivitySegment(1,TimeUnit.MINUTES);

        DataReadRequest dataReadRequest = dataReadRequestBuilder.build();
        DataReadRequest dataReadRequestServer = dataReadRequestBuilder.enableServerQueries().build();

        return RxFit.checkConnection()
                .andThen(RxFit.History.read(dataReadRequestServer)
                        .compose(new RxFit.OnExceptionResumeNext.Single<>(RxFit.History.read(dataReadRequest)))
                        .flatMapObservable(dataReadResult -> Observable.just(
                                SyncServiceUtils.getSleepActivityListFromBuckets(dataReadResult.getBuckets())
                        ))
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread()));
    }

    @Override
    public void uploadPeriodicLocations(List<LocationSampleFit> locations) {
        ItemsList<LocationSampleFit> items = new ItemsList<>(
                getUserFromPreferences().getEmail(),
                locations);
        uploadLocationsToHermesCitizen(items);
        uploadDataToZtreamy(items,ZtreamyApi.LIST_KEY_LOCATIONS,ZtreamyApi.EVENT_TYPE_PERIODIC_LOCATIONS);
        mPrefs.edit().putLong(Constants.PROPERTY_LAST_LOCATION_TIME_SENT,new Date().getTime()).commit();
    }

    @Override
    public void uploadPeriodicActivities(List<ActivitySegmentFit> activities) {
        ItemsList<ActivitySegmentFit> items = new ItemsList<>(
                getUserFromPreferences().getEmail(),
                activities);
        uploadActivitiesToHermesCitizen(items);
        uploadDataToZtreamy(items,ZtreamyApi.LIST_KEY_ACTIVITIES,ZtreamyApi.EVENT_TYPE_PERIODIC_ACTIVITIES);
        mPrefs.edit().putLong(Constants.PROPERTY_LAST_ACTIVITY_TIME_SENT,new Date().getTime()).commit();
    }

    @Override
    public void uploadPeriodicSteps(List<StepCountDeltaFit> stepsList) {
        ItemsList<StepCountDeltaFit> items = new ItemsList<>(
                getUserFromPreferences().getEmail(),
                stepsList);
        uploadDataToZtreamy(items, ZtreamyApi.LIST_KEY_STEPS, ZtreamyApi.EVENT_TYPE_PERIODIC_STEPS);
        mPrefs.edit().putLong(Constants.PROPERTY_LAST_STEPS_TIME_SENT,new Date().getTime()).commit();
    }

    @Override
    public void uploadPeriodicDistances(List<DistanceDeltaFit> distances) {
        ItemsList<DistanceDeltaFit> items = new ItemsList<>(
                getUserFromPreferences().getEmail(),
                distances);
        uploadDataToZtreamy(items, ZtreamyApi.LIST_KEY_DISTANCES, ZtreamyApi.EVENT_TYPE_PERIODIC_DISTANCES);
        mPrefs.edit().putLong(Constants.PROPERTY_LAST_DISTANCE_TIME_SENT,new Date().getTime()).commit();
    }

    @Override
    public void uploadPeriodicCaloriesExpended(List<CaloriesExpendedFit> caloriesExpended) {
        ItemsList<CaloriesExpendedFit> items = new ItemsList<>(
                getUserFromPreferences().getEmail(),
                caloriesExpended);
        uploadDataToZtreamy(items, ZtreamyApi.LIST_KEY_CALORIES_EXPENDED, ZtreamyApi.EVENT_TYPE_PERIODIC_CALORIES_EXPENDED);
        mPrefs.edit().putLong(Constants.PROPERTY_LAST_CALORIES_EXPENDED_TIME_SENT,new Date().getTime()).commit();
    }

    @Override
    public void uploadPeriodicHeartRateSample(List<HeartRateSampleFit> heartRates) {
        ItemsList<HeartRateSampleFit> items = new ItemsList<>(
                getUserFromPreferences().getEmail(),
                heartRates);
        uploadDataToZtreamy(items, ZtreamyApi.LIST_KEY_HEART_RATES, ZtreamyApi.EVENT_TYPE_PERIODIC_HEART_RATES);
        mPrefs.edit().putLong(Constants.PROPERTY_LAST_HEART_RATE_TIME_SENT,new Date().getTime()).commit();
    }

    @Override
    public void uploadPeriodicSleep(List<ActivitySegmentFit> activities) {
        ItemsList<ActivitySegmentFit> items = new ItemsList<>(
                getUserFromPreferences().getEmail(),
                activities);
        uploadDataToZtreamy(items,ZtreamyApi.LIST_KEY_SLEEP,ZtreamyApi.EVENT_TYPE_PERIODIC_SLEEP);
        mPrefs.edit().putLong(Constants.PROPERTY_LAST_SLEEP_TIME_SENT,new Date().getTime()).commit();
    }

    @Override
    public void uploadFullDayLocations(List<LocationSampleFit> locations) {
        ItemsList<LocationSampleFit> items = new ItemsList<>(
                getUserFromPreferences().getEmail(),
                locations);
        uploadDataToZtreamy(items,ZtreamyApi.LIST_KEY_LOCATIONS,ZtreamyApi.EVENT_TYPE_FULL_LOCATIONS);
    }

    @Override
    public void uploadFullDayActivities(List<ActivitySegmentFit> activities) {
        ItemsList<ActivitySegmentFit> items = new ItemsList<>(
                getUserFromPreferences().getEmail(),
                activities);
        uploadDataToZtreamy(items,ZtreamyApi.LIST_KEY_ACTIVITIES,ZtreamyApi.EVENT_TYPE_FULL_ACTIVITIES);
    }

    @Override
    public void uploadFullDaySteps(List<StepCountDeltaFit> steps) {
        ItemsList<StepCountDeltaFit> items = new ItemsList<>(
                getUserFromPreferences().getEmail(),
                steps);
        uploadDataToZtreamy(items,ZtreamyApi.LIST_KEY_STEPS,ZtreamyApi.EVENT_TYPE_FULL_STEPS);
    }

    @Override
    public void uploadFullDayDistances(List<DistanceDeltaFit> distances) {
        ItemsList<DistanceDeltaFit> items = new ItemsList<>(
                getUserFromPreferences().getEmail(),
                distances);
        uploadDataToZtreamy(items,ZtreamyApi.LIST_KEY_DISTANCES,ZtreamyApi.EVENT_TYPE_FULL_DISTANCES);
    }

    @Override
    public void uploadFullDayCaloriesExpended(List<CaloriesExpendedFit> caloriesExpended) {
        ItemsList<CaloriesExpendedFit> items = new ItemsList<>(
                getUserFromPreferences().getEmail(),
                caloriesExpended);
        uploadDataToZtreamy(items,ZtreamyApi.LIST_KEY_CALORIES_EXPENDED,ZtreamyApi.EVENT_TYPE_FULL_CALORIES_EXPENDED);
    }

    @Override
    public void uploadFullDayHeartRates(List<HeartRateSampleFit> heartRates) {
        ItemsList<HeartRateSampleFit> items = new ItemsList<>(
                getUserFromPreferences().getEmail(),
                heartRates);
        uploadDataToZtreamy(items,ZtreamyApi.LIST_KEY_HEART_RATES,ZtreamyApi.EVENT_TYPE_FULL_HEART_RATES);
    }

    private void uploadDataToZtreamy(ItemsList items, String listKey, String eventType){
        Map<String,Object> subMap = new HashMap<>(1);
        subMap.put(listKey,items.getItems());
        Map<String,Object> map = new HashMap<>(1);
        map.put(eventType,subMap);
        Event ztreamyEvent = new Event(
                SyncServiceUtils.getHash(items.getUser()),
                ZtreamyApi.SYNTAX,
                ZtreamyApi.APPLICATION_ID,
                eventType,
                map);
        mRxNetwork.checkInternetConnection()
                .andThen(mZtreamyApi.uploadEvent(ztreamyEvent)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread()))
                .subscribe(response -> {
                            if(response.isSuccessful()){
                                Log.i(TAG, eventType + " sent to Ztreamy");
                            }
                        },
                        throwable -> Log.e(TAG, eventType + " not sent to Ztreamy"));
    }

    //@RxLogObservable
    private void uploadLocationsToHermesCitizen(ItemsList<LocationSampleFit> items){
        mRxNetwork.checkInternetConnection()
                .andThen(mHermesCitizenApi.uploadLocations(items)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread()))
                .subscribe(integerResponse -> {
                            if(integerResponse.body() == HermesCitizenApi.RESPONSE_OK){
                                Log.i(TAG,"Locations uploaded to Hermes Citizen server");
                            }
                        },
                        throwable -> Log.e(TAG,"Locations not uploaded to Hermes Citizen server"));
    }

    //@RxLogObservable
    private void uploadActivitiesToHermesCitizen(ItemsList<ActivitySegmentFit> items){
        mRxNetwork.checkInternetConnection()
                .andThen(mHermesCitizenApi.uploadActivities(items)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()))
                .subscribe(integerResponse -> {
                    if(integerResponse.body() == HermesCitizenApi.RESPONSE_OK){
                        Log.i(TAG,"Activities uploaded to Hermes Citizen server");
                    }
                },
                throwable -> Log.e(TAG,"Activities not uploaded to Hermes Citizen server"));
    }

}