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

import java.util.ArrayList;
import java.util.List;

import br.edu.uepb.nutes.ocariot.uaal_poc.R;
import br.edu.uepb.nutes.ocariot.uaal_poc.data.model.Activity;
import br.edu.uepb.nutes.ocariot.uaal_poc.data.model.ActivityLevel;
import br.edu.uepb.nutes.ocariot.uaal_poc.utils.DateUtils;
import br.edu.uepb.nutes.ocariot.uaal_poc.utils.UaalAPI;
import br.edu.uepb.nutes.ocariot.uaal_poc.view.ui.fragment.OnClickActivityListener;
import br.edu.uepb.nutes.ocariot.uaal_poc.view.ui.fragment.PhysicalActivityDetail;
import br.edu.uepb.nutes.ocariot.uaal_poc.view.ui.fragment.PhysicalActivityListFragment;
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

    private Animation mAnimation;

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
            case R.id.action_start_uaal:
                UaalAPI.startUaalService(this);
                break;
            case R.id.action_send_activity: {
                Activity a = new Activity();
                a.setName("Run");
                a.setCalories(1548);
                a.setSteps(8541495);
                List<ActivityLevel> activityLevels = new ArrayList<>();
                activityLevels.add(new ActivityLevel(ActivityLevel.VERY_LEVEL, 4));
                activityLevels.add(new ActivityLevel(ActivityLevel.FAIRLY_LEVEL, 2));
                activityLevels.add(new ActivityLevel(ActivityLevel.LIGHTLY_LEVEL, 1));
                activityLevels.add(new ActivityLevel(ActivityLevel.SEDENTARY_LEVEL, 0));

                a.setActivityLevel(activityLevels);
                a.setDuration(1256);
                a.setHeartRate(1); // TODO REMOVER
                a.setStartTime(DateUtils.getCurrentDateISO8601(null));
                a.setEndTime(DateUtils.getCurrentDateISO8601(null));
                a.setUserId("488YU984984ELKGU218A894849CD");

                UaalAPI.publishPhysicalActivity(this, a);
            }
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
    public void messageAlertAction() {
        Log.w(LOG_TAG, "messageAlertAction()");
    }

    private boolean hasStoragePermissions() {
        PackageManager packageManager = getPackageManager();
        return ((packageManager.checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, getPackageName())
                == PackageManager.PERMISSION_GRANTED)
                && (packageManager.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, getPackageName())
                == PackageManager.PERMISSION_GRANTED));
    }

    private void requestStoragePermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_WRITE_PERMISSION);
    }

    private void showMessageAlert(boolean isVisible) {
        if (isVisible) {
            mAnimation = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
            mBoxMessageAlert.startAnimation(mAnimation);
            mBoxMessageAlert.setVisibility(View.VISIBLE);
            return;
        }

        mAnimation = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);
        mBoxMessageAlert.startAnimation(mAnimation);
        mBoxMessageAlert.setVisibility(View.GONE);
    }

}
