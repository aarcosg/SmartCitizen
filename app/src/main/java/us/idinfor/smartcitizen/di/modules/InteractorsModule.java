package us.idinfor.smartcitizen.di.modules;

import android.content.SharedPreferences;

import dagger.Module;
import dagger.Provides;
import us.idinfor.smartcitizen.data.api.hermes.HermesCitizenApi;
import us.idinfor.smartcitizen.di.scopes.PerApp;
import us.idinfor.smartcitizen.interactor.LoginInteractor;
import us.idinfor.smartcitizen.interactor.LoginInteractorImpl;

@Module
public class InteractorsModule {

    @Provides
    @PerApp
    public LoginInteractor provideLoginInteractor(
            HermesCitizenApi hermesCitizenApi,
            SharedPreferences preferences) {
        return new LoginInteractorImpl(hermesCitizenApi,preferences);
    }
}
