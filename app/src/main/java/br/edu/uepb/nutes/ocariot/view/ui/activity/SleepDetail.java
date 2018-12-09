package br.edu.uepb.nutes.ocariot.view.ui.activity;


import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
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
import br.edu.uepb.nutes.ocariot.data.model.Sleep;
import br.edu.uepb.nutes.ocariot.data.model.SleepPatternDataSet;
import br.edu.uepb.nutes.ocariot.data.model.SleepPatternSummaryData;
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

//    @BindView(R.id.sleep_chart_bar)
//    BarChart mSleepChartBar;

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
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                break;
            default:
                break;
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
            mActionBar.setTitle(DateUtils.formatDateISO8601(sleep.getStartTime(),
                    getString(R.string.date_time_abb4), null));
        }
    }

    private void populateView() {
        String date_start = DateUtils.formatDateISO8601(sleep.getStartTime(),
                getString(R.string.hour_format1), null);
        String date_end = DateUtils.formatDateISO8601(sleep.getEndTime(),
                getString(R.string.hour_format1), null);
        mPeriodTextView.setText(String.format(Locale.getDefault(), "%s - %s",
                date_start, date_end));

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
        mDateStartGraph.setText(date_start);
        mDateEndGraph.setText(date_end);

        // asleep
        int asleepHour = 0, asleepMinute = 0;
        SleepPatternSummaryData asleep = sleep.getPattern().getSummary().getAsleep();
        if (asleep.getDuration() > 0) {
            asleepHour = (int) asleep.getDuration() / 60;
            asleepMinute = (int) asleep.getDuration() % 60;
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

//    private void printChart() {
//        mSleepChartBar.getDescription().setEnabled(false);
//
//        // o escalonamento agora só pode ser feito nos eixos x e y separadamente
//        mSleepChartBar.setPinchZoom(false);
//        mSleepChartBar.setDrawGridBackground(false);
//
//        mSleepChartBar.getAxisLeft().setDrawGridLines(false);
//        mSleepChartBar.getAxisRight().setDrawGridLines(false);
//        mSleepChartBar.getAxisRight().setEnabled(false);
//        mSleepChartBar.getAxisLeft().setEnabled(false);
//        mSleepChartBar.setDrawValueAboveBar(false);
//
//        //XAxis
//        mSleepChartBar.getXAxis().setEnabled(false);
//        mSleepChartBar.getXAxis().setDrawGridLines(false);
//        mSleepChartBar.getXAxis().setDrawAxisLine(false);
//
//        mSleepChartBar.setHighlightFullBarEnabled(false);
//
//        mSleepChartBar.getAxisLeft().setDrawLabels(false);
//        mSleepChartBar.getAxisRight().setDrawLabels(false);
//        mSleepChartBar.getXAxis().setDrawLabels(false);
//
//        mSleepChartBar.getLegend().setEnabled(false);   // Hide the legend
//
////        mSleepChartBar.setScaleEnabled(false);
////        mSleepChartBar.setFitBars(true);
////        mSleepChartBar.getXAxis().setSpaceMax(0.1f);
////
////        // Settings for Y-Axis
////        YAxis leftAxis = mSleepChartBar.getAxisLeft();
////        YAxis rightAxis = mSleepChartBar.getAxisRight();
////
////        leftAxis.setAxisMinimum(0f);
////        rightAxis.setAxisMinimum(0f);
//
////        mSleepChartBar.setVisibleXRangeMinimum(400);
//
//        setData();
//    }

//    private void setData() {
//        int valueYMaxRestless = 3;
//        int valueYMaxAsleep = 2;
//        int valueYMaxAwake = 1;
//        List<BarEntry> entries = new ArrayList<>(); // yValues
//        List<BarEntry> entries1 = new ArrayList<>(); // yValues
//        List<BarEntry> entries2 = new ArrayList<>(); // yValues
//        List<BarEntry> entries3 = new ArrayList<>(); // yValues
//
//        List<SleepPatternDataSet> dataSet = sleep.getPattern().getDataSet();
//
////        int totalAsleep = 0, totalRestless = 0, totalAwake = 0;
////
////        // Pego os totais em minutos
////        for (SleepPatternDataSet item : dataSet) {
////            if (item.getName().toLowerCase().equals("restless")) {
////                totalRestless += (int) item.getDuration() / 60000;
////            } else if (item.getName().toLowerCase().equals("asleep")) {
////                totalAsleep += (int) item.getDuration() / 60000;
////            } else if (item.getName().toLowerCase().equals("awake")) {
////                totalAwake += (int) item.getDuration() / 60000;
////            }
////        }
//        int totalBars = sleep.getPattern().getSummary().getAsleep().getCount()
//                + sleep.getPattern().getSummary().getRestless().getCount()
//                + sleep.getPattern().getSummary().getAwake().getCount();
//
//        List<Integer> colors = new ArrayList<>();
//        for (int i = 0; i < dataSet.size(); i++) {
//            SleepPatternDataSet item = dataSet.get(i);
//            int total = 0;
//            if (item.getName().toLowerCase().equals("restless")) {
//                total = (int) item.getDuration() / 60000;
//                for (int j = 0; j < total; j++) {
//                    colors.add(ContextCompat.getColor(this, R.color.colorPrimary));
//                    entries.add(new BarEntry(i, valueYMaxRestless));
//                    entries1.add(new BarEntry(i, valueYMaxRestless));
//                }
//            } else if (item.getName().toLowerCase().equals("asleep")) {
//                total = (int) item.getDuration() / 60000;
//                for (int j = 0; j < total; j++) {
//                    colors.add(ContextCompat.getColor(this, R.color.colorPrimaryDark));
//                    entries.add(new BarEntry(i, valueYMaxAsleep));
//                    entries2.add(new BarEntry(i, valueYMaxRestless));
//                }
//            } else if (item.getName().toLowerCase().equals("awake")) {
//                total = (int) item.getDuration() / 60000;
//                for (int j = 0; j < total; j++) {
//                    entries3.add(new BarEntry(i, valueYMaxRestless));
//                    colors.add(ContextCompat.getColor(this, R.color.colorWarning));
//                    entries.add(new BarEntry(i, valueYMaxAwake));
//                }
//            }
//        }
//
////        List<BarEntry> entries1 = new ArrayList<>();
////        entries.add(new BarEntry(0, 1));
//
//        Log.w(LOG_TAG, "TOTAIS: " + entries.size());
//        BarDataSet barDataSet1 = new BarDataSet(entries, "restless");
//        BarDataSet barDataSet2 = new BarDataSet(entries, "asleep");
//        BarDataSet barDataSet3 = new BarDataSet(entries, "awake");
//
////        colors.add(ContextCompat.getColor(this, R.color.colorWarning));
////        colors.add(ContextCompat.getColor(this, R.color.colorPrimaryDark));
//        barDataSet1.setColors(colors);
//        barDataSet2.setColors(colors);
//        barDataSet3.setColors(colors);
////        barDataSet2.setColor(ContextCompat.getColor(this, R.color.colorWarning));
////        barDataSet2.setColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
//        BarData data = new BarData(barDataSet1, barDataSet2, barDataSet3);
//
//        data.setValueFormatter((value, entry, dataSetIndex, viewPortHandler) -> {
//            return value + "!";
//        });
////        XAxis xAxis = mSleepChartBar.getXAxis();
////        xAxis.setSpaceMin(data.getBarWidth() / 2f);
////        xAxis.setSpaceMax(data.getBarWidth() / 2f);
//        data.setBarWidth(1f);
//        mSleepChartBar.setData(data);
//        mSleepChartBar.animateY(1500);
//
//        mSleepChartBar.getXAxis().setSpaceMax(0f);
//        mSleepChartBar.getXAxis().setSpaceMin(0f);
////        mSleepChartBar.getXAxis().setAxisMinimum(0);
//        mSleepChartBar.getXAxis().setAxisMaximum(entries.size());
//        mSleepChartBar.setVisibleXRangeMaximum(entries.size());
//        mSleepChartBar.setVisibleXRangeMinimum(0f);
//        mSleepChartBar.getXAxis().setGranularity(1f);
//
////        for (IBarDataSet set : mSleepChartBar.getData().getDataSets())
////            ((BarDataSet) set).setBarBorderWidth(set.getBarBorderWidth() == 1.f ? 0.f : 1.f);
//        mSleepChartBar.groupBars(0, 0f, 0f);
//
//        mSleepChartBar.invalidate();
//    }
}
