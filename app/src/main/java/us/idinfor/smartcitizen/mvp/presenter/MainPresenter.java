package us.idinfor.smartcitizen.mvp.presenter;


public interface MainPresenter extends Presenter {
    void bindUserLoggedIn();
    void onUserNotFound();
    void bindDrawerLearned();
    void onDrawerLearned();
}
