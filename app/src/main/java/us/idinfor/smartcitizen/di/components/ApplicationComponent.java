package us.idinfor.smartcitizen.di.components;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import dagger.Component;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import us.idinfor.smartcitizen.data.api.hermes.HermesCitizenApi;
import us.idinfor.smartcitizen.di.modules.ApplicationModule;
import us.idinfor.smartcitizen.di.modules.NetworkModule;
import us.idinfor.smartcitizen.di.scopes.PerApp;
import us.idinfor.smartcitizen.ui.activity.BaseActivity;

@PerApp
@Component(
        modules = {
                ApplicationModule.class,
                NetworkModule.class
        }
)
public interface ApplicationComponent {

    void inject(BaseActivity baseActivity);

    Context context();
    SharedPreferences sharedPreferences();

    OkHttpClient okHttpClient();
    Gson gson();
    Retrofit.Builder retrofitBuilder();
    HermesCitizenApi hermesCitizenApi();

}