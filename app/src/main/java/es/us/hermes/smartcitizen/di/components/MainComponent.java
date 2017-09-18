package es.us.hermes.smartcitizen.di.components;

import dagger.Component;
import es.us.hermes.smartcitizen.di.modules.ActivityModule;
import es.us.hermes.smartcitizen.di.modules.MainModule;
import es.us.hermes.smartcitizen.di.scopes.PerActivity;
import es.us.hermes.smartcitizen.mvp.presenter.MainPresenter;
import es.us.hermes.smartcitizen.ui.activity.MainActivity;
import es.us.hermes.smartcitizen.ui.fragment.BackupFragment;
import es.us.hermes.smartcitizen.ui.fragment.MainFitnessFragment;

@PerActivity
@Component(
        dependencies = ApplicationComponent.class,
        modules = {
                ActivityModule.class,
                MainModule.class
        })
public interface MainComponent extends ActivityComponent{

    void inject(MainActivity mainActivity);
    void inject(MainFitnessFragment mainFitnessFragment);
    void inject(BackupFragment backupFragment);

    MainPresenter getMainPresenter();

}