package es.us.hermes.smartcitizen.di.modules;

import dagger.Module;
import dagger.Provides;
import es.us.hermes.smartcitizen.di.scopes.PerActivity;
import es.us.hermes.smartcitizen.interactor.FitnessInteractor;
import es.us.hermes.smartcitizen.mvp.presenter.FitnessPresenter;
import es.us.hermes.smartcitizen.mvp.presenter.FitnessPresenterImpl;

@Module
public class FitnessModule {

    @Provides
    @PerActivity
    public FitnessPresenter provideFitnessPresenter(FitnessInteractor fitnessInteractor) {
        return new FitnessPresenterImpl(fitnessInteractor);
    }
}