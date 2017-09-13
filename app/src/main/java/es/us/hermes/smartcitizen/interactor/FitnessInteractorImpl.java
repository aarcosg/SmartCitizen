package es.us.hermes.smartcitizen.interactor;

import android.content.Context;

import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.patloew.rxfit.RxFit;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import es.us.hermes.smartcitizen.mvp.model.ActivityDetails;
import es.us.hermes.smartcitizen.data.GoogleFitHelper;
import es.us.hermes.smartcitizen.utils.Utils;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class FitnessInteractorImpl implements FitnessInteractor {

    private static final String TAG = FitnessInteractorImpl.class.getCanonicalName();

    private final Context mContext;

    @Inject
    public FitnessInteractorImpl(Context context){
        this.mContext = context;
    }

    @Override
    public void initGoogleFitApi() {
        if(Utils.areMandatoryAppPermissionsGranted(mContext)){
            GoogleFitHelper.initFitApi(mContext);
        }
    }

    @Override
    public void subscribeUserToGoogleFit(){
        if(Utils.areMandatoryAppPermissionsGranted(mContext)){
            RxFit.checkConnection().subscribe(() -> GoogleFitHelper.subscribeFitnessData(mContext));
        }
    }

    //@RxLogObservable
    @Override
    public Observable<ActivityDetails> getGoogleFitQueryResponse(long statTime, long endTime) {

        Observable<ActivityDetails> observable = Observable.empty();

        if(Utils.areMandatoryAppPermissionsGranted(mContext)){
            DataReadRequest.Builder dataReadRequestBuilder = buildFitDataReadRequest();

            dataReadRequestBuilder.setTimeRange(statTime, endTime, TimeUnit.MILLISECONDS);
            dataReadRequestBuilder.bucketByTime(1, TimeUnit.DAYS);

            DataReadRequest dataReadRequest = dataReadRequestBuilder.build();
            DataReadRequest dataReadRequestServer = dataReadRequestBuilder.enableServerQueries().build();

            observable = RxFit.checkConnection()
                    .andThen(RxFit.History.read(dataReadRequestServer)
                            .compose(RxFit.OnExceptionResumeNext.with(RxFit.History.read(dataReadRequest)))
                            .flatMapObservable(dataReadResult -> Observable.from(dataReadResult.getBuckets()))
                            .flatMap(bucket -> Observable.just(GoogleFitHelper.getActivityDetailsFromBucket(bucket))
                                    .subscribeOn(Schedulers.computation())
                                    .observeOn(AndroidSchedulers.mainThread())));
        }

        return observable;

    }

    @Override
    public DataReadRequest.Builder buildFitDataReadRequest() {
        DataReadRequest.Builder builder = new DataReadRequest.Builder()
                .aggregate(DataType.TYPE_LOCATION_SAMPLE, DataType.AGGREGATE_LOCATION_BOUNDING_BOX)
                .aggregate(DataType.TYPE_ACTIVITY_SEGMENT, DataType.AGGREGATE_ACTIVITY_SUMMARY)
                .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
                .aggregate(DataType.TYPE_HEART_RATE_BPM, DataType.AGGREGATE_HEART_RATE_SUMMARY)
                .aggregate(DataType.TYPE_CALORIES_EXPENDED, DataType.AGGREGATE_CALORIES_EXPENDED)
                .aggregate(DataType.TYPE_DISTANCE_DELTA, DataType.AGGREGATE_DISTANCE_DELTA);
        return builder;
    }

}