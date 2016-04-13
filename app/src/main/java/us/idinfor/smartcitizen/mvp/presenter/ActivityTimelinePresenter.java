package us.idinfor.smartcitizen.mvp.presenter;


public interface ActivityTimelinePresenter extends Presenter {

    void onCreateView();
    void onResume();
    void queryGoogleFit(int timeRange);
}
