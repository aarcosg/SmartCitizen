package us.idinfor.smartcitizen.fragment;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import us.idinfor.smartcitizen.Constants;
import us.idinfor.smartcitizen.R;
import us.idinfor.smartcitizen.Utils;
import us.idinfor.smartcitizen.adapter.ActivityAdapter;
import us.idinfor.smartcitizen.asynctask.LoadActivitiesAsyncTask;
import us.idinfor.smartcitizen.backend.contextApi.model.Activity;
import us.idinfor.smartcitizen.decorator.MarginDecoration;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment_old#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment_old extends Fragment {

    private static final String TAG = HomeFragment_old.class.getCanonicalName();

    SharedPreferences prefs;
    SparseArray activities;
    ActivityAdapter adapter;

    @Bind(R.id.progressBar)
    ProgressBar mProgressBar;
    @Bind(R.id.activitiesRecyclerView)
    RecyclerView mActivitiesRecyclerView;

    public static HomeFragment_old newInstance() {
        HomeFragment_old fragment = new HomeFragment_old();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public HomeFragment_old() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = Utils.getSharedPreferences(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home_old, container, false);
        ButterKnife.bind(this, view);

        mActivitiesRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        mActivitiesRecyclerView.addItemDecoration(new MarginDecoration(getActivity()));
        mActivitiesRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mActivitiesRecyclerView.setHasFixedSize(true);
        activities = new SparseArray();
        adapter = new ActivityAdapter(activities);
        mActivitiesRecyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        new LoadActivitiesAsyncTask(prefs.getLong(Constants.PROPERTY_DEVICE_ID, 0L)) {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                mProgressBar.setVisibility(View.VISIBLE);
            }

            @Override
            protected void onPostExecute(List<Activity> activitiesResult) {
                super.onPostExecute(activitiesResult);
                mProgressBar.setVisibility(View.GONE);
                if (activitiesResult != null && !activitiesResult.isEmpty()) {
                    SparseArray aggregatedDuration = new SparseArray();
                    for (Activity activity : activitiesResult) {
                        if (aggregatedDuration.indexOfKey(activity.getId()) < 0) {
                            aggregatedDuration.put(activity.getId(), activity.getDuration());
                        } else {
                            aggregatedDuration.setValueAt(activity.getId(),
                                    (Long) aggregatedDuration.get(activity.getId()) + activity.getDuration());
                        }
                    }

                    adapter.clear();
                    adapter.addAll(aggregatedDuration);
                }
            }
        }.execute();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
