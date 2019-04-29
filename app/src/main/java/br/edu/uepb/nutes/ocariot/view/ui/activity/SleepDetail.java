package br.edu.uepb.nutes.ocariot.view.ui.activity;


import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import br.edu.uepb.nutes.ocariot.R;
import br.edu.uepb.nutes.ocariot.data.model.ocariot.Sleep;
import br.edu.uepb.nutes.ocariot.data.model.ocariot.SleepPatternDataSet;
import br.edu.uepb.nutes.ocariot.data.model.ocariot.SleepPatternSummaryData;
import br.edu.uepb.nutes.ocariot.utils.DateUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Activity Sleep detail.
 *
 * @author Copyright (c) 2018, NUTES/UEPB
 */
public class SleepDetail extends AppCompatActivity {
    private final String LOG_TAG = "PhysicalActivityDetail";

    public static String SLEEP_DETAIL = "sleep_detail";
    private Sleep sleep;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.sleep_period_tv)
    TextView mPeriodTextView;

    @BindView(R.id.sleep_duration_hour_tv)
    TextView mDurationHourTextView;

    @BindView(R.id.sleep_duration_minutes_tv)
    TextView mDurationMinuteTextView;

    @BindView(R.id.sleep_efficiency_tv)
    TextView mEfficiencyTextView;

    @BindView(R.id.sleep_date_start_graph_tv)
    TextView mDateStartGraph;

    @BindView(R.id.sleep_date_end_graph_tv)
    TextView mDateEndGraph;

    @BindView(R.id.title_asleep)
    TextView mTitleAsleepTextView;

    @BindView(R.id.box_asleep_min)
    FrameLayout mBoxAsleepMinute;

    @BindView(R.id.box_asleep_hour_min)
    FrameLayout mBoxAsleepHourMinute;

    @BindView(R.id.asleep_duration_min_tv)
    TextView mAsleepDurationMinTextView;

    @BindView(R.id.asleep_duration_hour_tv)
    TextView mAsleepDurationHourTextView;

    @BindView(R.id.asleep_duration_min2_tv)
    TextView mAsleepDurationMin2TextView;

    @BindView(R.id.asleep_count_times_tv)
    TextView mAsleepCountTimesTextView;

    @BindView(R.id.title_restless)
    TextView mTitleRestlessTextView;

    @BindView(R.id.restless_count_times_tv)
    TextView mRestlessCountTimesTextView;

    @BindView(R.id.restless_duration_min_tv)
    TextView mRestlessDurationMinTextView;

    @BindView(R.id.title_awake)
    TextView mTitleAwakeTextView;

    @BindView(R.id.awake_count_times_tv)
    TextView mAwakeCountTimesTextView;

    @BindView(R.id.awake_duration_min_tv)
    TextView mAwakeDurationMinTextView;

    @BindView(R.id.restless_awake_duration_min_tv)
    TextView mRestlessAwakeDurationMinTextView;

    @BindView(R.id.sleep_chart)
    LineChart mSleepChart;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep_detail);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);

        if (getIntent() != null) {
            sleep = getIntent().getParcelableExtra(SLEEP_DETAIL);
        }
        initComponents();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            super.onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private void initComponents() {
        if (sleep == null) return;

        initToobar();
        populateView();
    }

    private void initToobar() {
        ActionBar mActionBar = getSupportActionBar();
        if (mActionBar == null) return;

        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setDisplayShowTitleEnabled(true);
        mActionBar.setHomeAsUpIndicator(R.drawable.ic_close_dark);

        if (sleep.getStartTime() != null) {
            mActionBar.setTitle(DateUtils.convertDateTimeUTCToLocale(sleep.getStartTime(),
                    getString(R.string.date_time_abb4), null));
        }
    }

    private void populateView() {
        String dateStart = DateUtils.convertDateTimeUTCToLocale(sleep.getStartTime(),
                getString(R.string.hour_format1), null);
        String dateEnd = DateUtils.convertDateTimeUTCToLocale(sleep.getEndTime(),
                getString(R.string.hour_format1), null);
        mPeriodTextView.setText(String.format(Locale.getDefault(), "%s - %s",
                dateStart, dateEnd));

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
            mEfficiencyTextView.setTextColor(ContextCompat.getColor(this, R.color.colorAccent));
        } else if (efficiency > 80) {
            mEfficiencyTextView.setTextColor(ContextCompat.getColor(this, R.color.colorWarning));
        } else {
            mEfficiencyTextView.setTextColor(ContextCompat.getColor(this, R.color.colorDanger));
        }
        mEfficiencyTextView.setText(String.format(Locale.getDefault(), "%01d%%", efficiency));
        mDurationHourTextView.setText(String.format(Locale.getDefault(), "%02d",
                (int) (sleep.getDuration() / 3600000)));
        mDurationMinuteTextView.setText(String.format(Locale.getDefault(), "%02d",
                (int) (sleep.getDuration() / 60000 % 60)));
        mDateStartGraph.setText(dateStart);
        mDateEndGraph.setText(dateEnd);

        // asleep
        int asleepHour = 0;
        int asleepMinute = 0;
        SleepPatternSummaryData asleep = sleep.getPattern().getSummary().getAsleep();
        if (asleep.getDuration() > 0) {
            asleepHour = asleep.getDuration() / 60;
            asleepMinute = asleep.getDuration() % 60;
        }

        if (asleepHour > 0) {
            mBoxAsleepHourMinute.setVisibility(View.VISIBLE);
            mBoxAsleepMinute.setVisibility(View.GONE);

            mAsleepDurationHourTextView.setText(String.format(Locale.getDefault(), "%02d", asleepHour));
            mAsleepDurationMin2TextView.setText(String.format(Locale.getDefault(), "%02d", asleepMinute));
        } else {
            mBoxAsleepMinute.setVisibility(View.VISIBLE);
            mBoxAsleepHourMinute.setVisibility(View.GONE);
            mAsleepDurationMinTextView.setText(String.format(Locale.getDefault(), "%02d", asleepMinute));
        }
        mTitleAsleepTextView.setText(getString(R.string.title_asleep));
        mAsleepCountTimesTextView.setText(getString(R.string.times_count, asleep.getCount()));

        // restless
        SleepPatternSummaryData restless = sleep.getPattern().getSummary().getRestless();
        mTitleRestlessTextView.setText(getString(R.string.title_restless));
        mRestlessCountTimesTextView.setText(getString(R.string.times_count, restless.getCount()));
        mRestlessDurationMinTextView.setText(String.format(Locale.getDefault(), "%02d",
                restless.getDuration() > 0 ? restless.getDuration() % 60 : 0));

        // awake
        SleepPatternSummaryData awake = sleep.getPattern().getSummary().getAwake();
        mTitleAwakeTextView.setText(getString(R.string.title_awake));
        mAwakeCountTimesTextView.setText(getString(R.string.times_count, awake.getCount()));
        mAwakeDurationMinTextView.setText(String.format(Locale.getDefault(), "%02d",
                awake.getDuration() > 0 ? awake.getDuration() % 60 : 0));

        // restless+awake
        mRestlessAwakeDurationMinTextView.setText(String.format(Locale.getDefault(), "%02d",
                restless.getDuration() + awake.getDuration()));

        printChart();
    }

    private void printChart() {
        final List<Entry> entries = new ArrayList<>();

        int aux = -1;
        List<Integer> colors = new ArrayList<>();
        for (int i = 0; i < sleep.getPattern().getDataSet().size(); i++) {
            // turn your data into Entry objects
            SleepPatternDataSet item = sleep.getPattern().getDataSet().get(i);
            int total = (int) item.getDuration() / 60000;
            for (int j = 0; j < total; j++) {
                aux++;
                if (item.getName().equalsIgnoreCase("restless")) {
                    entries.add(new Entry(aux, 3, item));
                    colors.add(ContextCompat.getColor(this, R.color.colorPrimary));
                } else if (item.getName().equalsIgnoreCase("asleep")) {
                    entries.add(new Entry(aux, 2, item));
                    colors.add(ContextCompat.getColor(this, R.color.colorPrimaryDark));
                } else {
                    entries.add(new Entry(aux, 1, item));
                    colors.add(ContextCompat.getColor(this, R.color.colorWarning));
                }
            }
        }
        // if disabled, scaling can be done on x- and y-axis separately
        mSleepChart.setPinchZoom(false);
        mSleepChart.getLegend().setEnabled(false);
        mSleepChart.setDrawGridBackground(false);
        mSleepChart.getDescription().setEnabled(false);
        mSleepChart.setDoubleTapToZoomEnabled(false);

        mSleepChart.getAxisRight().setDrawGridLines(false);
        mSleepChart.getAxisRight().setEnabled(false);

        mSleepChart.getAxisLeft().setDrawGridLines(false);
        mSleepChart.getAxisLeft().setEnabled(true);
        mSleepChart.getAxisLeft().setDrawLabels(true);
        mSleepChart.getAxisLeft().setLabelCount(3);
        mSleepChart.getAxisLeft().setAxisMinimum(0f);
        mSleepChart.getAxisLeft().setAxisMaximum(4f);
        mSleepChart.getXAxis().setAxisMinimum(1f);
        mSleepChart.getXAxis().setAxisMaximum(entries.size() - 1);
        mSleepChart.getXAxis().setEnabled(false);
        mSleepChart.getXAxis().setDrawGridLines(false);
        mSleepChart.getXAxis().setDrawAxisLine(false);


        Log.w("RESULT", "TOTAL - " + entries.size());


        LineDataSet dataSet = new LineDataSet(entries, "");
        dataSet.setDrawCircles(false);
        dataSet.setDrawValues(false);
        dataSet.setMode(LineDataSet.Mode.STEPPED);
        dataSet.setDrawHighlightIndicators(false);
        dataSet.setLineWidth(1f);
        dataSet.setColors(colors);
        dataSet.setDrawFilled(true);

        LineData lineData = new LineData(dataSet);
        mSleepChart.setData(lineData);
        mSleepChart.setDrawGridBackground(false);
        mSleepChart.setHardwareAccelerationEnabled(false);
        mSleepChart.getXAxis().setGranularity(1f);

        mSleepChart.getAxisLeft().setLabelCount(3);

        if (Utils.getSDKInt() >= 18) {
            // fill drawable only supported on api level 18 and above
            Drawable drawable = ContextCompat.getDrawable(this, R.drawable.fade_sleep_chart);
            dataSet.setFillDrawable(drawable);
        } else {
            dataSet.setFillColor(ContextCompat.getColor(this, R.color.colorWarningDark));
        }

        mSleepChart.getAxisLeft().setValueFormatter(new MyXAxisValueFormatter());
        mSleepChart.getAxisLeft().setAxisLineColor(Color.TRANSPARENT);

        mSleepChart.setMarker(new MyMarkerView(this, R.layout.sleep_chart_marker_view));

        mSleepChart.invalidate(); // refresh
    }

    private class MyXAxisValueFormatter implements IAxisValueFormatter {
        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            String result = "";
            if (value == 1f) {
                result = "Awake";
            } else if (value == 2f) {
                result = "Asleep";
            } else if (value == 3f) {
                result = "Restless";
            }

            return result;
        }
    }

    private class MyMarkerView extends MarkerView {
        private MPPointF mOffset;

        private TextView mTitle;
        private TextView mSubtitle;

        public MyMarkerView(Context context, int layoutResource) {
            super(context, layoutResource);

            mTitle = findViewById(R.id.marker_title_tv);
            mSubtitle = findViewById(R.id.marker_subtitle_tv);
        }

        @Override
        public void refreshContent(Entry e, Highlight highlight) {
            if (e.getData() instanceof SleepPatternDataSet) {
                SleepPatternDataSet pattern = (SleepPatternDataSet) e.getData();
                String title = "";
                if (pattern.getName().equalsIgnoreCase("restless")) {
                    title = getString(R.string.title_restless);
                } else if (pattern.getName().equalsIgnoreCase("asleep")) {
                    title = getString(R.string.title_asleep);
                } else if (pattern.getName().equalsIgnoreCase("awake")) {
                    title = getString(R.string.title_awake);
                }

                mTitle.setText(title.concat(String.format(Locale.getDefault(), " %d min",
                        pattern.getDuration() / 60000)));

                mSubtitle.setText(DateUtils
                        .formatDateHour(pattern.getStartTime(), getString(R.string.hour_format1))
                        .concat(" - ")
                        .concat(DateUtils.formatDateHour(DateUtils.addMinutesToString(
                                pattern.getStartTime(), (int) pattern.getDuration() / 60000),
                                getString(R.string.hour_format1))
                        )
                );
            }

            super.refreshContent(e, highlight);
        }

        @Override
        public MPPointF getOffset() {
            if (mOffset == null) {
                // center the marker horizontally and vertically
                mOffset = new MPPointF(-(getWidth() / 2), -getHeight());
            }
            return mOffset;
        }
    }
}
