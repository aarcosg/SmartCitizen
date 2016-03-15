package us.idinfor.smartcitizen.di.components;

import dagger.Component;
import us.idinfor.smartcitizen.di.modules.BaseActivityModule;
import us.idinfor.smartcitizen.di.modules.GoogleApiClientModule;
import us.idinfor.smartcitizen.di.scopes.PerActivity;
import us.idinfor.smartcitizen.fragment.LoginActivityFragment;

@PerActivity
@Component(
        dependencies = {ApplicationComponent.class},
        modules = {BaseActivityModule.class, GoogleApiClientModule.class}
)
public interface GoogleApiClientComponent extends BaseActivityComponent {

    //void inject(GoogleApiClientBaseActivity googleApiClientBaseActivity);

    void inject(LoginActivityFragment loginActivityFragment);

}