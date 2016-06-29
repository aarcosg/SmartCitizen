package es.us.hermes.smartcitizen.mvp.presenter;

import javax.inject.Inject;

import es.us.hermes.smartcitizen.data.api.hermes.entity.User;
import es.us.hermes.smartcitizen.interactor.MainInteractor;
import es.us.hermes.smartcitizen.mvp.view.MainView;
import es.us.hermes.smartcitizen.mvp.view.View;
import rx.Subscription;
import rx.subscriptions.Subscriptions;

public class MainPresenterImpl implements MainPresenter{

    private MainView mMainView;
    private final MainInteractor mMainInteractor;
    private Subscription mSubscription = Subscriptions.empty();

    @Inject
    public MainPresenterImpl(MainInteractor mainInteractor){
        this.mMainInteractor = mainInteractor;
    }

    @Override
    public void setView(View v) {
        mMainView = (MainView) v;
    }

    @Override
    public User getUser() {
        return this.mMainInteractor.getUserFromPreferences();
    }

    @Override
    public void onCreate() {
        // Read in the flag indicating whether or not the user has demonstrated awareness of the drawer.
        this.bindDrawerLearnedFlag();
        this.mMainView.setupNavigationDrawer();
        this.mMainView.openDrawerNotLearned();
        this.mMainView.setupNavigationDrawerHeader();
        this.mMainView.selectDrawerItem();
    }

    @Override
    public void onPause() {
        if(!mSubscription.isUnsubscribed()){
            mSubscription.unsubscribe();
        }
    }

    @Override
    public void bindDrawerLearnedFlag() {
        boolean isDrawerLearned = this.mMainInteractor.isDrawerLearnedInPreferences();
        this.mMainView.bindDrawerLearned(isDrawerLearned);
    }

    @Override
    public void onDrawerLearned() {
        this.mMainInteractor.setDrawerLearnedInPreferences(true);
        this.mMainView.bindDrawerLearned(true);
    }

}