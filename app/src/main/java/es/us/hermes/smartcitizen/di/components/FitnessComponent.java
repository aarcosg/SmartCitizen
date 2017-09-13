package es.us.hermes.smartcitizen.di.components;

import dagger.Component;
import es.us.hermes.smartcitizen.di.modules.FitnessModule;
import es.us.hermes.smartcitizen.di.scopes.PerFragment;
import es.us.hermes.smartcitizen.interactor.FitnessInteractor;
import es.us.hermes.smartcitizen.mvp.presenter.FitnessPresenter;
import es.us.hermes.smartcitizen.ui.fragment.FitnessFragment;

@PerFragment
@Component(
        dependencies = {ApplicationComponent.class},
        modules = {FitnessModule.class})
public interface FitnessComponent{

    void inject(FitnessFragment fitnessFragment);

    FitnessPresenter getFitnessPresenter();
    FitnessInteractor getFitnessInteractor();
}