package es.us.hermes.smartcitizen.ui.fragment;


import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.us.hermes.smartcitizen.R;
import es.us.hermes.smartcitizen.di.components.MainComponent;
import es.us.hermes.smartcitizen.interactor.SyncServiceInteractor;
import rx.subscriptions.CompositeSubscription;

public class BackupFragment extends BaseFragment {

    private static final String TAG = BackupFragment.class.getCanonicalName();

    @Inject
    SyncServiceInteractor mSyncServiceInteractor;

    @Bind(R.id.sendBtn)
    Button mSendBtn;
    @Bind(R.id.fromDateTV)
    TextView mFromDateTV;
    @Bind(R.id.toDateTV)
    TextView mToDateTV;
    @Bind(R.id.logTV)
    TextView mLogTV;

    private CompositeSubscription mSubscriptions = new CompositeSubscription();
    private DatePickerDialog mFromDatePickerDialog;
    private DatePickerDialog mToDatePickerDialog;
    private Date mFromDate = new Date();
    private Date mToDate = new Date();

    public static BackupFragment newInstance() {
        BackupFragment fragment = new BackupFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public BackupFragment() {
        setRetainInstance(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getComponent(MainComponent.class).inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View fragmentView = inflater.inflate(R.layout.fragment_backup, container, false);
        ButterKnife.bind(this, fragmentView);
        return fragmentView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupDatePickerDialogs();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (!this.mSubscriptions.isUnsubscribed()) {
            this.mSubscriptions.unsubscribe();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(mFromDatePickerDialog.isShowing()){
            mFromDatePickerDialog.cancel();
        }
        if(mToDatePickerDialog.isShowing()){
            mToDatePickerDialog.cancel();
        }
        ButterKnife.unbind(this);
    }

    private void setupDatePickerDialogs() {
        Calendar calendar = Calendar.getInstance();

        mFromDatePickerDialog = new DatePickerDialog(getContext()
                , (view1, year, month, dayOfMonth) -> {
                    calendar.set(year, month, dayOfMonth);
                    mFromDate = calendar.getTime();
                    mFromDateTV.setText(DateUtils.formatDateTime(getContext()
                            , calendar.getTimeInMillis()
                            , DateUtils.FORMAT_ABBREV_ALL));
                }
                , calendar.get(Calendar.YEAR)
                , calendar.get(Calendar.MONTH)
                , calendar.get(Calendar.DAY_OF_MONTH));

        mToDatePickerDialog = new DatePickerDialog(getContext()
                , (view1, year, month, dayOfMonth) -> {
                    calendar.set(year, month, dayOfMonth);
                    mToDate = calendar.getTime();
                    mToDateTV.setText(DateUtils.formatDateTime(getContext()
                            , calendar.getTimeInMillis()
                            , DateUtils.FORMAT_ABBREV_ALL));
                }
                , calendar.get(Calendar.YEAR)
                , calendar.get(Calendar.MONTH)
                , calendar.get(Calendar.DAY_OF_MONTH));
    }

    private void queryAndSendFitData(){
        mSubscriptions.add(this.mSyncServiceInteractor.queryLocationsToGoogleFit(
                mFromDate.getTime(),mToDate.getTime())
                .subscribe(
                        locations ->  {
                            if(!locations.isEmpty()){
                                mSyncServiceInteractor.uploadPeriodicLocations(locations);
                                mLogTV.setText(mLogTV.getText().toString().concat("\n ").concat(getString(R.string.locations_sent)));
                            }
                        }
                        , throwable -> {
                            mLogTV.setText(mLogTV.getText().toString().concat("\n Locations error: ").concat(throwable.getLocalizedMessage()));
                            Log.e(TAG,"Error @queryAndSendFitData: " + throwable.getLocalizedMessage());
                        }
                )
        );

        mSubscriptions.add(this.mSyncServiceInteractor.queryActivitiesToGoogleFit(
                mFromDate.getTime(),mToDate.getTime())
                .subscribe(
                        activities ->  {
                            if(!activities.isEmpty()){
                                mSyncServiceInteractor.uploadPeriodicActivities(activities);
                                mLogTV.setText(mLogTV.getText().toString().concat("\n ").concat(getString(R.string.activities_sent)));
                            }
                        }
                        , throwable -> {
                            mLogTV.setText(mLogTV.getText().toString().concat("\n Activities error: ").concat(throwable.getLocalizedMessage()));
                            Log.e(TAG,"Error @queryAndSendFitData: " + throwable.getLocalizedMessage());
                        }
                )
        );

        mSubscriptions.add(this.mSyncServiceInteractor.queryStepsToGoogleFit(
                mFromDate.getTime(),mToDate.getTime())
                .subscribe(
                        steps ->  {
                            if(!steps.isEmpty()){
                                mSyncServiceInteractor.uploadPeriodicSteps(steps);
                                mLogTV.setText(mLogTV.getText().toString().concat("\n ").concat(getString(R.string.steps_sent)));
                            }
                        }
                        , throwable -> {
                            mLogTV.setText(mLogTV.getText().toString().concat("\n Steps error: ").concat(throwable.getLocalizedMessage()));
                            Log.e(TAG,"Error @queryAndSendFitData: " + throwable.getLocalizedMessage());
                        }
                )
        );

        mSubscriptions.add(this.mSyncServiceInteractor.queryDistancesToGoogleFit(
                mFromDate.getTime(),mToDate.getTime())
                .subscribe(
                        distances ->  {
                            if(!distances.isEmpty()){
                                mSyncServiceInteractor.uploadPeriodicDistances(distances);
                                mLogTV.setText(mLogTV.getText().toString().concat("\n ").concat(getString(R.string.distances_sent)));
                            }
                        }
                        , throwable -> {
                            mLogTV.setText(mLogTV.getText().toString().concat("\n Distances error: ").concat(throwable.getLocalizedMessage()));
                            Log.e(TAG,"Error @queryAndSendFitData: " + throwable.getLocalizedMessage());
                        }
                )
        );

        mSubscriptions.add(this.mSyncServiceInteractor.queryCaloriesExpendedToGoogleFit(
                mFromDate.getTime(),mToDate.getTime())
                .subscribe(
                        caloriesExpended ->  {
                            if(!caloriesExpended.isEmpty()){
                                mSyncServiceInteractor.uploadPeriodicCaloriesExpended(caloriesExpended);
                                mLogTV.setText(mLogTV.getText().toString().concat("\n ").concat(getString(R.string.calories_sent)));
                            }
                        }
                        , throwable -> {
                            mLogTV.setText(mLogTV.getText().toString().concat("\n Calories error: ").concat(throwable.getLocalizedMessage()));
                            Log.e(TAG,"Error @queryAndSendFitData: " + throwable.getLocalizedMessage());
                        }
                )
        );

        mSubscriptions.add(this.mSyncServiceInteractor.queryHeartRateSamplesToGoogleFit(
                mFromDate.getTime(),mToDate.getTime())
                .subscribe(
                        heartRates ->  {
                            if(!heartRates.isEmpty()){
                                mSyncServiceInteractor.uploadPeriodicHeartRateSample(heartRates);
                                mLogTV.setText(mLogTV.getText().toString().concat("\n ").concat(getString(R.string.heart_rates_sent)));
                            }
                        }
                        , throwable -> {
                            mLogTV.setText(mLogTV.getText().toString().concat("\n HeartRates error: ").concat(throwable.getLocalizedMessage()));
                            Log.e(TAG,"Error @queryAndSendFitData: " + throwable.getLocalizedMessage());
                        }
                )
        );
    }

    @OnClick(R.id.sendBtn)
    public void onSendClicked() {
        mSendBtn.setEnabled(false);
        mLogTV.setText(getText(R.string.sending_data));
        queryAndSendFitData();
    }

    @OnClick({R.id.fromDateTV, R.id.toDateTV})
    public void onDateViewClicked(View view) {
        switch (view.getId()) {
            case R.id.fromDateTV:
                mFromDatePickerDialog.show();
                break;
            case R.id.toDateTV:
                mToDatePickerDialog.show();
                break;
        }
    }
}
