package us.idinfor.smartcitizen.di.components;

import dagger.Component;
import us.idinfor.smartcitizen.di.modules.ActivityModule;
import us.idinfor.smartcitizen.di.modules.LocationDetailsModule;
import us.idinfor.smartcitizen.di.scopes.PerActivity;
import us.idinfor.smartcitizen.mvp.presenter.LocationDetailsPresenter;
import us.idinfor.smartcitizen.ui.fragment.LocationDetailsFragment;

@PerActivity
@Component(
        dependencies = ApplicationComponent.class,
        modules = {ActivityModule.class, LocationDetailsModule.class})
public interface LocationDetailsComponent extends ActivityComponent{

    void inject(LocationDetailsFragment locationDetailsFragment);

    LocationDetailsPresenter getLocationDetailsPresenter();

}