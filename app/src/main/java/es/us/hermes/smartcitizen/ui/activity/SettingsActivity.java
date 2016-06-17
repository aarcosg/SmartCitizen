package es.us.hermes.smartcitizen.ui.activity;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
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

import es.us.hermes.smartcitizen.Constants;
import es.us.hermes.smartcitizen.R;
import es.us.hermes.smartcitizen.utils.Utils;

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
            /*Preference recordDataPref = findPreference(Constants.PROPERTY_RECORD_DATA);
            recordDataPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    boolean recordData = (boolean) newValue;
                    GoogleFitApi fitHelper = GoogleFitApi.getInstance(context);
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
                        HermesCitizenSyncService.startSync(context);
                        Toast.makeText(context, "Sync with Hermes Citizen started", Toast.LENGTH_LONG).show();
                    }else{
                        HermesCitizenSyncService.stopSync(context);
                        Toast.makeText(context, "Sync with Hermes Citizen stopped", Toast.LENGTH_LONG).show();
                    }
                    return true;
                }
            });*/
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
    }

    public static void launch(Activity activity) {
        Intent intent = new Intent(activity, SettingsActivity.class);
        ActivityCompat.startActivity(activity, intent, null);
    }

}
