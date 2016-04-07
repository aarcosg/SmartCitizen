package us.idinfor.smartcitizen.di.modules;

import dagger.Module;
import dagger.Provides;
import us.idinfor.smartcitizen.di.scopes.PerActivity;
import us.idinfor.smartcitizen.interactor.LoginInteractor;
import us.idinfor.smartcitizen.mvp.presenter.LoginPresenter;
import us.idinfor.smartcitizen.mvp.presenter.LoginPresenterImpl;

@Module
public class LoginModule {

    @Provides
    @PerActivity
    public LoginPresenter provideLoginPresenter(LoginInteractor loginInteractor) {
        return new LoginPresenterImpl(loginInteractor);
    }
}