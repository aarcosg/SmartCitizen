package us.idinfor.smartcitizen.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import butterknife.Bind;
import butterknife.ButterKnife;
import us.idinfor.smartcitizen.Constants;
import us.idinfor.smartcitizen.R;
import us.idinfor.smartcitizen.Utils;
import us.idinfor.smartcitizen.fragment.HomeFragment;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MainActivity.class.getCanonicalName();

    private static final long DRAWER_DELAY_MS = 265;
    private static final String NAV_ITEM_ID = "nav_item_id";

    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.navigation_view)
    NavigationView mNavigationView;
    @Bind(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    /*@Nullable
    @Bind(R.id.username)
    TextView mUserNameTV;*/

    private final Handler mDrawerActionHandler = new Handler();
    private ActionBarDrawerToggle mDrawerToggle;
    private int mNavItemId;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;

    private GoogleCloudMessaging mGCM;
    private String mRegId;
    private String UDID;

    private boolean mUserLearnedDrawer;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = Utils.getSharedPreferences(this);
        if(TextUtils.isEmpty(prefs.getString(Constants.PROPERTY_USER_NAME, ""))){
            LoginActivity.launch(this);
        }
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        UDID = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        Toolbar toolbar = buildActionBarToolbar(getString(R.string.app_name),false);

        mTitle = mDrawerTitle = getTitle();

        TextView userNameTV = (TextView) mNavigationView.getHeaderView(0).findViewById(R.id.username);
        userNameTV.setText(prefs.getString(Constants.PROPERTY_USER_NAME,getString(R.string.user)));

        /*if (checkPlayServices()) {
            if(mGCM == null){
                mGCM = GoogleCloudMessaging.getInstance(this);
            }
            mRegId = getRegistrationId(this);

            if (mRegId.isEmpty() && Utils.isInternetAvailable(this)) {
                new GcmRegistrationAsyncTask(this){
                    @Override
                    protected void onPostExecute(Device device) {
                        if(device != null){
                            Utils.getSharedPreferences(MainActivity.this).edit().putLong(Constants.PROPERTY_DEVICE_ID,device.getId()).apply();
                            storeRegistrationId(MainActivity.this,device.getGcmId());
                        }
                    }
                }.execute(UDID);
            }
        } else {
            Log.i(TAG, "No valid Google Play Services APK found.");
        }*/

        // Read in the flag indicating whether or not the user has demonstrated awareness of the drawer.
        mUserLearnedDrawer = prefs.getBoolean(Constants.PROPERTY_DRAWER_LEARNED, false);

        // load saved navigation state if present
        if (null == savedInstanceState) {
            mNavItemId = R.id.navigation_home;
        } else {
            mNavItemId = savedInstanceState.getInt(NAV_ITEM_ID);
        }

        // listen for navigation events
        mNavigationView.setNavigationItemSelectedListener(this);

        // select the correct nav menu item
        mNavigationView.getMenu().findItem(mNavItemId).setChecked(true);

        // set up the hamburger icon to open and close the drawer
        mDrawerToggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close){
            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                mToolbar.setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                mToolbar.setTitle(mDrawerTitle);
                if(!mUserLearnedDrawer){
                    // The user manually opened the drawer; store this flag to prevent auto-showing
                    // the navigation drawer automatically in the future.
                    mUserLearnedDrawer = true;
                    prefs.edit().putBoolean(Constants.PROPERTY_DRAWER_LEARNED,true).apply();
                }
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);

        selectDrawerItem(mNavItemId);

        // If the user hasn't 'learned' about the drawer, open it to introduce them to the drawer,
        // per the navigation drawer design guidelines.
        if(!mUserLearnedDrawer){
            mDrawerLayout.openDrawer(GravityCompat.START);
        }
    }

    private void selectDrawerItem(int itemId) {
        Fragment fragment = null;
        switch (itemId) {
            /*case R.id.navigation_home:
                fragment = HomeFragment_old.newInstance();
                break;
            case R.id.navigation_location:
                fragment = LocationFragment.newInstance();
                break;*/
            case R.id.navigation_home:
                fragment = HomeFragment.newInstance();
                //FitnessActivity.launch(this);
                break;
            case R.id.navigation_settings:
                SettingsActivity.launch(this);
                break;
        }

        if(fragment != null){
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame,fragment).commit();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        item.setChecked(true);
        mNavItemId = item.getItemId();
        mDrawerLayout.closeDrawer(GravityCompat.START);
        mDrawerActionHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                selectDrawerItem(mNavItemId);
            }
        }, DRAWER_DELAY_MS);
        return true;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(final Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (item.getItemId() == android.support.v7.appcompat.R.id.home) {
            return mDrawerToggle.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(NAV_ITEM_ID, mNavItemId);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    /**
     * Verify that Google Play services is available before making a request.
     *
     * @return true if Google Play services is available, otherwise false
     */
   /* protected boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        Constants.PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }*/

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    /*private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }*/

    /**
     * Gets the current registration ID for application on GCM service, if there is one.
     * <p>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     *         registration ID.
     */
    /*private String getRegistrationId(Context context) {
        final SharedPreferences prefs = Utils.getSharedPreferences(context);
        String registrationId = prefs.getString(Constants.PROPERTY_GCM_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(Constants.PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }*/

    /**
     * Stores the registration ID and the app versionCode in the application's
     * {@code SharedPreferences}.
     *
     * @param context application's context.
     * @param regId registration ID
     */
    /*private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = Utils.getSharedPreferences(context);
        int appVersion = getAppVersion(context);
        Log.i(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.PROPERTY_GCM_ID, regId);
        editor.putInt(Constants.PROPERTY_APP_VERSION, appVersion);
        editor.apply();
    }*/

    public static void launch(Activity activity) {
        Intent intent = new Intent(activity, MainActivity.class);
        ActivityCompat.startActivity(activity, intent, null);
    }
}
