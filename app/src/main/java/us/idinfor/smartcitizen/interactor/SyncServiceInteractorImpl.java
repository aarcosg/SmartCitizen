package us.idinfor.smartcitizen.interactor;

import android.content.SharedPreferences;
import android.util.Log;

import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.patloew.rxfit.RxFit;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
import us.idinfor.smartcitizen.data.api.google.fit.entity.LocationSampleFit;
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

    @Override
    public void uploadPeriodicLocations(List<LocationSampleFit> locations) {
        ItemsList<LocationSampleFit> items = new ItemsList<>(
                getUserFromPreferences().getEmail(),
                locations);
        uploadLocationsToHermesCitizen(items);
        uploadLocationsToZtreamy(items, ZtreamyApi.EVENT_TYPE_PERIODIC_LOCATIONS);
        mPrefs.edit().putLong(Constants.PROPERTY_LAST_LOCATION_TIME_SENT,new Date().getTime()).commit();
    }

    @Override
    public void uploadFullDayLocations(List<LocationSampleFit> locations) {
        ItemsList<LocationSampleFit> items = new ItemsList<>(
                getUserFromPreferences().getEmail(),
                locations);
        uploadLocationsToZtreamy(items,ZtreamyApi.EVENT_TYPE_FULL_LOCATIONS);
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
    private void uploadLocationsToZtreamy(ItemsList<LocationSampleFit> items, String eventType){
        Map<String,Object> subMap = new HashMap<>(1);
        subMap.put(ZtreamyApi.LOCATIONS_LIST_KEY,items.getItems());
        Map<String,Object> map = new HashMap<>(1);
        map.put(eventType,subMap);
        Event ztreamyEvent = new Event(
                getHash(items.getUser()),
                ZtreamyApi.SYNTAX,
                ZtreamyApi.APPLICATION_ID,
                eventType,
                map);
        mRxNetwork.checkInternetConnection()
                .andThen(mZtreamyApi.uploadLocations(ztreamyEvent)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()))
                .subscribe(response -> {
                    if(response.isSuccessful()){
                        Log.i(TAG,"Locations uploaded to Ztreamy");
                    }
                },
                throwable -> Log.e(TAG,"Locations not uploaded to Ztreamy"));
    }

    @Override
    public void uploadPeriodicActivities(List<ActivitySegmentFit> activities) {
        ItemsList<ActivitySegmentFit> items = new ItemsList<>(
                getUserFromPreferences().getEmail(),
                activities);
        uploadActivitiesToHermesCitizen(items);
        uploadActivitiesToZtreamy(items, ZtreamyApi.EVENT_TYPE_PERIODIC_ACTIVITIES);
        mPrefs.edit().putLong(Constants.PROPERTY_LAST_ACTIVITY_TIME_SENT,new Date().getTime()).commit();
    }

    @Override
    public void uploadFullDayActivities(List<ActivitySegmentFit> activities) {
        ItemsList<ActivitySegmentFit> items = new ItemsList<>(
                getUserFromPreferences().getEmail(),
                activities);
        uploadActivitiesToZtreamy(items, ZtreamyApi.EVENT_TYPE_FULL_ACTIVITIES);
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

    //@RxLogObservable
    private void uploadActivitiesToZtreamy(ItemsList<ActivitySegmentFit> items, String eventType){
        Map<String,Object> subMap = new HashMap<>(1);
        subMap.put(ZtreamyApi.ACTIVITIES_LIST_KEY,items.getItems());
        Map<String,Object> map = new HashMap<>(1);
        map.put(eventType,subMap);
        Event ztreamyEvent = new Event(
                getHash(items.getUser()),
                ZtreamyApi.SYNTAX,
                ZtreamyApi.APPLICATION_ID,
                eventType,
                map);
        mRxNetwork.checkInternetConnection()
                .andThen(mZtreamyApi.uploadActivities(ztreamyEvent)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()))
                .subscribe(response -> {
                    if(response.isSuccessful()){
                        Log.i(TAG,"Activities uploaded to Ztreamy");
                    }
                },
                throwable -> Log.e(TAG,"Activities not uploaded to Ztreamy"));
    }

    private String getHash(String stringToHash){
        String hashedEmail = "";
        MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.reset();
            hashedEmail =  bin2hex(messageDigest.digest(stringToHash.getBytes()));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return hashedEmail;
    }

    private String bin2hex(byte[] data) {
        return String.format("%0" + (data.length*2) + "x", new BigInteger(1, data));
    }

}