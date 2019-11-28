package br.edu.uepb.nutes.ocariot.service;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;

import androidx.annotation.NonNull;

import no.nordicsemi.android.ble.BleManager;
import no.nordicsemi.android.ble.callback.DataReceivedCallback;

public abstract class BluetoothManager extends BleManager<BluetoothManagerCallback> {
    BluetoothGattCharacteristic mCharacteristic;

    BluetoothManager(@NonNull Context context) {
        super(context);
    }

    public void connectDevice(BluetoothDevice device) {
        super.connect(device)
                .retry(10, 100)
                .useAutoConnect(false)
                .enqueue();
    }

    @NonNull
    @Override
    protected BleManagerGattCallback getGattCallback() {
        return mGattCallback;
    }

    DataReceivedCallback dataReceivedCallback = (device, data) -> mCallbacks.measurementReceiver(device, data);

    public void disconnectDevice() {
        disconnect();
    }

    abstract void initializeCharacteristic();

    abstract void setCharacteristicWrite(BluetoothGatt gatt);

    /**
     * BluetoothGatt callbacks object.
     */
    private boolean mSupported;

    private final BleManagerGattCallback mGattCallback = new BleManagerGattCallback() {

        @Override
        protected void initialize() {
            initializeCharacteristic();
        }

        @Override
        public boolean isRequiredServiceSupported(@NonNull final BluetoothGatt gatt) {
            setCharacteristicWrite(gatt);
            boolean writeRequest = false;
            if (mCharacteristic != null) {
                final int rxProperties = mCharacteristic.getProperties();
                writeRequest = (rxProperties & BluetoothGattCharacteristic.PROPERTY_WRITE) > 0;
            }

            mSupported = mCharacteristic != null && writeRequest;
            return true;
        }

        @Override
        protected void onDeviceDisconnected() {
        }
    };
}