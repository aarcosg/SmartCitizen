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

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import us.idinfor.smartcitizen.Constants;
import us.idinfor.smartcitizen.R;
import us.idinfor.smartcitizen.Utils;
import us.idinfor.smartcitizen.adapter.ActivityAdapter;
import us.idinfor.smartcitizen.asynctask.LoadActivitiesAsyncTask;
import us.idinfor.smartcitizen.backend.contextApi.model.Activity;
import us.idinfor.smartcitizen.decorator.MarginDecoration;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    private static final String TAG = HomeFragment.class.getCanonicalName();
    @InjectView(R.id.activitiesRecyclerView)
    RecyclerView activitiesRecyclerView;
    //AutofitRecyclerView activitiesRecyclerView;

    boolean isRunning;
    SharedPreferences prefs;
    SparseArray activities;
    ActivityAdapter adapter;

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public HomeFragment() {
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
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.inject(this, view);

        activitiesRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        activitiesRecyclerView.addItemDecoration(new MarginDecoration(getActivity()));
        activitiesRecyclerView.setItemAnimator(new DefaultItemAnimator());
        activitiesRecyclerView.setHasFixedSize(true);
        activities = new SparseArray();
        adapter = new ActivityAdapter(activities);
        activitiesRecyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        new LoadActivitiesAsyncTask(prefs.getLong(Constants.PROPERTY_DEVICE_ID, 0L)) {
            @Override
            protected void onPostExecute(List<Activity> activitiesResult) {
                super.onPostExecute(activitiesResult);
                if (activitiesResult != null && !activitiesResult.isEmpty()) {
                    SparseArray aggregatedDuration = new SparseArray();
                    for(Activity activity : activitiesResult){
                        if(aggregatedDuration.indexOfKey(activity.getId()) < 0){
                            aggregatedDuration.put(activity.getId(), activity.getDuration());
                        }else{
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
        ButterKnife.reset(this);
    }
}
