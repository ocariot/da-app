package br.edu.uepb.nutes.ocariot.view.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Objects;

import br.edu.uepb.nutes.ocariot.R;
import br.edu.uepb.nutes.ocariot.data.model.ocariot.PhysicalActivity;
import br.edu.uepb.nutes.ocariot.data.model.ocariot.Sleep;
import br.edu.uepb.nutes.ocariot.data.model.ocariot.User;
import br.edu.uepb.nutes.ocariot.data.repository.local.pref.AppPreferencesHelper;
import br.edu.uepb.nutes.ocariot.utils.AlertMessage;
import br.edu.uepb.nutes.ocariot.utils.MessageEvent;
import br.edu.uepb.nutes.ocariot.view.ui.fragment.IotFragment;
import br.edu.uepb.nutes.ocariot.view.ui.fragment.PhysicalActivityListFragment;
import br.edu.uepb.nutes.ocariot.view.ui.fragment.SleepListFragment;
import br.edu.uepb.nutes.ocariot.view.ui.fragment.WelcomeFragment;
import br.edu.uepb.nutes.ocariot.view.ui.preference.LoginFitBit;
import br.edu.uepb.nutes.ocariot.view.ui.preference.SettingsActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * MainActivity implementation.
 *
 * @author Copyright (c) 2018, NUTES/UEPB
 */
public class MainActivity extends AppCompatActivity implements
        PhysicalActivityListFragment.OnClickActivityListener,
        SleepListFragment.OnClickSleepListener,
        WelcomeFragment.OnClickWelcomeListener,
        BottomNavigationView.OnNavigationItemSelectedListener {
    private final String LOG_TAG = MainActivity.class.getSimpleName();
    public final int FIRST_OPEN_CHILDREN_MANAGER = 1;
    public static final String KEY_DO_NOT_LOGIN_FITBIT = "key_do_not_login_fitbit";

    private AppPreferencesHelper appPref;
    private PhysicalActivityListFragment physicalActivityListFragment;
    private SleepListFragment sleepListFragment;
    private IotFragment iotFragment;
    private int lastViewIndex;
    private AlertMessage mAlertMessage;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.navigation)
    BottomNavigationView mBottomNavigationView;

    @BindView(R.id.frame_content)
    FrameLayout mWelcomeContent;

    @BindView(R.id.child_username_bar)
    TextView mChildUsernameBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.title_physical_activities);

        lastViewIndex = 0; // 0 - Physical Activity, 1 - Sleep, 2 - Environment
        appPref = AppPreferencesHelper.getInstance(this);
        physicalActivityListFragment = PhysicalActivityListFragment.newInstance();
        sleepListFragment = SleepListFragment.newInstance();
        iotFragment = IotFragment.newInstance();
        mAlertMessage = new AlertMessage(this);

        mBottomNavigationView.setOnNavigationItemSelectedListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (appPref.getLastSelectedChild() == null) {
            openChildrenManagerActivity(true);
            return;
        }

        if (!appPref.getUserAccessOcariot().getSubjectType().equalsIgnoreCase(User.Type.CHILD)) {
            Objects.requireNonNull(getSupportActionBar())
                    .setSubtitle(appPref.getLastSelectedChild().getUsername());
        }

        if (appPref.getLastSelectedChild().getFitBitAccess() != null
                && (appPref.getLastSelectedChild().getFitBitAccess().getAccessToken() == null
                || appPref.getLastSelectedChild().getFitBitAccess().isExpired())
                && !appPref.getBoolean(KEY_DO_NOT_LOGIN_FITBIT)) {
            replaceFragment(WelcomeFragment.newInstance());
            mBottomNavigationView.setVisibility(View.GONE);
        } else {
            mBottomNavigationView.setVisibility(View.VISIBLE);
            if (lastViewIndex == 1) loadSleepView();
            else if (lastViewIndex == 2) loadIotView();
            else loadPhysicalActivitiesView();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem menuItem = menu.findItem(R.id.action_child);
        if (appPref.getUserAccessOcariot().getSubjectType().equalsIgnoreCase(User.Type.CHILD)) {
            menuItem.setVisible(false);
        }
        return super.onCreateOptionsMenu(menu);
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
            case R.id.action_child:
                openChildrenManagerActivity(false);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.navigation_activities:
                loadPhysicalActivitiesView();
                break;
            case R.id.navigation_sleep:
                loadSleepView();
                break;
            case R.id.navigation_iot:
                loadIotView();
                break;
            default:
                break;
        }
        return false;
    }

    @Override
    public void onClickActivity(PhysicalActivity activity) {
        Intent intent = new Intent(this, PhysicalActivityDetail.class);
        intent.putExtra(PhysicalActivityDetail.ACTIVITY_DETAIL, activity);
        startActivity(intent);
    }

    @Override
    public void onClickSleep(Sleep sleep) {
        Intent intent = new Intent(this, SleepDetail.class);
        intent.putExtra(SleepDetail.SLEEP_DETAIL, sleep);
        startActivity(intent);
    }

    @Override
    public void onClickFitBit() {
        new LoginFitBit(this).doAuthorizationCode();
    }

    @Override
    public void onDoNotLoginFitBitClick() {
        appPref.addBoolean(KEY_DO_NOT_LOGIN_FITBIT, true);
        mBottomNavigationView.setVisibility(View.VISIBLE);
        loadPhysicalActivitiesView();
    }

    /**
     * Replace fragment.
     *
     * @param fragment {@link Fragment}
     */
    private void replaceFragment(Fragment fragment) {
        if (fragment != null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frame_content, fragment).commit();
        }
    }

    /**
     * Replace fragment physical activities view.
     */
    private void loadPhysicalActivitiesView() {
        replaceFragment(physicalActivityListFragment);
        mBottomNavigationView.getMenu().getItem(0).setChecked(true);
        Objects.requireNonNull(getSupportActionBar())
                .setTitle(R.string.title_physical_activities);
        lastViewIndex = 0;
    }

    /**
     * Replace fragment sleep view.
     */
    private void loadSleepView() {
        replaceFragment(sleepListFragment);
        mBottomNavigationView.getMenu().getItem(1).setChecked(true);
        Objects.requireNonNull(getSupportActionBar())
                .setTitle(R.string.title_sleep);
        lastViewIndex = 1;
    }

    /**
     * Replace fragment environments view.
     */
    private void loadIotView() {
        replaceFragment(iotFragment);
        mBottomNavigationView.getMenu().getItem(2).setChecked(true);
        Objects.requireNonNull(getSupportActionBar())
                .setTitle(R.string.title_weight);
        lastViewIndex = 2;
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
     * Open children manager activity.
     */
    private void openChildrenManagerActivity(boolean isFirst) {
        Intent it = new Intent(this, ChildrenManagerActivity.class);
        it.putExtra(ChildrenManagerActivity.EXTRA_IS_FIRST_OPEN, isFirst);
        startActivityForResult(it, FIRST_OPEN_CHILDREN_MANAGER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FIRST_OPEN_CHILDREN_MANAGER && resultCode == Activity.RESULT_FIRST_USER) {
            finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
