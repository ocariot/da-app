package br.edu.uepb.nutes.ocariot.view.ui.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
    public static final String EXTRA_IS_FIRST_OPEN = "extra_is_first_open";
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
    private boolean isFirstOpen;
    private SearchView searchView;

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

    @BindView(R.id.logout_button)
    AppCompatButton mLogoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_children_manager);
        ButterKnife.bind(this);

        isFirstOpen = getIntent().getBooleanExtra(EXTRA_IS_FIRST_OPEN, false);

        mChildUsername.setVisibility(View.GONE);

        mToolbar.setTitle(getResources().getString(R.string.title_children));
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        appPref = AppPreferencesHelper.getInstance();
        userAccess = appPref.getUserAccessOcariot();
        mDisposable = new CompositeDisposable();
        ocariotRepository = OcariotNetRepository.getInstance();
        mAlertMessage = new AlertMessage(this);
        mLogoutButton.setOnClickListener(v -> this.openDialogSignOut());

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

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                mAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                mAdapter.getFilter().filter(query);
                return false;
            }
        });
        return true;
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
    public void onBackPressed() {
        // close search view on back button pressed
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
            return;
        }

        if (isFirstOpen) {
            Intent intent = new Intent();
            setResult(Activity.RESULT_FIRST_USER, intent);
            finish();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_sort_username:
            case R.id.action_sort_sync:
                if (this.children == null || this.children.isEmpty()) return false;
                clearMenuFilters();
                appPref.addInt(KEY_SORT_SELECTED, item.getItemId());
                populateViewChildren(null);
                item.setChecked(true);
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void clearMenuFilters() {
        Menu submenu = mMenu.getItem(1).getSubMenu();
        for (int i = 0; i < submenu.size(); i++) {
            submenu.getItem(i).setChecked(false);
        }
    }

    private void selectMenuFilters() {
        Menu submenu = mMenu.getItem(1).getSubMenu();
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

    /**
     * Show dialog confirm sign out in app.
     */
    private void openDialogSignOut() {
        runOnUiThread(() -> {
            AlertDialog.Builder mDialog = new AlertDialog.Builder(this);
            mDialog.setMessage(R.string.dialog_confirm_sign_out)
                    .setPositiveButton(R.string.title_yes, (dialog, which) -> {
                                if (appPref.removeSession()) {
                                    Intent intent = new Intent(this, LoginActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                                            Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                }
                            }
                    ).setNegativeButton(R.string.title_no, null)
                    .create().show();
        });
    }
}
