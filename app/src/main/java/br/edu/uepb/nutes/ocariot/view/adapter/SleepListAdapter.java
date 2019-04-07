package br.edu.uepb.nutes.ocariot.view.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

import br.edu.uepb.nutes.ocariot.R;
import br.edu.uepb.nutes.ocariot.data.model.Sleep;
import br.edu.uepb.nutes.ocariot.utils.DateUtils;
import br.edu.uepb.nutes.ocariot.view.adapter.base.BaseAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * SleepListAdapter implementation.
 *
 * @author Copyright (c) 2018, NUTES/UEPB
 */
public class SleepListAdapter extends BaseAdapter<Sleep> {
    private final Context context;

    public SleepListAdapter(Context context) {
        this.context = context;
    }

    @Override
    public View createView(ViewGroup viewGroup, int viewType) {
        return View.inflate(context, R.layout.sleep_item, null);
    }

    @Override
    public RecyclerView.ViewHolder createViewHolder(View view) {
        return new ViewHolder(view);
    }

    @Override
    public void showData(RecyclerView.ViewHolder holder, int position, List<Sleep> itemsList) {
        if (holder instanceof ViewHolder) {
            final Sleep sleep = itemsList.get(position);
            ViewHolder h = (ViewHolder) holder;

            h.dateStart.setText(DateUtils.formatDateISO8601(sleep.getStartTime(),
                    context.getResources().getString(R.string.date_time_abb4), null));
            h.period.setText(String.format(Locale.getDefault(), "%s - %s",
                    DateUtils.formatDateISO8601(sleep.getStartTime(),
                            context.getResources().getString(R.string.hour_format1), null),
                    DateUtils.formatDateISO8601(sleep.getEndTime(),
                            context.getResources().getString(R.string.hour_format1), null)));
            h.duration.setText(String.format(Locale.getDefault(), "%02dhrs %02dmin",
                    sleep.getDuration() / 3600000, (sleep.getDuration() / 60000) % 60));

            int efficiency = 0;
            int divider = sleep.getPattern().getSummary().getAsleep().getDuration() +
                    sleep.getPattern().getSummary().getAwake().getDuration() +
                    sleep.getPattern().getSummary().getRestless().getDuration();
            if (divider > 0) {
                efficiency = Math.round(
                        (sleep.getPattern().getSummary().getAsleep().getDuration() / (float) divider) * 100
                );
            }
            if (efficiency >= 90) {
                h.efficiency.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
            } else if (efficiency > 80) {
                h.efficiency.setTextColor(ContextCompat.getColor(context, R.color.colorWarning));
            } else {
                h.efficiency.setTextColor(ContextCompat.getColor(context, R.color.colorDanger));
            }

            h.efficiency.setText(String.format(Locale.getDefault(), "%01d%%", efficiency));

            // OnClick Item
            h.mView.setOnClickListener(v -> {
                if (mListener != null) mListener.onItemClick(sleep);
            });
        }
    }

    @Override
    public void clearAnimation(RecyclerView.ViewHolder holder) {
        // Not implemented!
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;

        @BindView(R.id.sleep_date_start_tv)
        TextView dateStart;

        @BindView(R.id.sleep_duration_tv)
        TextView duration;

        @BindView(R.id.sleep_period_tv)
        TextView period;

        @BindView(R.id.sleep_efficiency_tv)
        TextView efficiency;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

            mView = view.getRootView();
        }
    }
}
