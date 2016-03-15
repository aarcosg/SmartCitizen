package us.idinfor.smartcitizen.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;
import us.idinfor.smartcitizen.R;
import us.idinfor.smartcitizen.SmartCitizenApplication;
import us.idinfor.smartcitizen.di.components.ApplicationComponent;
import us.idinfor.smartcitizen.di.components.BaseActivityComponent;
import us.idinfor.smartcitizen.di.components.DaggerBaseActivityComponent;
import us.idinfor.smartcitizen.di.modules.BaseActivityModule;


public abstract class BaseActivity extends AppCompatActivity {

    protected Toolbar mActionBarToolbar;

    private final CompositeSubscription mCompositeSubscription = new CompositeSubscription();
    private BaseActivityComponent mBaseActivityComponent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initDependencies();
    }

    private void initDependencies() {
        mBaseActivityComponent = DaggerBaseActivityComponent.builder()
                .applicationComponent(getApplicationComponent())
                .baseActivityModule(getBaseActivityModule())
                .build();
        injectComponent(getBaseActivityComponent());
    }

    @Override
    protected void onDestroy() {
        unSubscribeFromActivity();
        super.onDestroy();
    }

    protected int getToolbarId() {
        return R.id.toolbar;
    }

    public ApplicationComponent getApplicationComponent(){
        return SmartCitizenApplication.getApplicationComponent();
    }

    protected void injectComponent(BaseActivityComponent baseActivityComponent) {
        baseActivityComponent.inject(this);
    }

    public BaseActivityComponent getBaseActivityComponent() {
        return mBaseActivityComponent;
    }

    public BaseActivityModule getBaseActivityModule() {
        return new BaseActivityModule(this);
    }

    protected void subscribeToActivity(Subscription subscription) {
        mCompositeSubscription.add(subscription);
    }

    protected void unSubscribeFromActivity() {
        mCompositeSubscription.clear();
    }

    protected Toolbar buildActionBarToolbar(String title, boolean upEnabled) {
        if (mActionBarToolbar == null) {
            mActionBarToolbar = (Toolbar) findViewById(getToolbarId());
            if (mActionBarToolbar != null) {
                setSupportActionBar(mActionBarToolbar);
                if(getSupportActionBar()!=null){
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
