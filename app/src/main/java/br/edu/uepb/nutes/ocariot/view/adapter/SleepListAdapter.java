package br.edu.uepb.nutes.ocariot.view.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

import br.edu.uepb.nutes.ocariot.R;
import br.edu.uepb.nutes.ocariot.data.model.ocariot.Sleep;
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
        return new ViewHolderSleep(view);
    }

    @Override
    public void showData(RecyclerView.ViewHolder holder, int position, List<Sleep> itemsList) {
        if (holder instanceof ViewHolderSleep) {
            final Sleep sleep = itemsList.get(position);
            ViewHolderSleep h = (ViewHolderSleep) holder;

            h.dateStart.setText(DateUtils.convertDateTimeUTCToLocale(sleep.getEndTime(),
                    context.getResources().getString(R.string.date_time_abb4), null));
            h.period.setText(String.format(Locale.getDefault(), "%s - %s",
                    DateUtils.convertDateTimeUTCToLocale(sleep.getStartTime(),
                            context.getResources().getString(R.string.hour_format1), null),
                    DateUtils.convertDateTimeUTCToLocale(sleep.getEndTime(),
                            context.getResources().getString(R.string.hour_format1), null)));
            h.duration.setText(String.format(Locale.getDefault(), "%02dhrs %02dmin",
                    sleep.getDuration() / 3600000, (sleep.getDuration() / 60000) % 60));

            int dividend = 0;
            if (sleep.getType().equalsIgnoreCase(Sleep.Type.CLASSIC)) {
                dividend = sleep.getPattern().getSummary().getAsleep().getDuration();
            } else if (sleep.getType().equalsIgnoreCase(Sleep.Type.STAGES)) {
                dividend = (sleep.getPattern().getSummary().getDeep().getDuration()) +
                        (sleep.getPattern().getSummary().getLight().getDuration()) +
                        (sleep.getPattern().getSummary().getRem().getDuration());
            }

            int efficiency = Math.round((dividend / (float) sleep.getDuration()) * 100);
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

            // call Animation function
            setAnimation(h.mView, position);
        }
    }

    @Override
    public void clearAnimation(RecyclerView.ViewHolder holder) {
        ((SleepListAdapter.ViewHolderSleep) holder).clearAnimation();
    }

    class ViewHolderSleep extends RecyclerView.ViewHolder {
        final View mView;

        @BindView(R.id.sleep_date_start_tv)
        TextView dateStart;

        @BindView(R.id.sleep_duration_tv)
        TextView duration;

        @BindView(R.id.sleep_period_tv)
        TextView period;

        @BindView(R.id.sleep_efficiency_tv)
        TextView efficiency;

        ViewHolderSleep(View view) {
            super(view);
            ButterKnife.bind(this, view);
            mView = view.getRootView();
        }

        void clearAnimation() {
            mView.clearAnimation();
        }
    }
}
