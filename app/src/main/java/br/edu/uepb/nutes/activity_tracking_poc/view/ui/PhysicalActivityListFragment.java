package br.edu.uepb.nutes.activity_tracking_poc.view.ui;

import android.content.Context;
import android.os.Bundle;
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
import android.widget.Toast;

import java.util.Arrays;

import br.edu.uepb.nutes.activity_tracking_poc.R;
import br.edu.uepb.nutes.activity_tracking_poc.data.model.Activity;
import br.edu.uepb.nutes.activity_tracking_poc.data.repository.remote.fitbit.FitBitNetRepository;
import br.edu.uepb.nutes.activity_tracking_poc.utils.DateUtils;
import br.edu.uepb.nutes.activity_tracking_poc.view.ui.adapter.PhysicalActivityListAdapter;
import br.edu.uepb.nutes.activity_tracking_poc.view.ui.adapter.base.OnRecyclerViewListener;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.Disposable;

/**
 * A fragment representing a list of Items.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2018, NUTES/UEPB
 */
public class PhysicalActivityListFragment extends Fragment {
    private final String LOG_TAG = "PhysicalActivityList";

    private Disposable disposable;
    private PhysicalActivityListAdapter mAdapter;
    private FitBitNetRepository fitBitRepository;

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

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PhysicalActivityListFragment() {
    }

    public static PhysicalActivityListFragment newInstance() {
        PhysicalActivityListFragment fragment = new PhysicalActivityListFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_physical_activity_list, container, false);
        ButterKnife.bind(this, view);

        getActivity().setTitle(R.string.title_physical_activity);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        fitBitRepository = FitBitNetRepository.getInstance(getContext());

        initComponents();
    }

    /**
     * Initialize components
     */
    private void initComponents() {
        initRecyclerView();
        initDataSwipeRefresh();
        loadData();
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
        mDataSwipeRefresh.setOnRefreshListener(() -> {
            if (itShouldLoadMore) loadData();
        });
    }

    /**
     * Load data.
     * If there is no internet connection, we can display the local database.
     * Otherwise it displays from the remote server.
     */
    private void loadData() {
        String currentDate = DateUtils.getCurrentDatetime(getResources().getString(R.string.date_format1));
        mAdapter.clearItems();

        disposable = fitBitRepository.listActivities(currentDate,
                null, "desc", 0, 10)
                .subscribe(activityList -> {
                    if (activityList != null)
                        mAdapter.addItems(activityList.getActivities());
                    mDataSwipeRefresh.setRefreshing(false);
                }, error -> {
                    Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                    mDataSwipeRefresh.setRefreshing(false);
                });
    }


    @Override
    public void onDetach() {
        super.onDetach();
        if (disposable != null) disposable = null;
    }
}
