package br.edu.uepb.nutes.ocariot.view.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.SkeletonScreen;

import java.util.List;
import java.util.Objects;

import br.edu.uepb.nutes.ocariot.R;
import br.edu.uepb.nutes.ocariot.data.model.common.UserAccess;
import br.edu.uepb.nutes.ocariot.data.model.ocariot.Sleep;
import br.edu.uepb.nutes.ocariot.data.repository.local.pref.AppPreferencesHelper;
import br.edu.uepb.nutes.ocariot.data.repository.remote.ocariot.OcariotNetRepository;
import br.edu.uepb.nutes.ocariot.utils.AlertMessage;
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
    private SleepListAdapter mAdapter;
    private OcariotNetRepository ocariotRepository;
    private AppPreferencesHelper appPref;
    private OnClickSleepListener mListener;
    private UserAccess userAccess;
    private Context mContext;
    private CompositeDisposable mDisposable;
    private SkeletonScreen mSkeletonScreen;
    private AlertMessage mAlertMessage;

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

    @BindView(R.id.box_no_data)
    View mNoData;

    @BindView(R.id.img_no_data)
    AppCompatImageView mImgNoData;

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
        ocariotRepository = OcariotNetRepository.getInstance();
        appPref = AppPreferencesHelper.getInstance();

        mDisposable = new CompositeDisposable();
        mAlertMessage = new AlertMessage(getActivity());
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
        mImgNoData.setImageDrawable(AppCompatResources.getDrawable(mContext, R.drawable.moon));
        initComponents();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnClickSleepListener) {
            mListener = (OnClickSleepListener) context;
        } else {
            throw new ClassCastException("The implementation of the " +
                    "OnClickSleepListener interface is mandatory!");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        userAccess = appPref.getUserAccessOcariot();
        loadDataOcariot();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        mDisposable.dispose();
    }

    /**
     * Initialize components
     */
    private void initComponents() {
        initRecyclerView();
        initDataSwipeRefresh();
    }

    private void initRecyclerView() {
        mAdapter = new SleepListAdapter(mContext);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext,
                LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(),
                new LinearLayoutManager(mContext).getOrientation()));

        mAdapter.setListener(new OnRecyclerViewListener<Sleep>() {
            @Override
            public void onItemClick(Sleep item) {
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

        mSkeletonScreen = Skeleton.bind(mRecyclerView)
                .adapter(mAdapter)
                .load(R.layout.sleep_item_shimmer)
                .count(10)
                .duration(1200)
                .show();
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
        if (userAccess == null) return;

        mDisposable.add(
                ocariotRepository
                        .listSleep(appPref.getLastSelectedChild().getId(), "-start_time", 1, 100)
                        .doOnSubscribe(disposable -> loading(true))
                        .doAfterTerminate(() -> loading(false))
                        .subscribe(this::populateViewSleep, error -> mAlertMessage.handleError(error))
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
        mDataSwipeRefresh.setRefreshing(false);
        if (!enabled) {
            mSkeletonScreen.hide();
            itShouldLoadMore = true;
            return;
        }
        if (mAdapter.itemsIsEmpty()) mSkeletonScreen.show();
        itShouldLoadMore = false;
    }

    public interface OnClickSleepListener {
        void onClickSleep(Sleep sleep);
    }
}
