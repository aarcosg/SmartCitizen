package us.idinfor.smartcitizen.fragment;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import us.idinfor.smartcitizen.Constants;
import us.idinfor.smartcitizen.R;
import us.idinfor.smartcitizen.Utils;
import us.idinfor.smartcitizen.asynctask.LoadActivitiesAsyncTask;
import us.idinfor.smartcitizen.backend.contextApi.model.Context;
import us.idinfor.smartcitizen.service.ActivityRecognitionService;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    private static final String TAG = HomeFragment.class.getCanonicalName();
    @InjectView(R.id.startBtn)
    Button startBtn;
    @InjectView(R.id.stopBtn)
    Button stopBtn;

    boolean isRunning;
    SharedPreferences prefs;
    @InjectView(R.id.stillVal)
    TextView stillVal;
    @InjectView(R.id.walkingVal)
    TextView walkingVal;

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
        isRunning = Utils.getSharedPreferences(getActivity())
                .getBoolean(Constants.PROPERTY_ACTIVITY_UPDATES, false);
        setButtonState();
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        new LoadActivitiesAsyncTask(prefs.getLong(Constants.PROPERTY_DEVICE_ID, 0L)) {
            @Override
            protected void onPostExecute(List<Context> contexts) {
                super.onPostExecute(contexts);
                Map<String, Integer> counterMap = new HashMap<String, Integer>();
                for (Context c : contexts) {
                    if (!counterMap.containsKey(c.getContext())) {
                        counterMap.put(c.getContext(), 1);
                    } else {
                        counterMap.put(c.getContext(), counterMap.get(c.getContext()) + 1);
                    }
                }

                if (counterMap.containsKey("Still")) {
                    stillVal.setText(counterMap.get("Still").toString());
                }
                if (counterMap.containsKey("On foot")) {
                    walkingVal.setText(counterMap.get("On foot").toString());
                }

            }
        }.execute();

    }

    @OnClick(R.id.startBtn)
    public void startService() {
        ActivityRecognitionService.actionStartActivityRecognition(getActivity().getApplicationContext());
        setRunning(true);
        setButtonState();
    }

    @OnClick(R.id.stopBtn)
    public void stopService() {
        ActivityRecognitionService.actionStopActivityRecognition(getActivity().getApplicationContext());
        setRunning(false);
        setButtonState();
    }

    private void setButtonState() {
        startBtn.setEnabled(!isRunning);
        stopBtn.setEnabled(isRunning);
    }

    private void setRunning(boolean running) {
        isRunning = running;
        prefs.edit().putBoolean(Constants.PROPERTY_ACTIVITY_UPDATES, isRunning).apply();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
