package es.us.hermes.smartcitizen.di.components;

import dagger.Component;
import es.us.hermes.smartcitizen.di.modules.ActivityModule;
import es.us.hermes.smartcitizen.di.modules.LocationDetailsModule;
import es.us.hermes.smartcitizen.di.scopes.PerActivity;
import es.us.hermes.smartcitizen.mvp.presenter.LocationDetailsPresenter;
import es.us.hermes.smartcitizen.ui.fragment.LocationDetailsFragment;

@PerActivity
@Component(
        dependencies = ApplicationComponent.class,
        modules = {ActivityModule.class, LocationDetailsModule.class})
public interface LocationDetailsComponent extends ActivityComponent{

    void inject(LocationDetailsFragment locationDetailsFragment);

    LocationDetailsPresenter getLocationDetailsPresenter();

}