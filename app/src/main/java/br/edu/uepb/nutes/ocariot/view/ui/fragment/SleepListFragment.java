package br.edu.uepb.nutes.ocariot.view.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import br.edu.uepb.nutes.ocariot.R;
import br.edu.uepb.nutes.ocariot.data.model.ocariot.Sleep;
import br.edu.uepb.nutes.ocariot.data.model.ocariot.SleepPatternDataSet;
import br.edu.uepb.nutes.ocariot.data.model.common.UserAccess;
import br.edu.uepb.nutes.ocariot.data.repository.local.pref.AppPreferencesHelper;
import br.edu.uepb.nutes.ocariot.data.repository.remote.fitbit.FitBitNetRepository;
import br.edu.uepb.nutes.ocariot.data.repository.remote.ocariot.OcariotNetRepository;
import br.edu.uepb.nutes.ocariot.utils.DateUtils;
import br.edu.uepb.nutes.ocariot.view.adapter.SleepListAdapter;
import br.edu.uepb.nutes.ocariot.view.adapter.base.OnRecyclerViewListener;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.CompositeDisposable;

/**
 * A fragment representing a list of Items.
 *
 * @author Copyright (c) 2018, NUTES/UEPB
 */
public class SleepListFragment extends Fragment {
    private final String LOG_TAG = "SleepListFragment";

    private SleepListAdapter mAdapter;
    private FitBitNetRepository fitBitRepository;
    private OcariotNetRepository ocariotRepository;
    private OnClickSleepListener mListener;
    private UserAccess userAccess;
    private Context mContext;
    private CompositeDisposable mDisposable;

    /**
     * We need this variable to lock and unlock loading more.
     * We should not charge more when a request has already been made.
     * The load will be activated when the requisition is completed.
     */
    private boolean itShouldLoadMore = true;

    @BindView(R.id.sleep_list)
    RecyclerView mRecyclerView;

    @BindView(R.id.data_swipe_refresh)
    SwipeRefreshLayout mDataSwipeRefresh;

    @BindView(R.id.no_data_textView)
    TextView mNoData;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SleepListFragment() {
        // Empty constructor  required!
    }

    public static SleepListFragment newInstance() {
        return new SleepListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = Objects.requireNonNull(getActivity()).getApplicationContext();
        mDisposable = new CompositeDisposable();
        fitBitRepository = FitBitNetRepository.getInstance(mContext);
        ocariotRepository = OcariotNetRepository.getInstance(mContext);
        userAccess = AppPreferencesHelper.getInstance(mContext).getUserAccessOcariot();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sleep_list, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initComponents();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnClickSleepListener) {
            mListener = (OnClickSleepListener) context;
        } else {
            throw new ClassCastException("The implementation of the " +
                    "OnClickSleepListener interface is mandatory!");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (fitBitRepository != null) fitBitRepository.dispose();
        mListener = null;
        mDisposable.dispose();
    }

    /**
     * Initialize components
     */
    private void initComponents() {
        initRecyclerView();
        initDataSwipeRefresh();
        loadDataFitBit();
    }

    private void initRecyclerView() {
        mAdapter = new SleepListAdapter(mContext);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(),
                new LinearLayoutManager(mContext).getOrientation()));

        mAdapter.setListener(new OnRecyclerViewListener<Sleep>() {
            @Override
            public void onItemClick(Sleep item) {
                Log.w(LOG_TAG, "item: " + item.toString());
                if (mListener != null) mListener.onClickSleep(item);
            }

            @Override
            public void onLongItemClick(View v, Sleep item) {
                // Not implemented!
            }

            @Override
            public void onMenuContextClick(View v, Sleep item) {
                // Not implemented!
            }
        });

        mRecyclerView.setAdapter(mAdapter);
    }

    /**
     * Initialize SwipeRefresh
     */
    private void initDataSwipeRefresh() {
        mDataSwipeRefresh.setOnRefreshListener(() -> {
            if (itShouldLoadMore) loadDataFitBit();
        });
    }

    /**
     * Load data in FitBit Server.
     */
    private void loadDataFitBit() {
        String currentDate = DateUtils.formatDate(DateUtils.addDays(1).getTimeInMillis(),
                getResources().getString(R.string.date_format1));

        mDisposable.add(
                fitBitRepository
                        .listSleep(currentDate, null, "desc", 0, 100)
                        .doOnSubscribe(disposable -> loading(true))
                        .subscribe(this::sendSleepToOcariot, error -> loadDataOcariot())
        );
    }

    /**
     * Load data in OCARIoT Server.
     * If there is no internet connection, we can display the local database.
     * Otherwise it displays from the remote server.
     */
    private void loadDataOcariot() {
        Log.w(LOG_TAG, "loadDataOcariotInit()");
        if (userAccess == null) return;

        mDisposable.add(
                ocariotRepository
                        .listSleep(userAccess.getSubject(), "-start_time", 1, 100)
                        .doOnSubscribe(disposable -> loading(true))
                        .doAfterTerminate(() -> loading(false))
                        .subscribe(this::populateViewSleep, error -> Toast.makeText(mContext,
                                R.string.error_500, Toast.LENGTH_SHORT).show())
        );
    }

    /**
     * Populate RecyclerView with sleep records.
     *
     * @param sleepList {@link List<Sleep>}
     */
    private void populateViewSleep(final List<Sleep> sleepList) {
        Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
            if (sleepList.isEmpty()) {
                mNoData.setVisibility(View.VISIBLE);
                return;
            }
            mAdapter.clearItems();
            mAdapter.addItems(sleepList);
            mNoData.setVisibility(View.GONE);
        });
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

    /**
     * Publish Sleep in OCARioT API Service.
     *
     * @param sleepList {@link List<Sleep>} List of sleep to be published.
     */
    private void sendSleepToOcariot(List<Sleep> sleepList) {
        Log.w(LOG_TAG, "sendOcariot() TOTAL: " + sleepList.size());
        int total = sleepList.size();

        int count = 0;
        for (Sleep sleep : sleepList) {
            sleep.setChildId(userAccess.getSubject());
            count++;
            final int aux = count;
            new Handler().postDelayed(() -> mDisposable.add(
                    ocariotRepository
                            .publishSleep(sleep)
                            .subscribe(resultSleep -> {
                                if (aux == total) loadDataOcariot();
                            }, error -> {
//                                Log.w(LOG_TAG, "ERROR OCARIoT POST SLEEP " + error.getMessage());
                                if (aux == total) loadDataOcariot();
                            })
            ), 100);
        }
    }

//    /**
//     * Handles data conversions from the FitBit API to data
//     * supported by the OCARIoT API.
//     *
//     * @param sleepList List of sleep.
//     */
//    private List<Sleep> convertFitBitDataToOcariot(List<Sleep> sleepList) {
//        if (sleepList == null) return new ArrayList<>();
//
//        for (Sleep sleep : sleepList) {
//            if (sleep.getPattern() == null) {
//                sleepList.remove(sleep);
//                continue;
//            }
//
//            for (SleepPatternDataSet patternDataSet : sleep.getPattern().getDataSet()) {
//                // In the FitBit API the duration comes in seconds.
//                // The OCARIoT API waits in milliseconds.
//                // Converts the duration in seconds to milliseconds.
//                patternDataSet.setDuration(patternDataSet.getDuration() * 1000);
//            }
//            sleep.setChildId(userAccess.getSubject());
//        }
//        return sleepList;
//    }

    public interface OnClickSleepListener {
        void onClickSleep(Sleep sleep);
    }
}
