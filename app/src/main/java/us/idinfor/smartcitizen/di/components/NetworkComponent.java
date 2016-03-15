package us.idinfor.smartcitizen.di.components;

import javax.inject.Singleton;

import dagger.Component;
import us.idinfor.smartcitizen.activity.BaseActivity;
import us.idinfor.smartcitizen.di.modules.ApplicationModule;
import us.idinfor.smartcitizen.di.modules.NetworkModule;
import us.idinfor.smartcitizen.hermes.HermesCitizenApiService;

@Singleton
@Component(dependencies = ApplicationModule.class, modules = NetworkModule.class)
public interface NetworkComponent {

    void inject(BaseActivity baseActivity);

    HermesCitizenApiService provideHermesCitizenApiService();

}
