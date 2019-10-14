package br.edu.uepb.nutes.ocariot.view.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.SkeletonScreen;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import br.edu.uepb.nutes.ocariot.R;
import br.edu.uepb.nutes.ocariot.data.model.common.UserAccess;
import br.edu.uepb.nutes.ocariot.data.model.ocariot.Child;
import br.edu.uepb.nutes.ocariot.data.model.ocariot.User;
import br.edu.uepb.nutes.ocariot.data.repository.local.pref.AppPreferencesHelper;
import br.edu.uepb.nutes.ocariot.data.repository.remote.ocariot.OcariotNetRepository;
import br.edu.uepb.nutes.ocariot.utils.AlertMessage;
import br.edu.uepb.nutes.ocariot.utils.DateUtils;
import br.edu.uepb.nutes.ocariot.utils.MessageEvent;
import br.edu.uepb.nutes.ocariot.view.adapter.ChildListAdapter;
import br.edu.uepb.nutes.ocariot.view.adapter.base.OnRecyclerViewListener;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.CompositeDisposable;

public class ChildrenManagerActivity extends AppCompatActivity {
    private final String KEY_SORT_SELECTED = "key_sort_selected";

    private ChildListAdapter mAdapter;
    private AppPreferencesHelper appPref;
    private UserAccess userAccess;
    private CompositeDisposable mDisposable;
    private OcariotNetRepository ocariotRepository;
    private SkeletonScreen mSkeletonScreen;
    private AlertMessage mAlertMessage;
    private boolean itShouldLoadMore = true;
    private Menu mMenu;
    private List<Child> children;

    @BindView(R.id.children_list)
    RecyclerView mRecyclerView;

    @BindView(R.id.box_no_data)
    View mNoData;

    @BindView(R.id.img_no_data)
    AppCompatImageView mImgNoData;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.data_swipe_refresh)
    SwipeRefreshLayout mDataSwipeRefresh;

    @BindView(R.id.child_username_bar)
    TextView mChildUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_children_manager);
        ButterKnife.bind(this);

        mChildUsername.setVisibility(View.GONE);

        mToolbar.setTitle(getResources().getString(R.string.title_children));
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        appPref = AppPreferencesHelper.getInstance(this);
        userAccess = appPref.getUserAccessOcariot();
        mDisposable = new CompositeDisposable();
        ocariotRepository = OcariotNetRepository.getInstance(this);
        mAlertMessage = new AlertMessage(this);
        mImgNoData.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.children));

        initComponents();
        disableBack();
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_children_manager, menu);
        this.mMenu = menu;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        selectMenuFilters();
        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * Initialize components
     */
    private void initComponents() {
        initRecyclerView();
        initDataSwipeRefresh();
    }

    /**
     * Initialize SwipeRefresh
     */
    private void initDataSwipeRefresh() {
        mDataSwipeRefresh.setOnRefreshListener(() -> {
            if (itShouldLoadMore) loadChildren();
        });
    }

    private void initRecyclerView() {
        mAdapter = new ChildListAdapter(this);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(),
                new LinearLayoutManager(this).getOrientation()));
        mAdapter.setListener(new OnRecyclerViewListener<Child>() {
            @Override
            public void onItemClick(Child item) {
                appPref.addBoolean(MainActivity.KEY_DO_NOT_LOGIN_FITBIT, false);
                appPref.addLastSelectedChild(item);
                finish();
            }

            @Override
            public void onLongItemClick(View v, Child item) {
                // Not implemented!
            }

            @Override
            public void onMenuContextClick(View v, Child item) {
                // Not implemented!
            }
        });
        mRecyclerView.setAdapter(mAdapter);

        mSkeletonScreen = Skeleton.bind(mRecyclerView)
                .adapter(mAdapter)
                .load(R.layout.child_item_shimmer)
                .count(10)
                .duration(1200)
                .show();
    }

    private void disableBack() {
        if (appPref.getLastSelectedChild() == null) {
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadChildren();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDisposable != null) mDisposable.dispose();
    }

    /**
     * Load data in OCARIoT Server.
     * If there is no internet connection, we can display the local database.
     * Otherwise it displays from the remote server.
     */
    private void loadChildren() {
        if (userAccess.getSubjectType().equalsIgnoreCase(User.Type.FAMILY)) {
            mDisposable.add(
                    ocariotRepository
                            .getChildrenOfFamily(userAccess.getSubject())
                            .doOnSubscribe(disposable -> loading(true))
                            .doAfterTerminate(() -> loading(false))
                            .subscribe(this::populateViewChildren, err -> mAlertMessage.handleError(err))
            );
        } else if (userAccess.getSubjectType().equalsIgnoreCase(User.Type.EDUCATOR)) {
            mDisposable.add(ocariotRepository
                    .getChildrenOfEducator(userAccess.getSubject())
                    .doOnSubscribe(disposable -> loading(true))
                    .doAfterTerminate(() -> loading(false))
                    .subscribe(this::populateViewChildren, err -> mAlertMessage.handleError(err))
            );
        } else if (userAccess.getSubjectType().equalsIgnoreCase(User.Type.HEALTH_PROFESSIONAL)) {
            mDisposable.add(ocariotRepository
                    .getChildrenOfHealthProfessional(userAccess.getSubject())
                    .doOnSubscribe(disposable -> loading(true))
                    .doAfterTerminate(() -> loading(false))
                    .subscribe(this::populateViewChildren, err -> mAlertMessage.handleError(err))
            );
        } else {
            populateViewChildren(new ArrayList<>());
        }
    }

    /**
     * Populate RecyclerView with physical activities.
     *
     * @param children {@link List <Child>}
     */
    private void populateViewChildren(List<Child> children) {
        if (children != null) this.children = children;

        int sortSelected = appPref.getInt(KEY_SORT_SELECTED);

        if (sortSelected == R.id.action_sort_sync) {
            Collections.sort(this.children, (o1, o2) -> {
                if (o1.getLastSync() == null) return -1;
                else if (o2.getLastSync() == null) return 1;
                return DateUtils.convertDateTimeToUTC(o1.getLastSync())
                        .compareTo(DateUtils.convertDateTimeToUTC(o2.getLastSync()));
            });
        } else { // default username ASC
            Collections.sort(this.children);
        }

        runOnUiThread(() -> {
            mAdapter.clearItems();
            if (this.children.isEmpty()) {
                mNoData.setVisibility(View.VISIBLE);
                return;
            }
            mAdapter.addItems(this.children);
            mNoData.setVisibility(View.GONE);
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sort_username:
            case R.id.action_sort_sync:
                clearMenuFilters();
                appPref.addInt(KEY_SORT_SELECTED, item.getItemId());
                populateViewChildren(null);
                break;
            default:
                break;
        }
        item.setChecked(true);
        return false;
    }

    private void clearMenuFilters() {
        Menu submenu = mMenu.getItem(0).getSubMenu();
        for (int i = 0; i < submenu.size(); i++) {
            submenu.getItem(i).setChecked(false);
        }
    }

    private void selectMenuFilters() {
        Menu submenu = mMenu.getItem(0).getSubMenu();
        int sortSelected = appPref.getInt(KEY_SORT_SELECTED);
        if (sortSelected != -1) {
            for (int i = 0; i < submenu.size(); i++) {
                if (submenu.getItem(i).getItemId() == sortSelected) {
                    submenu.getItem(i).setChecked(true);
                    return;
                }
            }
        } else { // Default
            submenu.getItem(0).setChecked(true);
        }
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

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onMessageEvent(MessageEvent event) {
        if (event.getName().equals(MessageEvent.EventType.OCARIOT_ACCESS_TOKEN_EXPIRED)) {
            mAlertMessage.show(R.string.alert_title_token_expired,
                    R.string.alert_token_expired_ocariot,
                    R.color.colorWarning, R.drawable.ic_warning_dark, 20000,
                    true, new AlertMessage.AlertMessageListener() {
                        @Override
                        public void onHideListener() {
                            redirectToLogin();
                        }

                        @Override
                        public void onClickListener() {
                            redirectToLogin();
                        }
                    });
        }
    }

    private void redirectToLogin() {
        appPref.removeSession();
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        getApplicationContext().startActivity(intent);
    }
}
