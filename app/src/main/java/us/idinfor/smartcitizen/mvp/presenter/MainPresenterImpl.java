package us.idinfor.smartcitizen.mvp.presenter;

import javax.inject.Inject;

import rx.Subscription;
import rx.subscriptions.Subscriptions;
import us.idinfor.smartcitizen.data.api.hermes.entity.User;
import us.idinfor.smartcitizen.interactor.MainInteractor;
import us.idinfor.smartcitizen.mvp.model.UserNotFoundInPreferencesException;
import us.idinfor.smartcitizen.mvp.view.MainView;
import us.idinfor.smartcitizen.mvp.view.View;

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
    public void onPause() {
        if(!mSubscription.isUnsubscribed()){
            mSubscription.unsubscribe();
        }
    }

    @Override
    public void bindUserLoggedIn() {
        try {
            User user = this.mMainInteractor.getUserInPreferences();
            this.mMainView.bindUser(user);
        } catch (UserNotFoundInPreferencesException e) {
            onUserNotFound();
        }
    }

    @Override
    public void onUserNotFound() {
        this.mMainView.navigateToLoginScreen();
        this.mMainView.finishActivity();
    }

    @Override
    public void bindDrawerLearned() {
        boolean isDrawerLearned = this.mMainInteractor.isDrawerLearnedInPreferences();
        this.mMainView.bindDrawerLearned(isDrawerLearned);
    }

    @Override
    public void onDrawerLearned() {
        this.mMainInteractor.setDrawerLearnedInPreferences(true);
        this.mMainView.bindDrawerLearned(true);
    }


}