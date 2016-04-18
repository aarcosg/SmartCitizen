package us.idinfor.smartcitizen.di.components;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import dagger.Component;
import okhttp3.OkHttpClient;
import us.idinfor.smartcitizen.data.api.hermes.HermesCitizenApi;
import us.idinfor.smartcitizen.data.api.ztreamy.ZtreamyApi;
import us.idinfor.smartcitizen.di.modules.ApplicationModule;
import us.idinfor.smartcitizen.di.modules.InteractorsModule;
import us.idinfor.smartcitizen.di.modules.NetworkModule;
import us.idinfor.smartcitizen.di.modules.SyncServiceModule;
import us.idinfor.smartcitizen.di.scopes.PerApp;
import us.idinfor.smartcitizen.interactor.ActivityTimelineInteractor;
import us.idinfor.smartcitizen.interactor.FitnessInteractor;
import us.idinfor.smartcitizen.interactor.LocationDetailsInteractor;
import us.idinfor.smartcitizen.interactor.LoginInteractor;
import us.idinfor.smartcitizen.interactor.MainInteractor;
import us.idinfor.smartcitizen.interactor.SyncServiceInteractor;
import us.idinfor.smartcitizen.receiver.OnBootReceiver;
import us.idinfor.smartcitizen.service.SyncService;
import us.idinfor.smartcitizen.ui.activity.BaseActivity;

@PerApp
@Component(
        modules = {
                ApplicationModule.class,
                NetworkModule.class,
                InteractorsModule.class,
                SyncServiceModule.class
        }
)
public interface ApplicationComponent {

    void inject(BaseActivity baseActivity);
    void inject(SyncService syncService);
    void inject(OnBootReceiver onBootReceiver);

    Context getContext();
    SharedPreferences getSharedPreferences();

    OkHttpClient getOkHttpClient();
    Gson getGson();
    HermesCitizenApi getHermesCitizenApi();
    ZtreamyApi getZtreamyApi();
    SyncService getSyncService();

    LoginInteractor getLoginInteractor();
    MainInteractor getMainInteractor();
    FitnessInteractor getFitnessInteractor();
    ActivityTimelineInteractor getActivityTimelineInteractor();
    LocationDetailsInteractor getLocationDetailsInteractor();
    SyncServiceInteractor getSyncServiceInteractor();

}