package us.idinfor.smartcitizen.di.modules;

import dagger.Module;
import dagger.Provides;
import us.idinfor.smartcitizen.di.scopes.PerActivity;
import us.idinfor.smartcitizen.interactor.FitnessInteractor;
import us.idinfor.smartcitizen.mvp.presenter.FitnessPresenter;
import us.idinfor.smartcitizen.mvp.presenter.FitnessPresenterImpl;

@Module
public class FitnessModule {

    @Provides
    @PerActivity
    public FitnessPresenter provideFitnessPresenter(FitnessInteractor fitnessInteractor) {
        return new FitnessPresenterImpl(fitnessInteractor);
    }
}