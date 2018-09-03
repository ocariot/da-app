package br.edu.uepb.nutes.ocariot.uaal_poc.view.ui.preference;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ProgressBar;

import org.universAAL.android.utils.AppConstants;

import br.edu.uepb.nutes.ocariot.uaal_poc.R;
import br.edu.uepb.nutes.ocariot.uaal_poc.utils.UaalAPI;
import butterknife.BindView;
import butterknife.ButterKnife;

public class UaalStatusActivity extends AppCompatActivity {
    static private String TAG = "UAAL_STATUS_FRAGMENT";
    static public int mPercentage = 0;
    static public int uaal_state = AppConstants.STATUS_STOPPED;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.pbUaalStatus)
    ProgressBar pbUaalStatus;

    private Menu mOptionMenu;
    private ProgressReceiver mReceiver;
    private Handler mHandler;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uaal_status);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);

        mHandler = new Handler();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_uaal_status_view, menu);
        updateUaalState();
        mOptionMenu = menu;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_action_uaal_start:
                startUAAL();
                return true;
            case R.id.menu_action_uaal_stop:
                stopUAAL();
                return true;
            case android.R.id.home:
                super.onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mReceiver == null) {
            mReceiver = new ProgressReceiver();
        }
        /* Register UAAL Service status receiver */
        IntentFilter filter = new IntentFilter(AppConstants.ACTION_UI_PROGRESS);
        filter.addAction(AppConstants.ACTION_NOTIF_STARTED);
        filter.addAction(AppConstants.ACTION_NOTIF_STOPPED);
        registerReceiver(mReceiver, filter);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
            mReceiver = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void stopUAAL() {
        UaalAPI.stopUallService(this);
    }

    private void startUAAL() {
        UaalAPI.startUaalService(this);
    }

    private void updateUaalState() {
        if ((pbUaalStatus != null) && (mOptionMenu != null)) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            switch (uaal_state) {
                                case AppConstants.STATUS_STOPPED:
                                    mOptionMenu.findItem(R.id.menu_action_uaal_start).setVisible(true);
                                    mOptionMenu.findItem(R.id.menu_action_uaal_stop).setVisible(false);
                                    pbUaalStatus.setIndeterminate(false);
                                    pbUaalStatus.setProgress(0);
                                    mPercentage = 0;
                                    break;
                                case AppConstants.STATUS_STARTING:
                                    mOptionMenu.findItem(R.id.menu_action_uaal_start).setVisible(false);
                                    mOptionMenu.findItem(R.id.menu_action_uaal_stop).setVisible(false);
                                    pbUaalStatus.setIndeterminate(false);
                                    pbUaalStatus.setProgress(mPercentage);
                                    break;
                                case AppConstants.STATUS_STARTED:
                                    mOptionMenu.findItem(R.id.menu_action_uaal_start).setVisible(false);
                                    mOptionMenu.findItem(R.id.menu_action_uaal_stop).setVisible(true);
                                    pbUaalStatus.setIndeterminate(true);
                                    mPercentage = 100;
                                    break;
                                default:
                                    return;
                            }
                        }
                    });
                }
            }).start();
        } else {
            Log.e(TAG, "The option cant be update with the current uaal state");
        }
    }

    /**
     * Broadcast receiver to get notified about progress in the progress bar by
     * the other services
     *
     * @author alfiva
     */
    public class ProgressReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            switch (action) {
                case AppConstants.ACTION_NOTIF_STARTED:
                    Log.i(TAG, "Received broadcast UAAL SERVICE STARTED");
                    mPercentage = 100;
                    uaal_state = AppConstants.STATUS_STARTED;
                    updateUaalState();
                    break;
                case AppConstants.ACTION_NOTIF_STOPPED:
                    Log.e(TAG, "Received broadcast UAAL SERVICE STOPPED");
                    mPercentage = 0;
                    uaal_state = AppConstants.STATUS_STOPPED;
                    updateUaalState();
                    break;
                case AppConstants.ACTION_UI_PROGRESS:
                    mPercentage = intent.getIntExtra(
                            AppConstants.ACTION_UI_PROGRESS_X_PERCENTAGE, 0);
                    setUaalStartingPercentage();
                    break;
                default:
                    return;
            }
        }
    }

    /**
     * Notify the progress to update its status to that set by the
     * MiddlewareService
     */
    private void setUaalStartingPercentage() {
        if ((pbUaalStatus != null) && (mOptionMenu != null)) {
            if (mPercentage >= 100) {
                /* Uaal service started */
                uaal_state = AppConstants.STATUS_STARTED;
                updateUaalState();
            } else if (mPercentage > 0) {
                /* Uaal service starting */
                uaal_state = AppConstants.STATUS_STARTING;
                updateUaalState();
            } else {
                /* Uaal service stoped */
                uaal_state = AppConstants.STATUS_STOPPED;
                updateUaalState();
            }
        }
    }
}
