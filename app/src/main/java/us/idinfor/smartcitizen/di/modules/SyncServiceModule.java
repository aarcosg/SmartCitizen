package us.idinfor.smartcitizen.di.modules;

import dagger.Module;
import dagger.Provides;
import us.idinfor.smartcitizen.di.scopes.PerApp;
import us.idinfor.smartcitizen.interactor.SyncServiceInteractor;
import us.idinfor.smartcitizen.mvp.presenter.SyncServicePresenter;
import us.idinfor.smartcitizen.mvp.presenter.SyncServicePresenterImpl;

@Module
public class SyncServiceModule {

    @Provides
    @PerApp
    public SyncServicePresenter provideSyncServicePresenter(SyncServiceInteractor syncServiceInteractor) {
        return new SyncServicePresenterImpl(syncServiceInteractor);
    }
}