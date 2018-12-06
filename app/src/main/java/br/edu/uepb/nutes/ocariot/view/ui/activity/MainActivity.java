package br.edu.uepb.nutes.ocariot.view.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import br.edu.uepb.nutes.ocariot.R;
import br.edu.uepb.nutes.ocariot.data.model.Activity;
import br.edu.uepb.nutes.ocariot.data.model.Sleep;
import br.edu.uepb.nutes.ocariot.view.ui.fragment.EnvironmentFragment;
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
public class MainActivity extends AppCompatActivity implements PhysicalActivityListFragment.OnClickActivityListener,
        SleepListFragment.OnClickSleepListener,
        WelcomeFragment.OnClickWelcomeListener,
        BottomNavigationView.OnNavigationItemSelectedListener {
    private final String LOG_TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.navigation)
    BottomNavigationView mBuBottomNavigationView;

    @BindView(R.id.viewpager)
    ViewPager viewPager;

    private MenuItem prevMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.title_physical_activities);

        mBuBottomNavigationView.setOnNavigationItemSelectedListener(this);
        mBuBottomNavigationView.setSelected(false);
        mBuBottomNavigationView.setEnabled(false);
        mBuBottomNavigationView.setFocusable(false);
        mBuBottomNavigationView.setClickable(false);
        initViewPager();
    }

    /**
     * Initialize View Pager.
     */
    private void initViewPager() {
        viewPager.addOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int position) {
                if (prevMenuItem != null) {
                    prevMenuItem.setChecked(false);
                } else {
                    mBuBottomNavigationView.getMenu().getItem(0).setChecked(false);
                }
                mBuBottomNavigationView.getMenu().getItem(position).setChecked(true);
                prevMenuItem = mBuBottomNavigationView.getMenu().getItem(position);

                switch (position) {
                    case 1:
                        Objects.requireNonNull(getSupportActionBar())
                                .setTitle(R.string.title_sleep);
                        break;
                    case 2:
                        Objects.requireNonNull(getSupportActionBar())
                                .setTitle(R.string.title_temperature_humidity);
                        break;
                    default:
                        Objects.requireNonNull(getSupportActionBar())
                                .setTitle(R.string.title_physical_activities);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int position) {
            }
        });

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(PhysicalActivityListFragment.newInstance());
        adapter.addFragment(SleepListFragment.newInstance());
        adapter.addFragment(EnvironmentFragment.newInstance());
        viewPager.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();

//        if (AppPreferencesHelper.getInstance(this).getAuthStateFitBit() == null) {
//            replaceFragment(WelcomeFragment.newInstance());
//            return;
//        }
//        replaceFragment(PhysicalActivityListFragment.newInstance());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
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
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.navigation_activities:
                viewPager.setCurrentItem(0);
                break;
            case R.id.navigation_sleep:
                viewPager.setCurrentItem(1);
                break;
            case R.id.navigation_temp_humi:
                viewPager.setCurrentItem(2);
                break;
            default:
                break;
        }
        return false;
    }

    @Override
    public void onClickActivity(Activity activity) {
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

    private class ViewPagerAdapter extends FragmentStatePagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment) {
            mFragmentList.add(fragment);
        }
    }
}
