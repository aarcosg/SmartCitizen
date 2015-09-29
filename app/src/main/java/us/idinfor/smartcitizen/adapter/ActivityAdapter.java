package us.idinfor.smartcitizen.adapter;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import us.idinfor.smartcitizen.R;
import us.idinfor.smartcitizen.backend.contextApi.model.Activity;

public class ActivityAdapter extends RecyclerView.Adapter<ActivityAdapter.ViewHolder> {

    private static final String TAG = ActivityAdapter.class.getCanonicalName();
    private List<Activity> activities;

    public ActivityAdapter(List<Activity> activities){
        this.activities = activities;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_activity_item, parent, false);
        ViewHolder holder = new ViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Activity activity = activities.get(position);
        holder.label.setText(activity.getName());
        holder.value.setText(activity.getDuration().toString());
    }

    @Override
    public int getItemCount() {
        return activities.size();
    }

    public void add(Activity acitivty, int position){
        activities.add(position,acitivty);
        notifyItemInserted(position);
    }

    public void add(Activity activity){
        activities.add(activity);
        notifyItemInserted(activities.size()-1);
    }

    public void addAll(List<Activity> activities){
        this.activities.addAll(activities);
        notifyDataSetChanged();
    }

    public void clear(){
        activities.clear();
        notifyDataSetChanged();
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.activityLbl)
        TextView label;
        @InjectView(R.id.activityVal)
        TextView value;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }
}
