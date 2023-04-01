package com.bytehogs.hogwheels;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import java.lang.reflect.Method;
import java.security.Provider;
import java.util.ArrayList;
import java.util.Set;

public class HogwheelsService extends Service {

    ArrayList<String> bluetoothMacAddressesConnected;

    private final String macAddress = "DC:0D:30:F6:40:E5";
    //private final String macAddress = "C8:7B:23:51:05:CA";


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true)
                {
                    Log.e("Service: ", "Service is running...");
                    try
                    {
                        //check if bluetooth is connected every 5 seconds
                        BluetoothManager bluetoothManager = null;
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                            bluetoothManager = (BluetoothManager) getApplicationContext().getSystemService(Context.BLUETOOTH_SERVICE);
                        }
                        if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {

                            int request_code = 1;

                            Log.e("Error: ", "Bluetooth Permission not Granted");
                            return;
                        }


                        //Log.d("Bluetooth : ", "Permission Granted");

                        if(bluetoothManager == null)
                        {
                            //Log.d("Error: ", "Bluetooth Manager is Null");
                        }
                        else
                        {
                           // Log.d("Bluetooth: ", "Bluetooth Managaer is Not Null");
                            Set<BluetoothDevice> pairedDevices = BluetoothAdapter.getDefaultAdapter().getBondedDevices();

                            bluetoothMacAddressesConnected = new ArrayList<String>();

                            prefs.edit().putBoolean("OBD_Connected", false).apply();

                            if (pairedDevices.size() > 0) {
                                for (BluetoothDevice d: pairedDevices) {
                                    String deviceName = d.getName();
                                    String currentMacAddress = d.getAddress();
                                    //Log.d("Bluetooth", "paired device: " + deviceName + " at " + currentMacAddress);
                                    // do what you need/want this these list items
                                    bluetoothMacAddressesConnected.add(currentMacAddress);

                                    //if (isConnected(d) && macAddress != null && macAddress.equals(macAddress))
                                    if (macAddress != null && currentMacAddress.equals(macAddress))
                                    {
                                        //Log.d("Bluetooth : ", "Device is Connected and Paired");
                                        prefs.edit().putBoolean("OBD_Connected", true).apply();


                                    }
                                    else
                                    {
                                        Log.d("Bluetooth : ", "Device is not both Connected and Paired");

                                    }

                                }
                            }


                        }

                        Thread.sleep(5000);
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }

                }
            }


        }).start();


        return START_STICKY;

    }



    public boolean isConnected(BluetoothDevice device) {
        try {
            Method m = device.getClass().getMethod("isConnected", (Class[]) null);
            boolean connected = (boolean) m.invoke(device, (Object[]) null);
            return connected;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }

    }


}
