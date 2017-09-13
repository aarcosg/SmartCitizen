package es.us.hermes.smartcitizen.interactor;

import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.patloew.rxfit.RxFit;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import es.us.hermes.smartcitizen.mvp.model.ActivityDetails;
import es.us.hermes.smartcitizen.data.GoogleFitHelper;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ActivityTimelineInteractorImpl implements ActivityTimelineInteractor {

    private static final String TAG = ActivityTimelineInteractorImpl.class.getCanonicalName();

    @Inject
    public ActivityTimelineInteractorImpl(){}

    //@RxLogObservable
    @Override
    public Observable<List<ActivityDetails>> getGoogleFitQueryResponse(long statTime, long endTime) {
        DataReadRequest.Builder dataReadRequestBuilder = buildFitDataReadRequest();

        dataReadRequestBuilder.setTimeRange(statTime, endTime, TimeUnit.MILLISECONDS);
        dataReadRequestBuilder.bucketByActivitySegment(1, TimeUnit.MINUTES);

        DataReadRequest dataReadRequest = dataReadRequestBuilder.build();
        DataReadRequest dataReadRequestServer = dataReadRequestBuilder.enableServerQueries().build();

        return RxFit.checkConnection()
            .andThen(RxFit.History.read(dataReadRequestServer)
            .compose(RxFit.OnExceptionResumeNext.with(RxFit.History.read(dataReadRequest)))
            .flatMapObservable(dataReadResult -> Observable.just(
                    GoogleFitHelper.getActivityDetailsListFromBuckets(dataReadResult.getBuckets())
            ))
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread()));
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