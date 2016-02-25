package us.idinfor.smartcitizen.activity;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.request.DataReadRequest;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Date;
import java.util.List;

import us.idinfor.smartcitizen.Constants;
import us.idinfor.smartcitizen.GoogleFitHelper;
import us.idinfor.smartcitizen.HermesCitizenApi;
import us.idinfor.smartcitizen.R;
import us.idinfor.smartcitizen.Utils;
import us.idinfor.smartcitizen.asynctask.UploadActivitiesAsyncTask;
import us.idinfor.smartcitizen.asynctask.UploadLocationsAsyncTask;
import us.idinfor.smartcitizen.model.ActivitySegmentFit;
import us.idinfor.smartcitizen.model.LocationSampleFit;

public class SettingsActivity extends AppCompatPreferenceActivity {

    private static final String TAG = SettingsActivity.class.getCanonicalName();
    private static Context context;
    private static String version;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = SettingsActivity.this;
        try {
            version = "v. " + getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        getFragmentManager().beginTransaction().replace(android.R.id.content,new GeneralPreferenceFragment()).commit();
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            if (!super.onMenuItemSelected(featureId, item)) {
                NavUtils.navigateUpFromSameTask(this);
            }
            return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    public static class GeneralPreferenceFragment extends PreferenceFragment {

        private SharedPreferences prefs;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            prefs = Utils.getSharedPreferences(getActivity().getApplicationContext());
            addPreferencesFromResource(R.xml.pref_general);
            setHasOptionsMenu(true);
            findPreference(Constants.PROPERTY_APP_VERSION).setSummary(version);
            Preference recordDataPref = findPreference(Constants.PROPERTY_RECORD_DATA);
            recordDataPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    boolean recordData = (boolean) newValue;
                    GoogleFitHelper fitHelper = GoogleFitHelper.getInstance(context);
                    if(recordData){
                        fitHelper.subscribeFitnessData();
                        Toast.makeText(context, "Recording sensor data", Toast.LENGTH_LONG).show();
                    }else{
                        fitHelper.unsubscribeFitnessData();
                        Toast.makeText(context, "Recording stopped", Toast.LENGTH_LONG).show();
                    }
                    return true;
                }
            });
            Preference sendLocationsPref = findPreference("upload_locations");
            sendLocationsPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    uploadLocations();
                    return true;
                }
            });
            Preference sendActivitiesPref = findPreference("upload_activities");
            sendActivitiesPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    uploadActivities();
                    return true;
                }
            });
        }

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View layout = inflater.inflate(R.layout.fragment_settings, container, false);
            if (layout != null) {
                AppCompatPreferenceActivity activity = (AppCompatPreferenceActivity) context;
                Toolbar toolbar = (Toolbar) layout.findViewById(R.id.toolbar);
                activity.setSupportActionBar(toolbar);
                ActionBar bar = activity.getSupportActionBar();
                bar.setDisplayHomeAsUpEnabled(true);
            }
            return layout;
        }

        @Override
        public void onStart() {
            super.onStart();
            EventBus.getDefault().register(this);
        }

        @Override
        public void onStop() {
            EventBus.getDefault().unregister(this);
            super.onStop();
        }

        private void uploadLocations(){
            GoogleFitHelper fitHelper = GoogleFitHelper.getInstance(getActivity().getApplicationContext());
            long startTime = prefs.getLong(Constants.PROPERTY_LAST_LOCATION_TIME_SENT,Utils.getStartTimeRange(Constants.RANGE_DAY));
            long endTime = new Date().getTime();
            boolean sameDay = Utils.sameDay(startTime,endTime);
            if(!sameDay){
                endTime = Utils.getLastMinuteOfDay(endTime).getTimeInMillis();
            }
            DataReadRequest.Builder builder = new DataReadRequest.Builder()
                    .read(DataType.TYPE_LOCATION_SAMPLE);
            fitHelper.queryFitnessData(
                    startTime,
                    endTime,
                    builder,
                    GoogleFitHelper.QUERY_LOCATIONS);
            if(!sameDay){
                endTime = Utils.getFirstMinuteOfNextDay(endTime).getTimeInMillis();
            }
            //prefs.edit().putLong(Constants.PROPERTY_LAST_LOCATION_TIME_SENT,endTime).apply();
        }

        @Subscribe
        public void onLocationsResult(List<LocationSampleFit> locations){
            if (Utils.isInternetAvailable(context)) {
                new UploadLocationsAsyncTask(context,prefs.getString(Constants.PROPERTY_USER_NAME,""),locations) {
                    @Override
                    protected void onPostExecute(Integer res) {
                        super.onPostExecute(res);
                        switch (res) {
                            case HermesCitizenApi.RESPONSE_OK:
                                Toast.makeText(context, "Locations uploaded successfully", Toast.LENGTH_LONG).show();
                                break;
                            case HermesCitizenApi.RESPONSE_ERROR_USER_NOT_FOUND:
                                Toast.makeText(context, "Error: User not found", Toast.LENGTH_LONG).show();
                                Utils.getSharedPreferences(context).edit().remove(Constants.PROPERTY_USER_NAME).commit();
                                break;
                            case HermesCitizenApi.RESPONSE_ERROR_DATA_NOT_UPLOADED:
                                Toast.makeText(context, "Error: Locations not uploaded", Toast.LENGTH_LONG).show();
                                break;
                            default:
                                Toast.makeText(context, "Unknown error", Toast.LENGTH_LONG).show();
                        }
                    }
                }.execute();
            } else {
                Toast.makeText(context, "Internet unavailable", Toast.LENGTH_LONG).show();
            }
        }

        private void uploadActivities(){
            GoogleFitHelper fitHelper = GoogleFitHelper.getInstance(getActivity().getApplicationContext());
            long startTime = prefs.getLong(Constants.PROPERTY_LAST_ACTIVITY_TIME_SENT,Utils.getStartTimeRange(Constants.RANGE_DAY));
            long endTime = new Date().getTime();
            boolean sameDay = Utils.sameDay(startTime,endTime);
            if(!sameDay){
                endTime = Utils.getLastMinuteOfDay(endTime).getTimeInMillis();
            }
            DataReadRequest.Builder builder = new DataReadRequest.Builder()
                    .read(DataType.TYPE_ACTIVITY_SEGMENT);
            fitHelper.queryFitnessData(
                    startTime,
                    endTime,
                    builder,
                    GoogleFitHelper.QUERY_ACTIVITIES);
            if(!sameDay){
                endTime = Utils.getFirstMinuteOfNextDay(endTime).getTimeInMillis();
            }
            //prefs.edit().putLong(Constants.PROPERTY_LAST_ACTIVITY_TIME_SENT,endTime).apply();
        }

        @Subscribe
        public void onActivitiesResult(List<ActivitySegmentFit> activities){
            if (Utils.isInternetAvailable(context)) {
                new UploadActivitiesAsyncTask(context,prefs.getString(Constants.PROPERTY_USER_NAME,""),activities) {
                    @Override
                    protected void onPostExecute(Integer res) {
                        super.onPostExecute(res);
                        switch (res) {
                            case HermesCitizenApi.RESPONSE_OK:
                                Toast.makeText(context, "Activities uploaded successfully", Toast.LENGTH_LONG).show();
                                break;
                            case HermesCitizenApi.RESPONSE_ERROR_USER_NOT_FOUND:
                                Toast.makeText(context, "Error: User not found", Toast.LENGTH_LONG).show();
                                Utils.getSharedPreferences(context).edit().remove(Constants.PROPERTY_USER_NAME).commit();
                                break;
                            case HermesCitizenApi.RESPONSE_ERROR_DATA_NOT_UPLOADED:
                                Toast.makeText(context, "Error: Activities not uploaded", Toast.LENGTH_LONG).show();
                                break;
                            default:
                                Toast.makeText(context, "Unknown error", Toast.LENGTH_LONG).show();
                        }
                    }
                }.execute();
            } else {
                Toast.makeText(context, "Internet unavailable", Toast.LENGTH_LONG).show();
            }
        }
    }

    public static void launch(Activity activity) {
        Intent intent = new Intent(activity, SettingsActivity.class);
        ActivityCompat.startActivity(activity, intent, null);
    }

}
