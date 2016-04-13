package us.idinfor.smartcitizen.di.modules;

import dagger.Module;
import dagger.Provides;
import us.idinfor.smartcitizen.di.scopes.PerActivity;
import us.idinfor.smartcitizen.interactor.ActivityTimelineInteractor;
import us.idinfor.smartcitizen.mvp.presenter.ActivityTimelinePresenter;
import us.idinfor.smartcitizen.mvp.presenter.ActivityTimelinePresenterImpl;

@Module
public class ActivityTimelineModule {

    @Provides
    @PerActivity
    public ActivityTimelinePresenter provideActivityTimelinePresenter(ActivityTimelineInteractor activityTimelineInteractor) {
        return new ActivityTimelinePresenterImpl(activityTimelineInteractor);
    }
}