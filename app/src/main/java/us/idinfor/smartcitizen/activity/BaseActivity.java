package us.idinfor.smartcitizen.activity;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;
import us.idinfor.smartcitizen.R;
import us.idinfor.smartcitizen.SmartCitizenApplication;
import us.idinfor.smartcitizen.di.components.ApplicationComponent;
import us.idinfor.smartcitizen.di.components.BaseActivityComponent;
import us.idinfor.smartcitizen.di.modules.BaseActivityModule;


public abstract class BaseActivity extends AppCompatActivity {

    Toolbar mActionBarToolbar;

    private final CompositeSubscription mCompositeSubscription = new CompositeSubscription();
    private ViewDataBinding mBinding;
    private BaseActivityComponent mBaseActivityComponent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        initDependencies();
        super.onCreate(savedInstanceState);

        int layoutResourceId = getLayoutResourceId();

        mBinding = DataBindingUtil.setContentView(this, layoutResourceId);
    }

    private void initDependencies() {
        mBaseActivityComponent = DaggerBaseActivityComponent
                .builder()
                .nearbooksApplicationComponent(getNearbooksApplicationComponent())
                .baseActivityModule(new BaseActivityModule(this))
                .build();
        injectComponent(mBaseActivityComponent);
    }

    @Override
    protected void onDestroy() {
        unSubscribeFromActivity();

        super.onDestroy();
    }

    protected abstract int getLayoutResourceId();

    protected int getToolbarId() {
        return R.id.toolbar;
    }

    protected <T extends ViewDataBinding> T getBinding(Class<T> clazz) {
        return clazz.cast(mBinding);
    }

    protected void injectComponent(BaseActivityComponent baseActivityComponent) {
        baseActivityComponent.inject(this);
    }

    protected void subscribeToActivity(Subscription subscription) {
        mCompositeSubscription.add(subscription);
    }

    protected void unSubscribeFromActivity() {
        mCompositeSubscription.clear();
    }

    public BaseActivityComponent getBaseActivityComponent() {
        return mBaseActivityComponent;
    }

    protected BaseActivityModule getBaseActivityModule() {
        return new BaseActivityModule(this);
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
