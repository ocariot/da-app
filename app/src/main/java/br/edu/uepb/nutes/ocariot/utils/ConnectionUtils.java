package br.edu.uepb.nutes.ocariot.utils;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Provides routines for checking connection with internet and bluetooth.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 0.1
 * @copyright Copyright (c) 2017, NUTES/UEPB
 */
public class ConnectionUtils {
    /**
     * Checks if the device supports bluetooth.
     *
     * @return true for device supports bluetooth or false otherwise.
     */
    public static boolean isSupportedBluetooth() {
        return BluetoothAdapter.getDefaultAdapter() != null;
    }

    /**
     * Checks if bluetooth is enabled.
     *
     * @return true to enabled or false otherwise.
     */
    public static boolean isBluetoothAvailable() {
        return BluetoothAdapter.getDefaultAdapter() != null &&
                BluetoothAdapter.getDefaultAdapter().isEnabled();
    }

    /**
     * Checks if the device has an internet connection.
     *
     * @param context
     * @return boolean
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnectedOrConnecting());
    }
}
