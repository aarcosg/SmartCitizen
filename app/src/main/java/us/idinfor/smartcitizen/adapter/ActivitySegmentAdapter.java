package us.idinfor.smartcitizen.adapter;

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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import us.idinfor.smartcitizen.R;
import us.idinfor.smartcitizen.Utils;
import us.idinfor.smartcitizen.model.ActivitySegmentFit;

public class ActivitySegmentAdapter extends RecyclerView.Adapter<ActivitySegmentAdapter.ViewHolder> implements View.OnClickListener {

    private static final String TAG = ActivityAdapter.class.getCanonicalName();
    private static final String DATE_FORMAT = "HH:mm";

    private List<ActivitySegmentFit> mActivities;

    public ActivitySegmentAdapter(List<ActivitySegmentFit> activities) {
        this.mActivities = activities;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_segment_item, parent, false);
        ViewHolder holder = new ViewHolder(v);
        holder.itemView.setOnClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ActivitySegmentFit activitySegment = mActivities.get(position);
        Context context = holder.itemView.getContext();

        Integer iconResId = Utils.getIconResourceId(context, activitySegment.getName());
        if (iconResId == 0) {
            iconResId = R.drawable.ic_activity_default_24dp;
        }
        Drawable drawable = ContextCompat.getDrawable(context, iconResId);
        drawable = DrawableCompat.wrap(drawable);
        drawable.mutate();
        DrawableCompat.setTint(drawable, ContextCompat.getColor(context, R.color.icons));
        holder.mIcon.setBackgroundColor(Utils.getIconColorId(context, activitySegment.getName()));
        holder.mIcon.setImageDrawable(drawable);

        StringBuilder sb = new StringBuilder();
        sb.append(" ");
        sb.append(activitySegment.getName());
        int duration = (int) (activitySegment.getEndTime() - activitySegment.getStartTime()) / 1000 / 60;
        if (duration < 60) {
            sb.insert(0, holder.itemView.getContext().getResources().getString(R.string.duration_min, duration));
            holder.mDuration.setText(sb.toString());
        } else {
            int hours = (int) Math.floor(duration / 60);
            int minutes = duration - hours * 60;
            sb.insert(0, holder.itemView.getContext().getString(R.string.duration_hour_min, hours, minutes));
            holder.mDuration.setText(sb.toString());
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        holder.mStartTime.setText(dateFormat.format(new Date(activitySegment.getStartTime())));

    }

    @Override
    public int getItemCount() {
        return mActivities.size();
    }

    @Override
    public void onClick(View v) {

    }

    public void addAll(List<ActivitySegmentFit> activities) {
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
