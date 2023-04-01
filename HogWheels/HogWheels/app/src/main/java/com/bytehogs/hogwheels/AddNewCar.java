package com.bytehogs.hogwheels;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;


public class AddNewCar extends AppCompatActivity {

    private boolean OBDAdapterConnected = false;

    private ArrayList<String> bluetoothMacAddressesConnected;

    private ConstraintLayout connectDeviceLayout;
    private ConstraintLayout deviceConnectedLayout;
    private ConstraintLayout deviceRetryConnectLayout;

    private SharedPreferences sharedPreferences;


    //number of times this activity has been viewed
    private int timesViewed = 0;

    private SharedPreferences.OnSharedPreferenceChangeListener sharedPreferenceChangeListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_car);

        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() >= 0) {
            for (BluetoothDevice device : pairedDevices) {

                ConnectThread mConnectedThread = new ConnectThread(device, this, mBluetoothAdapter);
                mConnectedThread.start();

            }
        }

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        OBDAdapterConnected = sharedPreferences.getBoolean("OBD_Connected", false);

        TextView exitText = findViewById(R.id.exit_text);
        TextView scanForDeviceTextView = findViewById(R.id.scan_for_device);

        TextView exitTextRetry = findViewById(R.id.exit_text_retry_layout);

        TextView editTextRetry = findViewById(R.id.retry_text);

        connectDeviceLayout = findViewById(R.id.layout_scan_for_device);
        deviceConnectedLayout = findViewById(R.id.layout_device_connected_successfully);
        deviceRetryConnectLayout = findViewById(R.id.layout_retry_connect);

        exitTextRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();

            }
        });

        scanForDeviceTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                openBluetoothMenu();
            }
        });

        editTextRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                openBluetoothMenu();

            }
        });


        exitText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });


        sharedPreferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                // your stuff
                Log.d("Key : ", key);

                OBDAdapterConnected = sharedPreferences.getBoolean("OBD_Connected", false);

                Log.d("Is OBD_Connected?", String.valueOf(OBDAdapterConnected));

                if (OBDAdapterConnected) {
                    deviceConnectedLayout.setVisibility(View.VISIBLE);
                    connectDeviceLayout.setVisibility(View.INVISIBLE);
                    deviceRetryConnectLayout.setVisibility(View.INVISIBLE);

                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    }, 1000);
                } else if (timesViewed > 0) {
                    deviceConnectedLayout.setVisibility(View.INVISIBLE);
                    connectDeviceLayout.setVisibility(View.INVISIBLE);
                    deviceRetryConnectLayout.setVisibility(View.VISIBLE);
                }

            }
        };


    }


    public void openBluetoothMenu() {
        startActivity(new Intent().setAction(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS));

    }

    @Override
    public void onResume() {

        super.onResume();

        sharedPreferences.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
        // put your code here...



        timesViewed++;

        Log.d("Activity: ", "Resumed");

    }



}