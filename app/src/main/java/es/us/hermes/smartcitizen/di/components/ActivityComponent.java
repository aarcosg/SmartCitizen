package es.us.hermes.smartcitizen.di.components;

import android.app.Activity;

import dagger.Component;
import es.us.hermes.smartcitizen.di.modules.ActivityModule;
import es.us.hermes.smartcitizen.di.scopes.PerActivity;

@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = ActivityModule.class)
public interface ActivityComponent {

    Activity getActivity();

}