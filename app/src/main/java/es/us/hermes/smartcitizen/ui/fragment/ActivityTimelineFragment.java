package es.us.hermes.smartcitizen.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import es.us.hermes.smartcitizen.R;
import es.us.hermes.smartcitizen.di.components.ActivityTimelineComponent;
import es.us.hermes.smartcitizen.mvp.model.ActivityDetails;
import es.us.hermes.smartcitizen.mvp.presenter.ActivityTimelinePresenter;
import es.us.hermes.smartcitizen.mvp.view.ActivityTimelineView;
import es.us.hermes.smartcitizen.ui.adapter.ActivityTimelineAdapter;


public class ActivityTimelineFragment extends BaseFragment implements ActivityTimelineView {

    private static final String TAG = ActivityTimelineFragment.class.getCanonicalName();
    private static final String ARG_RANGE_START_TIME = "ARG_RANGE_START_TIME";
    private static final String ARG_RANGE_END_TIME = "ARG_RANGE_END_TIME";

    @Inject
    ActivityTimelinePresenter mActivityTimelinePresenter;
    @Inject
    Tracker mTracker;

    @Bind(R.id.progressBar)
    ProgressBar mProgressBar;
    @Bind(R.id.activitiesRecyclerView)
    RecyclerView mActivitiesRecyclerView;

    private ActivityTimelineAdapter mAdapter;
    private long mRangeStartTime;
    private long mRangeEndTime;

    public static ActivityTimelineFragment newInstance(long rangeStartTime, long rangeEndTime) {
        ActivityTimelineFragment fragment = new ActivityTimelineFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_RANGE_START_TIME, rangeStartTime);
        args.putLong(ARG_RANGE_END_TIME, rangeEndTime);
        fragment.setArguments(args);
        return fragment;
    }

    public ActivityTimelineFragment() {
        setRetainInstance(true);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getComponent(ActivityTimelineComponent.class).inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View fragmentView = inflater.inflate(R.layout.fragment_activity_timeline, container, false);
        ButterKnife.bind(this, fragmentView);
        this.mActivityTimelinePresenter.setView(this);
        this.mActivityTimelinePresenter.onCreateView();
        return fragmentView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mTracker.setScreenName(ActivityTimelineFragment.class.getName());
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        this.mActivityTimelinePresenter.queryFitnessData(mRangeStartTime,mRangeEndTime);
    }

    @Override
    public void onPause() {
        super.onPause();
        this.mActivityTimelinePresenter.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void showProgressDialog() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgressDialog() {
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void showGoogleFitErrorMessage() {
        Snackbar.make(mProgressBar.getRootView(),
                getString(R.string.exception_message_google_fit_query),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(R.string.retry),
                        v -> this.mActivityTimelinePresenter.queryFitnessData(mRangeStartTime,mRangeEndTime))
                .show();
    }

    @Override
    public void setTimeRange() {
        if (getArguments() != null && !getArguments().isEmpty()) {
            mRangeStartTime = getArguments().getLong(ARG_RANGE_START_TIME);
            mRangeEndTime = getArguments().getLong(ARG_RANGE_END_TIME);
        }
    }

    @Override
    public void setupAdapter() {
        mAdapter = new ActivityTimelineAdapter(new ArrayList<>());
    }

    @Override
    public void setupRecyclerView() {
        mActivitiesRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mActivitiesRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mActivitiesRecyclerView.setHasFixedSize(true);
        mActivitiesRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void bindActivitiesList(List<ActivityDetails> activities) {
        if(!activities.isEmpty()){
            Collections.reverse(activities);
            mAdapter.clear();
            mAdapter.addAll(activities);
            mAdapter.notifyDataSetChanged();
        }
    }
}
