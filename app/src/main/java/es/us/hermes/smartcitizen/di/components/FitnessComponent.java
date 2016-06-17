package es.us.hermes.smartcitizen.di.components;

import dagger.Component;
import es.us.hermes.smartcitizen.di.modules.ActivityModule;
import es.us.hermes.smartcitizen.di.modules.FitnessModule;
import es.us.hermes.smartcitizen.di.scopes.PerActivity;

@PerActivity
@Component(
        dependencies = ApplicationComponent.class,
        modules = {ActivityModule.class, FitnessModule.class})
public interface FitnessComponent extends ActivityComponent{

    /*void inject(FitnessFragment fitnessFragment);

    FitnessPresenter getFitnessPresenter();*/

}