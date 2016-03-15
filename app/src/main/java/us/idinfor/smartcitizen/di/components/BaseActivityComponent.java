package us.idinfor.smartcitizen.di.components;

import dagger.Component;
import us.idinfor.smartcitizen.activity.BaseActivity;
import us.idinfor.smartcitizen.activity.MainActivity;
import us.idinfor.smartcitizen.di.modules.BaseActivityModule;
import us.idinfor.smartcitizen.di.scopes.PerActivity;
import us.idinfor.smartcitizen.fragment.BaseFragment;

@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = BaseActivityModule.class)
public interface BaseActivityComponent {
    void inject (BaseActivity baseActivity);
    void inject (BaseFragment baseFragment);
    void inject (MainActivity mainActivity);
}