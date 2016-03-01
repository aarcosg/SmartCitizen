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

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import us.idinfor.smartcitizen.Constants;
import us.idinfor.smartcitizen.GoogleFitService;
import us.idinfor.smartcitizen.HermesCitizenSyncUtils;
import us.idinfor.smartcitizen.R;
import us.idinfor.smartcitizen.Utils;
import us.idinfor.smartcitizen.event.FitBucketsResultEvent;
import us.idinfor.smartcitizen.event.FitDataSetsResultEvent;
import us.idinfor.smartcitizen.model.fit.ActivitySegmentFit;
import us.idinfor.smartcitizen.model.fit.LocationSampleFit;
import us.idinfor.smartcitizen.service.SyncHermesCitizenService;

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
                    GoogleFitService fitHelper = GoogleFitService.getInstance(context);
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
            Preference syncDataPref = findPreference(Constants.PROPERTY_SYNC_DATA_HERMES);
            syncDataPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    boolean syncData = (boolean) newValue;
                    if(syncData){
                        SyncHermesCitizenService.startSync(context);
                        Toast.makeText(context, "Sync with Hermes Citizen started", Toast.LENGTH_LONG).show();
                    }else{
                        SyncHermesCitizenService.stopSync(context);
                        Toast.makeText(context, "Sync with Hermes Citizen stopped", Toast.LENGTH_LONG).show();
                    }
                    return true;
                }
            });
            Preference sendLocationsPref = findPreference("upload_locations");
            sendLocationsPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    HermesCitizenSyncUtils.queryLocationsFit(context);
                    return true;
                }
            });
            Preference sendActivitiesPref = findPreference("upload_activities");
            sendActivitiesPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    HermesCitizenSyncUtils.queryActivitiesFit(context);
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

        /*@Override
        public void onStart() {
            super.onStart();
            EventBus.getDefault().register(this);
        }

        @Override
        public void onStop() {
            EventBus.getDefault().unregister(this);
            super.onStop();
        }*/

        @Subscribe
        public void onFitDataResult(FitDataSetsResultEvent result){
            if (Utils.isInternetAvailable(context)) {
                String username = prefs.getString(Constants.PROPERTY_USER_NAME,"");
                switch (result.getQueryType()){
                    case GoogleFitService.QUERY_LOCATIONS_HERMES:
                        /*List<List<LocationSampleFit>> locationsByDay =
                                Utils.splitSamplesInDays(
                                HermesCitizenSyncUtils.dataSetsToLocationSampleList(result.getDataSets()));
                        for(List<LocationSampleFit> locations : locationsByDay){
                            new UploadDataHermesCitizenAsyncTask<LocationSampleFit>(
                                    context,
                                    prefs.getString(Constants.PROPERTY_USER_NAME,""),
                                    locations)
                                    .execute();
                        }*/
                        /*new UploadDataHermesCitizenAsyncTask<LocationSampleFit>(
                                context,
                                username,
                                HermesCitizenSyncUtils.dataSetsToLocationSampleList(result.getDataSets()))
                                .execute();
                                */
                        SyncHermesCitizenService.startActionUploadLocations(
                                context,
                                username,
                                (ArrayList<LocationSampleFit>)HermesCitizenSyncUtils.dataSetsToLocationSampleList(result.getDataSets())
                        );
                        break;
                }
            } else {
                Toast.makeText(context, "Internet unavailable", Toast.LENGTH_LONG).show();
            }
        }

        @Subscribe
        public void onFitDataResult(FitBucketsResultEvent result){
            if (Utils.isInternetAvailable(context)) {
                String username = prefs.getString(Constants.PROPERTY_USER_NAME,"");
                switch (result.getQueryType()){
                    case GoogleFitService.QUERY_ACTIVITIES_HERMES:
                        /*List<List<ActivitySegmentFit>> activitiesByDay =
                                Utils.splitSamplesInDays(
                                        HermesCitizenSyncUtils.bucketsToActivitySegmentList(result.getBuckets()));
                        for(List<ActivitySegmentFit> activities : activitiesByDay){
                            new UploadDataHermesCitizenAsyncTask<ActivitySegmentFit>(
                                    context,
                                    username,
                                    activities)
                                    .execute();
                        }*/
                        /*new UploadDataHermesCitizenAsyncTask<ActivitySegmentFit>(
                                context,
                                prefs.getString(Constants.PROPERTY_USER_NAME,""),
                                HermesCitizenSyncUtils.bucketsToActivitySegmentList(result.getBuckets()))
                                .execute();
                        break;*/
                        SyncHermesCitizenService.startActionUploadActivities(
                                context,
                                username,
                                (ArrayList<ActivitySegmentFit>)HermesCitizenSyncUtils.bucketsToActivitySegmentList(result.getBuckets())
                        );
                }
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
