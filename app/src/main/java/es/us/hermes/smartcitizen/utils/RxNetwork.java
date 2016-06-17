package es.us.hermes.smartcitizen.utils;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import rx.Completable;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import es.us.hermes.smartcitizen.exception.InternetNotAvailableException;

public class RxNetwork {

    private static final String TAG = RxNetwork.class.getCanonicalName();

    private final Context mContext;

    public RxNetwork(Context context){
        this.mContext = context;
    }

    public Completable checkInternetConnection() {
        return Completable.fromObservable(
            Observable.create(new CheckInternetConnectionObservable(mContext))
                .doOnError(throwable -> Log.e(TAG,"Internet not available"))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()));
    }

    private class CheckInternetConnectionObservable implements Observable.OnSubscribe<Void> {

        private Context mContext;

        public CheckInternetConnectionObservable(Context context){
            this.mContext = context;
        }

        @Override
        public void call(Subscriber<? super Void> subscriber) {
            if(isInternetAvailable()){
                subscriber.onCompleted();
            }else{
                subscriber.onError(new InternetNotAvailableException());
            }
        }

        private boolean isInternetAvailable(){
            ConnectivityManager cm =
                    (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null &&
                    activeNetwork.isConnectedOrConnecting();
            return isConnected;
        }
    }
}

