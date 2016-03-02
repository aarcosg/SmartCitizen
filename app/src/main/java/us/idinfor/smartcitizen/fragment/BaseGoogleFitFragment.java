package us.idinfor.smartcitizen.fragment;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.fitness.request.DataReadRequest;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import us.idinfor.smartcitizen.GoogleFitApi;
import us.idinfor.smartcitizen.event.ConnectionResultEvent;

public abstract class BaseGoogleFitFragment extends Fragment {

    private static final String TAG = BaseGoogleFitFragment.class.getCanonicalName();
    private static final String AUTH_PENDING = "auth_state_pending";
    private static final int REQUEST_OAUTH = 1;
    private boolean authInProgress = false;
    protected GoogleFitApi fitHelper;

    protected abstract DataReadRequest.Builder buildFitQuery();
    protected abstract void queryGoogleFit(int timeRange);

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null){
            authInProgress = savedInstanceState.getBoolean(AUTH_PENDING);
        }
        fitHelper = GoogleFitApi.getInstance(getActivity().getApplicationContext());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(AUTH_PENDING, authInProgress);
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
        if(!fitHelper.getGoogleApiClient().isConnected()){
            fitHelper.getGoogleApiClient().connect();
        }
    }

    @Override
    public void onPause() {
        EventBus.getDefault().unregister(this);
        super.onPause();
    }

    @Subscribe
    public void onEvent(ConnectionResultEvent event) {
        Log.e(TAG, "Connection failed. Cause: " + event.getResult().toString());
        if (!event.getResult().hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(event.getResult().getErrorCode(),
                    getActivity(), 0).show();
        }
        /* The failure has a resolution. Resolve it.
        * Called typically when the app is not yet authorized, and an
        * authorization dialog is displayed to the user.
        * */
        if (!authInProgress) {
            Log.e(TAG, "Attempting to resolve failed connection");
            authInProgress = true;
            try {
                event.getResult().startResolutionForResult(getActivity(),REQUEST_OAUTH);
            } catch (IntentSender.SendIntentException e) {
                Log.e(TAG,"Exception while starting resolution activity");
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_OAUTH){
            authInProgress = false;
            if(resultCode == android.app.Activity.RESULT_OK){
                fitHelper.getGoogleApiClient().connect();
            }
        }
    }

}
