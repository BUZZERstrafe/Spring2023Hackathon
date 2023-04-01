package com.bytehogs.hogwheels;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class ConnectThread extends Thread {
    private BluetoothSocket mmSocket;
    private final BluetoothDevice mmDevice;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    private BluetoothAdapter bluetoothAdapter;

    private Context context;

    public ConnectThread(BluetoothDevice device, Context context, BluetoothAdapter bluetoothAdapter) {
        BluetoothSocket tmp = null;
        mmDevice = device;
        this.context = context;
        this.bluetoothAdapter = bluetoothAdapter;
        try {
            if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
        } catch (IOException ignored) {
        }
        mmSocket = tmp;
    }

    public void run() {
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        bluetoothAdapter.cancelDiscovery();
        try {
            mmSocket.connect();
            Log.d("Thread: ", "Thread Connected");

            try {
                InputStream tmpIn = null;
                OutputStream tmpOut = null;
                try {
                    tmpIn = mmSocket.getInputStream();
                    tmpOut = mmSocket.getOutputStream();

                    byte[] byteArrray1 = {65, 84, 83, 80, 48, 92, 110};
                    byte[] byteArrray2 = {65, 84, 83, 84, 56, 48, 92, 110};
                    byte[] byteArrray3 = {65, 84, 83, 84, 56, 48, 92, 110};

                    tmpOut.write(byteArrray1);
                    tmpOut.write(byteArrray2);
                    tmpOut.write(byteArrray3);

                    Log.d("Output Data: ", ""+tmpIn.read());

                } catch (IOException ignored) { }

            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (IOException connectException) {
            try {
                mmSocket.close();
            } catch (IOException closeException) { }
            return;
        }
    }
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) { }
    }
}
