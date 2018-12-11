package br.edu.uepb.nutes.ocariot.view.ui.fragment;

import android.content.Context;
import android.os.Bundle;
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

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import br.edu.uepb.nutes.ocariot.R;
import br.edu.uepb.nutes.ocariot.data.model.Sleep;
import br.edu.uepb.nutes.ocariot.data.model.SleepList;
import br.edu.uepb.nutes.ocariot.data.model.SleepPatternDataSet;
import br.edu.uepb.nutes.ocariot.data.model.User;
import br.edu.uepb.nutes.ocariot.data.model.UserAccess;
import br.edu.uepb.nutes.ocariot.data.repository.local.pref.AppPreferencesHelper;
import br.edu.uepb.nutes.ocariot.data.repository.remote.fitbit.FitBitNetRepository;
import br.edu.uepb.nutes.ocariot.data.repository.remote.ocariot.OcariotNetRepository;
import br.edu.uepb.nutes.ocariot.utils.DateUtils;
import br.edu.uepb.nutes.ocariot.view.adapter.SleepListAdapter;
import br.edu.uepb.nutes.ocariot.view.adapter.base.OnRecyclerViewListener;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.observers.DisposableObserver;

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
    }

    public static SleepListFragment newInstance() {
        return new SleepListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userAccess = AppPreferencesHelper.getInstance(getContext()).getUserAccessOcariot();
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
        fitBitRepository = FitBitNetRepository.getInstance(getContext());
        ocariotRepository = OcariotNetRepository.getInstance(getContext());

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
        mAdapter = new SleepListAdapter(getContext());
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(),
                new LinearLayoutManager(getContext()).getOrientation()));

        mAdapter.setListener(new OnRecyclerViewListener<Sleep>() {
            @Override
            public void onItemClick(Sleep item) {
                Log.w(LOG_TAG, "item: " + item.toString());
                if (mListener != null) mListener.onClickSleep(item);
            }

            @Override
            public void onLongItemClick(View v, Sleep item) {

            }

            @Override
            public void onMenuContextClick(View v, Sleep item) {

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
        loading(true);
        String currentDate = DateUtils.formatDate(DateUtils.addDays(1).getTimeInMillis(),
                getResources().getString(R.string.date_format1));

        fitBitRepository.listSleep(currentDate, null, "desc",0,100)
                .subscribe(new DisposableObserver<SleepList>() {
                    @Override
                    public void onNext(SleepList sleepList) {
                        Log.w("FITBIT-LOAD", sleepList.toJsonString());
                        if (sleepList.getSleepList().size() > 0) {
                            sendSleepToOcariot(convertFitBitDataToOcariot(sleepList.getSleepList()));
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.w(LOG_TAG, "FITIBIT - onError: " + e.getMessage());
                        loadDataOcariot();
                    }

                    @Override
                    public void onComplete() {
                    }
                });

    }

    /**
     * Load data in OCARIoT Server.
     * If there is no internet connection, we can display the local database.
     * Otherwise it displays from the remote server.
     */
    private void loadDataOcariot() {
        Log.w(LOG_TAG, "loadDataOcariotInit()");
        if (userAccess == null) return;
        loading(true);

        ocariotRepository
                .listSleep(userAccess.getSubject(), "-start_time", 1, 100)
                .subscribe(new DisposableObserver<List<Sleep>>() {
                    @Override
                    public void onNext(List<Sleep> sleep) {
                        Log.w(LOG_TAG, "OC " + Arrays.toString(sleep.toArray()));
                        if (sleep.size() > 0) {
                            mAdapter.clearItems();
                            mAdapter.addItems(sleep);
                            mNoData.setVisibility(View.GONE);
                        } else if (mAdapter.itemsIsEmpty()) {
                            mNoData.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.w(LOG_TAG, e.toString());
                        loading(false);
                        if (getContext() == null) return;

                        Toast.makeText(getContext(), R.string.error_500,
                                Toast.LENGTH_SHORT).show();
                        if (mAdapter.itemsIsEmpty()) {
                            mNoData.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onComplete() {
                        loading(false);
                    }
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
        } else {
            mDataSwipeRefresh.setRefreshing(true);
            itShouldLoadMore = false;
        }
    }


    /**
     * Publish Sleep in OCARioT API Service.
     *
     * @param sleepList {@link List< Sleep >} List of sleep to be published.
     */
    private void sendSleepToOcariot(List<Sleep> sleepList) {
        if (sleepList == null) return;
        int total = sleepList.size();
        Log.w(LOG_TAG, "sendOcariot() TOTAL: " + sleepList.size());

        // TODO Enviar lista completa na mesma requisição, quando o servidor oferecer suporte.
        int count = 0;
        for (Sleep sleep : sleepList) {
            count++;
            final int aux = count;
            Log.w(LOG_TAG, "SendSleep-pre" + new Gson().toJson(sleep));
            ocariotRepository.publishSleep(userAccess.getSubject(), sleep)
                    .subscribe(new DisposableObserver<Sleep>() {
                        @Override
                        public void onNext(Sleep slp) {
                            Log.w(LOG_TAG, "Sleep Published: " + slp);
                            if (aux == total) loadDataOcariot();
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.w(LOG_TAG, "SendSleep: " + e + "Ac " + new Gson().toJson(sleep));
                            if (aux == total) loadDataOcariot();
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        }
    }

    /**
     * Handles data conversions from the FitBit API to data
     * supported by the OCARIoT API.
     *
     * @param sleepList List of sleep.
     */
    private List<Sleep> convertFitBitDataToOcariot(List<Sleep> sleepList) {
        if (sleepList == null) return new ArrayList<>();

        for (Sleep sleep : sleepList) {
            if (sleep.getPattern() == null) {
                sleepList.remove(sleep);
                continue;
            }

            for (SleepPatternDataSet patternDataSet : sleep.getPattern().getDataSet()) {
                // In the FitBit API the duration comes in seconds.
                // The OCARIoT API waits in milliseconds.
                // Converts the duration in seconds to milliseconds.
                patternDataSet.setDuration(patternDataSet.getDuration() * 1000);
            }
            User user = new User();
            user.set_id(userAccess.getSubject());
            sleep.setUser(user);
        }
        return sleepList;
    }

    public interface OnClickSleepListener {
        void onClickSleep(Sleep sleep);
    }
}
