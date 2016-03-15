package us.idinfor.smartcitizen.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;
import us.idinfor.smartcitizen.activity.BaseActivity;
import us.idinfor.smartcitizen.di.components.BaseActivityComponent;

public abstract class BaseFragment extends Fragment {

    private final CompositeSubscription mCompositeSubscription = new CompositeSubscription();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        injectComponent(getBaseActivity().getBaseActivityComponent());
    }

    protected BaseActivity getBaseActivity() {
        return (BaseActivity) getActivity();
    }

    protected void injectComponent(BaseActivityComponent baseActivityComponent) {
        baseActivityComponent.inject(this);
    }

    @Override
    public void onDestroy() {
        unSubscribeFromActivity();

        super.onDestroy();
    }

    protected void subscribeToFragment(Subscription subscription) {
        mCompositeSubscription.add(subscription);
    }

    protected void unSubscribeFromActivity() {
        mCompositeSubscription.clear();
    }
}