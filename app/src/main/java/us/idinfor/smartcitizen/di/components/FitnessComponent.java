package us.idinfor.smartcitizen.di.components;

import dagger.Component;
import us.idinfor.smartcitizen.di.modules.ActivityModule;
import us.idinfor.smartcitizen.di.modules.FitnessModule;
import us.idinfor.smartcitizen.di.scopes.PerActivity;

@PerActivity
@Component(
        dependencies = ApplicationComponent.class,
        modules = {ActivityModule.class, FitnessModule.class})
public interface FitnessComponent extends ActivityComponent{

    /*void inject(FitnessFragment fitnessFragment);

    FitnessPresenter getFitnessPresenter();*/

}