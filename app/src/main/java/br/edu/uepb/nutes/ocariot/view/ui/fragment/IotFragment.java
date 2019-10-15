package br.edu.uepb.nutes.ocariot.view.ui.fragment;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.ImageViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.AppCompatImageView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.SkeletonScreen;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import br.edu.uepb.nutes.ocariot.R;
import br.edu.uepb.nutes.ocariot.data.model.ocariot.Weight;
import br.edu.uepb.nutes.ocariot.data.repository.local.pref.AppPreferencesHelper;
import br.edu.uepb.nutes.ocariot.data.repository.remote.ocariot.OcariotNetRepository;
import br.edu.uepb.nutes.ocariot.service.HRManager;
import br.edu.uepb.nutes.ocariot.service.HRManagerCallback;
import br.edu.uepb.nutes.ocariot.utils.AlertMessage;
import br.edu.uepb.nutes.ocariot.utils.DateUtils;
import br.edu.uepb.nutes.simpleblescanner.SimpleBleScanner;
import br.edu.uepb.nutes.simpleblescanner.SimpleScannerCallback;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.CompositeDisposable;

/**
 * A fragment representing iot devices.
 *
 * @author Copyright (c) 2018, NUTES/UEPB
 */
public class IotFragment extends Fragment {
    private final String LOG_TAG = "IotFragment";

    private OcariotNetRepository ocariotRepository;
    private Context mContext;
    private CompositeDisposable mDisposable;
    private AppPreferencesHelper appPref;
    private AlertMessage mAlertMessage;
    private SkeletonScreen mSkeletonScreen;
    private SimpleBleScanner mScanner;
    private HRManager mHRManager;
    private ObjectAnimator heartAnimation;
    private int minHR, maxHR, avgHR, sumHR, totalHR;
    private AlphaAnimation fadeIn;

    // We need this variable to lock and unlock loading more.
    // We should not charge more when a request has already been made.
    // The load will be activated when the requisition is completed.
    private boolean itShouldLoadMore = true;

    @BindView(R.id.data_swipe_refresh)
    SwipeRefreshLayout mDataSwipeRefresh;

    @BindView(R.id.box_no_data)
    View mBoxNoData;

    @BindView(R.id.img_no_data)
    AppCompatImageView mImgNoData;

    @BindView(R.id.weight_tv)
    TextView mWeight;

    @BindView(R.id.body_fat_tv)
    TextView mBodyFat;

    @BindView(R.id.weight_month_average_tv)
    TextView mWeightAverage;

    @BindView(R.id.box_weight)
    LinearLayout mBoxWeight;

    @BindView(R.id.hr_tv)
    TextView mHR;

    @BindView(R.id.hr_chart)
    LineChart mChart;

    @BindView(R.id.hr_img)
    ImageView mHeartImage;

    @BindView(R.id.box_hr)
    LinearLayout mBoxHR;

    @BindView(R.id.box_hr_summary)
    LinearLayout mBoxHRSummary;

    @BindView(R.id.hr_min_tv)
    TextView mMinHRTextView;

    @BindView(R.id.hr_max_tv)
    TextView mMaxHRTextView;

    @BindView(R.id.hr_avg_tv)
    TextView mAvgHRTextView;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public IotFragment() {
        // Empty constructor required!
    }

    public static IotFragment newInstance() {
        return new IotFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = Objects.requireNonNull(getActivity()).getApplicationContext();
        ocariotRepository = OcariotNetRepository.getInstance(mContext);
        appPref = AppPreferencesHelper.getInstance(mContext);

        mDisposable = new CompositeDisposable();
        mAlertMessage = new AlertMessage(getActivity());
        mScanner = new SimpleBleScanner.Builder()
                .addFilterServiceUuid(HRManager.HR_SERVICE_UUID.toString())
                .addScanPeriod(Integer.MAX_VALUE) // 15s in milliseconds
                .build();
        mHRManager = HRManager.getInstance(mContext);
        mHRManager.setHeartRateCallback(mHRManagerCallback);
        minHR = Integer.MAX_VALUE;
        fadeIn = new AlphaAnimation(1.0f, 0.0f);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_iot, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mImgNoData.setImageDrawable(AppCompatResources.getDrawable(mContext, R.drawable.scale));
        initComponents();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mDisposable.dispose();
    }

    /**
     * Initialize components
     */
    private void initComponents() {
        initDataSwipeRefresh();
        initChart();
        initAnimation();

        new android.os.Handler().postDelayed(
                () -> addEntry(new Random().nextInt((200 - 70 + 1) + 70)),
                300);

        mSkeletonScreen = Skeleton.bind(getView())
                .load(R.layout.fragment_iot_shimmer)
                .show();
    }

    /**
     * Animation for heart
     */
    private void initAnimation() {
        // Setting heartAnimation in heart image view
        heartAnimation = ObjectAnimator.ofPropertyValuesHolder(mHeartImage,
                PropertyValuesHolder.ofFloat("scaleX", 1.2f),
                PropertyValuesHolder.ofFloat("scaleY", 1.2f));
        heartAnimation.setDuration(500);
        heartAnimation.setRepeatCount(ObjectAnimator.INFINITE);
        heartAnimation.setRepeatMode(ObjectAnimator.REVERSE);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadDataOcariot();
        if ((BluetoothAdapter.getDefaultAdapter() != null &&
                BluetoothAdapter.getDefaultAdapter().isEnabled()) &&
                !mScanner.isScanStarted() && !mHRManager.isConnected()) {
            Log.w(LOG_TAG, "onStart()");
            mScanner.startScan(mScannerCallback);
        }
    }

    /**
     * Initialize SwipeRefresh
     */
    private void initDataSwipeRefresh() {
        mDataSwipeRefresh.setOnRefreshListener(() -> {
            if (itShouldLoadMore) loadDataOcariot();
        });
    }

    /**
     * Load data in OCARIoT Server.
     * If there is no internet connection, we can display the local database.
     * Otherwise it displays from the remote server.
     */
    private void loadDataOcariot() {
        if (appPref.getUserAccessOcariot() == null) return;

        mDisposable.add(
                ocariotRepository
                        .listWeights(
                                appPref.getLastSelectedChild().get_id(),
                                "gte:".concat(DateUtils.addMonths(DateUtils.getCurrentDate(), -1)),
                                "lt:".concat(DateUtils.addDaysToDatetimeString(DateUtils.getCurrentDatetimeUTC(), 1))
                        )
                        .doOnSubscribe(disposable -> loading(true))
                        .doAfterTerminate(() -> loading(false))
                        .subscribe(this::populateView, error -> mAlertMessage.handleError(error))
        );
    }

    private void populateView(List<Weight> weights) {
        Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
            if (weights.isEmpty()) {
                mBoxNoData.setVisibility(View.VISIBLE);
                mBoxWeight.setVisibility(View.GONE);
                return;
            }
            mBoxNoData.setVisibility(View.GONE);
            mBoxWeight.setVisibility(View.VISIBLE);

            Weight lastWeight = weights.get(0);
            DecimalFormat df = new DecimalFormat("#.#");
            mWeight.setText(Html.fromHtml(df.format(lastWeight.getValue())
                    .concat("<small>")
                    .concat(lastWeight.getUnit())
                    .concat("</small>"))
            );

            if (lastWeight.getBodyFat() != null) {
                mBodyFat.setText(Html.fromHtml(df.format(lastWeight.getBodyFat())
                        .concat("<small>%</small>"))
                );
            } else {
                mBodyFat.setText("--");
            }

            double weightSum = 0d;
            for (Weight weight : weights) weightSum += weight.getValue();

            double average = weightSum / weights.size();
            mWeightAverage.setText(Html.fromHtml(df.format(average)
                    .concat("<small>")
                    .concat(lastWeight.getUnit())
                    .concat("</small>"))
            );
            mBoxNoData.setVisibility(View.GONE);
        });
    }

    /**
     * Enable/Disable display loading data.
     *
     * @param enabled boolean
     */
    private void loading(final boolean enabled) {
        mDataSwipeRefresh.setRefreshing(false);
        if (!enabled) {
            mSkeletonScreen.hide();
            itShouldLoadMore = true;
            return;
        }
        mSkeletonScreen.show();
        itShouldLoadMore = false;
    }

    private SimpleScannerCallback mScannerCallback = new SimpleScannerCallback() {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onScanResult(int callbackType, @NonNull ScanResult result) {
            Log.w(LOG_TAG, "onScanResult(): " + result.getDevice().getName());
            mHRManager.connectDevice(result.getDevice());
            mScanner.stopScan();
        }

        @Override
        public void onBatchScanResults(@NonNull List<ScanResult> results) {

        }

        @Override
        public void onScanFailed(int errorCode) {
            Log.w(LOG_TAG, "onScanFailed(): " + errorCode);
        }

        @Override
        public void onFinish() {
            Log.w(LOG_TAG, "onFinish():");
        }
    };

    private HRManagerCallback mHRManagerCallback = new HRManagerCallback() {
        @Override
        public void onMeasurementReceived(@NonNull BluetoothDevice device, int heartRate, String timestamp) {
            if (heartRate > 0) {
                totalHR++;
                sumHR += heartRate;
                minHR = heartRate < minHR ? heartRate : minHR;
                maxHR = heartRate > maxHR ? heartRate : maxHR;
                avgHR = sumHR / totalHR;
            }
            updateViewHR(heartRate, timestamp);
        }

        @Override
        public void onConnected(@NonNull BluetoothDevice device) {
            Log.w(LOG_TAG, "onConnected(): " + device.getName());
        }

        @Override
        public void onDisconnected(@NonNull BluetoothDevice device) {
            Log.w(LOG_TAG, "onDisconnected(): " + device.getName());
            heartAnimation.pause();
            ImageViewCompat.setImageTintList(mHeartImage, ColorStateList.valueOf(
                    getResources().getColor(R.color.colorLineDivider)));
            mBoxHR.setVisibility(View.GONE);
            mScanner.startScan(mScannerCallback);
        }
    };

    private void updateViewHR(int hr, String timestamp) {
        if (getActivity() == null) return;
        Objects.requireNonNull(getActivity())
                .runOnUiThread(() -> {
                    if (heartAnimation.isPaused() || !heartAnimation.isRunning()) {
                        heartAnimation.start();
                        ImageViewCompat.setImageTintList(mHeartImage, ColorStateList.valueOf(
                                getResources().getColor(R.color.colorDanger)));
                        mBoxHR.setVisibility(View.VISIBLE);
                        mBoxHRSummary.setVisibility(View.VISIBLE);
                    }
                    mHR.setText(String.valueOf(hr));

                    // Update chart
                    addEntry((float) hr);

                    // Update summary
                    mMinHRTextView.setText(String.valueOf(minHR));
                    mMaxHRTextView.setText(String.valueOf(maxHR));
                    mAvgHRTextView.setText(String.valueOf(avgHR));
                 });
    }

    private void initChart() {
        mChart.getLegend().setEnabled(false);
        mChart.setDrawGridBackground(false);
        mChart.getDescription().setEnabled(false);
        mChart.setDoubleTapToZoomEnabled(false);
        mChart.setHardwareAccelerationEnabled(false);

        mChart.getAxisRight().setDrawGridLines(false);
        mChart.getAxisRight().setEnabled(false);

        mChart.getAxisLeft().setDrawGridLines(false);
        mChart.getAxisLeft().setEnabled(false);
        mChart.getAxisLeft().setDrawLabels(false);

        mChart.getXAxis().setEnabled(true);
        mChart.getXAxis().setDrawGridLines(false);
        mChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        mChart.getXAxis().setDrawAxisLine(true);
        mChart.getXAxis().setGranularity(1f);
        mChart.setViewPortOffsets(0f, 0f, 0f, 0f);

        // enable touch gestures
        mChart.setTouchEnabled(true);

        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(false);
        mChart.setDrawGridBackground(false);

        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(false);

        LineData data = new LineData();
        data.setDrawValues(false);

        // add empty data
        mChart.setData(data);

        XAxis xl = mChart.getXAxis();
        xl.setTextColor(Color.WHITE);
        xl.setDrawGridLines(false);
        xl.setAvoidFirstLastClipping(true);
        xl.setEnabled(true);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setDrawGridLines(true);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setEnabled(false);
    }

    private void addEntry(float value) {
        LineData data = mChart.getData();

        if (data != null) {
            ILineDataSet set = data.getDataSetByIndex(0);

            if (set == null) {
                set = createSet();
                data.addDataSet(set);
            }

            data.addEntry(new Entry(set.getEntryCount(), value), 0);
            data.notifyDataChanged();

            // let the chart know it's data has changed
            mChart.notifyDataSetChanged();

            // limit the number of visible entries
            mChart.setVisibleXRangeMaximum(10);

            // move to the latest entry
            mChart.moveViewToX(data.getEntryCount());
        }
    }

    private LineDataSet createSet() {
        LineDataSet set = new LineDataSet(null, "Heart Rate Data");
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setDrawHighlightIndicators(false);
        set.setLineWidth(2f);
        set.setValueTextColor(Color.BLACK);
        set.setDrawCircles(false);
        set.setDrawValues(false);
        set.setDrawFilled(true);

        int color = ContextCompat.getColor(Objects.requireNonNull(getActivity()),
                R.color.colorPrimaryDark);
        set.setFillColor(color);
        set.setColor(color);
        set.setHighLightColor(color);

        return set;
    }
}
