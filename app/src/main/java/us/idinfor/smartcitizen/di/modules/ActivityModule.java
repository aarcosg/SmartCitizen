package us.idinfor.smartcitizen.di.modules;

import dagger.Module;
import dagger.Provides;
import us.idinfor.smartcitizen.activity.BaseActivity;
import us.idinfor.smartcitizen.di.scopes.PerActivity;

@Module
public class ActivityModule {

    private final BaseActivity baseActivity;

    public ActivityModule(BaseActivity baseActivity) {
      this.baseActivity = baseActivity;
    }

    @Provides
    @PerActivity
    BaseActivity provideBaseActivity() {
      return this.baseActivity;
    }
}