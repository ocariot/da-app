package br.edu.uepb.nutes.ocariot.view.ui.activity;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

import br.edu.uepb.nutes.ocariot.R;
import br.edu.uepb.nutes.ocariot.data.model.ocariot.ActivityLevel;
import br.edu.uepb.nutes.ocariot.data.model.ocariot.Child;
import br.edu.uepb.nutes.ocariot.data.model.ocariot.PhysicalActivity;
import br.edu.uepb.nutes.ocariot.data.repository.local.pref.AppPreferencesHelper;
import br.edu.uepb.nutes.ocariot.utils.DateUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * A fragment to see the details of a physical physicalActivity.
 *
 * @author Copyright (c) 2018, NUTES/UEPB
 */
public class PhysicalActivityDetail extends AppCompatActivity {
    public static final String ACTIVITY_DETAIL = "activity_detail";

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.activity_date_start_details_tv)
    TextView dateStartDetailsTextView;

    @BindView(R.id.activity_range_duration_details_tv)
    TextView rangeDurationTextView;

    @BindView(R.id.activity_duration_tv)
    TextView durationTextView;

    @BindView(R.id.activity_steps_tv)
    TextView stepsTextView;

    @BindView(R.id.activity_calories_tv)
    TextView caloriesTextView;

    @BindView(R.id.activity_calories_min_tv)
    TextView caloriesMinuteTextView;

    @BindView(R.id.sedentary_Level_value_tv)
    TextView sedentaryTextView;

    @BindView(R.id.fairly_Level_value_tv)
    TextView fairlyTextView;

    @BindView(R.id.lightly_Level_value_tv)
    TextView lightlyTextView;

    @BindView(R.id.very_Level_value_tv)
    TextView veryTextView;

    @BindView(R.id.sedentary_Level_tv)
    TextView sedentaryBarTextView;

    @BindView(R.id.fairly_Level_tv)
    TextView fairlyBarTextView;

    @BindView(R.id.lightly_Level_tv)
    TextView lightlyBarTextView;

    @BindView(R.id.very_Level_tv)
    TextView veryBarTextView;


    @BindView(R.id.hr_avg_tv)
    TextView avgHeartRate;

    @BindView(R.id.activity_distance_tv)
    TextView distanceTextView;

    @BindView(R.id.activity_box_levels)
    RelativeLayout boxLevels;

    @BindView(R.id.box_hr)
    RelativeLayout boxHRZones;

    private PhysicalActivity physicalActivity;
    private Child childSeelcted;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_physical_activity_detail);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);

        childSeelcted = AppPreferencesHelper.getInstance().getLastSelectedChild();

        if (getIntent() != null) {
            physicalActivity = getIntent().getParcelableExtra(ACTIVITY_DETAIL);
        }
        initComponents();
    }

    private void initComponents() {
        if (physicalActivity == null) return;

        initToolbar();
        populateView(physicalActivity);
    }

    private void populateLevels(List<ActivityLevel> levels) {
        boxLevels.setVisibility(View.GONE);
        if (levels == null || levels.isEmpty()) return;

        boxLevels.setVisibility(View.VISIBLE);
        sedentaryTextView.setVisibility(View.VISIBLE);
        fairlyTextView.setVisibility(View.VISIBLE);
        lightlyTextView.setVisibility(View.VISIBLE);
        sedentaryTextView.setText(getString(R.string.level_sedentary, 0));
        lightlyTextView.setText(getString(R.string.level_lightly, 0));
        fairlyTextView.setText(getString(R.string.level_fairly, 0));
        veryTextView.setText(getString(R.string.level_very, 0));

        DecimalFormat df = new DecimalFormat("#");

        float total = 0;
        int durationMax = Integer.MIN_VALUE;
        for (ActivityLevel level : levels) {
            int duration = level.getDuration() / 60000;
            total += duration;
            if (duration > durationMax) durationMax = duration;
        }
        if (total == 0) return;

        int offsetBase = 500;
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        if (displaymetrics.widthPixels <= 768) offsetBase = 400;

        for (ActivityLevel activityLevel : levels) {
            if (activityLevel.getDuration() == 0) continue;

            int duration = activityLevel.getDuration() / 60000;
            double percent = (duration / total) * 100;

            int widthBar = (duration * (offsetBase / durationMax)) + 50;

            switch (activityLevel.getName()) {
                case ActivityLevel.SEDENTARY_LEVEL:
                    sedentaryTextView.setText(getString(R.string.level_sedentary, duration));
                    sedentaryBarTextView.setText(df.format(percent).concat("%"));
                    sedentaryBarTextView.getLayoutParams().width = widthBar;
                    break;
                case ActivityLevel.LIGHTLY_LEVEL:
                    lightlyTextView.setText(getString(R.string.level_lightly, duration));
                    lightlyBarTextView.setText(df.format(percent).concat("%"));
                    lightlyBarTextView.getLayoutParams().width = widthBar;
                    break;
                case ActivityLevel.FAIRLY_LEVEL:
                    fairlyTextView.setText(getString(R.string.level_fairly, duration));
                    fairlyBarTextView.setText(df.format(percent).concat("%"));
                    fairlyBarTextView.getLayoutParams().width = widthBar;
                    break;
                case ActivityLevel.VERY_LEVEL:
                    veryTextView.setText(getString(R.string.level_very, duration));
                    veryBarTextView.setText(df.format(percent).concat("%"));
                    veryBarTextView.getLayoutParams().width = widthBar;
                    break;
                default:
                    break;
            }
        }
    }

    private void populateView(PhysicalActivity a) {
        dateStartDetailsTextView.setText(DateUtils.convertDateTimeUTCToLocale(a.getStartTime(),
                getResources().getString(R.string.date_time_abb3), null));

        rangeDurationTextView.setText(getResources().getString(
                R.string.activity_range_time,
                DateUtils.convertDateTimeUTCToLocale(a.getStartTime(), getString(R.string.hour_format2)),
                DateUtils.convertDateTimeUTCToLocale(a.getEndTime(), getString(R.string.hour_format2))
        ));

        int duration = (int) (a.getDuration() / (60 * 1000));
        durationTextView.setText(getResources().getString(R.string.duration_min, duration));
        stepsTextView.setText(String.valueOf(a.getSteps()));
        caloriesTextView.setText(String.valueOf(a.getCalories()));

        if (duration > 0) {
            caloriesMinuteTextView.setText(String.valueOf(a.getCalories() / duration));
        }

        // levels
        populateLevels(a.getLevels());

        // heart rate
        boxHRZones.setVisibility(View.GONE);
        if (a.getHeartRate() != null && a.getHeartRate().getAverage() > 0) {
            boxHRZones.setVisibility(View.VISIBLE);
            avgHeartRate.setText(String.valueOf(a.getHeartRate().getAverage()));
        }

        // distance
        if (a.getDistance() != null && a.getDistance() > 0) {
            double distance = a.getDistance() / 1000;
            double distanceRest = a.getDistance() % 1000;
            if (distanceRest > 0) {
                distanceTextView.setText(String.format(Locale.getDefault(), "%.2f", distance));
            } else {
                distanceTextView.setText(String.format(Locale.getDefault(), "%d", (int) distance));
            }
        }
    }

    private void initToolbar() {
        ActionBar mActionBar = getSupportActionBar();
        if (mActionBar == null) return;

        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setDisplayShowTitleEnabled(true);
        mActionBar.setHomeAsUpIndicator(R.drawable.ic_close_dark);
        mActionBar.setTitle(physicalActivity.getName());
        mActionBar.setSubtitle(childSeelcted.getUsername());
    }
}
