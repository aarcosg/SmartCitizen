package us.idinfor.smartcitizen.di.components;

import dagger.Component;
import us.idinfor.smartcitizen.di.modules.ApplicationModule;
import us.idinfor.smartcitizen.di.modules.NetworkModule;
import us.idinfor.smartcitizen.di.scopes.PerApp;
import us.idinfor.smartcitizen.fragment.LoginActivityFragment;
import us.idinfor.smartcitizen.hermes.HermesCitizenApi;
import us.idinfor.smartcitizen.hermes.HermesCitizenSyncService;

@PerApp
@Component(dependencies = ApplicationModule.class, modules = NetworkModule.class)
public interface NetworkComponent {

    void inject(HermesCitizenSyncService hermesCitizenSyncService);
    void inject(LoginActivityFragment loginActivityFragment);

    HermesCitizenApi provideHermesCitizenApi();

}
