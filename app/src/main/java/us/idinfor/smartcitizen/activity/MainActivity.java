package us.idinfor.smartcitizen.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
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

import com.crashlytics.android.Crashlytics;

import butterknife.Bind;
import butterknife.ButterKnife;
import us.idinfor.smartcitizen.Constants;
import us.idinfor.smartcitizen.R;
import us.idinfor.smartcitizen.Utils;
import us.idinfor.smartcitizen.fragment.FitnessFragment;
import us.idinfor.smartcitizen.hermes.HermesCitizenSyncService;

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
    TextView mUserNameTV;

    private final Handler mDrawerActionHandler = new Handler();
    private ActionBarDrawerToggle mDrawerToggle;
    private int mNavItemId;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;

    private boolean mUserLearnedDrawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!Utils.checkPlayServices(this)) {
            finish();
        }

        if (TextUtils.isEmpty(prefs.getString(Constants.PROPERTY_USER_NAME, ""))) {
            LoginActivity.launch(this);
            finish();
        }

        //logFabricUser();

        if(!Utils.isServiceRunning(this,HermesCitizenSyncService.class)){
            HermesCitizenSyncService.startSync(this);
        }

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        buildActionBarToolbar(getString(R.string.app_name),false);

        mTitle = mDrawerTitle = getTitle();

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
                this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close){
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

        mUserNameTV = (TextView) mNavigationView.getHeaderView(0).findViewById(R.id.username);
        if(mUserNameTV != null){
            mUserNameTV.setText(prefs.getString(Constants.PROPERTY_USER_NAME,getString(R.string.user)));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    private void logFabricUser() {
        Crashlytics.setUserIdentifier(prefs.getString(Constants.PROPERTY_USER_NAME,getString(R.string.user)));
    }

    private void selectDrawerItem(int itemId) {
        Fragment fragment = null;
        switch (itemId) {
            case R.id.navigation_home:
                fragment = FitnessFragment.newInstance();
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

    public static void launch(Activity activity) {
        Intent intent = new Intent(activity, MainActivity.class);
        ActivityCompat.startActivity(activity, intent, null);
    }
}
