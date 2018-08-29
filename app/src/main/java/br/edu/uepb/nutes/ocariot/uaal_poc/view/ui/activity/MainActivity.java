package br.edu.uepb.nutes.ocariot.uaal_poc.view.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import br.edu.uepb.nutes.ocariot.uaal_poc.R;
import br.edu.uepb.nutes.ocariot.uaal_poc.data.model.Activity;
import br.edu.uepb.nutes.ocariot.uaal_poc.utils.UaalAPI;
import br.edu.uepb.nutes.ocariot.uaal_poc.view.ui.fragment.OnClickActivityListener;
import br.edu.uepb.nutes.ocariot.uaal_poc.view.ui.fragment.PhysicalActivityDetail;
import br.edu.uepb.nutes.ocariot.uaal_poc.view.ui.fragment.PhysicalActivityListFragment;
import br.edu.uepb.nutes.ocariot.uaal_poc.view.ui.preference.SettingsActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * MainActivity implementation.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2018, NUTES/UEPB
 */
public class MainActivity extends AppCompatActivity implements OnClickActivityListener {
    private final String LOG_TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    private UaalAPI uaalAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);

        uaalAPI = UaalAPI.getInstance(this);

        initComponents();
    }

    private void initComponents() {
        replaceFragment(PhysicalActivityListFragment.newInstance());
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        if(!uaalAPI.isStarted())
            uaalAPI.startUaalService();
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
            transaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

            if (fragment instanceof PhysicalActivityDetail) {
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

    private void showMessageError(String message) {
        message = message != null ? message : getString(R.string.error_oauth_fitbit_permission);
//        Alerter.create(MainActivity.this)
//                .setText(message)
//                .setDuration(10000)
//                .setBackgroundColorRes(R.color.colorWarning)
//                .setIcon(R.drawable.ic_warning_dark)
//                .setOnClickListener(v -> {
//                    startActivity(new Intent(this, SettingsActivity.class));
//                    Alerter.hide();
//                })
//                .show();
    }
}
