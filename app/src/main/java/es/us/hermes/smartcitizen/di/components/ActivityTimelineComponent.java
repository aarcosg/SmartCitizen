package es.us.hermes.smartcitizen.di.components;

import dagger.Component;
import es.us.hermes.smartcitizen.di.modules.ActivityModule;
import es.us.hermes.smartcitizen.di.modules.ActivityTimelineModule;
import es.us.hermes.smartcitizen.di.scopes.PerActivity;
import es.us.hermes.smartcitizen.mvp.presenter.ActivityTimelinePresenter;
import es.us.hermes.smartcitizen.ui.fragment.ActivityTimelineFragment;

@PerActivity
@Component(
        dependencies = ApplicationComponent.class,
        modules = {ActivityModule.class, ActivityTimelineModule.class})
public interface ActivityTimelineComponent extends ActivityComponent{

    void inject(ActivityTimelineFragment activityTimelineFragment);

    ActivityTimelinePresenter getActivityTimelinePresenter();

}