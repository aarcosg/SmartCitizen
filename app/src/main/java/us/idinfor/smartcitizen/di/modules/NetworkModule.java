package us.idinfor.smartcitizen.di.modules;

import android.content.Context;

import com.google.gson.Gson;

import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import us.idinfor.smartcitizen.di.qualifiers.Named;
import us.idinfor.smartcitizen.hermes.HermesCitizenApiService;

@Module
public class NetworkModule {

    private final static String NAME_RETROFIT_HERMESCITIZEN = "NAME_RETROFIT_HERMESCITIZEN";
    private final static long SECONDS_TIMEOUT = 20;

    @Provides
    @Singleton
    Cache provideOkHttpCache(Context context) {
        int cacheSize = 10 * 1024 * 1024; // 10 MiB
        return new Cache(context.getCacheDir(), cacheSize);
    }

    @Provides
    @Singleton
    OkHttpClient provideOkHttpClient(Cache cache) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(SECONDS_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(SECONDS_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(SECONDS_TIMEOUT, TimeUnit.SECONDS)
                .cache(cache)
                .build();
        return client;
    }

    @Provides
    @Singleton
    Gson provideGson() {
        return new Gson();
    }

    @Provides
    @Singleton
    Retrofit.Builder provideRetrofit(Gson gson, OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(okHttpClient);
    }

    @Named(NAME_RETROFIT_HERMESCITIZEN)
    @Provides
    @Singleton
    public Retrofit provideHermesCitizenRetrofit(Retrofit.Builder builder) {
        return builder
                .baseUrl(HermesCitizenApiService.SERVICE_ENDPOINT)
                .build();
    }

    @Provides
    @Singleton
    HermesCitizenApiService provideHermesCitizenApiService(
            @Named(NAME_RETROFIT_HERMESCITIZEN) Retrofit retrofit) {
        return retrofit.create(HermesCitizenApiService.class);
    }

}
