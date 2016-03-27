package us.idinfor.smartcitizen.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;
import us.idinfor.smartcitizen.activity.BaseActivity;

public abstract class BaseFragment extends Fragment {

    private final CompositeSubscription mCompositeSubscription = new CompositeSubscription();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        injectActivityComponent();
    }

    @Override
    public void onDestroy() {
        unSubscribeFromFragment();
        super.onDestroy();
    }

    protected BaseActivity getBaseActivity() {
        return (BaseActivity) getActivity();
    }

    protected abstract void injectActivityComponent();

    protected void subscribeToFragment(Subscription subscription) {
        mCompositeSubscription.add(subscription);
    }

    protected void unSubscribeFromFragment() {
        mCompositeSubscription.unsubscribe();
    }
}