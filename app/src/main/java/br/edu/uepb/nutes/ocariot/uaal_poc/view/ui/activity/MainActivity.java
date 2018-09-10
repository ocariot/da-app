package br.edu.uepb.nutes.ocariot.uaal_poc.view.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import br.edu.uepb.nutes.ocariot.uaal_poc.R;
import br.edu.uepb.nutes.ocariot.uaal_poc.data.model.Activity;
import br.edu.uepb.nutes.ocariot.uaal_poc.data.repository.local.pref.AppPreferencesHelper;
import br.edu.uepb.nutes.ocariot.uaal_poc.utils.UaalAPI;
import br.edu.uepb.nutes.ocariot.uaal_poc.view.ui.fragment.OnClickActivityListener;
import br.edu.uepb.nutes.ocariot.uaal_poc.view.ui.fragment.PhysicalActivityDetail;
import br.edu.uepb.nutes.ocariot.uaal_poc.view.ui.fragment.PhysicalActivityListFragment;
import br.edu.uepb.nutes.ocariot.uaal_poc.view.ui.preference.LoginFitBit;
import br.edu.uepb.nutes.ocariot.uaal_poc.view.ui.preference.SettingsActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * MainActivity implementation.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2018, NUTES/UEPB
 */
public class MainActivity extends AppCompatActivity implements OnClickActivityListener {
    private final String LOG_TAG = MainActivity.class.getSimpleName();
    private final int REQUEST_READ_WRITE_PERMISSION = 786;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.box_message_error)
    LinearLayout mBoxMessageAlert;

    @BindView(R.id.description_message_error_tv)
    TextView descriptionMessageAlert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);

        if (hasStoragePermissions()) {
            Log.w(LOG_TAG, "onCreate() startUAAL");
            UaalAPI.initUaal(this);
        } else requestStoragePermissions();

        initComponents();
    }

    private void initComponents() {
        replaceFragment(PhysicalActivityListFragment.newInstance());
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        if (AppPreferencesHelper.getInstance(this).getAuthStateFitBit() == null) {
            showMessageAlert(true);
            return;
        }
        showMessageAlert(false);
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
            transaction.setCustomAnimations(android.R.anim.slide_in_left,
                    android.R.anim.slide_out_right);

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_READ_WRITE_PERMISSION) {
            if (resultCode == android.app.Activity.RESULT_OK) {
                /* initialize universaal */
                Log.w(LOG_TAG, "onActivityResult()");
                UaalAPI.initUaal(this);
            } else {
                finish();
            }
        }
    }

    @OnClick(R.id.box_message_error)
    public void messageAlertAction(View view) {
        View mView = mBoxMessageAlert.getChildAt(0);
        if (mView instanceof TextView) {
            TextView textView = (TextView) mView;
            if (textView.getText().equals(getString(R.string.error_oauth_fitbit_permission))) {
                new LoginFitBit(this).doAuthorizationCode();
            }
        }
    }

    private boolean hasStoragePermissions() {
        PackageManager packageManager = getPackageManager();
        return ((packageManager.checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE,
                getPackageName()) == PackageManager.PERMISSION_GRANTED)
                && (packageManager.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                getPackageName()) == PackageManager.PERMISSION_GRANTED));
    }

    private void requestStoragePermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE},
                REQUEST_READ_WRITE_PERMISSION);
    }

    /**
     * Displays message or hides according to parameter.
     * True to display and False to hide
     *
     * @param isVisible
     */
    private void showMessageAlert(boolean isVisible) {
        if (isVisible) {
            mBoxMessageAlert.setVisibility(View.VISIBLE);
            return;
        }
        mBoxMessageAlert.setVisibility(View.GONE);
    }

}
