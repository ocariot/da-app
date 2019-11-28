package br.edu.uepb.nutes.ocariot.service;

import android.bluetooth.BluetoothDevice;

import androidx.annotation.NonNull;

import no.nordicsemi.android.ble.BleManagerCallbacks;
import no.nordicsemi.android.ble.data.Data;

public interface BluetoothManagerCallback extends BleManagerCallbacks {
    void measurementReceiver(@NonNull final BluetoothDevice device, @NonNull final Data data);
}