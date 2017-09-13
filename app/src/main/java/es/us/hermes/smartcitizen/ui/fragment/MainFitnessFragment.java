package es.us.hermes.smartcitizen.ui.fragment;


import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.Calendar;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import es.us.hermes.smartcitizen.R;
import es.us.hermes.smartcitizen.di.components.MainComponent;

public class MainFitnessFragment extends BaseFragment {

    private static final String TAG = MainFitnessFragment.class.getCanonicalName();
    private static final String STATE_DATE_PICKED = "STATE_DATE_PICKED";

    @Bind(R.id.viewpager)
    ViewPager mViewPager;

    @Inject
    Tracker mTracker;

    private final Calendar mDatePickedCalendar = Calendar.getInstance();
    private final Calendar mTodayCalendar = Calendar.getInstance();
    private DatePickerDialog mDatePickerDialog;

    public static MainFitnessFragment newInstance() {
        MainFitnessFragment fragment = new MainFitnessFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public MainFitnessFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getComponent(MainComponent.class).inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View fragmentView = inflater.inflate(R.layout.fragment_main_fitness, container, false);
        ButterKnife.bind(this, fragmentView);
        setupDatePickerDialog();
        setHasOptionsMenu(true);
        return fragmentView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewPager.setAdapter(new FitnessPagerAdapter(getChildFragmentManager()));
        mViewPager.setCurrentItem(mDatePickedCalendar.get(Calendar.DAY_OF_MONTH) - 1);
    }

    @Override
    public void onResume() {
        super.onResume();
        mTracker.setScreenName(MainFitnessFragment.class.getName());
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(mDatePickerDialog != null && mDatePickerDialog.isShowing()){
            mDatePickerDialog.dismiss();
        }
        ButterKnife.unbind(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fitness, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                mDatePickerDialog.show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupDatePickerDialog(){
       mDatePickerDialog = new DatePickerDialog(
                getContext()
                , (view, year, monthOfYear, dayOfMonth) -> {
                    mDatePickedCalendar.set(Calendar.YEAR, year);
                    mDatePickedCalendar.set(Calendar.MONTH, monthOfYear);
                    mDatePickedCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    mViewPager.getAdapter().notifyDataSetChanged();
                    mViewPager.setCurrentItem(mDatePickedCalendar.get(Calendar.DAY_OF_MONTH) - 1, true);
                }
                , mDatePickedCalendar.get(Calendar.YEAR)
                , mDatePickedCalendar.get(Calendar.MONTH)
                , mDatePickedCalendar.get(Calendar.DAY_OF_MONTH)
       );
    }

    private class FitnessPagerAdapter extends FragmentStatePagerAdapter {

        public FitnessPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "";
        }

        @Override
        public int getCount() {
            if(mDatePickedCalendar.before(mTodayCalendar)
                    && mDatePickedCalendar.get(Calendar.MONTH) == mTodayCalendar.get(Calendar.MONTH)
                    && mDatePickedCalendar.get(Calendar.YEAR) == mTodayCalendar.get(Calendar.YEAR)){
                return mTodayCalendar.get(Calendar.DAY_OF_MONTH);
            }else{
                return mDatePickedCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
            }
        }

        @Override
        public Fragment getItem(int position) {
            return FitnessFragment.newInstance(position + 1, mDatePickedCalendar.get(Calendar.MONTH), mDatePickedCalendar.get(Calendar.YEAR));
        }
    }

}
