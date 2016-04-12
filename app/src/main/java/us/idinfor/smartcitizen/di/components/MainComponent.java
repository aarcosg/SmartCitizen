package us.idinfor.smartcitizen.di.components;

import dagger.Component;
import us.idinfor.smartcitizen.di.modules.ActivityModule;
import us.idinfor.smartcitizen.di.modules.FitnessModule;
import us.idinfor.smartcitizen.di.modules.MainModule;
import us.idinfor.smartcitizen.di.scopes.PerActivity;
import us.idinfor.smartcitizen.mvp.presenter.FitnessPresenter;
import us.idinfor.smartcitizen.mvp.presenter.MainPresenter;
import us.idinfor.smartcitizen.ui.activity.MainActivity;
import us.idinfor.smartcitizen.ui.fragment.FitnessFragment;

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