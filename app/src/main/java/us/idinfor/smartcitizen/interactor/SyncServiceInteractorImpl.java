package us.idinfor.smartcitizen.interactor;

import android.content.SharedPreferences;
import android.util.Log;

import com.fernandocejas.frodo.annotation.RxLogObservable;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.patloew.rxfit.RxFit;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
import us.idinfor.smartcitizen.utils.RxNetwork;
import us.idinfor.smartcitizen.utils.Utils;
import us.idinfor.smartcitizen.data.api.google.fit.entity.ActivitySegmentFit;
import us.idinfor.smartcitizen.data.api.google.fit.entity.LocationSampleFit;
import us.idinfor.smartcitizen.data.api.hermes.HermesCitizenApi;
import us.idinfor.smartcitizen.data.api.hermes.entity.ItemsList;
import us.idinfor.smartcitizen.data.api.hermes.entity.User;
import us.idinfor.smartcitizen.data.api.ztreamy.ZtreamyApi;
import us.idinfor.smartcitizen.data.api.ztreamy.entity.Event;
import us.idinfor.smartcitizen.service.SyncServiceUtils;

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

    @RxLogObservable
    @Override
    public Observable<List<LocationSampleFit>> queryLocationsToGoogleFit() {

        DataReadRequest.Builder dataReadRequestBuilder = buildGoogleFitLocationsRequest();

        long startTime = mPrefs.getLong(Constants.PROPERTY_LAST_LOCATION_TIME_SENT,Utils.getStartTimeRange(Constants.RANGE_DAY));
        long endTime = new Date().getTime();
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

    @RxLogObservable
    @Override
    public Observable<List<ActivitySegmentFit>> queryActivitiesToGoogleFit() {

        DataReadRequest.Builder dataReadRequestBuilder = buildGoogleFitActivitiesRequest();

        long startTime = mPrefs.getLong(Constants.PROPERTY_LAST_ACTIVITY_TIME_SENT,Utils.getStartTimeRange(Constants.RANGE_DAY));
        long endTime = new Date().getTime();
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
    public void uploadLocations(List<LocationSampleFit> locations) {
        ItemsList<LocationSampleFit> items = new ItemsList<>(
                getUserFromPreferences().getEmail(),
                locations);
        uploadLocationsToHermesCitizen(items);
        uploadLocationsToZtreamy(items);
        mPrefs.edit().putLong(Constants.PROPERTY_LAST_LOCATION_TIME_SENT,new Date().getTime()).commit();
    }

    @RxLogObservable
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

    @RxLogObservable
    private void uploadLocationsToZtreamy(ItemsList<LocationSampleFit> items){
        Map<String,Object> map = new HashMap<>(1);
        map.put(ZtreamyApi.EVENT_TYPE_LOCATIONS,items.getItems());
        Event ztreamyEvent = new Event(
                getHash(items.getUser()),
                ZtreamyApi.SYNTAX,
                ZtreamyApi.APPLICATION_ID,
                ZtreamyApi.EVENT_TYPE_LOCATIONS,
                map);
        mRxNetwork.checkInternetConnection()
                .andThen(mZtreamyApi.uploadLocations(ztreamyEvent)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()))
                .subscribe(response -> {
                    if(response.isSuccess()){
                        Log.i(TAG,"Locations uploaded to Ztreamy");
                    }
                },
                throwable -> Log.e(TAG,"Locations not uploaded to Ztreamy"));
    }

    @Override
    public void uploadActivities(List<ActivitySegmentFit> activities) {
        ItemsList<ActivitySegmentFit> items = new ItemsList<>(
                getUserFromPreferences().getEmail(),
                activities);
        uploadActivitiesToHermesCitizen(items);
        uploadActivitiesToZtreamy(items);
        mPrefs.edit().putLong(Constants.PROPERTY_LAST_ACTIVITY_TIME_SENT,new Date().getTime()).commit();
    }

    @RxLogObservable
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

    @RxLogObservable
    private void uploadActivitiesToZtreamy(ItemsList<ActivitySegmentFit> items){
        Map<String,Object> map = new HashMap<>(1);
        map.put(ZtreamyApi.EVENT_TYPE_ACTIVITIES,items.getItems());
        Event ztreamyEvent = new Event(
                getHash(items.getUser()),
                ZtreamyApi.SYNTAX,
                ZtreamyApi.APPLICATION_ID,
                ZtreamyApi.EVENT_TYPE_ACTIVITIES,
                map);
        mRxNetwork.checkInternetConnection()
                .andThen(mZtreamyApi.uploadActivities(ztreamyEvent)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()))
                .subscribe(response -> {
                    if(response.isSuccess()){
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
        return String.format("%0" + (data.length*2) + "X", new BigInteger(1, data));
    }

}