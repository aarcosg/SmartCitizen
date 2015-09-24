package us.idinfor.smartcitizen.fragment;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import us.idinfor.smartcitizen.Constants;
import us.idinfor.smartcitizen.R;
import us.idinfor.smartcitizen.Utils;
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

    private void setRunning(boolean running){
        isRunning = running;
        prefs.edit().putBoolean(Constants.PROPERTY_ACTIVITY_UPDATES,isRunning).apply();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
