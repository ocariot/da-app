package br.edu.uepb.nutes.ocariot.view.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ImageViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import br.edu.uepb.nutes.ocariot.R;
import br.edu.uepb.nutes.ocariot.data.model.ocariot.Child;
import br.edu.uepb.nutes.ocariot.data.model.ocariot.User;
import br.edu.uepb.nutes.ocariot.utils.DateUtils;
import br.edu.uepb.nutes.ocariot.view.adapter.base.BaseAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * ChildListAdapter implementation.
 *
 * @author Copyright (c) 2018, NUTES/UEPB
 */
public class ChildListAdapter extends BaseAdapter<Child> {
    private final Context mContext;

    public ChildListAdapter(Context context) {
        this.mContext = context;
    }

    @Override
    public View createView(ViewGroup viewGroup, int viewType) {
        return View.inflate(mContext, R.layout.child_item, null);
    }

    @Override
    public RecyclerView.ViewHolder createViewHolder(View view) {
        return new ViewHolder(view);
    }

    @Override
    public void showData(RecyclerView.ViewHolder holder, int position, List<Child> itemsList) {
        if (holder instanceof ViewHolder) {
            final Child child = itemsList.get(position);
            ViewHolder h = (ViewHolder) holder;

            h.name.setText(child.getUsername());

            // Last sync
            if (child.getLastSync() != null && !child.getLastSync().isEmpty()) {
                h.lastSync.setText(mContext.getResources().getString(
                        R.string.last_sync_date_time,
                        DateUtils.convertDateTimeUTCToLocale(child.getLastSync(),
                                mContext.getString(R.string.date_time_abb5)
                        ))
                );
            } else {
                h.lastSync.setText(mContext.getResources()
                        .getString(R.string.last_sync_date_time, "--")
                );
            }

            // Fitbit status
            ColorStateList colorFitbitStatus = ColorStateList.valueOf(mContext.getResources()
                    .getColor(R.color.colorFitbitInactive));
            if (child.getFitBitAccess() != null && child.getFitBitAccess().getStatus() != null &&
                    (child.getFitBitAccess().getStatus().equals("valid_token") ||
                            child.getFitBitAccess().getStatus().equals("expired_token"))) {
                colorFitbitStatus = ColorStateList.valueOf(mContext.getResources()
                        .getColor(R.color.colorFitbitActive));
            }
            ImageViewCompat.setImageTintList(h.fitBitStatus, colorFitbitStatus);

            // Gender
            if (child.getGender().equalsIgnoreCase("male")) {
                h.gender.setImageDrawable(mContext.getResources()
                        .getDrawable(R.drawable.ic_action_gender_male));
            } else {
                h.gender.setImageDrawable(mContext.getResources()
                        .getDrawable(R.drawable.ic_action_gender_female));
            }

            // OnClick Item
            h.mView.setOnClickListener(v -> {
                if (mListener != null) mListener.onItemClick(child);
            });
        }
    }

    @Override
    public void clearAnimation(RecyclerView.ViewHolder holder) {
        // Not implemented!
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;

        @BindView(R.id.name_child)
        TextView name;

        @BindView(R.id.child_last_sync_tv)
        TextView lastSync;

        @BindView(R.id.fitbit_status_img)
        ImageView fitBitStatus;

        @BindView(R.id.gender_img)
        ImageView gender;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

            mView = view.getRootView();
        }
    }
}
