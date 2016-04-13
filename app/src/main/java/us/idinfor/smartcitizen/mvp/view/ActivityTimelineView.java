package us.idinfor.smartcitizen.mvp.view;

import java.util.List;

import us.idinfor.smartcitizen.data.api.google.fit.ActivityDetails;

public interface ActivityTimelineView extends View {

    void showProgressDialog();
    void hideProgressDialog();
    void bindActivitiesList(List<ActivityDetails> activities);
    void setupAdapter();
    void setupRecyclerView();
    void showGoogleFitErrorMessage();
}