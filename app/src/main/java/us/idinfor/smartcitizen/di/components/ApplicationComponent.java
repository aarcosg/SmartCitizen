package us.idinfor.smartcitizen.di.components;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import dagger.Component;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import us.idinfor.smartcitizen.data.api.hermes.HermesCitizenApi;
import us.idinfor.smartcitizen.di.modules.ApplicationModule;
import us.idinfor.smartcitizen.di.modules.InteractorsModule;
import us.idinfor.smartcitizen.di.modules.NetworkModule;
import us.idinfor.smartcitizen.di.scopes.PerApp;
import us.idinfor.smartcitizen.interactor.FitnessInteractor;
import us.idinfor.smartcitizen.interactor.LoginInteractor;
import us.idinfor.smartcitizen.interactor.MainInteractor;
import us.idinfor.smartcitizen.ui.activity.BaseActivity;

@PerApp
@Component(
        modules = {
                ApplicationModule.class,
                NetworkModule.class,
                InteractorsModule.class
        }
)
public interface ApplicationComponent {

    void inject(BaseActivity baseActivity);

    Context getContext();
    SharedPreferences getSharedPreferences();

    OkHttpClient getOkHttpClient();
    Gson getGson();
    Retrofit.Builder getRetrofitBuilder();
    HermesCitizenApi getHermesCitizenApi();

    LoginInteractor getLoginInteractor();
    MainInteractor getMainInteractor();
    FitnessInteractor getFitnessInteractor();

}