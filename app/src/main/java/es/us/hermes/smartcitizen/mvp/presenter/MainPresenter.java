package es.us.hermes.smartcitizen.mvp.presenter;


import es.us.hermes.smartcitizen.mvp.model.hermes.User;

public interface MainPresenter extends Presenter {

    User getUser();
    void onCreate();
    void bindDrawerLearnedFlag();
    void onDrawerLearned();
}
