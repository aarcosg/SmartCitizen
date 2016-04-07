package us.idinfor.smartcitizen.di.components;

import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;

import dagger.Component;
import us.idinfor.smartcitizen.di.modules.ActivityModule;
import us.idinfor.smartcitizen.di.modules.GoogleApiClientSignInModule;
import us.idinfor.smartcitizen.di.modules.LoginModule;
import us.idinfor.smartcitizen.di.scopes.PerActivity;
import us.idinfor.smartcitizen.interactor.LoginInteractor;
import us.idinfor.smartcitizen.mvp.presenter.LoginPresenter;
import us.idinfor.smartcitizen.ui.fragment.LoginFragment;

@PerActivity
@Component(
        dependencies = ApplicationComponent.class,
        modules = {ActivityModule.class, LoginModule.class, GoogleApiClientSignInModule.class})
public interface LoginComponent extends ActivityComponent{

    void inject (LoginFragment loginFragment);

    LoginInteractor loginInteractor();
    LoginPresenter loginPresenter();

    GoogleApiClient googleApiClient();
    GoogleSignInOptions googleSignInOptions();

}