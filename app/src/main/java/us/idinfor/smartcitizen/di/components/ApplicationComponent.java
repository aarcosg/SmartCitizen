package us.idinfor.smartcitizen.di.components;

import android.content.Context;
import android.content.SharedPreferences;

import javax.inject.Singleton;

import dagger.Component;
import us.idinfor.smartcitizen.activity.BaseActivity;
import us.idinfor.smartcitizen.di.modules.ApplicationModule;

@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {

    void inject(BaseActivity baseActivity);

    Context context();
    SharedPreferences sharedPreferences();
}