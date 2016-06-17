package es.us.hermes.smartcitizen.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import es.us.hermes.smartcitizen.R;
import es.us.hermes.smartcitizen.SmartCitizenApplication;
import es.us.hermes.smartcitizen.di.components.ApplicationComponent;
import es.us.hermes.smartcitizen.di.modules.ActivityModule;


public abstract class BaseActivity extends AppCompatActivity {

    protected Toolbar mActionBarToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getApplicationComponent().inject(this);
    }

    protected void addFragment(int containerViewId, Fragment fragment){
        FragmentTransaction fragmentTransaction =
                this.getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(containerViewId,fragment);
        fragmentTransaction.commit();
    }

    protected ApplicationComponent getApplicationComponent(){
        return SmartCitizenApplication.get(this).getApplicationComponent();
    }

    protected ActivityModule getActivityModule(){
        return new ActivityModule(this);
    }

    protected int getToolbarId() {
        return R.id.toolbar;
    }

    protected Toolbar buildActionBarToolbar(String title, boolean upEnabled) {
        if (mActionBarToolbar == null) {
            mActionBarToolbar = (Toolbar) findViewById(getToolbarId());
            if (mActionBarToolbar != null) {
                setSupportActionBar(mActionBarToolbar);
                if(getSupportActionBar()!= null){
                    getSupportActionBar().setDisplayHomeAsUpEnabled(upEnabled);
                    if(title != null){
                        getSupportActionBar().setTitle(title);
                    }
                }
            }
        }
        return mActionBarToolbar;
    }
}
