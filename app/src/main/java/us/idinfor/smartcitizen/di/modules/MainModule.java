package us.idinfor.smartcitizen.di.modules;

import dagger.Module;
import dagger.Provides;
import us.idinfor.smartcitizen.di.scopes.PerActivity;
import us.idinfor.smartcitizen.interactor.MainInteractor;
import us.idinfor.smartcitizen.mvp.presenter.MainPresenter;
import us.idinfor.smartcitizen.mvp.presenter.MainPresenterImpl;

@Module
public class MainModule {

    @Provides
    @PerActivity
    public MainPresenter provideMainPresenter(MainInteractor mainInteractor) {
        return new MainPresenterImpl(mainInteractor);
    }
}