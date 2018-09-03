package br.edu.uepb.nutes.ocariot.uaal_poc.utils;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import org.universAAL.android.services.MiddlewareService;
import org.universAAL.android.utils.AppConstants;
import org.universAAL.android.utils.RAPIManager;
import org.universAAL.android.utils.RESTManager;
import org.universAAL.android.utils.UaalConfig;

import br.edu.uepb.nutes.ocariot.uaal_poc.data.model.Activity;
import br.edu.uepb.nutes.ocariot.uaal_poc.data.model.ActivityLevel;
import br.edu.uepb.nutes.ocariot.uaal_poc.data.repository.local.pref.AppPreferencesHelper;

/**
 * Created by Nutes on 30/01/2018.
 */

public class UaalAPI {
    private static String TAG = "UAAL_API";
    static public int uaal_state = AppConstants.STATUS_STOPPED;

    public static boolean initUaal(Context context) {
        stopUallService(context);
        UaalConfig.load(context); //Sync Preferences in UaalConfig util
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        UaalConfig.setmUAALUser(context, AppPreferencesHelper.getInstance(context)
                .getUserAccessOcariot().getSubject());
        UaalConfig.setmSettingRemoteType(context, AppConstants.REMOTE_TYPE_RESTAPI);
        UaalConfig.setmServerURL(context, "http://192.168.0.110:9000/ocariot_iot/uaal");
        UaalConfig.setmServerUSR(context, "admin");
        UaalConfig.setmServerPWD(context, "admin");
        UaalConfig.setmServerGCM(context, "102946860057");

        /*Activate REST API*/
        activateRestAPI(context);
        Log.i(TAG, "UAAL service initialized with config:\n" + UaalConfig.printUaalConfig());

        // TODO COLOCAR EM OUTRO LUGAR!!!
        /* Register UAAL Service status receiver */
        IntentFilter filter = new IntentFilter(AppConstants.ACTION_NOTIF_STARTED);
        filter.addAction(AppConstants.ACTION_NOTIF_STOPPED);
        context.registerReceiver(new ProgressReceiver(), filter);

        return true;
    }

    public static void loadUallConfig(Context context) {
        UaalConfig.load(context);
    }

    public static boolean activateRemoteAPI(Context context) {
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

    public static boolean updateGcmKeyRemoteAPI(Context context, String newGcmKey) {
        if (RAPIManager.checkPlayServices(context)) {
            RAPIManager.performRegistrationInThread(context, newGcmKey);
            return true;
        } else {
            Toast.makeText(context,
                    org.universAAL.android.R.string.uaal_warning_gplay, Toast.LENGTH_LONG)
                    .show();
            return false;
        }
    }

    public static boolean activateRestAPI(Context context) {
        if (RESTManager.checkPlayServices(context)) {
            RESTManager.performRegistrationInThread(context, null);
            return true;
        } else {
            Toast.makeText(context,
                    org.universAAL.android.R.string.uaal_warning_gplay, Toast.LENGTH_LONG)
                    .show();
            return false;
        }
    }

    public static boolean updateGcmKeyRestAPI(Context context, String newGcmKey) {
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

    public static boolean activateGatewayAPI(Context context) {
        /* TODO: Implment activation of Gateway API */
        return true;
    }

    public static boolean startUaalService(Context context) {
//        Intent startServiceIntent = new Intent(context.getApplicationContext(), MiddlewareService.class);
//        context.getApplicationContext().startService(start
        Log.d(TAG, "startUaalService()");
        Intent intent = new Intent(AppConstants.ACTION_SYS_START);
        context.sendBroadcast(intent);

        return true;
    }

    public static boolean stopUallService(Context context) {
        Intent intent = new Intent(AppConstants.ACTION_SYS_STOP);
        context.sendBroadcast(intent);
        Intent stopServiceIntent = new Intent(context.getApplicationContext(), MiddlewareService.class);
        return context.getApplicationContext().stopService(stopServiceIntent);
    }

    public static boolean restartUaalService(Context context) {
        if (stopUallService(context)) {// The service was running, restart it
            return startUaalService(context);
        }
        return false;
    }

    public static void publishHeartRate(Context context, int value) {
        Intent heartrateBroadcast = new Intent(
                "br.edu.uepb.nutes.ocariot.uaal_poc.ACTION_HEART_RATE_EVENT");
        heartrateBroadcast.addCategory(Intent.CATEGORY_DEFAULT);
        heartrateBroadcast.putExtra("heartrate_value", value);
        context.sendBroadcast(heartrateBroadcast);
    }


    public static void publishPhysicalActivity(Context context, Activity activity) {
        Log.w(TAG, "publishPhysicalActivity() " + activity.toString());
        Log.w(TAG, "publishPhysicalActivity() uAAL STATUS: " + UaalAPI.uaal_state);
        Intent physicalActivityBroadcast = new Intent("br.edu.uepb.nutes.ocariot.uaal_poc.ACTION_PHYSICAL_ACTIVITY_EVENT");
        physicalActivityBroadcast.addCategory(Intent.CATEGORY_DEFAULT);

        physicalActivityBroadcast.putExtra("user_id", activity.getUserId());
        physicalActivityBroadcast.putExtra("name", activity.getName());
        physicalActivityBroadcast.putExtra("start_time", activity.getStartTime());
        physicalActivityBroadcast.putExtra("end_time", activity.getEndTime());
        physicalActivityBroadcast.putExtra("duration", activity.getDuration());
        physicalActivityBroadcast.putExtra("calories", 666);
        physicalActivityBroadcast.putExtra("steps", String.valueOf(activity.getSteps()));
        physicalActivityBroadcast.putExtra("max_intensity", ActivityLevel.VERY_LEVEL);
        physicalActivityBroadcast.putExtra("max_intensity_duration", String.valueOf((1561)));

//        if (activity.getActivityLevel() != null) {
//            for (ActivityLevel activityLevel : activity.getActivityLevel()) {
//                if (activityLevel.getName().toLowerCase()
//                        .equals(ActivityLevel.SEDENTARY_LEVEL)) {
//                    physicalActivityBroadcast.putExtra("intensity_sedentary_duration",
//                            activityLevel.getMinutes());
//                } else if (activityLevel.getName().toLowerCase()
//                        .equals(ActivityLevel.FAIRLY_LEVEL)) {
//                    physicalActivityBroadcast.putExtra("intensity_fairly_duration",
//                            activityLevel.getMinutes());
//                } else if (activityLevel.getName().toLowerCase()
//                        .equals(ActivityLevel.LIGHTLY_LEVEL)) {
//                    physicalActivityBroadcast.putExtra("intensity_lightly_duration",
//                            activityLevel.getMinutes());
//                } else if (activityLevel.getName().toLowerCase()
//                        .equals(ActivityLevel.VERY_LEVEL)) {
//                    physicalActivityBroadcast.putExtra("intensity_very_duration",
//                            activityLevel.getMinutes());
//                }
//            }
//        }

        context.sendBroadcast(physicalActivityBroadcast);
    }

    /**
     * Broadcast receiver to get notified about progress in the progress bar by
     * the other services
     *
     * @author alfiva
     */
    public static class ProgressReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            switch (action) {
                case AppConstants.ACTION_NOTIF_STARTED:
                    Log.i(TAG, "Received broadcast UAAL SERVICE STARTED");
                    uaal_state = AppConstants.STATUS_STARTED;
                    break;
                case AppConstants.ACTION_NOTIF_STOPPED:
                    Log.e(TAG, "Received broadcast UAAL SERVICE STOPPED");
                    uaal_state = AppConstants.STATUS_STOPPED;
                    break;
                default:
                    return;
            }
        }
    }
}
