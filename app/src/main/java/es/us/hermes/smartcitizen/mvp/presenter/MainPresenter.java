package es.us.hermes.smartcitizen.mvp.presenter;


import es.us.hermes.smartcitizen.data.api.hermes.entity.User;

public interface MainPresenter extends Presenter {

    User getUser();
    void onCreate();
    void bindDrawerLearnedFlag();
    void onDrawerLearned();
}
