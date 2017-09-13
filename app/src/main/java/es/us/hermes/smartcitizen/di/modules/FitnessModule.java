package es.us.hermes.smartcitizen.di.modules;

import android.content.Context;

import dagger.Module;
import dagger.Provides;
import es.us.hermes.smartcitizen.di.scopes.PerFragment;
import es.us.hermes.smartcitizen.interactor.FitnessInteractor;
import es.us.hermes.smartcitizen.interactor.FitnessInteractorImpl;
import es.us.hermes.smartcitizen.mvp.presenter.FitnessPresenter;
import es.us.hermes.smartcitizen.mvp.presenter.FitnessPresenterImpl;

@Module
public class FitnessModule {

    @Provides
    @PerFragment
    public FitnessInteractor provideFitnessInteractor(Context context) {
        return new FitnessInteractorImpl(context);
    }

    @Provides
    @PerFragment
    public FitnessPresenter provideFitnessPresenter(FitnessInteractor fitnessInteractor) {
        return new FitnessPresenterImpl(fitnessInteractor);
    }
}