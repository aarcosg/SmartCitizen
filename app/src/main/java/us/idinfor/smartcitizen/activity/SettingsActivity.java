package us.idinfor.smartcitizen.activity;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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

import us.idinfor.smartcitizen.Constants;
import us.idinfor.smartcitizen.HermesCitizenApi;
import us.idinfor.smartcitizen.R;
import us.idinfor.smartcitizen.SmartCitizenApplication;
import us.idinfor.smartcitizen.Utils;
import us.idinfor.smartcitizen.asynctask.SendLocalDataAsyncTask;
import us.idinfor.smartcitizen.model.ContextDao;
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
                        new SendLocalDataAsyncTask(context) {
                            @Override
                            protected void onPostExecute(Integer res) {
                                super.onPostExecute(res);
                                switch (res) {
                                    case HermesCitizenApi.RESPONSE_OK:
                                        Toast.makeText(context, "Data uploaded successfully", Toast.LENGTH_LONG).show();
                                        ((SmartCitizenApplication)context.getApplicationContext())
                                                .getDaoSession().getDatabase()
                                                .execSQL("UPDATE " + ContextDao.TABLENAME + " SET " +
                                                        ContextDao.Properties.Sent.columnName + "=?",
                                                        new Object[]{1});
                                        break;
                                    case HermesCitizenApi.RESPONSE_ERROR_USER_NOT_FOUND:
                                        Toast.makeText(context, "Error: User not found", Toast.LENGTH_LONG).show();
                                        Utils.getSharedPreferences(context).edit().remove(Constants.PROPERTY_USER_NAME).commit();
                                        break;
                                    case HermesCitizenApi.RESPONSE_ERROR_DATA_NOT_UPLOADED:
                                        Toast.makeText(context, "Error: Data not uploaded", Toast.LENGTH_LONG).show();
                                        break;
                                    default:
                                        Toast.makeText(context, "Unknown error", Toast.LENGTH_LONG).show();
                                }
                            }
                        }.execute();
                    } else {
                        Toast.makeText(context, "Internet unavailable", Toast.LENGTH_LONG).show();
                    }
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
    }

    public static void launch(Activity activity) {
        Intent intent = new Intent(activity, SettingsActivity.class);
        ActivityCompat.startActivity(activity, intent, null);
    }

}
