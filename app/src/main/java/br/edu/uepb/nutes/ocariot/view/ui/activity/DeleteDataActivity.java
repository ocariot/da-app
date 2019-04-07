package br.edu.uepb.nutes.ocariot.view.ui.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import br.edu.uepb.nutes.ocariot.R;
import br.edu.uepb.nutes.ocariot.data.model.Activity;
import br.edu.uepb.nutes.ocariot.data.model.PhysicalActivity;
import br.edu.uepb.nutes.ocariot.data.model.Sleep;
import br.edu.uepb.nutes.ocariot.data.repository.local.pref.AppPreferencesHelper;
import br.edu.uepb.nutes.ocariot.data.repository.remote.ocariot.OcariotNetRepository;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.CompositeDisposable;

public class DeleteDataActivity extends AppCompatActivity implements View.OnClickListener {
    private final String LOG_TAG = "DeleteData";

    private OcariotNetRepository ocariotRepository;
    private AppPreferencesHelper pref;
    private int progressStatusValue = 0;
    private int totalActivities = 0;
    private int totalSleep = 0;
    private int totalActivitiesProcess = 0;
    private int totalSleepProcess = 0;
    private CompositeDisposable mDisposable;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.activities_checkBox)
    AppCompatCheckBox activitiesCheckBox;

    @BindView(R.id.sleep_checkBox)
    AppCompatCheckBox sleepCheckBox;

    @BindView(R.id.environments_checkBox)
    AppCompatCheckBox environmentsCheckBox;

    @BindView(R.id.delete_data_button)
    AppCompatButton deleteDataButton;

    @BindView(R.id.delete_data_progressBar)
    ProgressBar progressBar;

    @BindView(R.id.status_progressBar)
    TextView statusProgressTextView;

    @BindView(R.id.box_sttaus_progressBar)
    LinearLayout boxStatusProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_data);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);

        mDisposable = new CompositeDisposable();
        ocariotRepository = OcariotNetRepository.getInstance(this);
        pref = AppPreferencesHelper.getInstance(this);
        deleteDataButton.setOnClickListener(this);
        boxStatusProgressBar.setVisibility(View.GONE);

        initToobar();
    }

    private void initToobar() {
        ActionBar mActionBar = getSupportActionBar();
        if (mActionBar == null) return;
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setDisplayShowTitleEnabled(true);
        mActionBar.setHomeAsUpIndicator(R.drawable.ic_close_dark);
        mActionBar.setTitle(R.string.delete_data_title);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.delete_data_button) {
            boxStatusProgressBar.setVisibility(View.VISIBLE);
            processData(activitiesCheckBox.isChecked(),
                    sleepCheckBox.isChecked(),
                    environmentsCheckBox.isChecked());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) super.onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDisposable.dispose();
    }

    private void processData(boolean isActivities, boolean isSleep, boolean isEnvironments) {
        Log.w(LOG_TAG, "processData() " + isActivities + " " + isSleep);
        if (!isActivities && !isSleep && !isEnvironments) {
            Toast.makeText(this, R.string.delete_data_invalid_select, Toast.LENGTH_SHORT).show();
            return;
        }

        updateStatus(false);

        if (isActivities) {
            mDisposable.add(
                    ocariotRepository
                            .listActivities(pref.getUserAccessOcariot().getSubject(),
                                    null, 1, 100)
                            .subscribe(physicalActivities -> {
                                totalActivities = physicalActivities.size();
                                deleteActivities(physicalActivities);
                            }, error -> {
                                updateStatus(true);
                                if (error != null) {
                                    Toast.makeText(getApplicationContext(),
                                            error.getMessage(), Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                Toast.makeText(getApplicationContext(),
                                        getString(R.string.error_500), Toast.LENGTH_SHORT).show();
                            })
            );
        }

        if (isSleep) {
            mDisposable.add(
                    ocariotRepository.listSleep(pref.getUserAccessOcariot().getSubject(),
                            null, 1, 100)
                            .subscribe(sleeps -> {
                                totalSleep = sleeps.size();
                                deleteSleep(sleeps);
                            }, error -> {
                                updateStatus(true);
                                if (error != null) {
                                    Toast.makeText(getApplicationContext(),
                                            error.getMessage(), Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                Toast.makeText(getApplicationContext(),
                                        getString(R.string.error_500), Toast.LENGTH_SHORT).show();
                            })
            );
        }
    }

    private void deleteActivities(List<PhysicalActivity> activities) {
        Log.w(LOG_TAG, "deleteActivities() " + totalActivities);
        if (activities == null) return;

        for (Activity activity : activities) {
            new Handler().postDelayed(() -> {
                totalActivitiesProcess++;
                mDisposable.add(
                        ocariotRepository
                                .deleteActivity(
                                        pref.getUserAccessOcariot().getSubject(), activity.get_id()
                                )
                                .doAfterTerminate(() -> updateStatus(false))
                                .subscribe()
                );
            }, 200);
        }
    }

    private void deleteSleep(List<Sleep> sleeps) {
        Log.w(LOG_TAG, "deleteSleep() " + totalSleep);
        if (sleeps == null) return;

        for (Sleep sleep : sleeps) {
            new Handler().postDelayed(() -> {
                totalSleepProcess++;

                mDisposable.add(
                        ocariotRepository
                                .deleteSleep(
                                        pref.getUserAccessOcariot().getSubject(),
                                        sleep.get_id()
                                )
                                .doAfterTerminate(() -> updateStatus(false))
                                .subscribe()
                );
            }, 200);
        }
    }

    private void updateStatus(boolean isFinished) {
        int total = totalActivities + totalSleep;
        int totalProcess = totalActivitiesProcess + totalSleepProcess;

        if (isFinished || totalProcess >= total) {
            progressBar.setProgress(100);
            statusProgressTextView.setText(String.format("%S%%", 100));
        } else if (progressStatusValue < 99) {
            progressBar.setProgress(++progressStatusValue);
            statusProgressTextView.setText(String.format("%S%%", progressStatusValue));
        }
    }
}
