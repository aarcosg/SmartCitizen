package us.idinfor.smartcitizen.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;
import us.idinfor.smartcitizen.R;
import us.idinfor.smartcitizen.SmartCitizenApplication;
import us.idinfor.smartcitizen.di.components.ActivityComponent;
import us.idinfor.smartcitizen.di.components.ApplicationComponent;
import us.idinfor.smartcitizen.di.components.DaggerActivityComponent;


public abstract class BaseActivity extends AppCompatActivity {

    private final CompositeSubscription mCompositeSubscription = new CompositeSubscription();
    private ActivityComponent mActivityComponent;

    protected Toolbar mActionBarToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initDependencies();
        injectActivityComponent();
    }

    @Override
    protected void onDestroy() {
        unSubscribeFromActivity();
        super.onDestroy();
    }

    private void initDependencies() {
        mActivityComponent = DaggerActivityComponent.builder()
                .applicationComponent(getApplicationComponent())
                .build();
    }

    protected abstract void injectActivityComponent();

    public ApplicationComponent getApplicationComponent(){
        return SmartCitizenApplication.get(this).getApplicationComponent();
    }

    public ActivityComponent getActivityComponent() {
        return mActivityComponent;
    }

    protected void subscribeToActivity(Subscription subscription) {
        mCompositeSubscription.add(subscription);
    }

    protected void unSubscribeFromActivity() {
        mCompositeSubscription.unsubscribe();
    }

    protected int getToolbarId() {
        return R.id.toolbar;
    }

    protected Toolbar buildActionBarToolbar(String title, boolean upEnabled) {
        if (mActionBarToolbar == null) {
            mActionBarToolbar = (Toolbar) findViewById(getToolbarId());
            if (mActionBarToolbar != null) {
                setSupportActionBar(mActionBarToolbar);
                if(getSupportActionBar()!= null){
                    getSupportActionBar().setDisplayHomeAsUpEnabled(upEnabled);
                    if(title != null){
                        getSupportActionBar().setTitle(title);
                    }
                }
            }
        }
        return mActionBarToolbar;
    }
}
