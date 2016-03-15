package us.idinfor.smartcitizen.di.components;

import android.content.Context;
import android.content.SharedPreferences;

import dagger.Component;
import us.idinfor.smartcitizen.activity.BaseActivity;
import us.idinfor.smartcitizen.di.modules.ApplicationModule;
import us.idinfor.smartcitizen.di.modules.NetworkModule;
import us.idinfor.smartcitizen.di.scopes.PerApp;
import us.idinfor.smartcitizen.hermes.HermesCitizenApi;

@PerApp
@Component(modules = {ApplicationModule.class, NetworkModule.class})
public interface ApplicationComponent {

    void inject(BaseActivity baseActivity);

    Context context();
    SharedPreferences sharedPreferences();
    HermesCitizenApi hermesCitizenApi();
}