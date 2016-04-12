package us.idinfor.smartcitizen.mvp.presenter;


import us.idinfor.smartcitizen.data.api.hermes.entity.User;

public interface MainPresenter extends Presenter {

    User getUser();
    void onCreate();
    void bindDrawerLearnedFlag();
    void onDrawerLearned();
}
