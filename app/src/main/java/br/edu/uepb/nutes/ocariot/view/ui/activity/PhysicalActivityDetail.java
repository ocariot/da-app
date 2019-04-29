package br.edu.uepb.nutes.ocariot.view.ui.activity;


import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import br.edu.uepb.nutes.ocariot.R;
import br.edu.uepb.nutes.ocariot.data.model.ocariot.ActivityLevel;
import br.edu.uepb.nutes.ocariot.data.model.ocariot.PhysicalActivity;
import br.edu.uepb.nutes.ocariot.utils.DateUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A fragment to see the details of a physical physicalActivity.
 *
 * @author Copyright (c) 2018, NUTES/UEPB
 */
public class PhysicalActivityDetail extends AppCompatActivity {
    private final String LOG_TAG = "PhysicalActivityDetail";

    public static String ACTIVITY_DETAIL = "activity_detail";

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.activity_period_tv)
    TextView dateStartTextView;

    @BindView(R.id.activity_datetime_start_details_tv)
    TextView datetimeStartTextView;

    @BindView(R.id.activity_duration_tv)
    TextView durationTextView;

    @BindView(R.id.activity_steps_tv)
    TextView stepsTextView;

    @BindView(R.id.activity_calories_tv)
    TextView caloriesTextView;

    @BindView(R.id.activity_calories_min_tv)
    TextView caloriesMinuteTextView;

    @BindView(R.id.activity_title_level_sedentary_tv)
    TextView sedentaryTextView;

    @BindView(R.id.activity_title_level_fairly_tv)
    TextView fairlyTextView;

    @BindView(R.id.activity_title_level_lightly_tv)
    TextView lightlyTextView;

    @BindView(R.id.activity_title_level_very_tv)
    TextView veryTextView;

    @BindView(R.id.activity_box_levels)
    RelativeLayout boxLevels;

    private PhysicalActivity physicalActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_physical_activity_detail);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);

        if (getIntent() != null) {
            physicalActivity = getIntent().getParcelableExtra(ACTIVITY_DETAIL);
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
        if (physicalActivity == null) return;

        initToobar();
        populateView(physicalActivity);
    }

    private void populateView(PhysicalActivity a) {
        dateStartTextView.setText(DateUtils.convertDateTimeUTCToLocale(a.getStartTime(),
                getResources().getString(R.string.date_format1), null));
        datetimeStartTextView.setText(DateUtils.convertDateTimeUTCToLocale(a.getStartTime(),
                getResources().getString(R.string.date_time_abb3), null));

        int duration = (int) (a.getDuration() / (60 * 1000));
        durationTextView.setText(getResources().getString(R.string.duration_min, duration));
        stepsTextView.setText(String.valueOf(a.getSteps()));
        caloriesTextView.setText(String.valueOf(a.getCalories()));

        if (duration > 0)
            caloriesMinuteTextView.setText(String.valueOf(a.getCalories() / duration));

        if (a.getLevels() != null && !a.getLevels().isEmpty()) {
            sedentaryTextView.setVisibility(View.VISIBLE);
            fairlyTextView.setVisibility(View.VISIBLE);
            lightlyTextView.setVisibility(View.VISIBLE);

            for (ActivityLevel activityLevel : a.getLevels()) {
                if (activityLevel.getName().equals(ActivityLevel.SEDENTARY_LEVEL)) {
                    sedentaryTextView.setText(getResources().getString(
                            R.string.level_sedentary, activityLevel.getDuration() / 60000));
                } else if (activityLevel.getName().equals(ActivityLevel.FAIRLY_LEVEL)) {
                    fairlyTextView.setText(getResources().getString(
                            R.string.level_fairly, activityLevel.getDuration() / 60000));
                } else if (activityLevel.getName().equals(ActivityLevel.LIGHTLY_LEVEL)) {
                    lightlyTextView.setText(getResources().getString(
                            R.string.level_lightly, activityLevel.getDuration() / 60000));
                } else if (activityLevel.getName().equals(ActivityLevel.VERY_LEVEL)) {
                    veryTextView.setText(getResources().getString(
                            R.string.level_very, activityLevel.getDuration() / 60000));
                }
            }
        } else {
            boxLevels.setVisibility(View.GONE);
        }
    }

    private void initToobar() {
        ActionBar mActionBar = getSupportActionBar();
        if (mActionBar == null) return;

        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setDisplayShowTitleEnabled(true);
        mActionBar.setHomeAsUpIndicator(R.drawable.ic_close_dark);
        mActionBar.setTitle(physicalActivity.getName());
    }
}
