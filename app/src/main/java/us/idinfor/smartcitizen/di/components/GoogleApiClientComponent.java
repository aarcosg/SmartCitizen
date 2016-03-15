package us.idinfor.smartcitizen.di.components;

import dagger.Component;
import us.idinfor.smartcitizen.activity.LoginActivity;
import us.idinfor.smartcitizen.activity.MainActivity;
import us.idinfor.smartcitizen.di.modules.ApplicationModule;
import us.idinfor.smartcitizen.di.modules.BaseActivityModule;
import us.idinfor.smartcitizen.di.modules.GoogleApiClientModule;
import us.idinfor.smartcitizen.di.scopes.PerActivity;

@PerActivity
@Component(
        dependencies = {ApplicationComponent.class},
        modules = {BaseActivityModule.class, GoogleApiClientModule.class}
)
public interface GoogleApiClientComponent extends BaseActivityComponent {

    void inject(GoogleApiClientBaseActivity googleApiClientBaseActivity);

    void inject(LoginActivity loginActivity);

}