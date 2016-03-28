package us.idinfor.smartcitizen.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnItemSelected;
import rx.Observable;
import rx.subjects.PublishSubject;
import us.idinfor.smartcitizen.R;

public class LocationDetailsActivity extends BaseActivity {

    private static final String TAG = LocationDetailsActivity.class.getCanonicalName();

    @Bind(R.id.toolbarSpinner)
    AppCompatSpinner mToolbarSpinner;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    private final PublishSubject<Integer> mTimeRangeSubject = PublishSubject.create();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_details);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @Override
    protected void injectActivityComponent() {
        getActivityComponent().inject(this);
    }

    @OnItemSelected(R.id.toolbarSpinner)
    public void setTimeRange(int position){
        mTimeRangeSubject.onNext(position);
    }

    public Observable<Integer> getTimeRange(){
        return mTimeRangeSubject;
    }

    public static void launch(Activity activity) {
        Intent intent = new Intent(activity, LocationDetailsActivity.class);
        ActivityCompat.startActivity(activity, intent, null);
    }

}
