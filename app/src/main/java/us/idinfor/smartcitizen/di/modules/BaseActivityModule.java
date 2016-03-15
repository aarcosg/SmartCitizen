package us.idinfor.smartcitizen.di.modules;

import dagger.Module;
import dagger.Provides;
import us.idinfor.smartcitizen.activity.BaseActivity;
import us.idinfor.smartcitizen.di.scopes.PerActivity;

@Module
public class BaseActivityModule {

    private final BaseActivity baseActivity;

    public BaseActivityModule(BaseActivity baseActivity) {
      this.baseActivity = baseActivity;
    }

    @Provides
    @PerActivity
    BaseActivity provideBaseActivity() {
      return this.baseActivity;
    }
}