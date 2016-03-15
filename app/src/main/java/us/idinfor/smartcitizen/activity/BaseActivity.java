package us.idinfor.smartcitizen.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import butterknife.Bind;
import butterknife.ButterKnife;
import us.idinfor.smartcitizen.R;
import us.idinfor.smartcitizen.SmartCitizenApplication;
import us.idinfor.smartcitizen.di.components.ApplicationComponent;
import us.idinfor.smartcitizen.di.modules.ActivityModule;


public class BaseActivity extends AppCompatActivity {

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getApplicationComponent().inject(this);
        ButterKnife.bind(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    protected ApplicationComponent getApplicationComponent() {
        return ((SmartCitizenApplication)getApplication()).getApplicationComponent();
    }


    protected ActivityModule getActivityModule() {
        return new ActivityModule(this);
    }

    protected void buildActionBarToolbar(String title, boolean upEnabled) {
        setSupportActionBar(mToolbar);
        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(upEnabled);
            if(title != null){
                getSupportActionBar().setTitle(title);
            }
        }
    }

}
