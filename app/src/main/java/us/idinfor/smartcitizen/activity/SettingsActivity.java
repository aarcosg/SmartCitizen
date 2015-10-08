package us.idinfor.smartcitizen.activity;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.widget.Toast;

import us.idinfor.smartcitizen.Constants;
import us.idinfor.smartcitizen.R;
import us.idinfor.smartcitizen.Utils;
import us.idinfor.smartcitizen.asynctask.SendLocalDataAsyncTask;
import us.idinfor.smartcitizen.service.ActivityRecognitionService;

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
        setupActionBar();
        getFragmentManager().beginTransaction().replace(android.R.id.content,new GeneralPreferenceFragment()).commit();
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
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
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);
            setHasOptionsMenu(true);
            findPreference(Constants.PROPERTY_APP_VERSION).setSummary(version);
            Preference activityUpdatesPref = findPreference(Constants.PROPERTY_ACTIVITY_UPDATES);
            activityUpdatesPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    boolean activityUpdates = (boolean) newValue;
                    if(activityUpdates){
                        ActivityRecognitionService.actionStartActivityRecognition(context);
                        Toast.makeText(context, "Activity recognition enabled", Toast.LENGTH_LONG).show();
                    }else{
                        ActivityRecognitionService.actionStopActivityRecognition(context);
                        Toast.makeText(context, "Activity recognition disabled", Toast.LENGTH_LONG).show();
                    }
                    return true;
                }
            });
            Preference sendDataPref = findPreference("send_data");
            sendDataPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    if (Utils.isInternetAvailable(context)) {
                        new SendLocalDataAsyncTask(context).execute();
                    } else {
                        Toast.makeText(context,"Internet unavailable",Toast.LENGTH_LONG).show();
                    }
                    return true;
                }
            });
        }
    }

    public static void launch(Activity activity) {
        Intent intent = new Intent(activity, SettingsActivity.class);
        ActivityCompat.startActivity(activity, intent, null);
    }

}
