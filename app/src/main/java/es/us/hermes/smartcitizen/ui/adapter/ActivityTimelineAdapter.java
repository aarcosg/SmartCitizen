package es.us.hermes.smartcitizen.ui.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import es.us.hermes.smartcitizen.R;
import es.us.hermes.smartcitizen.data.api.google.fit.ActivityDetails;
import es.us.hermes.smartcitizen.utils.Utils;

public class ActivityTimelineAdapter extends RecyclerView.Adapter<ActivityTimelineAdapter.ViewHolder> implements View.OnClickListener {

    private static final String TAG = ActivityTimelineAdapter.class.getCanonicalName();
    private static final String DATE_FORMAT = "HH:mm";

    private List<ActivityDetails> mActivities;
    private NumberFormat df;

    public ActivityTimelineAdapter(List<ActivityDetails> activities) {
        this.mActivities = activities;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_details_item, parent, false);
        ViewHolder holder = new ViewHolder(v);
        holder.itemView.setOnClickListener(this);
        df = DecimalFormat.getInstance();
        df.setMaximumFractionDigits(2);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ActivityDetails activityDetails = mActivities.get(position);
        Context context = holder.itemView.getContext();

        // Icon
        Integer iconResId = Utils.getIconResourceId(context, activityDetails.getActivitySummary().getName());
        if (iconResId == 0) {
            iconResId = R.drawable.ic_activity_default;
        }
        Drawable drawable = ContextCompat.getDrawable(context, iconResId);
        drawable = DrawableCompat.wrap(drawable);
        drawable.mutate();
        DrawableCompat.setTint(drawable, ContextCompat.getColor(context, R.color.icons));
        holder.mIcon.setBackgroundColor(Utils.getIconColorId(context, activityDetails.getActivitySummary().getName()));
        holder.mIcon.setImageDrawable(drawable);

        // Activity  + duration
        StringBuilder sb = new StringBuilder();
        sb.append(" ");
        sb.append(activityDetails.getActivitySummary().getName());
        int duration = (int) (activityDetails.getActivitySummary().getEndTime() - activityDetails.getActivitySummary().getStartTime()) / 1000 / 60;
        if (duration < 60) {
            sb.insert(0, holder.itemView.getContext().getResources().getString(R.string.duration_min, duration));
            holder.mDuration.setText(sb.toString());
        } else {
            int hours = (int) Math.floor(duration / 60);
            int minutes = duration - hours * 60;
            sb.insert(0, holder.itemView.getContext().getString(R.string.duration_hour_min, hours, minutes));
            holder.mDuration.setText(sb.toString());
        }

        // Start time
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
        holder.mStartTime.setText(dateFormat.format(new Date(activityDetails.getActivitySummary().getStartTime())));

        // Card background color
        holder.mWrapper.setBackgroundColor(Utils.getIconColorId(context,activityDetails.getActivitySummary().getName()));

        // Steps
        if(activityDetails.getStepCountDelta() != null){
            holder.mStepsCounter.setText(activityDetails.getStepCountDelta().getSteps().toString());
        }

        // Calories
        if(activityDetails.getCaloriesExpended() != null){
            holder.mCaloriesCounter.setText(df.format(activityDetails.getCaloriesExpended().getCalories()));
        }

        // Distance
        if(activityDetails.getDistanceDelta() != null){
            holder.mDistanceCounter.setText(df.format(activityDetails.getDistanceDelta().getDistance()));
        }


    }

    @Override
    public int getItemCount() {
        return mActivities.size();
    }

    @Override
    public void onClick(View v) {

    }

    public void addAll(List<ActivityDetails> activities) {
        mActivities.addAll(activities);
        notifyDataSetChanged();
    }

    public void clear() {
        mActivities.clear();
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.icon)
        RoundedImageView mIcon;
        @Bind(R.id.wrapper)
        LinearLayout mWrapper;
        @Bind(R.id.duration)
        TextView mDuration;
        @Bind(R.id.startTime)
        TextView mStartTime;
        @Bind(R.id.stepsCounter)
        TextView mStepsCounter;
        @Bind(R.id.caloriesCounter)
        TextView mCaloriesCounter;
        @Bind(R.id.distanceCounter)
        TextView mDistanceCounter;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


}
