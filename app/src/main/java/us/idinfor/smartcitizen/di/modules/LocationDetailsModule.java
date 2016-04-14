package us.idinfor.smartcitizen.di.modules;

import dagger.Module;
import dagger.Provides;
import us.idinfor.smartcitizen.di.scopes.PerActivity;
import us.idinfor.smartcitizen.interactor.LocationDetailsInteractor;
import us.idinfor.smartcitizen.mvp.presenter.LocationDetailsPresenter;
import us.idinfor.smartcitizen.mvp.presenter.LocationDetailsPresenterImpl;

@Module
public class LocationDetailsModule {

    @Provides
    @PerActivity
    public LocationDetailsPresenter provideLocationDetailsPresenter(LocationDetailsInteractor locationDetailsInteractor) {
        return new LocationDetailsPresenterImpl(locationDetailsInteractor);
    }
}