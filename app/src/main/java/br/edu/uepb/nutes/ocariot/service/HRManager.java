package br.edu.uepb.nutes.ocariot.service;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.UUID;

import br.edu.uepb.nutes.ocariot.utils.DateUtils;
import no.nordicsemi.android.ble.data.Data;

public class HRManager extends BluetoothManager {
    private final String LOG_TAG = "HRManager";

    public static final UUID HR_SERVICE_UUID = UUID.fromString("0000180D-0000-1000-8000-00805f9b34fb");

    private final UUID BODY_SENSOR_LOCATION_CHARACTERISTIC_UUID = UUID.fromString("00002A38-0000-1000-8000-00805f9b34fb");
    private final UUID HEART_RATE_MEASUREMENT_CHARACTERISTIC_UUID = UUID.fromString("00002A37-0000-1000-8000-00805f9b34fb");
    private final byte HEART_RATE_VALUE_FORMAT = 0x01; // 1 bit

    private static HRManager managerInstance = null;
    private HRManagerCallback mHeartRateCallback;

    private HRManager(final Context context) {
        super(context);
        super.setGattCallbacks(bleManagerCallbacks);
    }

    public void setHeartRateCallback(HRManagerCallback mHeartRateCallback) {
        this.mHeartRateCallback = mHeartRateCallback;
    }

    /**
     * Singleton implementation of HRSManager class.
     */
    public static synchronized HRManager getInstance(final Context context) {
        if (managerInstance == null) {
            managerInstance = new HRManager(context);
        }
        return managerInstance;
    }

    @Override
    void initializeCharacteristic() {
        setNotificationCallback(super.mCharacteristic).with(super.dataReceivedCallback);
        enableNotifications(super.mCharacteristic).enqueue();
    }

    @Override
    void setCharacteristicWrite(BluetoothGatt gatt) {
        final BluetoothGattService service = gatt.getService(HR_SERVICE_UUID);
        if (service != null) {
            mCharacteristic = service.getCharacteristic(HEART_RATE_MEASUREMENT_CHARACTERISTIC_UUID);
        }
    }

    private BluetoothManagerCallback bleManagerCallbacks = new BluetoothManagerCallback() {
        @Override
        public void measurementReceiver(@NonNull BluetoothDevice device, @NonNull Data data) {
            int offset = 0;
            final int flags = data.getIntValue(Data.FORMAT_UINT8, offset++);

            // false 	Heart Rate Value Format is set to UINT8. Units: beats per minute (bpm)
            // true 	Heart Rate Value Format is set to UINT16. Units: beats per minute (bpm)
            final boolean value16bit = (flags & HEART_RATE_VALUE_FORMAT) > 0;

            // heart rate value is 8 or 16 bit long
            int heartRate = data.getIntValue(value16bit ? Data.FORMAT_UINT16 : Data.FORMAT_UINT8, offset++); // bits per minute

            mHeartRateCallback.onMeasurementReceived(device, heartRate, DateUtils.getCurrentDatetimeUTC());
        }

        @Override
        public void onDeviceConnecting(@NonNull BluetoothDevice device) {
            Log.w(LOG_TAG, "Connecting to " + device.getName());
        }

        @Override
        public void onDeviceConnected(@NonNull BluetoothDevice device) {
            Log.w(LOG_TAG, "Connected to " + device.getName());
            mHeartRateCallback.onConnected(device);
        }

        @Override
        public void onDeviceDisconnecting(@NonNull BluetoothDevice device) {
            Log.w(LOG_TAG, "Disconnecting from " + device.getName());
        }

        @Override
        public void onDeviceDisconnected(@NonNull BluetoothDevice device) {
            Log.w(LOG_TAG, "Disconnected from " + device.getName());
            mHeartRateCallback.onDisconnected(device);
        }

        @Override
        public void onLinkLossOccurred(@NonNull BluetoothDevice device) {

        }

        @Override
        public void onServicesDiscovered(@NonNull BluetoothDevice device, boolean optionalServicesFound) {
            Log.w(LOG_TAG, "Services Discovered from " + device.getName());
        }

        @Override
        public void onDeviceReady(@NonNull BluetoothDevice device) {

        }

        @Override
        public void onBondingRequired(@NonNull BluetoothDevice device) {
            Log.w(LOG_TAG, "onBondingRequired > " + device.getName());
        }

        @Override
        public void onBonded(@NonNull BluetoothDevice device) {
            Log.w(LOG_TAG, "onBonded" + device.getName());
        }

        @Override
        public void onBondingFailed(@NonNull BluetoothDevice device) {
            Log.w(LOG_TAG, "onBondingFailed" + device.getName());
        }

        @Override
        public void onError(@NonNull BluetoothDevice device, @NonNull String message, int errorCode) {
            Log.w(LOG_TAG, "Error from " + device.getName() + " - " + message);
        }

        @Override
        public void onDeviceNotSupported(@NonNull BluetoothDevice device) {

        }
    };
}
