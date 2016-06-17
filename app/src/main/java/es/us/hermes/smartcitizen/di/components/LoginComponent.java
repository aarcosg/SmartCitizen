package es.us.hermes.smartcitizen.di.components;

import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;

import dagger.Component;
import es.us.hermes.smartcitizen.di.modules.ActivityModule;
import es.us.hermes.smartcitizen.di.modules.GoogleApiClientSignInModule;
import es.us.hermes.smartcitizen.di.modules.LoginModule;
import es.us.hermes.smartcitizen.di.scopes.PerActivity;
import es.us.hermes.smartcitizen.mvp.presenter.LoginPresenter;
import es.us.hermes.smartcitizen.ui.fragment.LoginFragment;

@PerActivity
@Component(
        dependencies = ApplicationComponent.class,
        modules = {ActivityModule.class, LoginModule.class, GoogleApiClientSignInModule.class})
public interface LoginComponent extends ActivityComponent{

    void inject (LoginFragment loginFragment);

    LoginPresenter getLoginPresenter();

    GoogleApiClient getGoogleApiClient();
    GoogleSignInOptions getGoogleSignInOptions();

}