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
import android.view.View;
import android.widget.TextView;

import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.SkeletonScreen;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import br.edu.uepb.nutes.ocariot.R;
import br.edu.uepb.nutes.ocariot.data.model.ocariot.Child;
import br.edu.uepb.nutes.ocariot.data.model.ocariot.ChildrenGroup;
import br.edu.uepb.nutes.ocariot.data.model.ocariot.Educator;
import br.edu.uepb.nutes.ocariot.data.model.ocariot.Family;
import br.edu.uepb.nutes.ocariot.data.model.ocariot.HealthProfessional;
import br.edu.uepb.nutes.ocariot.data.model.ocariot.User;
import br.edu.uepb.nutes.ocariot.data.repository.local.pref.AppPreferencesHelper;
import br.edu.uepb.nutes.ocariot.view.adapter.ChildListAdapter;
import br.edu.uepb.nutes.ocariot.view.adapter.base.OnRecyclerViewListener;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ChildrenManagerActivity extends AppCompatActivity {

    private OnClickActivityListener mListener;
    private SkeletonScreen mSkeletonScreen;
    private ChildListAdapter mAdapter;
    private AppPreferencesHelper appPreferencesHelper;
    // We need this variable to lock and unlock loading more.
    // We should not charge more when a request has already been made.
    // The load will be activated when the requisition is completed.
    private boolean itShouldLoadMore = true;

    @BindView(R.id.children_list)
    RecyclerView mRecyclerView;

    @BindView(R.id.no_data_textView)
    TextView mNoData;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_children_manager);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);

        initResources();
        initRecyclerView();

    }

    private void initResources() {
        appPreferencesHelper = AppPreferencesHelper.getInstance(this);
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
//                if (mListener != null) mListener.onClickActivity(item);
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

//
//        mSkeletonScreen = Skeleton.bind(mRecyclerView)
//                .adapter(mAdapter)
//                .load(R.layout.physical_activity_item_shimmer)
//                .count(7)
//                .duration(1200)
//                .show();
        loadChildren();
    }

    /**
     * Load data in OCARIoT Server.
     * If there is no internet connection, we can display the local database.
     * Otherwise it displays from the remote server.
     */
    private void loadChildren() {

        String typeUser = getIntent().getStringExtra(User.Type.FAMILY);
        Log.w("AAA", " typeUser = getIntent().getStringExtra(User.Type.FAMILY); " + typeUser);
        if (typeUser != null) {
            Family family = Family.jsonDeserialize(typeUser);
            populateViewActivities(family.getChildren());
            return;
        }

        typeUser = getIntent().getStringExtra(User.Type.EDUCATOR);
        Log.w("AAA", " typeUser = getIntent().getStringExtra(User.Type.EDUCATOR); " + typeUser);

        if (typeUser != null) {
            Educator educator = Educator.jsonDeserialize(typeUser);
            List<Child> children = new ArrayList<>();
            for (ChildrenGroup childrenGroup : educator.getChildrenGroups()) {
                children.addAll(childrenGroup.getChildren());
            }
            populateViewActivities(children);
            return;
        }

        typeUser = getIntent().getStringExtra(User.Type.HEALTH_PROFESSIONAL);
        Log.w("AAA", " typeUser = getIntent().getStringExtra(User.Type.HEALTH_PROFESSIONAL); " + typeUser);
        if (typeUser != null) {
            HealthProfessional healthProfessional = HealthProfessional.jsonDeserialize(typeUser);
            List<Child> children = new ArrayList<>();
            Log.w("AAA", "healthProfessional.getChildrenGroups(): " + healthProfessional.getChildrenGroups().size());
            for (ChildrenGroup childrenGroup : healthProfessional.getChildrenGroups()) {
                Log.w("AAA", "childrenGroup.getChildren(): " + childrenGroup.getChildren().size());
                for (Child child : childrenGroup.getChildren()) {
                    children.add(child);
                }
            }
            populateViewActivities(children);
        }
    }

    /**
     * Populate RecyclerView with physical activities.
     *
     * @param children {@link List <Child>}
     */
    private void populateViewActivities(final List<Child> children) {
        Log.w("AAA", "Children size: " + children.size());
        Objects.requireNonNull(this).runOnUiThread(() -> {
            mAdapter.clearItems();
            if (children.isEmpty()) {
                mNoData.setVisibility(View.VISIBLE);
                return;
            }
            mAdapter.addItems(children);
            mNoData.setVisibility(View.GONE);
        });
    }

    public interface OnClickActivityListener {
        void onClickActivity(Child child);
    }
}
