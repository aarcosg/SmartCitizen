package es.us.hermes.smartcitizen.mvp.presenter;


public interface ActivityTimelinePresenter extends Presenter {

    void onCreateView();
    void queryFitnessData(long statTime, long endTime);
}
