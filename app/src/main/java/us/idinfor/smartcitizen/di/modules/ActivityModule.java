package us.idinfor.smartcitizen.di.modules;

import android.app.Activity;

import dagger.Module;
import dagger.Provides;
import us.idinfor.smartcitizen.di.scopes.PerActivity;

@Module
public class ActivityModule {
    private final Activity activity;

    public ActivityModule(Activity activity) {
      this.activity = activity;
    }

    @Provides
    @PerActivity
    Activity activity() {
      return this.activity;
    }
}