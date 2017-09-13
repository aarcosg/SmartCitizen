package es.us.hermes.smartcitizen.interactor;

import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.maps.model.LatLng;
import com.patloew.rxfit.RxFit;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import es.us.hermes.smartcitizen.data.GoogleFitHelper;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class LocationDetailsInteractorImpl implements LocationDetailsInteractor {

    private static final String TAG = LocationDetailsInteractorImpl.class.getCanonicalName();

    @Inject
    public LocationDetailsInteractorImpl(){}

    //@RxLogObservable
    @Override
    public Observable<List<LatLng>> getGoogleFitQueryResponse(long statTime, long endTime) {
        DataReadRequest.Builder dataReadRequestBuilder = buildFitDataReadRequest();

        dataReadRequestBuilder.setTimeRange(statTime, endTime, TimeUnit.MILLISECONDS);

        DataReadRequest dataReadRequest = dataReadRequestBuilder.build();
        DataReadRequest dataReadRequestServer = dataReadRequestBuilder.enableServerQueries().build();

        return RxFit.checkConnection()
                .andThen(RxFit.History.read(dataReadRequestServer)
                        .compose(RxFit.OnExceptionResumeNext.with(RxFit.History.read(dataReadRequest)))
                        .flatMapObservable(dataReadResult -> Observable.just(
                                GoogleFitHelper.getPointListFromDataSets(dataReadResult.getDataSets())
                        ))
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread()));
    }

    @Override
    public DataReadRequest.Builder buildFitDataReadRequest() {
        DataReadRequest.Builder builder = new DataReadRequest.Builder()
                .read(DataType.TYPE_LOCATION_SAMPLE);
        return builder;
    }

}