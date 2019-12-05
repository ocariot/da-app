package br.edu.uepb.nutes.ocariot.service;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattService;
import android.content.Context;

import androidx.annotation.NonNull;

import java.util.UUID;

import br.edu.uepb.nutes.ocariot.utils.DateUtils;
import no.nordicsemi.android.ble.data.Data;
import timber.log.Timber;

public class HRManager extends BluetoothManager {
    public static final UUID HR_SERVICE_UUID = UUID.fromString("0000180D-0000-1000-8000-00805f9b34fb");

    private final UUID heartRateCharacteristicUUID = UUID.fromString("00002A37-0000-1000-8000-00805f9b34fb");

    @SuppressLint("StaticFieldLeak")
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
            mCharacteristic = service.getCharacteristic(heartRateCharacteristicUUID);
        }
    }

    private BluetoothManagerCallback bleManagerCallbacks = new BluetoothManagerCallback() {
        @Override
        public void measurementReceiver(@NonNull BluetoothDevice device, @NonNull Data data) {
            int offset = 0;
            final int flags = data.getIntValue(Data.FORMAT_UINT8, offset++);

            // false 	Heart Rate Value Format is set to UINT8. Units: beats per minute (bpm)
            // true 	Heart Rate Value Format is set to UINT16. Units: beats per minute (bpm)
            // 1 bit
            byte heartRateValueFormat = 0x01;
            final boolean value16bit = (flags & heartRateValueFormat) > 0;

            // heart rate value is 8 or 16 bit long
            int heartRate = data.getIntValue(value16bit ? Data.FORMAT_UINT16 : Data.FORMAT_UINT8, offset++); // bits per minute
            mHeartRateCallback.onMeasurementReceived(device, heartRate, DateUtils.getCurrentDatetimeUTC());
        }

        @Override
        public void onDeviceConnecting(@NonNull BluetoothDevice device) {
            Timber.d("onDeviceConnecting() - %s", device.getName());
        }

        @Override
        public void onDeviceConnected(@NonNull BluetoothDevice device) {
            Timber.d("onDeviceConnected() - %s", device.getName());
            mHeartRateCallback.onConnected(device);
        }

        @Override
        public void onDeviceDisconnecting(@NonNull BluetoothDevice device) {
            Timber.d("onDeviceDisconnecting() - %s", device.getName());
        }

        @Override
        public void onDeviceDisconnected(@NonNull BluetoothDevice device) {
            Timber.d("onDeviceDisconnected() - %s", device.getName());
            mHeartRateCallback.onDisconnected(device);
        }

        @Override
        public void onLinkLossOccurred(@NonNull BluetoothDevice device) {
            Timber.d("onLinkLossOccurred() - %s", device.getName());
        }

        @Override
        public void onServicesDiscovered(@NonNull BluetoothDevice device, boolean optionalServicesFound) {
            Timber.d("Services Discovered from %s", device.getName());
        }

        @Override
        public void onDeviceReady(@NonNull BluetoothDevice device) {
            Timber.d("onDeviceReady() - %s", device.getName());
        }

        @Override
        public void onBondingRequired(@NonNull BluetoothDevice device) {
            Timber.d("onBondingRequired() - %s", device.getName());
        }

        @Override
        public void onBonded(@NonNull BluetoothDevice device) {
            Timber.d("onBonded() - %s", device.getName());
        }

        @Override
        public void onBondingFailed(@NonNull BluetoothDevice device) {
            Timber.d("onBondingFailed() - %s", device.getName());
        }

        @Override
        public void onError(@NonNull BluetoothDevice device, @NonNull String message, int errorCode) {
            Timber.d("Error from %s - %s", device.getName(), message);
        }

        @Override
        public void onDeviceNotSupported(@NonNull BluetoothDevice device) {
            Timber.d("onDeviceNotSupported() - %s", device.getName());
        }
    };
}
