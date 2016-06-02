package us.idinfor.smartcitizen.mvp.presenter;


import us.idinfor.smartcitizen.data.api.hermes.entity.User;

public interface SyncServicePresenter extends Presenter {

    User getUser();
    void onDestroy();
    void queryPeriodicLocations();
    void queryPeriodicActivities();
    void queryFullDayData();

}
