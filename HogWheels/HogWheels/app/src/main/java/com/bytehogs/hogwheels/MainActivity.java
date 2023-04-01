package com.bytehogs.hogwheels;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NavUtils;
import androidx.core.splashscreen.SplashScreen;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpResponse;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpStatus;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.StatusLine;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.HttpClient;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.methods.HttpGet;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String URL = "google.com";
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    Toolbar appToolBar;

    private NavigationView hamburgerNavigationView;

    private ConstraintLayout carView;
    private TextView noCarsAddedView;

    public static int numCars = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        SplashScreen.installSplashScreen(this);

        super.onCreate(savedInstanceState);

        startService(new Intent(this, HogwheelsService.class));

        setContentView(R.layout.activity_main);

        carView = findViewById(R.id.car_view);
        noCarsAddedView= findViewById(R.id.noCarsAddedView);


        hamburgerNavigationView = findViewById(R.id.navigationViewID);
        hamburgerNavigationView.setNavigationItemSelectedListener(this);

        appToolBar = findViewById(R.id.toolbarID);
        setSupportActionBar(appToolBar);

        drawerLayout = findViewById(R.id.main_activity);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);
        actionBarDrawerToggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        carView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(carView.getVisibility() == View.VISIBLE)
                {
                    Intent intent = new Intent(view.getContext(), CarInfoActivity.class);
                    startActivity(intent);
                }



            }
        });

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {

            int request_code = 1;

            Log.e("Error: ", "Bluetooth Permission not Granted");
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.BLUETOOTH_CONNECT }, request_code);

            return;
        }

        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {

            int request_code = 1;

            Log.e("Error: ", "Bluetooth Permission not Granted");
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.BLUETOOTH_SCAN }, request_code);

            return;
        }

        ImageButton plusButton = findViewById(R.id.add_new_car_button);

        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showCustomDialog();
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.connect_bluetooth_menu, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch(item.getItemId()){
            case R.id.nav_about:
                getSupportFragmentManager().beginTransaction().replace(R.id.main_activity, new AboutFragment()).commit();
                break;
            case R.id.nav_connect:

                Intent intent = new Intent(this, AddNewCar.class);
                startActivity(intent);

                break;
            case R.id.nav_edit:
                getSupportFragmentManager().beginTransaction().replace(R.id.main_activity, new EditFragment()).commit();
                break;



        }

        drawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }

    public void showCustomDialog()
    {
        final Dialog dialog = new Dialog(MainActivity.this);
        //We have added a title in the custom layout. So let's disable the default title.
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //The user will be able to cancel the dialog bu clicking anywhere outside the dialog.
        dialog.setCancelable(true);
        //Mention the name of the layout of your custom dialog.
        dialog.setContentView(R.layout.custom_dialog);

        //Initializing the views of the dialog.
        final EditText modelEditText = dialog.findViewById(R.id.edit_text_model);
        final EditText makeEditText = dialog.findViewById(R.id.edit_text_make);
        final EditText yearEditText = dialog.findViewById(R.id.edit_text_year);
        Button submitButton = dialog.findViewById(R.id.submit_button);



        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String model = modelEditText.getText().toString();
                String make = makeEditText.getText().toString();
                String year = yearEditText.getText().toString();
                carView.setVisibility(View.VISIBLE);
                noCarsAddedView.setVisibility(View.INVISIBLE);
                dialog.dismiss();
            }
        });


        dialog.show();
    }
}