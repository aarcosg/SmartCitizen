package es.us.hermes.smartcitizen.mvp.view;

import java.util.List;

import es.us.hermes.smartcitizen.mvp.model.ActivityDetails;

public interface ActivityTimelineView extends View {

    void setTimeRange();
    void showProgressDialog();
    void hideProgressDialog();
    void bindActivitiesList(List<ActivityDetails> activities);
    void setupAdapter();
    void setupRecyclerView();
    void showGoogleFitErrorMessage();

}