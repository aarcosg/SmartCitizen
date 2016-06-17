package es.us.hermes.smartcitizen.di.modules;

import android.content.Context;

import com.google.gson.Gson;

import java.util.concurrent.TimeUnit;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import es.us.hermes.smartcitizen.BuildConfig;
import es.us.hermes.smartcitizen.data.api.hermes.HermesCitizenApi;
import es.us.hermes.smartcitizen.data.api.ztreamy.ZtreamyApi;
import es.us.hermes.smartcitizen.di.scopes.PerApp;
import es.us.hermes.smartcitizen.mvp.presenter.SyncServicePresenter;
import es.us.hermes.smartcitizen.service.SyncService;
import es.us.hermes.smartcitizen.utils.RxNetwork;

@Module
public class NetworkModule {

    public final static String NAME_RETROFIT_HERMESCITIZEN = "NAME_RETROFIT_HERMESCITIZEN";
    public final static String NAME_RETROFIT_ZTREAMY = "NAME_RETROFIT_ZTREAMY";
    private final static long SECONDS_TIMEOUT = 20;

    @Provides
    @PerApp
    RxNetwork provideRxNetwork(Context context){
        return new RxNetwork(context);
    }

    @Provides
    @PerApp
    Cache provideOkHttpCache(Context context) {
        int cacheSize = 10 * 1024 * 1024; // 10 MiB
        return new Cache(context.getCacheDir(), cacheSize);
    }

    @Provides
    @PerApp
    OkHttpClient provideOkHttpClient(Cache cache) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(SECONDS_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(SECONDS_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(SECONDS_TIMEOUT, TimeUnit.SECONDS)
                .cache(cache);
        if(BuildConfig.DEBUG){
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(logging);
        }
        return builder.build();
    }

    @Provides
    @PerApp
    Gson provideGson() {
        return new Gson();
    }

    @Provides
    @PerApp
    Retrofit.Builder provideRetrofit(Gson gson, OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(okHttpClient);
    }

    @Named(NAME_RETROFIT_HERMESCITIZEN)
    @Provides
    @PerApp
    Retrofit provideHermesCitizenRetrofit(Retrofit.Builder builder) {
        return builder
                .baseUrl(HermesCitizenApi.SERVICE_ENDPOINT)
                .build();
    }

    @Provides
    @PerApp
    HermesCitizenApi provideHermesCitizenApi(
            @Named(NAME_RETROFIT_HERMESCITIZEN) Retrofit retrofit) {
        return retrofit.create(HermesCitizenApi.class);
    }

    @Named(NAME_RETROFIT_ZTREAMY)
    @Provides
    @PerApp
    Retrofit provideZtreamyRetrofit(Retrofit.Builder builder) {
        return builder
                .baseUrl(ZtreamyApi.SERVICE_ENDPOINT)
                .build();
    }

    @Provides
    @PerApp
    ZtreamyApi provideZtreamyApi(
            @Named(NAME_RETROFIT_ZTREAMY) Retrofit retrofit) {
        return retrofit.create(ZtreamyApi.class);
    }

    @Provides
    @PerApp
    SyncService provideSyncService(Context context, SyncServicePresenter syncServicePresenter){
        return new SyncService(context,syncServicePresenter);
    }

}
