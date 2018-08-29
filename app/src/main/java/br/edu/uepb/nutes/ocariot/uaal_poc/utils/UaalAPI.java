package br.edu.uepb.nutes.ocariot.uaal_poc.utils;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.Toast;

import org.universAAL.android.services.MiddlewareService;
import org.universAAL.android.utils.AppConstants;
import org.universAAL.android.utils.RAPIManager;
import org.universAAL.android.utils.RESTManager;
import org.universAAL.android.utils.UaalConfig;

/**
 * Implementation UaalAPI.
 *
 * @author Alex Fernandes <alex.fernandes@nutes.uepb.edu.br>
 * @version 1.5
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
public class UaalAPI {
    private static String LOG_TAG = "UAAL_API";
    private int uaal_state = AppConstants.STATUS_STOPPED;
    ProgressReceiver mReceiver;
    public static UaalAPI instance;
    public Context context;

    private UaalAPI(Context context) {
        this.context = context;
        stopUallService();
        UaalConfig.load(context); //Sync Preferences in UaalConfig util
        UaalConfig.setmUAALUser(context, "Ocariot-user");
        UaalConfig.setmSettingRemoteType(context, AppConstants.REMOTE_TYPE_RESTAPI);
        UaalConfig.setmServerURL(context, "http://192.168.50.167:9000/uaal");
        UaalConfig.setmServerUSR(context, "Ocariot-client-ID");
        UaalConfig.setmServerPWD(context, "Ocariot-client123");
        UaalConfig.setmConfigFolder(context, "/data/ocariot/universAAL/configurations/etc/images/");
        UaalConfig.setmSettingRemoteMode(context, AppConstants.REMOTE_MODE_ALWAYS);
        UaalConfig.setmSettingRemoteType(context, AppConstants.REMOTE_TYPE_RESTAPI);
        UaalConfig.setmServerGCM(context, "603831459103");
        UaalConfig.setmSettingWifiEnabled(context, false);
        UaalConfig.setmUIHandler(context, false);
        UaalConfig.setmServiceCoord(context, false);

        activateRestAPI();
        initReceiver();
    }

    public static synchronized UaalAPI getInstance(Context context) {
        if (instance == null) instance = new UaalAPI(context);
        return instance;
    }

    public boolean startUaalService() {
//        Intent startServiceIntent = new Intent(context.getApplicationContext(), MiddlewareService.class);
//        context.getApplicationContext().startService(startServiceIntent);
        Intent intent = new Intent(AppConstants.ACTION_SYS_START);
        context.sendBroadcast(intent);

        return true;
    }

    private void initReceiver() {
        if (mReceiver != null) return;

        mReceiver = new ProgressReceiver();
        /* Register UAAL Service status receiver */
        IntentFilter filter = new IntentFilter(AppConstants.ACTION_UI_PROGRESS);
        filter.addAction(AppConstants.ACTION_NOTIF_STARTED);
        filter.addAction(AppConstants.ACTION_NOTIF_STOPPED);
        context.registerReceiver(mReceiver, filter);
    }

    public void loadUallConfig() {
        UaalConfig.load(context);
    }

    public boolean activateRemoteAPI() {
        if (RAPIManager.checkPlayServices(context)) {
            RAPIManager.performRegistrationInThread(context, null);
            return true;
        } else {
            Toast.makeText(context,
                    org.universAAL.android.R.string.uaal_warning_gplay, Toast.LENGTH_LONG)
                    .show();
            return false;
        }
    }

    public boolean updateGcmKeyRemoteAPI(String newGcmKey) {
        if (RAPIManager.checkPlayServices(context)) {
            RAPIManager.performRegistrationInThread(context, newGcmKey);
            return true;
        } else {
            Log.d(LOG_TAG, context.getResources()
                    .getString(org.universAAL.android.R.string.uaal_warning_gplay));
            return false;
        }
    }

    public boolean activateRestAPI() {
        if (RESTManager.checkPlayServices(context)) {
            RESTManager.performRegistrationInThread(context, null);
            return true;
        } else {
            Log.d(LOG_TAG, context.getResources()
                    .getString(org.universAAL.android.R.string.uaal_warning_gplay));
            return false;
        }
    }

    public boolean updateGcmKeyRestAPI(String newGcmKey) {
        if (RESTManager.checkPlayServices(context)) {
            RESTManager.performRegistrationInThread(context, newGcmKey);
            return true;
        } else {
            Toast.makeText(context,
                    org.universAAL.android.R.string.uaal_warning_gplay, Toast.LENGTH_LONG)
                    .show();
            return false;
        }
    }

    public boolean activateGatewayAPI() {
        /* TODO: Implment activation of Gateway API */
        return true;
    }

    public boolean stopUallService() {
        Intent intent = new Intent(AppConstants.ACTION_SYS_STOP);
        context.sendBroadcast(intent);
        Intent stopServiceIntent = new Intent(context.getApplicationContext(), MiddlewareService.class);
        return context.getApplicationContext().stopService(stopServiceIntent);
    }

    public boolean restartUaalService() {
        if (stopUallService()) // The service was running, restart it
            return startUaalService();

        return false;
    }

    public void publishHeartRate(Context context, int value) {
        Intent heartrateBroadcast = new Intent("br.edu.uepb.nutes.ocariot.uaal_poc.ACTION_HEART_RATE_EVENT");
        heartrateBroadcast.addCategory(Intent.CATEGORY_DEFAULT);
        heartrateBroadcast.putExtra("heartrate_value", value);
        context.sendBroadcast(heartrateBroadcast);
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
                    Log.w(LOG_TAG, "Received broadcast UAAL SERVICE STARTED");
                    uaal_state = AppConstants.STATUS_STARTED;
                    break;
                case AppConstants.ACTION_NOTIF_STOPPED:
                    Log.w(LOG_TAG, "Received broadcast UAAL SERVICE STOPPED");
                    uaal_state = AppConstants.STATUS_STOPPED;
                    break;
                default:
                    return;
            }
        }
    }

    public boolean isStarted() {
        return uaal_state == AppConstants.STATUS_STARTED;
    }

    public void unbinder() {
        if (mReceiver == null) return;
        context.unregisterReceiver(mReceiver);
        mReceiver = null;
    }

}
