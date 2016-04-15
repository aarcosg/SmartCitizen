package us.idinfor.smartcitizen.di.modules;

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
import us.idinfor.smartcitizen.data.api.hermes.HermesCitizenApi;
import us.idinfor.smartcitizen.data.api.ztreamy.ZtreamyApi;
import us.idinfor.smartcitizen.di.scopes.PerApp;
import us.idinfor.smartcitizen.mvp.presenter.SyncServicePresenter;
import us.idinfor.smartcitizen.service.SyncService;

@Module
public class NetworkModule {

    public final static String NAME_RETROFIT_HERMESCITIZEN = "NAME_RETROFIT_HERMESCITIZEN";
    public final static String NAME_RETROFIT_ZTREAMY = "NAME_RETROFIT_ZTREAMY";
    private final static long SECONDS_TIMEOUT = 20;

    @Provides
    @PerApp
    Cache provideOkHttpCache(Context context) {
        int cacheSize = 10 * 1024 * 1024; // 10 MiB
        return new Cache(context.getCacheDir(), cacheSize);
    }

    @Provides
    @PerApp
    OkHttpClient provideOkHttpClient(Cache cache) {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        return new OkHttpClient.Builder()
                .connectTimeout(SECONDS_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(SECONDS_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(SECONDS_TIMEOUT, TimeUnit.SECONDS)
                .cache(cache)
                .addInterceptor(logging)
                .build();
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
