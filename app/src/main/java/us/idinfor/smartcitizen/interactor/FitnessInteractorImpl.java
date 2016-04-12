package us.idinfor.smartcitizen.interactor;

import android.Manifest;
import android.content.Context;
import android.util.Log;

import com.fernandocejas.frodo.annotation.RxLogObservable;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.patloew.rxfit.RxFit;
import com.tbruyelle.rxpermissions.RxPermissions;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import us.idinfor.smartcitizen.Utils;
import us.idinfor.smartcitizen.data.api.google.fit.ActivityDetails;
import us.idinfor.smartcitizen.data.api.google.fit.GoogleFitHelper;

public class FitnessInteractorImpl implements FitnessInteractor {

    private static final String TAG = FitnessInteractorImpl.class.getCanonicalName();

    private final Context mContext;

    @Inject
    public FitnessInteractorImpl(Context context){
        this.mContext = context;
    }

    @Override
    public void initGoogleFitApi() {
        GoogleFitHelper.initFitApi(mContext);
    }

    @RxLogObservable
    @Override
    public Observable<ActivityDetails> getGoogleFitQueryResponse(int timeRange) {
        //Observable<ActivityDetails> activityDetailsObservable = Observable.empty();

        DataReadRequest.Builder dataReadRequestBuilder = buildFitDataReadRequest();

        dataReadRequestBuilder.setTimeRange(Utils.getStartTimeRange(timeRange),new Date().getTime(), TimeUnit.MILLISECONDS);
        dataReadRequestBuilder.bucketByTime(1, TimeUnit.DAYS);

        DataReadRequest dataReadRequest = dataReadRequestBuilder.build();
        DataReadRequest dataReadRequestServer = dataReadRequestBuilder.enableServerQueries().build();


         return RxFit.History.read(dataReadRequestServer)
                        //.doOnError(throwable -> handleException(throwable))
                        .compose(new RxFit.OnExceptionResumeNext.Single<>(RxFit.History.read(dataReadRequest)))
                        .flatMapObservable(dataReadResult -> Observable.from(dataReadResult.getBuckets()))
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .flatMap(bucket -> Observable.just(GoogleFitHelper.parseGoogleFitBucket(bucket)));



        //return activityDetailsObservable;


        /*return Observable.create(subscriber -> {
            RxFit.History.read(dataReadRequestServer)
                    .doOnError(throwable -> subscriber.onError(new QueryTimeOutGoogleFitException()))
                    .compose(new RxFit.OnExceptionResumeNext.Single<>(RxFit.History.read(dataReadRequest)))
                    .flatMapObservable(dataReadResult -> Observable.from(dataReadResult.getBuckets()))
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            bucket -> {
                                subscriber.onNext(GoogleFitHelper.parseGoogleFitBucket(bucket));
                                subscriber.onCompleted();
                            },
                            e -> subscriber.onError(new QueryDataGoogleFitException()));
        });*/


        /*subscribeToFragment(RxFit.History.read(dataReadRequestServer)
                .doOnError(throwable -> {
                    if(throwable instanceof StatusException && ((StatusException)throwable).getStatus().getStatusCode() == CommonStatusCodes.TIMEOUT) {
                        Log.e(TAG, "Timeout on server query request");
                    }
                })
                .compose(new RxFit.OnExceptionResumeNext.Single<>(RxFit.History.read(dataReadRequest)))
                .flatMapObservable(dataReadResult -> Observable.from(dataReadResult.getBuckets()))
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(bucket -> {
                    processFitBucket(bucket);
                }, e -> {
                    mProgressBar.setVisibility(View.GONE);
                    Log.e(TAG, "Error reading fitness data", e);
                    Snackbar.make(mProgressBar.getRootView(), "Error getting Fit data", Snackbar.LENGTH_LONG)
                            .setAction("Retry", v -> queryGoogleFit(timeRange))
                            .show();

                }, () -> {
                    mProgressBar.setVisibility(View.GONE);
                    updateUI();
                })
        );*/
    }

    /*private Exception handleException(Throwable throwable){
        Exception resultException;
        if(throwable.getMessage().equalsIgnoreCase(Manifest.permission.ACCESS_FINE_LOCATION)){
            requestMandatoryAppPermissions();
            resultException = new PermissionRequiredException();
        }else{
            resultException = new QueryTimeOutGoogleFitException();
        }
        return resultException;
    }*/

    private DataReadRequest.Builder buildFitDataReadRequest() {
        DataReadRequest.Builder builder = new DataReadRequest.Builder()
                .aggregate(DataType.TYPE_LOCATION_SAMPLE, DataType.AGGREGATE_LOCATION_BOUNDING_BOX)
                .aggregate(DataType.TYPE_ACTIVITY_SEGMENT, DataType.AGGREGATE_ACTIVITY_SUMMARY)
                .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
                .aggregate(DataType.TYPE_HEART_RATE_BPM, DataType.AGGREGATE_HEART_RATE_SUMMARY)
                .aggregate(DataType.TYPE_CALORIES_EXPENDED, DataType.AGGREGATE_CALORIES_EXPENDED)
                .aggregate(DataType.TYPE_DISTANCE_DELTA, DataType.AGGREGATE_DISTANCE_DELTA);
        return builder;
    }


    /*@RxLogObservable
    @Override
    public Observable<User> loginOrRegisterUserInHermes(final GoogleSignInAccount account) {
        String email = account.getEmail();
        return Observable.create(subscriber -> {
            mHermesCitizenApi.existsUser(email)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext(booleanResponse -> {
                        if(booleanResponse.body()) {
                            // User exists
                            subscriber.onNext(new User(email,null));
                            subscriber.onCompleted();
                        }else{
                            // User doesn't exist, sign up user
                            mHermesCitizenApi.registerUser(new User(email, Constants.DEFAULT_PASSWORD))
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .doOnNext(integerResponse -> {
                                    if (integerResponse.body() == HermesCitizenApi.RESPONSE_OK) {
                                        subscriber.onNext(new User(email, null));
                                        subscriber.onCompleted();
                                    } else {
                                        subscriber.onError(getHermesException(integerResponse.body()));
                                    }
                                })
                                .doOnError(throwable -> subscriber.onError(new UnknownErrorHermesException()))
                                .subscribe();
                        }
                    })
                    .doOnError(throwable -> subscriber.onError(new UnknownErrorHermesException()))
                    .subscribe();
        });
    }*/

}