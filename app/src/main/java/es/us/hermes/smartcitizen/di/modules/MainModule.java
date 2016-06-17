package es.us.hermes.smartcitizen.di.modules;

import dagger.Module;
import dagger.Provides;
import es.us.hermes.smartcitizen.di.scopes.PerActivity;
import es.us.hermes.smartcitizen.interactor.MainInteractor;
import es.us.hermes.smartcitizen.mvp.presenter.MainPresenter;
import es.us.hermes.smartcitizen.mvp.presenter.MainPresenterImpl;

@Module
public class MainModule {

    @Provides
    @PerActivity
    public MainPresenter provideMainPresenter(MainInteractor mainInteractor) {
        return new MainPresenterImpl(mainInteractor);
    }
}