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
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import br.edu.uepb.nutes.ocariot.R;
import br.edu.uepb.nutes.ocariot.data.model.Child;
import br.edu.uepb.nutes.ocariot.data.model.Environment;
import br.edu.uepb.nutes.ocariot.data.repository.local.pref.AppPreferencesHelper;
import br.edu.uepb.nutes.ocariot.data.repository.remote.fitbit.FitBitNetRepository;
import br.edu.uepb.nutes.ocariot.data.repository.remote.ocariot.OcariotNetRepository;
import br.edu.uepb.nutes.ocariot.utils.DateUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.CompositeDisposable;

/**
 * A fragment representing a list of Items.
 *
 * @author Copyright (c) 2018, NUTES/UEPB
 */
public class EnvironmentFragment extends Fragment implements View.OnClickListener {
    private final String LOG_TAG = "EnvListFragment";
    private final String FORMAT_DATE_DEFAULT = "yyyy-MM-dd";

    private FitBitNetRepository fitBitRepository;
    private OcariotNetRepository ocariotRepository;
    private String dateStart, dateEnd = null;
    private List<Environment> environments;
    private LineChart[] mCharts;
    private Child childProfile;
    private boolean isFirstRequest, isLoadBlocked = true;
    private String currentRoom;
    private Context mContext;
    private CompositeDisposable mDisposable;

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
        // Empty constructor required!
    }

    public static EnvironmentFragment newInstance() {
        return new EnvironmentFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = Objects.requireNonNull(getActivity()).getApplicationContext();
        mDisposable = new CompositeDisposable();
        fitBitRepository = FitBitNetRepository.getInstance(mContext);
        ocariotRepository = OcariotNetRepository.getInstance(mContext);
        childProfile = AppPreferencesHelper.getInstance(mContext).getUserProfile();
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
        dateStart = DateUtils.getCurrentDatetime(FORMAT_DATE_DEFAULT);
        isFirstRequest = true;
        currentRoom = null;

        initComponents();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (fitBitRepository != null) fitBitRepository.dispose();
        mDisposable.dispose();
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
        mDatetime.setText(getString(R.string.today_text));

        initDataSwipeRefresh();
        loadDataOcariot();
    }

    /**
     * Initialize SwipeRefresh
     */
    private void initDataSwipeRefresh() {
        mDataSwipeRefresh.setOnRefreshListener(() -> {
            Log.w(LOG_TAG, "onRefresh()");
            if (itShouldLoadMore) loadDataOcariot();
        });
    }

    /**
     * Load data in OCARIoT Server.
     * If there is no internet connection, we can display the local database.
     * Otherwise it displays from the remote server.
     */
    private void loadDataOcariot() {
        if (childProfile.getInstitution() == null) return;

        mDisposable.add(
                ocariotRepository
                        .listEnvironments("timestamp", 1, 1000,
                                childProfile.getInstitution().get_id(),
                                currentRoom,
                                "gte:".concat(dateStart),
                                (dateEnd != null ? "lt:".concat(dateEnd) : null))
                        .doOnSubscribe(disposable -> loading(true))
                        .doAfterTerminate(() -> loading(false))
                        .subscribe(environmentsList -> {
                            this.environments = environmentsList;
                            populateView(environmentsList);
                            isFirstRequest = false;
                        })
        );
    }

    private void populateView(List<Environment> environments) {
        if (mContext == null) return;
        Log.w(LOG_TAG, new Gson().toJson(environments));
        mLocation.setText(Objects.requireNonNull(mContext).getResources().getString(
                R.string.environment_location,
                childProfile.getInstitution().getName().concat(", ")
                        .concat(childProfile.getInstitution().getType())));

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
        double sumTemp = 0;
        double sumHum = 0;

        for (Environment env : environments) {
            String room = env.getLocation().getLocal()
                    .concat(", ")
                    .concat(env.getLocation().getRoom());
            if (!rooms.contains(room)) rooms.add(room);

            sumTemp += env.getTemperature();
            sumHum += env.getHumidity();
        }

        // Room
        if (isFirstRequest) {
            Collections.sort(rooms);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(Objects.requireNonNull(mContext),
                    android.R.layout.simple_spinner_item, rooms);
            adapter.setDropDownViewResource(R.layout.spinner_item);
            mRoomSpinner.setAdapter(adapter);
            currentRoom = rooms.get(0).split(", ")[1];

            mRoomSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    currentRoom = String.valueOf(parent.getAdapter().getItem(position)).split(", ")[1];
                    ((TextView) parent.getChildAt(0)).setTextColor(ContextCompat
                            .getColor(mContext, R.color.colorBlackLight)
                    );
                    if (!isLoadBlocked) loadDataOcariot();

                    isLoadBlocked = false;
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    // Not implemented!
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
            return;
        }
        if (mDataSwipeRefresh.isRefreshing()) return;
        mDataSwipeRefresh.setRefreshing(true);
        itShouldLoadMore = false;
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
                String dateToday = DateUtils.getCurrentDatetime(FORMAT_DATE_DEFAULT);
                if (dateStart.equals(dateToday)) return;

                dateStart = DateUtils.addDaysToDateString(dateStart, 1);
                if (dateStart.equals(dateToday)) dateEnd = null;
                else dateEnd = DateUtils.addDaysToDateString(dateStart, 1);

                if (!dateStart.equals(dateToday)) {
                    mDatetime.setText(DateUtils.formatDate(dateStart, getString(R.string.date_time_abb3)));
                } else {
                    mDatetime.setText(getString(R.string.title_today));
                }
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
        if (environments == null || environments.isEmpty()) return;

        final List<Entry> entriesTemperature = new ArrayList<>();
        final List<Entry> entriesHumidity = new ArrayList<>();

        for (int i = 0; i < environments.size(); i++) {
            entriesTemperature.add(new Entry(i, (float) environments.get(i).getTemperature()));
            entriesHumidity.add(new Entry(i, (float) environments.get(i).getHumidity()));
        }

        for (int i = 0; i < mCharts.length; i++) {
            final int chartId = i;
            mCharts[i].getLegend().setEnabled(true);
            mCharts[i].setDrawGridBackground(false);
            mCharts[i].getDescription().setEnabled(false);
            mCharts[i].setDoubleTapToZoomEnabled(false);
            mCharts[i].setHardwareAccelerationEnabled(false);

            mCharts[i].getAxisRight().setDrawGridLines(false);
            mCharts[i].getAxisRight().setEnabled(false);

            mCharts[i].getAxisLeft().setDrawGridLines(false);
            mCharts[i].getAxisLeft().setEnabled(false);
            mCharts[i].getAxisLeft().setDrawLabels(false);

            mCharts[i].getXAxis().setEnabled(true);
            mCharts[i].getXAxis().setDrawGridLines(false);
            mCharts[i].getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
            mCharts[i].getXAxis().setDrawAxisLine(true);
            mCharts[i].getXAxis().setGranularity(1f);
            mCharts[i].getXAxis().setValueFormatter((value, axis) -> {
                if (value >= entriesTemperature.size() || value < 0) return "";
                return DateUtils.formatDateHour(environments.get((int) value).getTimestamp(),
                        "HH\'h\':mm\'m\'");
            });

            LineDataSet dataSet;
            if (chartId == 0) { // temp
                dataSet = new LineDataSet(entriesTemperature, getString(R.string.title_temperature));
                int color = ContextCompat.getColor(Objects.requireNonNull(getActivity()),
                        R.color.colorWarningDark);
                dataSet.setFillColor(ContextCompat.getColor(
                        Objects.requireNonNull(mContext), R.color.colorWarning));
                dataSet.setColor(color);
                dataSet.setCircleColor(color);
                dataSet.setHighLightColor(color);
                dataSet.setCircleColorHole(color);
            } else {
                dataSet = new LineDataSet(entriesHumidity, getString(R.string.title_humidity));
                int color = ContextCompat.getColor(Objects.requireNonNull(getActivity()),
                        R.color.colorPrimaryDark);
                dataSet.setFillColor(ContextCompat.getColor(
                        Objects.requireNonNull(mContext), R.color.colorPrimary));
                dataSet.setColor(color);
                dataSet.setCircleColor(color);
                dataSet.setHighLightColor(color);
                dataSet.setCircleColorHole(color);
            }
            dataSet.setDrawHighlightIndicators(false);
            dataSet.setLineWidth(2.5f);
            dataSet.setDrawFilled(true);
            dataSet.setValueTextColor(Color.BLACK);
            dataSet.setValueTextSize(10f);
            dataSet.setDrawValues(true);
            dataSet.setDrawCircles(true);
            dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);

            LineData lineData = new LineData(dataSet);
            mCharts[i].setData(lineData);
            mCharts[i].setVisibleXRangeMaximum(7f); // The most I can decrease
            mCharts[i].setVisibleXRangeMinimum(2f); // The most I can stretch
            mCharts[i].animateX(1000);
            mCharts[i].moveViewToX(environments.size());
            mCharts[i].getAxisLeft().setAxisMinimum(mCharts[i].getYMin() - 2);
            mCharts[i].getAxisLeft().setAxisMaximum(mCharts[i].getYMax());

            mCharts[i].getLineData().setValueFormatter((value, entry, dataSetIndex, viewPortHandler) -> {
                if (chartId == 0) return String.format(Locale.getDefault(), "%.1f°C", value);
                return String.format(Locale.getDefault(), "%.1f%%", value);
            });
        }

        mCharts[0].invalidate(); // print graph temperature
        mCharts[1].invalidate(); // print graph humidity
    }

    private void cleanCharts() {
        for (LineChart mChart : mCharts) {
            mChart.clear();
            mChart.invalidate();
        }
    }

    private void cleanSummary() {
        mLastTemp.setText("0");
        mLastHumidity.setText("0");
        mAverageTemp.setText("0");
        mAverageHumidity.setText("0");
    }
}
