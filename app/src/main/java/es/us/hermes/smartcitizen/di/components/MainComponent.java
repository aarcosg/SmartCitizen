package es.us.hermes.smartcitizen.di.components;

import dagger.Component;
import es.us.hermes.smartcitizen.di.modules.ActivityModule;
import es.us.hermes.smartcitizen.di.modules.FitnessModule;
import es.us.hermes.smartcitizen.di.modules.MainModule;
import es.us.hermes.smartcitizen.di.scopes.PerActivity;
import es.us.hermes.smartcitizen.mvp.presenter.FitnessPresenter;
import es.us.hermes.smartcitizen.mvp.presenter.MainPresenter;
import es.us.hermes.smartcitizen.ui.activity.MainActivity;
import es.us.hermes.smartcitizen.ui.fragment.FitnessFragment;

@PerActivity
@Component(
        dependencies = ApplicationComponent.class,
        modules = {
                ActivityModule.class,
                MainModule.class,
                FitnessModule.class
        })
public interface MainComponent extends ActivityComponent{

    void inject(MainActivity mainActivity);
    void inject(FitnessFragment fitnessFragment);

    MainPresenter getMainPresenter();
    FitnessPresenter getFitnessPresenter();

}