package es.us.hermes.smartcitizen.di.modules;

import dagger.Module;
import dagger.Provides;
import es.us.hermes.smartcitizen.di.scopes.PerApp;
import es.us.hermes.smartcitizen.interactor.SyncServiceInteractor;
import es.us.hermes.smartcitizen.mvp.presenter.SyncServicePresenter;
import es.us.hermes.smartcitizen.mvp.presenter.SyncServicePresenterImpl;

@Module
public class SyncServiceModule {

    @Provides
    @PerApp
    public SyncServicePresenter provideSyncServicePresenter(SyncServiceInteractor syncServiceInteractor) {
        return new SyncServicePresenterImpl(syncServiceInteractor);
    }
}