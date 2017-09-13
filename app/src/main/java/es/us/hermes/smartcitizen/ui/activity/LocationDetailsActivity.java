package es.us.hermes.smartcitizen.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.MenuItem;

import java.util.Date;

import es.us.hermes.smartcitizen.R;
import es.us.hermes.smartcitizen.di.HasComponent;
import es.us.hermes.smartcitizen.di.components.DaggerLocationDetailsComponent;
import es.us.hermes.smartcitizen.di.components.LocationDetailsComponent;
import es.us.hermes.smartcitizen.ui.fragment.LocationDetailsFragment;

public class LocationDetailsActivity extends BaseActivity implements HasComponent<LocationDetailsComponent>{

    private static final String TAG = LocationDetailsActivity.class.getCanonicalName();
    private static final String EXTRA_RANGE_START_TIME = "EXTRA_RANGE_START_TIME";
    private static final String EXTRA_RANGE_END_TIME = "EXTRA_RANGE_END_TIME";

    private LocationDetailsComponent mLocationDetailsComponent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_details);
        this.initializeInjector();
        if(savedInstanceState == null
                && getIntent().hasExtra(EXTRA_RANGE_START_TIME)
                && getIntent().hasExtra(EXTRA_RANGE_END_TIME)){
            long startTime =  getIntent().getLongExtra(EXTRA_RANGE_START_TIME, new Date().getTime());
            long endTime = getIntent().getLongExtra(EXTRA_RANGE_END_TIME, new Date().getTime());
            addFragment(R.id.fragment_container, LocationDetailsFragment.newInstance(startTime, endTime));
        }
    }

    private void initializeInjector() {
        this.mLocationDetailsComponent = DaggerLocationDetailsComponent.builder()
                .applicationComponent(getApplicationComponent())
                .activityModule(getActivityModule())
                .build();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public LocationDetailsComponent getComponent() {
        if(this.mLocationDetailsComponent == null){
            this.initializeInjector();
        }
        return this.mLocationDetailsComponent;
    }

    public static void launch(Activity activity, long rangeStartTime, long rangeEndTime) {
        Intent intent = new Intent(activity, LocationDetailsActivity.class);
        intent.putExtra(EXTRA_RANGE_START_TIME, rangeStartTime);
        intent.putExtra(EXTRA_RANGE_END_TIME, rangeEndTime);
        ActivityCompat.startActivity(activity, intent, null);
    }

}
