package br.edu.uepb.nutes.ocariot.view.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import br.edu.uepb.nutes.ocariot.R;
import br.edu.uepb.nutes.ocariot.data.model.Activity;
import br.edu.uepb.nutes.ocariot.data.model.ActivityLevel;
import br.edu.uepb.nutes.ocariot.data.model.ActivitiesList;
import br.edu.uepb.nutes.ocariot.data.model.UserAccess;
import br.edu.uepb.nutes.ocariot.data.repository.local.pref.AppPreferencesHelper;
import br.edu.uepb.nutes.ocariot.data.repository.remote.fitbit.FitBitNetRepository;
import br.edu.uepb.nutes.ocariot.data.repository.remote.ocariot.OcariotNetRepository;
import br.edu.uepb.nutes.ocariot.utils.DateUtils;
import br.edu.uepb.nutes.ocariot.view.adapter.PhysicalActivityListAdapter;
import br.edu.uepb.nutes.ocariot.view.adapter.base.OnRecyclerViewListener;
import br.edu.uepb.nutes.ocariot.view.ui.activity.MainActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.observers.DisposableObserver;

/**
 * A fragment representing a list of Items.
 *
 * @author Copyright (c) 2018, NUTES/UEPB
 */
public class SleepListFragment extends Fragment {
    private final String LOG_TAG = "PhysicalActivityList";

    private PhysicalActivityListAdapter mAdapter;
    private FitBitNetRepository fitBitRepository;
    private OcariotNetRepository ocariotNetRepository;
    private OnClickActivityListener mListener;
    private UserAccess userAccess;

    /**
     * We need this variable to lock and unlock loading more.
     * We should not charge more when a request has already been made.
     * The load will be activated when the requisition is completed.
     */
    private boolean itShouldLoadMore = true;

    @BindView(R.id.activities_list)
    RecyclerView mRecyclerView;

    @BindView(R.id.data_swiperefresh)
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
        ocariotNetRepository = OcariotNetRepository.getInstance(getContext());

        initComponents();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnClickActivityListener) {
            mListener = (OnClickActivityListener) context;
        } else {
            throw new ClassCastException();
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
//        loadDataFitBit();
    }

    private void initRecyclerView() {
        mAdapter = new PhysicalActivityListAdapter(getContext());
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(),
                new LinearLayoutManager(getContext()).getOrientation()));

        mAdapter.setListener(new OnRecyclerViewListener<Activity>() {
            @Override
            public void onItemClick(Activity item) {
                Log.w(LOG_TAG, "item: " + item.toString());
                if (mListener != null) mListener.onClickActivity(item);
            }

            @Override
            public void onLongItemClick(View v, Activity item) {

            }

            @Override
            public void onMenuContextClick(View v, Activity item) {

            }
        });

        mRecyclerView.setAdapter(mAdapter);
    }

    /**
     * Initialize SwipeRefresh
     */
    private void initDataSwipeRefresh() {
        mDataSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.w(LOG_TAG, "onRefresh()");
                if (itShouldLoadMore) loadDataFitBit();
            }
        });
    }

    /**
     * Load data in FitBit Server.
     */
    private void loadDataFitBit() {
        loading(true);
        String currentDate = DateUtils.getCurrentDatetime(getResources().getString(R.string.date_format1));

        fitBitRepository.listActivities(currentDate, null, "desc", 0, 100)
                .subscribe(new DisposableObserver<ActivitiesList>() {
                    @Override
                    public void onNext(ActivitiesList activityList) {
                        if (activityList != null && activityList.getActivities().size() > 0) {
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.w(LOG_TAG, "FITIBIT - onError: " + e.getMessage());
                        loadDataOcariot();
                    }

                    @Override
                    public void onComplete() {
                        loadDataOcariot();
                    }
                });
    }

    /**
     * Load data in OCARIoT Server.
     * If there is no internet connection, we can display the local database.
     * Otherwise it displays from the remote server.
     */
    private void loadDataOcariot() {
        if (userAccess == null) return;

        mAdapter.clearItems();
        loading(true);

        ocariotNetRepository
                .listActivities(userAccess.getSubject())
                .subscribe(new DisposableObserver<List<Activity>>() {
                    @Override
                    public void onNext(List<Activity> activities) {
                        Log.w(LOG_TAG, "loadDataOcariot() -" + Arrays.toString(activities.toArray()));
                        if (activities != null && activities.size() > 0) {
                            mAdapter.addItems(activities);
                            mNoData.setVisibility(View.GONE);
                        } else {
                            mNoData.setVisibility(View.VISIBLE);
                        }

                        loading(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mNoData.setVisibility(View.VISIBLE);
                        mDataSwipeRefresh.setRefreshing(false);
                    }

                    @Override
                    public void onComplete() {

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
}
