package us.idinfor.smartcitizen.adapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import us.idinfor.smartcitizen.Constants;
import us.idinfor.smartcitizen.R;

public class ActivityAdapter extends RecyclerView.Adapter<ActivityAdapter.ViewHolder> {

    private static final String TAG = ActivityAdapter.class.getCanonicalName();
    private SparseArray activities;
    private Context context;

    public ActivityAdapter(SparseArray activities){
        this.activities = activities;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_activity_item, parent, false);
        ViewHolder holder = new ViewHolder(v);
        context = parent.getContext();
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.label.setText(Constants.getActivityString(context, activities.keyAt(position)));
        holder.value.setText(activities.valueAt(position).toString());
    }

    @Override
    public int getItemCount() {
        return activities.size();
    }

    public void add(Integer activityId , Long activityDuration){
        activities.put(activityId, activityDuration);
        notifyItemInserted(activities.size()-1);
    }

    public void addAll(SparseArray activities){
        for(int i = 0; i < activities.size(); i++){
            this.activities.put(activities.keyAt(i),activities.valueAt(i));
        }
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
