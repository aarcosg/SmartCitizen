package us.idinfor.smartcitizen.di.components;

import dagger.Component;
import us.idinfor.smartcitizen.di.modules.GoogleApiClientSignInModule;
import us.idinfor.smartcitizen.di.scopes.PerActivity;
import us.idinfor.smartcitizen.fragment.LoginActivityFragment;

@PerActivity
@Component(
        dependencies = {ApplicationComponent.class},
        modules = {GoogleApiClientSignInModule.class}
)
public interface GoogleApiClientSignInComponent extends ActivityComponent {

    void inject(LoginActivityFragment loginActivityFragment);

}