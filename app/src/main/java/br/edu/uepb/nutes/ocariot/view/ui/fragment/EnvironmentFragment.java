package br.edu.uepb.nutes.ocariot.view.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatSpinner;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

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
                    public void onNext(List<Environment> environments) {
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
}
