package es.us.hermes.smartcitizen.di.modules;

import android.content.Context;
import android.content.SharedPreferences;

import dagger.Module;
import dagger.Provides;
import es.us.hermes.smartcitizen.data.api.hermes.HermesCitizenApi;
import es.us.hermes.smartcitizen.data.api.ztreamy.ZtreamyApi;
import es.us.hermes.smartcitizen.di.scopes.PerApp;
import es.us.hermes.smartcitizen.interactor.ActivityTimelineInteractor;
import es.us.hermes.smartcitizen.interactor.ActivityTimelineInteractorImpl;
import es.us.hermes.smartcitizen.interactor.FitnessInteractor;
import es.us.hermes.smartcitizen.interactor.FitnessInteractorImpl;
import es.us.hermes.smartcitizen.interactor.LocationDetailsInteractor;
import es.us.hermes.smartcitizen.interactor.LocationDetailsInteractorImpl;
import es.us.hermes.smartcitizen.interactor.LoginInteractor;
import es.us.hermes.smartcitizen.interactor.LoginInteractorImpl;
import es.us.hermes.smartcitizen.interactor.MainInteractor;
import es.us.hermes.smartcitizen.interactor.MainInteractorImpl;
import es.us.hermes.smartcitizen.interactor.SyncServiceInteractor;
import es.us.hermes.smartcitizen.interactor.SyncServiceInteractorImpl;
import es.us.hermes.smartcitizen.utils.RxNetwork;

@Module
public class InteractorsModule {

    @Provides
    @PerApp
    public LoginInteractor provideLoginInteractor(RxNetwork rxNetwork, HermesCitizenApi hermesCitizenApi, SharedPreferences preferences) {
        return new LoginInteractorImpl(rxNetwork, hermesCitizenApi, preferences);
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

    @Provides
    @PerApp
    public LocationDetailsInteractor provideLocationDetailsInteractor() {
        return new LocationDetailsInteractorImpl();
    }

    @Provides
    @PerApp
    public SyncServiceInteractor provideSyncServiceInteractor(SharedPreferences preferences,
        RxNetwork rxNetwork, HermesCitizenApi hermesCitizenApi, ZtreamyApi ztreamyApi){
            return new SyncServiceInteractorImpl(preferences, rxNetwork, hermesCitizenApi, ztreamyApi);
    }
}
