package br.edu.uepb.nutes.ocariot.view.ui.activity;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
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
import br.edu.uepb.nutes.ocariot.data.model.ocariot.SleepPatternSummary;
import br.edu.uepb.nutes.ocariot.data.model.ocariot.SleepType;
import br.edu.uepb.nutes.ocariot.data.model.ocariot.User;
import br.edu.uepb.nutes.ocariot.data.repository.local.pref.AppPreferencesHelper;
import br.edu.uepb.nutes.ocariot.utils.DateUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Activity Sleep detail.
 *
 * @author Copyright (c) 2018, NUTES/UEPB
 */
public class SleepDetail extends AppCompatActivity {
    public static String SLEEP_DETAIL = "sleep_detail";

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

    @BindView(R.id.asleep_count_times_classic_tv)
    TextView mAsleepCountTimesTextView;

    @BindView(R.id.asleep_duration_classic_tv)
    TextView mAsleepDurationTextView;

    @BindView(R.id.restless_count_times_classic_tv)
    TextView mRestlessCountTimesTextView;

    @BindView(R.id.restless_duration_classic_tv)
    TextView mRestlessDurationTextView;

    @BindView(R.id.awake_count_times_classic_tv)
    TextView mAwakeCountTimesClassicTextView;

    @BindView(R.id.awake_duration_classic_tv)
    TextView mAwakeDurationClassicTextView;

    @BindView(R.id.restless_awake_duration_classic_tv)
    TextView mRestlessAwakeDurationTextView;

    @BindView(R.id.awake_count_times_stages_tv)
    TextView mAwakeCountTimesStagesTextView;

    @BindView(R.id.awake_duration_stages_tv)
    TextView mAwakeDurationStagesTextView;

    @BindView(R.id.rem_count_times_stages_tv)
    TextView mRemCountTimesTextView;

    @BindView(R.id.rem_duration_stages_tv)
    TextView mRemDurationTextView;

    @BindView(R.id.light_count_times_stages_tv)
    TextView mLightCountTimesTextView;

    @BindView(R.id.light_duration_stages_tv)
    TextView mLightDurationTextView;

    @BindView(R.id.deep_count_times_stages_tv)
    TextView mDeepCountTimesTextView;

    @BindView(R.id.deep_duration_stages_tv)
    TextView mDeepDurationTextView;

    @BindView(R.id.sleep_chart)
    LineChart mSleepChart;

    @BindView(R.id.box_type_classic)
    RelativeLayout boxClassic;

    @BindView(R.id.box_type_stages)
    RelativeLayout boxStages;

    private AppPreferencesHelper appPref;
    private Sleep sleep;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep_detail);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);

        appPref = AppPreferencesHelper.getInstance(this);

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

        initToolbar();
        populateView();
    }

    private void initToolbar() {
        ActionBar mActionBar = getSupportActionBar();
        if (mActionBar == null) return;

        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setDisplayShowTitleEnabled(true);
        mActionBar.setHomeAsUpIndicator(R.drawable.ic_close_dark);

        if (sleep.getStartTime() != null) {
            mActionBar.setTitle(DateUtils.convertDateTimeUTCToLocale(sleep.getStartTime(),
                    getString(R.string.date_time_abb4), null));
        }
        if (!appPref.getUserAccessOcariot().getSubjectType()
                .equalsIgnoreCase(User.Type.CHILD)) {
            mActionBar.setSubtitle(appPref.getLastSelectedChild().getUsername());
        }
    }

    private void populateView() {
        String dateStart = DateUtils.convertDateTimeUTCToLocale(sleep.getStartTime(), getString(R.string.hour_format2));
        String dateEnd = DateUtils.convertDateTimeUTCToLocale(sleep.getEndTime(), getString(R.string.hour_format2));
        mPeriodTextView.setText(String.format(Locale.getDefault(), "%s - %s",
                dateStart, dateEnd));

        int dividend = 0;
        if (sleep.getType().equalsIgnoreCase(SleepType.CLASSIC)) {
            dividend = sleep.getPattern().getSummary().getAsleep().getDuration();
        } else if (sleep.getType().equalsIgnoreCase(SleepType.STAGES)) {
            dividend = (sleep.getPattern().getSummary().getDeep().getDuration()) +
                    (sleep.getPattern().getSummary().getLight().getDuration()) +
                    (sleep.getPattern().getSummary().getRem().getDuration());
        }
        int efficiency = Math.round((dividend / (float) sleep.getDuration()) * 100);
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

        if (sleep.getType().equalsIgnoreCase(SleepType.CLASSIC)) {
            boxStages.setVisibility(View.GONE);
            boxClassic.setVisibility(View.VISIBLE);
            populatePatternClassic(sleep.getPattern().getSummary());
        } else if (sleep.getType().equalsIgnoreCase(SleepType.STAGES)) {
            boxClassic.setVisibility(View.GONE);
            boxStages.setVisibility(View.VISIBLE);
            populatePatternStages(sleep.getPattern().getSummary());
        } else {
            boxClassic.setVisibility(View.GONE);
            boxStages.setVisibility(View.GONE);
        }

        printChart();
    }

    private void populatePatternClassic(SleepPatternSummary summary) {
        // asleep
        mAsleepCountTimesTextView.setText(getString(R.string.times_count, summary.getAsleep().getCount()));
        mAsleepDurationTextView.setText(mountDuration(summary.getAsleep().getDuration()));

        // restless
        mRestlessCountTimesTextView.setText(getString(R.string.times_count, summary.getRestless().getCount()));
        mRestlessDurationTextView.setText(mountDuration(summary.getRestless().getDuration()));

        // awake
        mAwakeCountTimesClassicTextView.setText(getString(R.string.times_count, summary.getAwake().getCount()));
        mAwakeDurationClassicTextView.setText(mountDuration(summary.getAwake().getDuration()));

        // restless+awake
        mRestlessAwakeDurationTextView.setText(mountDuration(summary.getRestless().getDuration() +
                summary.getAwake().getDuration()));
    }

    private void populatePatternStages(SleepPatternSummary summary) {
        // awake
        mAwakeCountTimesStagesTextView.setText(getString(R.string.times_count, summary.getAwake().getCount()));
        mAwakeDurationStagesTextView.setText(mountDuration(summary.getAwake().getDuration()));

        // REM
        mRemCountTimesTextView.setText(getString(R.string.times_count, summary.getRem().getCount()));
        mRemDurationTextView.setText(mountDuration(summary.getRem().getDuration()));

        // light
        mLightCountTimesTextView.setText(getString(R.string.times_count, summary.getLight().getCount()));
        mLightDurationTextView.setText(mountDuration(summary.getLight().getDuration()));

        // deep
        mDeepCountTimesTextView.setText(getString(R.string.times_count, summary.getDeep().getCount()));
        mDeepDurationTextView.setText(mountDuration(summary.getDeep().getDuration()));
    }

    private String mountDuration(long duration) {
        int hours = (int) Math.floor(duration / 3600000);
        int minutes = (int) Math.floor((duration / 60000) % 60);

        if (hours > 0 || minutes > 0) {
            return (hours > 0 ? String.format(Locale.getDefault(), "%02dh ", hours) : "")
                    .concat((minutes > 0 ? String.format(Locale.getDefault(), "%02dmin", minutes) : ""));
        }
        return "";
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
                if (item.getName().equalsIgnoreCase("awake")) {
                    entries.add(new Entry(aux, 1, item));
                    colors.add(ContextCompat.getColor(this, R.color.colorWarning));
                } else if (item.getName().equalsIgnoreCase("asleep")) {
                    entries.add(new Entry(aux, 2, item));
                    colors.add(ContextCompat.getColor(this, R.color.colorPrimaryDark));
                } else if (item.getName().equalsIgnoreCase("restless")) {
                    entries.add(new Entry(aux, 3, item));
                    colors.add(ContextCompat.getColor(this, R.color.colorPrimary));
                } else if (item.getName().equalsIgnoreCase("rem")) {
                    entries.add(new Entry(aux, 2, item));
                    colors.add(ContextCompat.getColor(this, R.color.colorPrimaryDark));
                } else if (item.getName().equalsIgnoreCase("light")) {
                    entries.add(new Entry(aux, 3, item));
                    colors.add(ContextCompat.getColor(this, R.color.colorPrimary));
                } else if (item.getName().equalsIgnoreCase("deep")) {
                    entries.add(new Entry(aux, 4, item));
                    colors.add(ContextCompat.getColor(this, R.color.colorPurple));
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
                result = getString(R.string.title_awake);
            } else if (value == 2f) {
                result = sleep.getType().equalsIgnoreCase(SleepType.STAGES) ?
                        getString(R.string.title_rem) : getString(R.string.title_asleep);
            } else if (value == 3f) {
                result = sleep.getType().equalsIgnoreCase(SleepType.STAGES) ?
                        getString(R.string.title_light) : getString(R.string.title_restless);
            } else if (sleep.getType().equalsIgnoreCase(SleepType.STAGES) && value == 4f) {
                result = getString(R.string.title_deep);
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
                } else if (pattern.getName().equalsIgnoreCase("rem")) {
                    title = getString(R.string.title_rem);
                } else if (pattern.getName().equalsIgnoreCase("light")) {
                    title = getString(R.string.title_light);
                } else if (pattern.getName().equalsIgnoreCase("deep")) {
                    title = getString(R.string.title_deep);
                }

                mTitle.setText(title.concat(String.format(Locale.getDefault(), " %d min",
                        pattern.getDuration() / 60000)));

                mSubtitle.setText(DateUtils
                        .convertDateTimeUTCToLocale(pattern.getStartTime(), getString(R.string.hour_format1))
                        .concat(" - ")
                        .concat(DateUtils.convertDateTimeUTCToLocale(DateUtils.addMinutesToString(
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
