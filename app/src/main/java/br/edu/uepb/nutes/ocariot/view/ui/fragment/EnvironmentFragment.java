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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import br.edu.uepb.nutes.ocariot.R;
import br.edu.uepb.nutes.ocariot.data.model.Environment;
import br.edu.uepb.nutes.ocariot.data.model.User;
import br.edu.uepb.nutes.ocariot.data.repository.local.pref.AppPreferencesHelper;
import br.edu.uepb.nutes.ocariot.data.repository.remote.fitbit.FitBitNetRepository;
import br.edu.uepb.nutes.ocariot.data.repository.remote.ocariot.OcariotNetRepository;
import br.edu.uepb.nutes.ocariot.utils.DateUtils;
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

    private FitBitNetRepository fitBitRepository;
    private OcariotNetRepository ocariotRepository;
    private String dateStart, dateEnd = null;
    private List<Entry> entriesTemperature, entriesHumididy;
    private List<Environment> environments;
    private LineChart[] mCharts;
    private User userProfile;
    private boolean isFirstRequest;
    private String currentRoom;

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

    @BindView(R.id.env_chart_temp)
    LineChart mLineChartTemperature;

    @BindView(R.id.env_chart_humi)
    LineChart mLineChartHumidity;

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
        dateStart = DateUtils.getCurrentDatetime(FOMART_DATE_DEFAULT);
        fitBitRepository = FitBitNetRepository.getInstance(getContext());
        ocariotRepository = OcariotNetRepository.getInstance(getContext());
        userProfile = AppPreferencesHelper.getInstance(getContext()).getUserProfile();
        isFirstRequest = true;
        currentRoom = null;

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
        mCharts = new LineChart[2];
        mCharts[0] = mLineChartTemperature;
        mCharts[1] = mLineChartHumidity;

        mPrevButton.setOnClickListener(this);
        mNextButton.setOnClickListener(this);
        mDatetime.setOnClickListener(this);
        mDatetime.setText(DateUtils.formatDate(dateStart, getString(R.string.date_time_abb3)));

        initDataSwipeRefresh();
        loadDataOcariot();
    }

    /**
     * Initialize SwipeRefresh
     */
    private void initDataSwipeRefresh() {
        mDataSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.w(LOG_TAG, "onRefresh()");
                if (itShouldLoadMore) loadDataOcariot();
            }
        });
    }

    /**
     * Load data in OCARIoT Server.
     * If there is no internet connection, we can display the local database.
     * Otherwise it displays from the remote server.
     */
    private void loadDataOcariot() {
        loading(true);
        if (userProfile.getSchool() == null) return;

        ocariotRepository
                .listEnvironments("timestamp", 1, 1000,
                        userProfile.getSchool().getName(),
                        currentRoom,
                        "gte:".concat(dateStart),
                        (dateEnd != null ? "lt:".concat(dateEnd) : null))
                .subscribe(new DisposableObserver<List<Environment>>() {
                    @Override
                    public void onNext(List<Environment> environmentsList) {
                        environments = environmentsList;
                        Log.w(LOG_TAG, "loadDataOcariot() -" + Arrays.toString(environments.toArray()));
                        populateView(environments);

                        loading(false);
                        isFirstRequest = false;
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
        mLocation.setText(getString(
                R.string.environment_location,
                userProfile.getSchool()
                        .getCity().concat(", ")
                        .concat(userProfile.getSchool().getCountry())));

        if (environments == null || environments.isEmpty()) {
            cleanCharts();
            cleanSummary();
            return;
        }
        printCharts();

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
        if (isFirstRequest) {
            Collections.sort(rooms);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(Objects.requireNonNull(getContext()),
                    android.R.layout.simple_spinner_item, rooms);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mRoomSpinner.setAdapter(adapter);
            currentRoom = rooms.get(0).split(", ")[1];
            mRoomSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    currentRoom = String.valueOf(parent.getAdapter().getItem(position)).split(", ")[1];
                    loadDataOcariot();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }

        // Summary
        mLastTemp.setText(String.valueOf(Math.round(environments.get(environments.size() - 1).getTemperature())));
        mLastHumidity.setText(String.valueOf(Math.round(environments.get(environments.size() - 1).getHumidity())).concat("%"));
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
                dateEnd = dateStart;
                dateStart = DateUtils.addDaysToDateString(dateStart, -1);
                Log.w("DATE", " - " + dateStart);

                mDatetime.setText(DateUtils.formatDate(dateStart, getString(R.string.date_time_abb3)));
                loadDataOcariot();
            }
            break;
            case R.id.next_img_bt: {
                String dateToday = DateUtils.getCurrentDatetime(FOMART_DATE_DEFAULT);
                if (dateStart.equals(dateToday)) return;

                dateStart = DateUtils.addDaysToDateString(dateStart, 1);
                if (dateStart.equals(dateToday)) dateEnd = null;
                else dateEnd = DateUtils.addDaysToDateString(dateStart, 1);

                mDatetime.setText(DateUtils.formatDate(dateStart, getString(R.string.date_time_abb3)));
                loadDataOcariot();
            }
            break;
            case R.id.datetime_tv:
                break;
            default:
                break;
        }
    }

    private void printCharts() {
        for (int i = 0; i < mCharts.length; i++) {
            final int chartId = i;
            mCharts[i].getXAxis().setValueFormatter(prepareLineData()[i]);
            mCharts[i].setData(getData()[i]);

            mCharts[i].getDescription().setEnabled(false);

            // get the legend (only possible after setting data)
            mCharts[i].getLegend().setEnabled(true);

            mCharts[i].getXAxis().setEnabled(true);
            mCharts[i].getXAxis().setDrawGridLines(false);
            mCharts[i].getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
            mCharts[i].getXAxis().setDrawAxisLine(true);
            mCharts[i].getXAxis().setGranularity(1f);

            mCharts[i].setDrawGridBackground(false);
            mCharts[i].setGridBackgroundColor(Color.TRANSPARENT);
            mCharts[i].getAxisRight().setEnabled(true);
            mCharts[i].getAxisRight().setAxisLineColor(Color.TRANSPARENT);
            mCharts[i].getAxisRight().setGridColor(Color.TRANSPARENT);
            mCharts[i].getAxisRight().setTextColor(Color.TRANSPARENT);
            mCharts[i].setExtraOffsets(-15f, 0f, -5f, 0f);

            if (chartId == 0) { // temperature
                mCharts[i].getAxisLeft().setAxisMinimum(0);
                mCharts[i].getAxisLeft().setAxisMaximum(getData()[i].getYMax());
                mCharts[i].getAxisRight().setAxisMinimum(0);
                mCharts[i].getAxisRight().setAxisMaximum(getData()[i].getYMax());
            } else { // humidity
                mCharts[i].getAxisLeft().setAxisMinimum(0);
                mCharts[i].getAxisLeft().setAxisMaximum(getData()[i].getYMax() + 2);
                mCharts[i].getAxisRight().setAxisMinimum(0);
                mCharts[i].getAxisRight().setAxisMaximum(getData()[i].getYMax() + 2);
            }

            mCharts[i].getAxisLeft().setEnabled(true);
            mCharts[i].getAxisLeft().setAxisLineColor(Color.TRANSPARENT);
            mCharts[i].getAxisLeft().setGridColor(Color.TRANSPARENT);
            mCharts[i].getAxisLeft().setTextColor(Color.TRANSPARENT);

            mCharts[i].setAutoScaleMinMaxEnabled(false);
            mCharts[i].setVisibleXRangeMaximum(7f); // O maximo q posso diminuir
            mCharts[i].setVisibleXRangeMinimum(2f); // O máximo q posso esticar
            mCharts[i].animateX(1000);
            mCharts[i].moveViewToX(environments.size());
            mCharts[i].getLineData().setValueFormatter(new IValueFormatter() {
                @Override
                public String getFormattedValue(float value, Entry entry, int dataSetIndex,
                                                ViewPortHandler viewPortHandler) {
                    if (chartId == 0) return Math.round(value) + "°C";
                    return Math.round(value) + "%";
                }
            });
        }

        mCharts[0].invalidate(); // print graph temperature
        mCharts[1].invalidate(); // print graph humidity
    }

    private LineData[] getData() {
        LineData[] lineData = new LineData[mCharts.length];
        if (lineData.length == 0) return lineData;

        LineDataSet[] lineDataSets = new LineDataSet[lineData.length];
        for (int i = 0; i < mCharts.length; i++) {
            lineData[i] = new LineData();

            int color = 0;
            if (i == 0) { // temperature
                lineDataSets[i] = new LineDataSet(entriesTemperature,
                        getString(R.string.title_temperature));
                color = ContextCompat.getColor(
                        Objects.requireNonNull(getContext()), R.color.colorPrimaryDark);
                lineDataSets[i].setFillColor(ContextCompat.getColor(
                        Objects.requireNonNull(getContext()), R.color.colorPrimary));
            } else { // humidity
                lineDataSets[i] = new LineDataSet(entriesHumididy,
                        getString(R.string.title_humidity));
                color = ContextCompat.getColor(
                        Objects.requireNonNull(getContext()), R.color.colorWarningDark);
                lineDataSets[i].setFillColor(ContextCompat.getColor(
                        Objects.requireNonNull(getContext()), R.color.colorWarning));
            }

            lineDataSets[i].setValueTextColor(Color.BLACK);
            lineDataSets[i].setLineWidth(2.5f);
            lineDataSets[i].setValueTextSize(12f);
            lineDataSets[i].setDrawValues(true);
            lineDataSets[i].setDrawCircles(true);
            lineDataSets[i].setDrawFilled(true);
            lineDataSets[i].setMode(LineDataSet.Mode.CUBIC_BEZIER);
            lineDataSets[i].setColor(color);
            lineDataSets[i].setCircleColor(color);
            lineDataSets[i].setHighLightColor(color);
            lineDataSets[i].setCircleColorHole(color);
            lineData[i].addDataSet(lineDataSets[i]);
        }

        return lineData;
    }

    private IAxisValueFormatter[] prepareLineData() {
        final String[] quartersTemperature = new String[environments.size()];
        final String[] quartersHumidity = new String[environments.size()];
        entriesTemperature = new ArrayList<>();
        entriesHumididy = new ArrayList<>();

        for (int i = 0; i < environments.size(); i++) {
            Environment env = environments.get(i);

            String date = DateUtils.formatDateHour(env.getTimestamp(), "HH\'h\':mm\'m\'");
            entriesTemperature.add(new Entry(i, Math.round(environments.get(i).getTemperature())));
            entriesHumididy.add(new Entry(i, Math.round(environments.get(i).getHumidity())));

            quartersTemperature[i] = date;
            quartersHumidity[i] = date;
        }

        IAxisValueFormatter[] axisValueFormatters = new IAxisValueFormatter[2];
        axisValueFormatters[0] = ((value, axis) -> {
            if (value >= quartersTemperature.length || value < 0) return "";
            return quartersTemperature[(int) value];
        });

        axisValueFormatters[1] = ((value, axis) -> {
            if (value >= quartersHumidity.length || value < 0) return "";
            return quartersHumidity[(int) value];
        });

        return axisValueFormatters;
    }

    private void cleanCharts() {
        for (int i = 0; i < mCharts.length; i++) {
            mCharts[i].clear();
            mCharts[i].invalidate();
        }
    }

    private void cleanSummary() {
        mLastTemp.setText("0");
        mLastHumidity.setText("0");
        mAverageTemp.setText("0");
        mAverageHumidity.setText("0");
    }
}
