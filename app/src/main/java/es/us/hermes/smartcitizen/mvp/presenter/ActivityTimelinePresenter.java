package es.us.hermes.smartcitizen.mvp.presenter;


public interface ActivityTimelinePresenter extends Presenter {

    void onCreateView();
    void onResume();
    void queryGoogleFit(int timeRange);
}
