package br.edu.uepb.nutes.ocariot.uaal_poc.view.ui.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import br.edu.uepb.nutes.ocariot.uaal_poc.R;
import br.edu.uepb.nutes.ocariot.uaal_poc.data.model.Activity;
import br.edu.uepb.nutes.ocariot.uaal_poc.data.model.ActivityLevel;
import br.edu.uepb.nutes.ocariot.uaal_poc.utils.DateUtils;
import br.edu.uepb.nutes.ocariot.uaal_poc.view.ui.activity.MainActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A fragment to see the details of a physical activity.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2018, NUTES/UEPB
 */
public class PhysicalActivityDetail extends Fragment {
    private final String LOG_TAG = "PhysicalActivityDetail";

    public static String ACTIVITY_DETAIL = "activity_detail";

    @BindView(R.id.date_start_tv)
    TextView dateStartTextView;

    @BindView(R.id.datetime_start__details_tv)
    TextView datetimeStartTextView;

    @BindView(R.id.duration_tv)
    TextView durationTextView;

    @BindView(R.id.steps_tv)
    TextView stepsTextView;

    @BindView(R.id.calories_tv)
    TextView caloriesTextView;

    @BindView(R.id.calories_min_tv)
    TextView caloriesMinuteTextView;

    @BindView(R.id.title_level_sedentary_tv)
    TextView sedentaryTextView;

    @BindView(R.id.title_level_fairly_tv)
    TextView fairlyTextView;

    @BindView(R.id.title_level_lightly_tv)
    TextView lightlyTextView;

    @BindView(R.id.title_level_very_tv)
    TextView veryTextView;

    private Activity activity;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PhysicalActivityDetail() {

    }

    public static PhysicalActivityDetail newInstance() {
        PhysicalActivityDetail fragment = new PhysicalActivityDetail();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (getArguments() != null && getArguments().size() != 0) {
            activity = (Activity) getArguments().getParcelable(ACTIVITY_DETAIL);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_physical_activity_detail, container, false);
        ButterKnife.bind(this, view);
        initComponents();

        return view;
    }

    private void initComponents() {
        if (activity == null) return;

        initToobar();
        populateView(activity);
    }

    private void populateView(Activity a) {
        dateStartTextView.setText(DateUtils.formatDateISO8601(a.getStartTime(),
                getResources().getString(R.string.date_format1), null));
        datetimeStartTextView.setText(DateUtils.formatDateISO8601(a.getStartTime(),
                getResources().getString(R.string.date_time_abb2), null));

        int duration = (int) (a.getDuration() / (60 * 1000));
        durationTextView.setText(getResources().getString(R.string.duration_min, duration));
        stepsTextView.setText(String.valueOf(a.getSteps()));
        caloriesTextView.setText(String.valueOf(a.getCalories()));

        if (duration > 0)
            caloriesMinuteTextView.setText(String.valueOf(a.getCalories() / duration));

        if (a.getActivityLevel() != null && a.getActivityLevel().size() > 0) {
            sedentaryTextView.setVisibility(View.VISIBLE);
            fairlyTextView.setVisibility(View.VISIBLE);
            lightlyTextView.setVisibility(View.VISIBLE);

            for (ActivityLevel activityLevel : a.getActivityLevel()) {
                if (activityLevel.getName().equals(ActivityLevel.SEDENTARY_LEVEL)) {
                    sedentaryTextView.setText(getResources().getString(
                            R.string.level_sedentary, activityLevel.getMinutes()));
                } else if (activityLevel.getName().equals(ActivityLevel.FAIRLY_LEVEL)) {
                    fairlyTextView.setText(getResources().getString(
                            R.string.level_fairly, activityLevel.getMinutes()));
                } else if (activityLevel.getName().equals(ActivityLevel.LIGHTLY_LEVEL)) {
                    lightlyTextView.setText(getResources().getString(
                            R.string.level_lightly, activityLevel.getMinutes()));
                } else if (activityLevel.getName().equals(ActivityLevel.VERY_LEVEL)) {
                    veryTextView.setText(getResources().getString(
                            R.string.level_very, activityLevel.getMinutes()));
                }
            }
        } else {
            sedentaryTextView.setVisibility(View.GONE);
            fairlyTextView.setVisibility(View.GONE);
            lightlyTextView.setVisibility(View.GONE);
            veryTextView.setText(a.getIntensityLevel());
        }
    }

    private void initToobar() {
        ActionBar mActionBar = ((MainActivity) getActivity()).getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setDisplayShowTitleEnabled(true);
        mActionBar.setHomeAsUpIndicator(R.drawable.ic_close_dark);
        mActionBar.setTitle(activity.getName());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Log.w(LOG_TAG, "ACTIVITY SELECTED: " + activity.toString());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.clear();
    }
}
