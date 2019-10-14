package br.edu.uepb.nutes.ocariot.service;

import android.bluetooth.BluetoothDevice;
import android.support.annotation.NonNull;

public interface DeviceStatusCallback {
    void onConnected(@NonNull final BluetoothDevice device);

    void onDisconnected(@NonNull final BluetoothDevice device);
}