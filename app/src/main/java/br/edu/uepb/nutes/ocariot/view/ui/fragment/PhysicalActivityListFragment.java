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
import br.edu.uepb.nutes.ocariot.data.model.ocariot.PhysicalActivity;
import br.edu.uepb.nutes.ocariot.data.repository.local.pref.AppPreferencesHelper;
import br.edu.uepb.nutes.ocariot.data.repository.remote.ocariot.OcariotNetRepository;
import br.edu.uepb.nutes.ocariot.utils.AlertMessage;
import br.edu.uepb.nutes.ocariot.view.adapter.PhysicalActivityListAdapter;
import br.edu.uepb.nutes.ocariot.view.adapter.base.OnRecyclerViewListener;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.CompositeDisposable;

/**
 * A fragment representing a list of Items.
 *
 * @author Copyright (c) 2018, NUTES/UEPB
 */
public class PhysicalActivityListFragment extends Fragment {
    private static final int LIMIT_PER_PAGE = 20;
    private static final int INITIAL_PAGE = 1;
    private int page = INITIAL_PAGE;

    private PhysicalActivityListAdapter mAdapter;
    private OcariotNetRepository ocariotRepository;
    private AppPreferencesHelper appPref;
    private OnClickActivityListener mListener;
    private UserAccess userAccess;
    private Context mContext;
    private CompositeDisposable mDisposable;
    private SkeletonScreen mSkeletonScreen;
    private AlertMessage mAlertMessage;

    // We need this variable to lock and unlock loading more.
    // We should not charge more when a request has already been made.
    // The load will be activated when the requisition is completed.
    private boolean itShouldLoadMore = true;

    @BindView(R.id.activities_list)
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
    public PhysicalActivityListFragment() {
        // Empty constructor required!
    }

    public static PhysicalActivityListFragment newInstance() {
        return new PhysicalActivityListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = Objects.requireNonNull(getActivity()).getApplicationContext();

        ocariotRepository = OcariotNetRepository.getInstance();
        appPref = AppPreferencesHelper.getInstance();
        userAccess = appPref.getUserAccessOcariot();

        mDisposable = new CompositeDisposable();
        mAlertMessage = new AlertMessage(getActivity());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_physical_activity_list, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mImgNoData.setImageDrawable(AppCompatResources.getDrawable(mContext, R.drawable.running));

        initComponents();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof OnClickActivityListener) {
            mListener = (OnClickActivityListener) context;
        } else {
            throw new ClassCastException("The implementation of the " +
                    "OnClickActivityListener interface is mandatory!");
        }
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

    @Override
    public void onResume() {
        super.onResume();
        loadDataOcariot(true);
    }

    private void initRecyclerView() {
        mAdapter = new PhysicalActivityListAdapter(mContext);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext,
                LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(),
                new LinearLayoutManager(mContext).getOrientation()));

        mAdapter.setListener(new OnRecyclerViewListener<PhysicalActivity>() {
            @Override
            public void onItemClick(PhysicalActivity item) {
                if (mListener != null) mListener.onClickActivity(item);
            }

            @Override
            public void onLongItemClick(View v, PhysicalActivity item) {
                // Not implemented!
            }

            @Override
            public void onMenuContextClick(View v, PhysicalActivity item) {
                // Not implemented!
            }
        });

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                // Recycle view scrolling downwards...
                // this if statement detects when user reaches the end of recyclerView, this is only time we should load more
                // here we are now allowed to load more, but we need to be careful
                // we must check if itShouldLoadMore variable is true [unlocked]
                if (dy > 0 && !recyclerView.canScrollVertically(RecyclerView.FOCUS_DOWN)
                        && itShouldLoadMore) {
                    loadDataOcariot(false);
                }
            }
        });

        mSkeletonScreen = Skeleton.bind(mRecyclerView)
                .adapter(mAdapter)
                .load(R.layout.physical_activity_item_shimmer)
                .count(7)
                .duration(1200)
                .show();
    }

    /**
     * Initialize SwipeRefresh
     */
    private void initDataSwipeRefresh() {
        mDataSwipeRefresh.setOnRefreshListener(() -> {
            if (itShouldLoadMore) loadDataOcariot(true);
        });
    }

    /**
     * Load data in OCARIoT Server.
     * If there is no internet connection, we can display the local database.
     * Otherwise it displays from the remote server.
     *
     * @param isInit boolean
     */
    private void loadDataOcariot(boolean isInit) {
        if (userAccess == null) return;

        if (isInit) {
            page = INITIAL_PAGE;
            mAdapter.clearItems();
        }

        mDisposable.add(
                ocariotRepository
                        .listActivities(appPref.getLastSelectedChild().getId(),
                                "-end_time", page, LIMIT_PER_PAGE)
                        .doOnSubscribe(disposable -> loading(true, isInit))
                        .doAfterTerminate(() -> loading(false, isInit))
                        .subscribe(this::populateViewActivities, error -> mAlertMessage.handleError(error))
        );
    }

    /**
     * Populate RecyclerView with physical activities.
     *
     * @param activities {@link List<PhysicalActivity>}
     */
    private void populateViewActivities(final List<PhysicalActivity> activities) {
        Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
            if (activities != null && !activities.isEmpty()) {
                mNoData.setVisibility(View.GONE);
                mAdapter.addItems(activities);
                itShouldLoadMore = true;
                page++;
            } else if (mAdapter.itemsIsEmpty()) {
                mNoData.setVisibility(View.VISIBLE);
            }
        });
    }

    /**
     * Enable/Disable display loading data.
     *
     * @param enabled boolean
     * @param isInit  boolean
     */
    private void loading(final boolean enabled, boolean isInit) {
        if (!enabled) {
            if (isInit) mSkeletonScreen.hide();
            mDataSwipeRefresh.setRefreshing(false);
            itShouldLoadMore = true;
            return;
        }

        if (isInit) {
            mDataSwipeRefresh.setRefreshing(false);
            mSkeletonScreen.show();
        } else {
            mDataSwipeRefresh.setRefreshing(true);
        }
        itShouldLoadMore = false;
    }

    public interface OnClickActivityListener {
        void onClickActivity(PhysicalActivity activity);
    }
}