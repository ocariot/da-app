package br.edu.uepb.nutes.ocariot.view.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import br.edu.uepb.nutes.ocariot.R;
import br.edu.uepb.nutes.ocariot.data.model.Activity;
import br.edu.uepb.nutes.ocariot.data.repository.local.pref.AppPreferencesHelper;
import br.edu.uepb.nutes.ocariot.view.ui.fragment.OnClickActivityListener;
import br.edu.uepb.nutes.ocariot.view.ui.fragment.PhysicalActivityDetail;
import br.edu.uepb.nutes.ocariot.view.ui.fragment.PhysicalActivityListFragment;
import br.edu.uepb.nutes.ocariot.view.ui.fragment.WelcomeFragment;
import br.edu.uepb.nutes.ocariot.view.ui.preference.LoginFitBit;
import br.edu.uepb.nutes.ocariot.view.ui.preference.SettingsActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * MainActivity implementation.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2018, NUTES/UEPB
 */
public class MainActivity extends AppCompatActivity implements OnClickActivityListener,
        WelcomeFragment.OnClickWelcomeListener {
    private final String LOG_TAG = MainActivity.class.getSimpleName();
    private final int REQUEST_READ_WRITE_PERMISSION = 786;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (AppPreferencesHelper.getInstance(this).getAuthStateFitBit() == null) {
            replaceFragment(WelcomeFragment.newInstance());
            return;
        }
        replaceFragment(PhysicalActivityListFragment.newInstance());
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

    /**
     * Replace fragment.
     *
     * @param fragment {@link Fragment}
     */
    private void replaceFragment(Fragment fragment) {
        if (fragment != null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            if (fragment instanceof PhysicalActivityDetail) {
                transaction.setCustomAnimations(android.R.anim.slide_in_left,
                        android.R.anim.slide_out_right);
                transaction.replace(R.id.content, fragment).addToBackStack(null).commit();
                return;
            }

            transaction.replace(R.id.content, fragment).commit();
        }
    }

    @Override
    public void onClickActivity(Activity activity) {
        PhysicalActivityDetail physicalActivityDetail = PhysicalActivityDetail.newInstance();
        Bundle args = new Bundle();

        args.putParcelable(PhysicalActivityDetail.ACTIVITY_DETAIL, activity);
        physicalActivityDetail.setArguments(args);
        replaceFragment(physicalActivityDetail);
    }

    @Override
    public void onClickFitBit() {
        new LoginFitBit(this).doAuthorizationCode();
    }
}
