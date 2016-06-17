package es.us.hermes.smartcitizen.di.modules;

import dagger.Module;
import dagger.Provides;
import es.us.hermes.smartcitizen.di.scopes.PerActivity;
import es.us.hermes.smartcitizen.interactor.ActivityTimelineInteractor;
import es.us.hermes.smartcitizen.mvp.presenter.ActivityTimelinePresenter;
import es.us.hermes.smartcitizen.mvp.presenter.ActivityTimelinePresenterImpl;

@Module
public class ActivityTimelineModule {

    @Provides
    @PerActivity
    public ActivityTimelinePresenter provideActivityTimelinePresenter(ActivityTimelineInteractor activityTimelineInteractor) {
        return new ActivityTimelinePresenterImpl(activityTimelineInteractor);
    }
}