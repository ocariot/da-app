package br.edu.uepb.nutes.ocariot.view.ui.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatSpinner;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import br.edu.uepb.nutes.ocariot.R;
import br.edu.uepb.nutes.ocariot.data.model.Environment;
import br.edu.uepb.nutes.ocariot.data.model.Location;
import br.edu.uepb.nutes.ocariot.data.model.UserAccess;
import br.edu.uepb.nutes.ocariot.data.repository.local.pref.AppPreferencesHelper;
import br.edu.uepb.nutes.ocariot.data.repository.remote.fitbit.FitBitNetRepository;
import br.edu.uepb.nutes.ocariot.data.repository.remote.ocariot.OcariotNetRepository;
import br.edu.uepb.nutes.ocariot.utils.DateUtils;
import br.edu.uepb.nutes.ocariot.view.adapter.PhysicalActivityListAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.observers.DisposableObserver;

/**
 * A fragment representing a list of Items.
 *
 * @author Copyright (c) 2018, NUTES/UEPB
 */
public class EnvironmentFragment extends Fragment implements View.OnClickListener {
    private final String LOG_TAG = "EnvListFragment";
    private final String FOMART_DATE_DEFAULT = "yyyy-MM-dd";

    private PhysicalActivityListAdapter mAdapter;
    private FitBitNetRepository fitBitRepository;
    private OcariotNetRepository ocariotRepository;
    private UserAccess userAccess;
    private String currentDate;
    private List<Entry> entries;
    private List<Environment> environments;

    /**
     * We need this variable to lock and unlock loading more.
     * We should not charge more when a request has already been made.
     * The load will be activated when the requisition is completed.
     */
    private boolean itShouldLoadMore = true;

    @BindView(R.id.data_swipe_refresh)
    SwipeRefreshLayout mDataSwipeRefresh;

    @BindView(R.id.location_tv)
    TextView mLocation;

    @BindView(R.id.datetime_tv)
    TextView mDatetime;

    @BindView(R.id.last_temp_tv)
    TextView mLastTemp;

    @BindView(R.id.last_hum_tv)
    TextView mLastHumidity;

    @BindView(R.id.average_temp_tv)
    TextView mAverageTemp;

    @BindView(R.id.average_hum_tv)
    TextView mAverageHumidity;

    @BindView(R.id.room_spinner)
    AppCompatSpinner mRoomSpinner;

    @BindView(R.id.prev_img_bt)
    AppCompatImageButton mPrevButton;

    @BindView(R.id.next_img_bt)
    AppCompatImageButton mNextButton;

    @BindView(R.id.env_chart)
    LineChart mLineChart;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public EnvironmentFragment() {
    }

    public static EnvironmentFragment newInstance() {
        return new EnvironmentFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userAccess = AppPreferencesHelper.getInstance(getContext()).getUserAccessOcariot();
        currentDate = DateUtils.getCurrentDatetime(FOMART_DATE_DEFAULT);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_environment, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        fitBitRepository = FitBitNetRepository.getInstance(getContext());
        ocariotRepository = OcariotNetRepository.getInstance(getContext());

        initComponents();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (fitBitRepository != null) fitBitRepository.dispose();
    }

    /**
     * Initialize components
     */
    private void initComponents() {
        mPrevButton.setOnClickListener(this);
        mNextButton.setOnClickListener(this);
        mDatetime.setOnClickListener(this);
        mDatetime.setText(DateUtils.formatDate(currentDate, getString(R.string.date_time_abb3)));

        initDataSwipeRefresh();
        loadDataOcariot(currentDate);
    }

    /**
     * Initialize SwipeRefresh
     */
    private void initDataSwipeRefresh() {
        mDataSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.w(LOG_TAG, "onRefresh()");
                if (itShouldLoadMore) loadDataOcariot(currentDate);
            }
        });
    }

    /**
     * Load data in OCARIoT Server.
     * If there is no internet connection, we can display the local database.
     * Otherwise it displays from the remote server.
     */
    private void loadDataOcariot(String date) {
        if (userAccess == null) return;

        loading(true);

        ocariotRepository
                .listEnvironments("-timestamp", 1, 100, null, null)
                .subscribe(new DisposableObserver<List<Environment>>() {
                    @Override
                    public void onNext(List<Environment> environmentsList) {
                        environments = environmentsList;
                        Log.w(LOG_TAG, "loadDataOcariot() -" + Arrays.toString(environments.toArray()));
                        populateView(environments);

                        loading(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mDataSwipeRefresh.setRefreshing(false);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void populateView(List<Environment> environments) {
        if (environments == null) return;

        printChart();

        Location location = environments.get(0).getLocation();
        mLocation.setText(getString(
                R.string.environment_location,
                location.getCity().concat(", ").concat(location.getCountry())));

        populateRoomAndSummary(environments);
    }

    private void populateRoomAndSummary(List<Environment> environments) {
        List<String> rooms = new ArrayList<>();
        double sumTemp = 0, sumHum = 0;

        for (Environment env : environments) {
            String room = env.getLocation().getSchool()
                    .concat(", ")
                    .concat(env.getLocation().getRoom());
            if (!rooms.contains(room)) rooms.add(room);

            sumTemp += env.getTemperature();
            sumHum += env.getHumidity();
        }
        // Room
        ArrayAdapter<String> adapter = new ArrayAdapter<>(Objects.requireNonNull(getContext()),
                android.R.layout.simple_spinner_item, rooms);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mRoomSpinner.setAdapter(adapter);

        // Summary
        mLastTemp.setText(String.valueOf(Math.round(environments.get(0).getTemperature())));
        mLastHumidity.setText(String.valueOf(Math.round(environments.get(0).getHumidity())).concat("%"));
        mAverageTemp.setText(String.valueOf(Math.round((float) (sumTemp / environments.size()))));
        mAverageHumidity.setText(String.valueOf(Math.round((float) (sumHum / environments.size()))).concat("%"));
    }

    /**
     * Enable/Disable display loading data.
     *
     * @param enabled boolean
     */
    private void loading(final boolean enabled) {
        if (!enabled) {
            mDataSwipeRefresh.setRefreshing(false);
            itShouldLoadMore = true;
        } else {
            mDataSwipeRefresh.setRefreshing(true);
            itShouldLoadMore = false;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.prev_img_bt: {
                currentDate = DateUtils.addDaysToDateString(currentDate, -1);
                Log.w("DATE", " - " + currentDate);
                mDatetime.setText(DateUtils.formatDate(currentDate, getString(R.string.date_time_abb3)));

                mDatetime.setText(DateUtils.formatDate(currentDate, getString(R.string.date_time_abb3)));

                loadDataOcariot(currentDate);
            }
            break;
            case R.id.next_img_bt: {
                String dateToday = DateUtils.getCurrentDatetime(FOMART_DATE_DEFAULT);
                if (currentDate.equals(dateToday)) return;

                currentDate = DateUtils.addDaysToDateString(currentDate, 1);
                mDatetime.setText(DateUtils.formatDate(currentDate, getString(R.string.date_time_abb3)));
                loadDataOcariot(currentDate);
            }
            break;
            case R.id.datetime_tv:
                break;
            default:
                break;
        }
    }

    private void printChart() {
//        ((LineDataSet) data.getDataSetByIndex(0)).setCircleColorHole(Color.RED);

        // no description text
        mLineChart.getDescription().setEnabled(false);

        // get the legend (only possible after setting data)
        Legend l = mLineChart.getLegend();
        l.setEnabled(false);

        mLineChart.getAxisLeft().setEnabled(true);
        mLineChart.getAxisLeft().setEnabled(true);

        //XAxis
        mLineChart.getXAxis().setEnabled(true);
        mLineChart.getXAxis().setDrawGridLines(false);
        mLineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        mLineChart.getXAxis().setDrawAxisLine(true);
        mLineChart.getXAxis().setGranularity(1f);
        mLineChart.getXAxis().setValueFormatter(prepareVariablesLineData(environments));


        mLineChart.setDrawGridBackground(false);
        mLineChart.setGridBackgroundColor(Color.TRANSPARENT);
        mLineChart.getAxisRight().setEnabled(true);
        mLineChart.getAxisRight().setAxisLineColor(Color.TRANSPARENT);
        mLineChart.getAxisRight().setGridColor(Color.TRANSPARENT);
        mLineChart.getAxisRight().setTextColor(Color.TRANSPARENT);
        mLineChart.getAxisLeft().setAxisLineColor(Color.TRANSPARENT);
        mLineChart.getAxisLeft().setGridColor(Color.TRANSPARENT);
        mLineChart.getAxisLeft().setTextColor(Color.TRANSPARENT);


//        mLineChart.setVisibleXRangeMaximum(10f);

//        mLineChart.setVisibleYRangeMaximum(50, YAxis.AxisDependency.LEFT);
//        mLineChart.setVisibleYRangeMinimum(10.6f, YAxis.AxisDependency.LEFT);

//        mLineChart.setVisibleXRangeMinimum(2f);
//        mLineChart.setVisibleXRangeMaximum(10f);


////        mLineChart.setVisibleXRangeMaximum(10f);

        // animate calls invalidate()...
        mLineChart.animateX(2500);

        // add data
        mLineChart.setData(getData());

        mLineChart.setAutoScaleMinMaxEnabled(false);
        mLineChart.setVisibleXRangeMaximum(10f); // O maximo q posso diminuir
        mLineChart.setVisibleXRangeMinimum(5f); // O máximo q posso esticar
//        mLineChart.setVisibleYRangeMaximum(2f, YAxis.AxisDependency.RIGHT);
//        mLineChart.setVisibleYRangeMinimum(0f, YAxis.AxisDependency.LEFT);
//        mLineChart.getXAxis().setAxisMinValue(0);
//        mLineChart.getXAxis().setAxisMaxValue(10);
//        mLineChart.getAxisLeft().setAxisMinValue(-2);
        mLineChart.getAxisLeft().setAxisMaximum(29f);
        mLineChart.getAxisLeft().setAxisMinimum(0f);
//        mLineChart.getAxisLeft().setAxisMaxValue(40);
//        leftAxis.setAxisMaxValue(params.YMax);
//        mLineChart.getXAxis().mAxisRange(5f, YAxis.AxisDependency.LEFT);
//        mLineChart.getAxisLeft().setAxisMinimum(1.5f);
//        mLineChart.getAxisLeft().setAxisMaximum(50f);
        mLineChart.moveViewToX(environments.get(environments.size() - 1).getTemperature());
        mLineChart.invalidate();
    }

    private LineData getData() {
        if (environments == null) return new LineData();
        entries = new ArrayList<>();

        for (int i = 0; i < this.environments.size(); i++) {
            entries.add(new Entry(i, Math.round(this.environments.get(i).getTemperature())));
        }

        // create a dataset and give it a type
        LineDataSet set1 = new LineDataSet(entries, "DataSet 1");
        set1.setColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
        set1.setValueTextColor(Color.BLACK);

        set1.setLineWidth(2f);
        set1.setCircleColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
        set1.setHighLightColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
        set1.setCircleColorHole(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
        set1.setDrawValues(true);
        set1.setDrawCircles(true);
        set1.setDrawFilled(true);
        set1.setMode(LineDataSet.Mode.CUBIC_BEZIER);

        // create a data object with the data sets
        return new LineData(set1);
    }

    public IAxisValueFormatter prepareVariablesLineData(List<Environment> environments) {
        if (entries == null) entries = new ArrayList<>();
        final String[] quarters = new String[environments.size()];

        for (int i = 0; i < environments.size(); i++) {
            String date = DateUtils.formatDateHour(environments.get(i).getTimestamp(), "HH:mm");
            entries.add(new Entry((float) i, Math.round(environments.get(i).getTemperature())));

            quarters[i] = date;
        }

        // Format date
        return ((value, axis) -> {
            if (value >= quarters.length || value < 0) return "";
            return quarters[(int) value];
        });
    }
}
