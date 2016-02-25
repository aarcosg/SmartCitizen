package us.idinfor.smartcitizen.fragment;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.fitness.request.DataReadRequest;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Date;

import us.idinfor.smartcitizen.Constants;
import us.idinfor.smartcitizen.GoogleFitHelper;
import us.idinfor.smartcitizen.Utils;

public abstract class BaseGoogleFitFragment extends Fragment {

    private static final String TAG = BaseGoogleFitFragment.class.getCanonicalName();
    private static final String AUTH_PENDING = "auth_state_pending";
    private static final int REQUEST_OAUTH = 1;
    private boolean authInProgress = false;
    protected GoogleFitHelper fitHelper;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null){
            authInProgress = savedInstanceState.getBoolean(AUTH_PENDING);
        }
        //FIXME called when the user is not logged in
        fitHelper = GoogleFitHelper.getInstance(getActivity().getApplicationContext());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(AUTH_PENDING, authInProgress);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(!fitHelper.getGoogleApiClient().isConnected()){
            fitHelper.getGoogleApiClient().connect();
        }else{
            fitHelper.queryFitnessData(
                    Utils.getStartTimeRange(Constants.RANGE_DAY),
                    new Date().getTime(),
                    buildFitQuery());
        }
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    protected abstract DataReadRequest.Builder buildFitQuery();

    @Subscribe
    public void handleFailedConnection(ConnectionResult result) {
        Log.e(TAG, "Connection failed. Cause: " + result.toString());
        if (!result.hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(),
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
                result.startResolutionForResult(getActivity(),REQUEST_OAUTH);
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
