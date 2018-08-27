package br.edu.uepb.nutes.activity_tracking_poc.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import br.edu.uepb.nutes.activity_tracking_poc.R;
import br.edu.uepb.nutes.activity_tracking_poc.data.model.Activity;
import br.edu.uepb.nutes.activity_tracking_poc.data.model.ActivityType;
import br.edu.uepb.nutes.activity_tracking_poc.utils.DateUtils;
import br.edu.uepb.nutes.activity_tracking_poc.view.adapter.base.BaseAdapter;
import br.edu.uepb.nutes.activity_tracking_poc.view.ui.fragment.PhysicalActivityDetail;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * PhysicalActivityListAdapter implementation.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2018, NUTES/UEPB
 */
public class PhysicalActivityListAdapter extends BaseAdapter<Activity> {
    private final Context context;

    public PhysicalActivityListAdapter(Context context) {
        this.context = context;
    }

    @Override
    public View createView(ViewGroup viewGroup, int viewType) {
        return View.inflate(context, R.layout.physical_activity_item, null);
    }

    @Override
    public RecyclerView.ViewHolder createViewHolder(View view) {
        return new ViewHolder(view);
    }

    @Override
    public void showData(RecyclerView.ViewHolder holder, int position, List<Activity> itemsList) {
        if (holder instanceof ViewHolder) {
            final Activity activity = itemsList.get(position);
            ViewHolder h = (ViewHolder) holder;

            h.name.setText(activity.getName());
            h.dateStart.setText(DateUtils.formatDateISO8601(activity.getStartTime(),
                    context.getResources().getString(R.string.date_time_abb), null));
            int duration = (int) (activity.getDuration() / (60 * 1000));
            h.duration.setText(String.valueOf(duration));
            h.calories.setText(String.valueOf(activity.getCalories()));

            String name = activity.getName();
            if (name.equals(ActivityType.WALK)) {
                h.image.setImageResource(R.drawable.ic_walk);
            } else if (name.equals(ActivityType.RUN)) {
                h.image.setImageResource(R.drawable.ic_run);
            } else if (name.equals(ActivityType.BIKE)
                    || name.equals(ActivityType.MOUNTAIN_BIKING)
                    || name.equals(ActivityType.OUTDOOR_BIKE)) {
                h.image.setImageResource(R.drawable.ic_bike);
            } else if (name.equals(ActivityType.WORKOUT)) {
                h.image.setImageResource(R.drawable.ic_workout);
            } else if (name.equals(ActivityType.FITSTAR_PERSONAL)) {
                h.image.setImageResource(R.drawable.ic_star);
            }


            /**
             * OnClick Item
             */
            h.mView.setOnClickListener(v -> {
                if (PhysicalActivityListAdapter.super.mListener != null)
                    PhysicalActivityListAdapter.super.mListener.onItemClick(activity);
            });
        }
    }

    @Override
    public void clearAnimation(RecyclerView.ViewHolder holder) {

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;

        @BindView(R.id.activity_img)
        ImageView image;

        @BindView(R.id.name_activity_tv)
        TextView name;

        @BindView(R.id.duration_tv)
        TextView duration;

        @BindView(R.id.date_tv)
        TextView dateStart;

        @BindView(R.id.calories_tv)
        TextView calories;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

            mView = view.getRootView();
        }
    }
}
