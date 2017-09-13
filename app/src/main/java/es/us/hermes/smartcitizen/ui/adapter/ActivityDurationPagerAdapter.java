package es.us.hermes.smartcitizen.ui.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import es.us.hermes.smartcitizen.R;
import es.us.hermes.smartcitizen.mvp.model.google.fit.ActivitySummaryFit;
import es.us.hermes.smartcitizen.utils.Utils;

public class ActivityDurationPagerAdapter extends PagerAdapter {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private List<ActivitySummaryFit> mActivities;

    @Bind(R.id.activityIcon)
    ImageView mActivityIcon;
    @Bind(R.id.activityDuration)
    TextView mActivityDuration;
    @Bind(R.id.activityName)
    TextView mActivityName;

    public ActivityDurationPagerAdapter(Context context, List<ActivitySummaryFit> activities) {
        mContext = context;
        mActivities = activities;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mActivities.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((View) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = mLayoutInflater.inflate(R.layout.activity_duration_pager_item, container, false);
        ButterKnife.bind(this, itemView);
        ActivitySummaryFit activitySummary = mActivities.get(position);

        Integer icon = Utils.getIconResourceId(mContext,activitySummary.getName());

        if (icon > 0) {
            mActivityIcon.setImageResource(icon);
        } else {
            mActivityIcon.setVisibility(View.GONE);
            mActivityName.setText(activitySummary.getName());
            mActivityName.setVisibility(View.VISIBLE);
        }

        if(activitySummary.getDuration() < 60){
            mActivityDuration.setText(mContext.getResources().getString(R.string.duration_min, activitySummary.getDuration()));
        }else{
            int hours = (int) Math.floor(activitySummary.getDuration()/60);
            int minutes = activitySummary.getDuration() - hours * 60;
            mActivityDuration.setText(mContext.getString(R.string.duration_hour_min,hours,minutes));
        }

        container.addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
        ButterKnife.unbind(this);
    }
}
