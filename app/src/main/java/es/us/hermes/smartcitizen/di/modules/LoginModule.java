package es.us.hermes.smartcitizen.di.modules;

import dagger.Module;
import dagger.Provides;
import es.us.hermes.smartcitizen.di.scopes.PerActivity;
import es.us.hermes.smartcitizen.interactor.LoginInteractor;
import es.us.hermes.smartcitizen.mvp.presenter.LoginPresenter;
import es.us.hermes.smartcitizen.mvp.presenter.LoginPresenterImpl;

@Module
public class LoginModule {

    @Provides
    @PerActivity
    public LoginPresenter provideLoginPresenter(LoginInteractor loginInteractor) {
        return new LoginPresenterImpl(loginInteractor);
    }
}