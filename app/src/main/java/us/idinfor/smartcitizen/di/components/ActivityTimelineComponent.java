package us.idinfor.smartcitizen.di.components;

import dagger.Component;
import us.idinfor.smartcitizen.di.modules.ActivityModule;
import us.idinfor.smartcitizen.di.modules.ActivityTimelineModule;
import us.idinfor.smartcitizen.di.scopes.PerActivity;
import us.idinfor.smartcitizen.mvp.presenter.ActivityTimelinePresenter;
import us.idinfor.smartcitizen.ui.fragment.ActivityTimelineFragment;

@PerActivity
@Component(
        dependencies = ApplicationComponent.class,
        modules = {ActivityModule.class, ActivityTimelineModule.class})
public interface ActivityTimelineComponent extends ActivityComponent{

    void inject(ActivityTimelineFragment activityTimelineFragment);

    ActivityTimelinePresenter getActivityTimelinePresenter();

}