package br.edu.uepb.nutes.ocariot.view.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

import br.edu.uepb.nutes.ocariot.R;
import br.edu.uepb.nutes.ocariot.data.model.ocariot.ActivityType;
import br.edu.uepb.nutes.ocariot.data.model.ocariot.PhysicalActivity;
import br.edu.uepb.nutes.ocariot.utils.DateUtils;
import br.edu.uepb.nutes.ocariot.view.adapter.base.BaseAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * PhysicalActivityListAdapter implementation.
 *
 * @author Copyright (c) 2018, NUTES/UEPB
 */
public class PhysicalActivityListAdapter extends BaseAdapter<PhysicalActivity> {
    private final Context mContext;
    private final DecimalFormat df = new DecimalFormat("#.##");

    public PhysicalActivityListAdapter(Context context) {
        this.mContext = context;
    }

    @Override
    public View createView(ViewGroup viewGroup, int viewType) {
        return View.inflate(mContext, R.layout.physical_activity_item, null);
    }

    @Override
    public RecyclerView.ViewHolder createViewHolder(View view) {
        return new ViewHolderActivity(view);
    }

    @Override
    public void showData(RecyclerView.ViewHolder holder, int position, List<PhysicalActivity> itemsList) {
        if (holder instanceof ViewHolderActivity) {
            final PhysicalActivity activity = itemsList.get(position);
            ViewHolderActivity h = (ViewHolderActivity) holder;

            h.name.setText(activity.getName());
            h.dateStart.setText(DateUtils.convertDateTimeUTCToLocale(activity.getStartTime(),
                    mContext.getResources().getString(R.string.date_time_abb1), null));

            double duration = activity.getDuration() / 60000D;
            if (duration >= 1) h.duration.setText(String.valueOf((int) duration));
            else h.duration.setText(df.format((duration)));

            h.calories.setText(String.valueOf(activity.getCalories()));

            populateImgByName(h.image, activity.getName());

            // distance
            h.boxDistance.setVisibility(View.INVISIBLE);
            if (activity.getDistance() != null && activity.getDistance() > 0) {
                double distance = activity.getDistance() / 1000;
                double distanceRest = activity.getDistance() % 1000;
                if (distanceRest > 0) {
                    h.distance.setText(String.format(Locale.getDefault(), "%.2f", distance));
                } else {
                    h.distance.setText(String.format(Locale.getDefault(), "%d", (int) distance));
                }
                h.boxDistance.setVisibility(View.VISIBLE);
            }

            // OnClick Item
            h.mView.setOnClickListener(v -> {
                if (mListener != null) mListener.onItemClick(activity);
            });

            // call Animation function
            setAnimation(h.mView, position);
        }
    }

    private void populateImgByName(ImageView img, String name) {
        switch (name) {
            case ActivityType.WALK:
                img.setImageResource(R.drawable.ic_walk);
                break;
            case ActivityType.RUN:
                img.setImageResource(R.drawable.ic_run);
                break;
            case ActivityType.BIKE:
            case ActivityType.MOUNTAIN_BIKING:
            case ActivityType.OUTDOOR_BIKE:
                img.setImageResource(R.drawable.ic_bike);
                break;
            case ActivityType.FITSTAR_PERSONAL:
                img.setImageResource(R.drawable.ic_star);
                break;
            case ActivityType.SWIM:
                img.setImageResource(R.drawable.ic_swimming);
                break;
            case ActivityType.SPORT:
                img.setImageResource(R.drawable.ic_sport);
                break;
            default:
                img.setImageResource(R.drawable.ic_workout);
                break;
        }
    }

    @Override
    public void clearAnimation(RecyclerView.ViewHolder holder) {
        ((PhysicalActivityListAdapter.ViewHolderActivity) holder).clearAnimation();
    }

    class ViewHolderActivity extends RecyclerView.ViewHolder {
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

        @BindView(R.id.distance_tv)
        TextView distance;

        @BindView(R.id.box_distance)
        LinearLayout boxDistance;

        ViewHolderActivity(View view) {
            super(view);
            ButterKnife.bind(this, view);
            mView = view.getRootView();
        }

        void clearAnimation() {
            mView.clearAnimation();
        }
    }
}
