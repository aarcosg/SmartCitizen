package us.idinfor.smartcitizen.di.modules;

import android.content.Context;
import android.content.SharedPreferences;

import dagger.Module;
import dagger.Provides;
import us.idinfor.smartcitizen.data.api.hermes.HermesCitizenApi;
import us.idinfor.smartcitizen.di.scopes.PerApp;
import us.idinfor.smartcitizen.interactor.ActivityTimelineInteractor;
import us.idinfor.smartcitizen.interactor.ActivityTimelineInteractorImpl;
import us.idinfor.smartcitizen.interactor.FitnessInteractor;
import us.idinfor.smartcitizen.interactor.FitnessInteractorImpl;
import us.idinfor.smartcitizen.interactor.LoginInteractor;
import us.idinfor.smartcitizen.interactor.LoginInteractorImpl;
import us.idinfor.smartcitizen.interactor.MainInteractor;
import us.idinfor.smartcitizen.interactor.MainInteractorImpl;

@Module
public class InteractorsModule {

    @Provides
    @PerApp
    public LoginInteractor provideLoginInteractor(HermesCitizenApi hermesCitizenApi, SharedPreferences preferences) {
        return new LoginInteractorImpl(hermesCitizenApi,preferences);
    }

    @Provides
    @PerApp
    public MainInteractor provideMainInteractor(SharedPreferences preferences) {
        return new MainInteractorImpl(preferences);
    }

    @Provides
    @PerApp
    public FitnessInteractor provideFitnessInteractor(Context context) {
        return new FitnessInteractorImpl(context);
    }

    @Provides
    @PerApp
    public ActivityTimelineInteractor provideActivityTimelineInteractor() {
        return new ActivityTimelineInteractorImpl();
    }
}
