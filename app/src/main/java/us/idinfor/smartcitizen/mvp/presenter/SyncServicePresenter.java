package us.idinfor.smartcitizen.mvp.presenter;


import us.idinfor.smartcitizen.data.api.hermes.entity.User;

public interface SyncServicePresenter extends Presenter {

    void queryLocations();
    void queryActivities();
    User getUser();
    void onDestroy();
}
