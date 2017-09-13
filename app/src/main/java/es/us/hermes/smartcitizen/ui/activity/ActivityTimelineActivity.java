package es.us.hermes.smartcitizen.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.text.format.DateUtils;
import android.view.MenuItem;

import java.util.Date;

import es.us.hermes.smartcitizen.R;
import es.us.hermes.smartcitizen.di.HasComponent;
import es.us.hermes.smartcitizen.di.components.ActivityTimelineComponent;
import es.us.hermes.smartcitizen.di.components.DaggerActivityTimelineComponent;
import es.us.hermes.smartcitizen.ui.fragment.ActivityTimelineFragment;

public class ActivityTimelineActivity extends BaseActivity implements HasComponent<ActivityTimelineComponent> {

    private static final String TAG = ActivityTimelineActivity.class.getCanonicalName();
    private static final String EXTRA_RANGE_START_TIME = "EXTRA_RANGE_START_TIME";
    private static final String EXTRA_RANGE_END_TIME = "EXTRA_RANGE_END_TIME";

    private ActivityTimelineComponent mActivityTimelineComponent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_timeline);
        buildActionBarToolbar(getString(R.string.title_activity_activity_timeline),true);
        this.initializeInjector();
        if(savedInstanceState == null
                && getIntent().hasExtra(EXTRA_RANGE_START_TIME)
                && getIntent().hasExtra(EXTRA_RANGE_END_TIME)){
            long startTime =  getIntent().getLongExtra(EXTRA_RANGE_START_TIME, new Date().getTime());
            long endTime = getIntent().getLongExtra(EXTRA_RANGE_END_TIME, new Date().getTime());
            if(getSupportActionBar() != null){
                getSupportActionBar().setSubtitle(DateUtils.formatDateTime(this,
                        startTime,
                        DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_WEEKDAY | DateUtils.FORMAT_ABBREV_MONTH));
            }
            addFragment(R.id.fragment_container, ActivityTimelineFragment.newInstance(startTime, endTime));
        }
    }

    private void initializeInjector() {
        this.mActivityTimelineComponent = DaggerActivityTimelineComponent.builder()
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
    public ActivityTimelineComponent getComponent() {
        if(this.mActivityTimelineComponent == null){
            this.initializeInjector();
        }
        return this.mActivityTimelineComponent;
    }

    public static void launch(Activity activity, long rangeStartTime, long rangeEndTime) {
        Intent intent = new Intent(activity, ActivityTimelineActivity.class);
        intent.putExtra(EXTRA_RANGE_START_TIME, rangeStartTime);
        intent.putExtra(EXTRA_RANGE_END_TIME, rangeEndTime);
        ActivityCompat.startActivity(activity, intent, null);
    }

}
