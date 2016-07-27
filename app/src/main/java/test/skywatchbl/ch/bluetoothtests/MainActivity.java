package test.skywatchbl.ch.bluetoothtests;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String DEVICE_ADDRESS = "00:A0:50:12:1B:0D";
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothGatt bluetoothGatt;

    private final BluetoothAdapter.LeScanCallback bluetoothLeScanCallback = new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            if (DEVICE_ADDRESS.equalsIgnoreCase(device.getAddress())) {
                connectToDevice(device);
                bluetoothAdapter.stopLeScan(bluetoothLeScanCallback);
            }
        }
    };

    private final BluetoothGattCallback bluetoothGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            Log.d(TAG, "Connection state changed to status " + status + ", state " + newState);
            if (BluetoothGatt.GATT_SUCCESS == status && BluetoothGatt.STATE_CONNECTED == newState) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        bluetoothGatt.discoverServices();
                    }
                });
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            Log.d(TAG, "Services discovered");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();

        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        bluetoothAdapter.startLeScan(bluetoothLeScanCallback);
    }

    private void connectToDevice(final BluetoothDevice device) {
        Log.d(TAG, "Connecting to " + device.getAddress());
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                bluetoothGatt = device.connectGatt(MainActivity.this, false, bluetoothGattCallback);
            }
        });
    }
}
