package es.us.hermes.smartcitizen.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import es.us.hermes.smartcitizen.R;
import es.us.hermes.smartcitizen.data.api.hermes.entity.User;
import es.us.hermes.smartcitizen.di.HasComponent;
import es.us.hermes.smartcitizen.di.components.DaggerMainComponent;
import es.us.hermes.smartcitizen.di.components.MainComponent;
import es.us.hermes.smartcitizen.di.modules.FitnessModule;
import es.us.hermes.smartcitizen.mvp.presenter.MainPresenter;
import es.us.hermes.smartcitizen.mvp.view.MainView;
import es.us.hermes.smartcitizen.service.SyncService;
import es.us.hermes.smartcitizen.ui.fragment.FitnessFragment;
import es.us.hermes.smartcitizen.utils.Utils;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class MainActivity extends BaseActivity
        implements MainView, HasComponent<MainComponent>, NavigationView.OnNavigationItemSelectedListener {

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

    @Inject
    SyncService mSyncService;
    @Inject
    MainPresenter mMainPresenter;

    private MainComponent mMainComponent;
    private final Handler mDrawerActionHandler = new Handler();
    private ActionBarDrawerToggle mDrawerToggle;
    private int mNavItemId;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private boolean mUserLearnedDrawer;
    private User mUser;
    private Snackbar mPermissionsSnackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(!Utils.isGooglePlayServicesAvailable(this)){
            finish();
            return;
        }
        this.initializeInjector();
        this.mMainPresenter.setView(this);
        mUser = this.mMainPresenter.getUser();
        if(TextUtils.isEmpty(mUser.getEmail())){
            this.navigateToLoginScreen();
            finish();
            return;
        }
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        buildActionBarToolbar(getString(R.string.app_name),false);

        mTitle = mDrawerTitle = getTitle();

        // load saved navigation state if present
        if (null == savedInstanceState) {
            mNavItemId = R.id.navigation_home;
        } else {
            mNavItemId = savedInstanceState.getInt(NAV_ITEM_ID);
        }

        this.mMainPresenter.onCreate();

        MainActivityPermissionsDispatcher.setupBackgroundSyncServiceWithCheck(this);

    }

    @Override
    protected void onPause() {
        super.onPause();
        this.mMainPresenter.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mDrawerLayout != null){
            mDrawerLayout.removeDrawerListener(mDrawerToggle);
        }
        ButterKnife.unbind(this);
    }

    @Override
    public MainComponent getComponent() {
        if(this.mMainComponent == null){
            this.initializeInjector();
        }
        return this.mMainComponent;
    }

    @Override
    public void navigateToLoginScreen() {
        LoginActivity.launch(this);
    }

    @Override
    public void bindDrawerLearned(boolean isDrawerLearned) {
        this.mUserLearnedDrawer = isDrawerLearned;
    }

    private void initializeInjector() {
        this.mMainComponent = DaggerMainComponent.builder()
                .applicationComponent(getApplicationComponent())
                .activityModule(getActivityModule())
                .fitnessModule(new FitnessModule())
                .build();
        this.mMainComponent.inject(this);
    }

    @OnShowRationale({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.BODY_SENSORS})
    protected void showRationaleForAppPermissions(PermissionRequest request) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.app_permissions_required)
                .setMessage(R.string.app_permissions_rationale)
                .setPositiveButton(android.R.string.ok, (dialog, button) -> request.proceed())
                .setNegativeButton(android.R.string.cancel, (dialog, button) -> request.cancel())
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MainActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @OnPermissionDenied({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.BODY_SENSORS})
    public void onAppPermissionsDenied() {
        mPermissionsSnackbar = Snackbar.make(mNavigationView.getRootView(),
                getString(R.string.exception_message_permissions_denied),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(R.string.retry),
                        v -> MainActivityPermissionsDispatcher.setupBackgroundSyncServiceWithCheck(this));
        mPermissionsSnackbar.show();
    }

    @OnNeverAskAgain({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.BODY_SENSORS})
    protected void onNeverAskAppPermissions() {
        mPermissionsSnackbar = Snackbar.make(mNavigationView.getRootView(),
                getString(R.string.exception_message_permissions_denied),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(R.string.settings), v -> {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", this.getPackageName(), null);
                    intent.setData(uri);
                    this.startActivity(intent);
                });
        mPermissionsSnackbar.show();
    }

    @NeedsPermission({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.BODY_SENSORS})
    @Override
    public void setupBackgroundSyncService() {
        if(!mSyncService.isRunning()){
            mSyncService.start();
        }
    }

    @Override
    public void setupNavigationDrawer() {
        // listen for navigation events
        mNavigationView.setNavigationItemSelectedListener(this);

        // select the correct nav menu item
        mNavigationView.getMenu().findItem(mNavItemId).setChecked(true);

        // set up the hamburger icon to open and close the drawer
        mDrawerToggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close){
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                MainActivity.this.onDrawerClosed();
            }
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                MainActivity.this.onDrawerOpened();
            }
        };
        mDrawerLayout.addDrawerListener(mDrawerToggle);
    }

    private void onDrawerClosed(){
        mToolbar.setTitle(mTitle);
        invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
    }

    private void onDrawerOpened() {
        mToolbar.setTitle(mDrawerTitle);
        if(!mUserLearnedDrawer){
            // The user manually opened the drawer; store this flag to prevent auto-showing
            // the navigation drawer automatically in the future.
            mMainPresenter.onDrawerLearned();
        }
        invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
    }

    @Override
    public void setupNavigationDrawerHeader() {
        mUserNameTV = (TextView) mNavigationView.getHeaderView(0).findViewById(R.id.username);
        if(mUserNameTV != null){
            mUserNameTV.setText(this.mUser.getEmail());
        }
    }

    @Override
    public void openDrawerNotLearned() {
        // If the user hasn't 'learned' about the drawer, open it to introduce them to the drawer,
        // per the navigation drawer design guidelines.
        if(!mUserLearnedDrawer){
            mDrawerLayout.openDrawer(GravityCompat.START);
        }
    }

    @Override
    public void selectDrawerItem() {
        Fragment fragment = null;
        switch (mNavItemId) {
            case R.id.navigation_home:
                fragment = FitnessFragment.newInstance();
                break;
            case R.id.navigation_settings:
                SettingsActivity.launch(this);
                break;
        }

        if(fragment != null){
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.fragment_container,fragment).commit();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        item.setChecked(true);
        mNavItemId = item.getItemId();
        mDrawerLayout.closeDrawer(GravityCompat.START);
        mDrawerActionHandler
                .postDelayed(() -> selectDrawerItem(),
                        DRAWER_DELAY_MS);
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
