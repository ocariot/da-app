package br.edu.uepb.nutes.ocariot.view.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.SkeletonScreen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import br.edu.uepb.nutes.ocariot.R;
import br.edu.uepb.nutes.ocariot.data.model.common.UserAccess;
import br.edu.uepb.nutes.ocariot.data.model.ocariot.Child;
import br.edu.uepb.nutes.ocariot.data.model.ocariot.ChildrenGroup;
import br.edu.uepb.nutes.ocariot.data.model.ocariot.User;
import br.edu.uepb.nutes.ocariot.data.repository.local.pref.AppPreferencesHelper;
import br.edu.uepb.nutes.ocariot.data.repository.remote.ocariot.OcariotNetRepository;
import br.edu.uepb.nutes.ocariot.view.adapter.ChildListAdapter;
import br.edu.uepb.nutes.ocariot.view.adapter.base.OnRecyclerViewListener;
import br.edu.uepb.nutes.ocariot.view.ui.preference.SettingsActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.CompositeDisposable;

public class ChildrenManagerActivity extends AppCompatActivity {

    private ChildListAdapter mAdapter;
    private AppPreferencesHelper appPreferencesHelper;
    private UserAccess userAccess;
    private CompositeDisposable mDisposable;
    private OcariotNetRepository ocariotRepository;
    private SkeletonScreen mSkeletonScreen;

    @BindView(R.id.children_list)
    RecyclerView mRecyclerView;

    @BindView(R.id.no_data_textView)
    TextView mNoData;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.data_swipe_refresh)
    SwipeRefreshLayout mDataSwipeRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_children_manager);
        ButterKnife.bind(this);
        mToolbar.setTitle(getResources().getString(R.string.manager_child));
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initResources();
        initRecyclerView();
        disableBack();
    }

    private void initResources() {
        appPreferencesHelper = AppPreferencesHelper.getInstance(this);
        userAccess = appPreferencesHelper.getUserAccessOcariot();
        mDisposable = new CompositeDisposable();
        ocariotRepository = OcariotNetRepository.getInstance(this);
    }

    /**
     * Initialize SwipeRefresh
     */
    private void initDataSwipeRefresh() {
        mDataSwipeRefresh.setOnRefreshListener(this::loadChildren);
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
                appPreferencesHelper.addChildProfile(item);
                startActivity(new Intent(ChildrenManagerActivity.this, MainActivity.class));
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
                .load(R.layout.children_item_shimmer)
                .count(7)
                .duration(1200)
                .show();

        initDataSwipeRefresh();
        loadChildren();
    }

    private void disableBack() {
        if (appPreferencesHelper.getChildProfile() == null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    /**
     * Load data in OCARIoT Server.
     * If there is no internet connection, we can display the local database.
     * Otherwise it displays from the remote server.
     */
    private void loadChildren() {
        if (userAccess.getSubjectType().equalsIgnoreCase(User.Type.FAMILY)) {
            mDisposable.add(ocariotRepository
                    .getFamilyChildrenById(userAccess.getSubject())
                    .doOnSubscribe(disposable -> loading(true))
                    .doAfterTerminate(() -> loading(false))
                    .subscribe(children -> {
                        populateViewActivities(children);
                    }, this::errorHandler));
        } else if (userAccess.getSubjectType().equalsIgnoreCase(User.Type.EDUCATOR)) {
            mDisposable.add(ocariotRepository
                    .getEducatorGroupsById(userAccess.getSubject())
                    .doOnSubscribe(disposable -> loading(true))
                    .doAfterTerminate(() -> loading(false))
                    .subscribe(groups -> {
                        populateViewActivities(getChildrenOfGroup(groups));
                    }, this::errorHandler));
        } else if (userAccess.getSubjectType().equalsIgnoreCase(User.Type.HEALTH_PROFESSIONAL)) {
            mDisposable.add(ocariotRepository
                    .getHealthProfessionalGroupsById(userAccess.getSubject())
                    .doOnSubscribe(disposable -> loading(true))
                    .doAfterTerminate(() -> loading(false))
                    .subscribe(groups -> {
                        populateViewActivities(getChildrenOfGroup(groups));
                    }, this::errorHandler));
        }
    }

    /**
     * Get children list of all groups.
     *
     * @param childrenGroup
     * @return
     */
    private List<Child> getChildrenOfGroup(List<ChildrenGroup> childrenGroup) {
        List<Child> children = new ArrayList<>();
        for (ChildrenGroup group : childrenGroup) {
            for (Child child : group.getChildren())
                if (!children.contains(child)) children.add(child);
        }
        return children;
    }

    /**
     * Populate RecyclerView with physical activities.
     *
     * @param children {@link List <Child>}
     */
    private void populateViewActivities(final List<Child> children) {
        Objects.requireNonNull(this).runOnUiThread(() -> {
            mAdapter.clearItems();
            if (children.isEmpty()) {
                mNoData.setText(getResources().getText(R.string.message_no_data_display));
                mNoData.setVisibility(View.VISIBLE);
                return;
            }
            mAdapter.addItems(children);
            mNoData.setVisibility(View.GONE);
        });
    }

    /**
     * @param error
     */
    private void errorHandler(Throwable error) {
        mNoData.setText(getResources().getText(R.string.error_500));
        mNoData.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                break;
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            default:
                break;
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        menu.findItem(R.id.action_child).setVisible(false);
        if (appPreferencesHelper.getChildProfile() != null)
            menu.findItem(R.id.action_settings).setVisible(false);
        return super.onCreateOptionsMenu(menu);
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
            return;
        }
        mSkeletonScreen.show();
    }
}
