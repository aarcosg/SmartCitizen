package es.us.hermes.smartcitizen.di.modules;

import dagger.Module;
import dagger.Provides;
import es.us.hermes.smartcitizen.di.scopes.PerActivity;
import es.us.hermes.smartcitizen.interactor.LocationDetailsInteractor;
import es.us.hermes.smartcitizen.mvp.presenter.LocationDetailsPresenter;
import es.us.hermes.smartcitizen.mvp.presenter.LocationDetailsPresenterImpl;

@Module
public class LocationDetailsModule {

    @Provides
    @PerActivity
    public LocationDetailsPresenter provideLocationDetailsPresenter(LocationDetailsInteractor locationDetailsInteractor) {
        return new LocationDetailsPresenterImpl(locationDetailsInteractor);
    }
}