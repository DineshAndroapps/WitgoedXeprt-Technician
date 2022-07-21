package com.witgoedxpert.technician;

import static com.witgoedxpert.technician.Forms.LoginActivity.SHARED_PREFERENCES_NAME;
import static com.witgoedxpert.technician.Forms.LoginActivity.USER_ID;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.witgoedxpert.technician.Activity.Home.MainActivity;
import com.witgoedxpert.technician.Activity.Home.SchedulePage_A;
import com.witgoedxpert.technician.Forms.LoginActivity;
import com.witgoedxpert.technician.Location_Services.GpsUtils;

import cat.ereza.customactivityoncrash.config.CaocConfig;

public class SplashScreen extends AppCompatActivity {
    private final int SPLASH_DISPLAY_LENGTH = 2000;
    SharedPreferences sharedPreferences;
    String user_id;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CaocConfig.Builder.create()
                .backgroundMode(CaocConfig.BACKGROUND_MODE_SILENT) //default: CaocConfig.BACKGROUND_MODE_SHOW_CUSTOM
                .enabled(true) //default: true
                .showErrorDetails(true) //default: true
                .showRestartButton(true) //default: true
                .logErrorOnRestart(true) //default: true
                .trackActivities(false) //default: false
                .minTimeBetweenCrashesMs(2000) //default: 3000
                .errorDrawable(R.drawable.app_icon) //default: bug image
                .restartActivity(SplashScreen.class) //default: null (your app's launch activity)
                .errorActivity(null) //default: null (default error activity)
                .eventListener(null) //default: null
                .apply();
        setContentView(R.layout.activity_splash_screen);

        sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        user_id = sharedPreferences.getString(USER_ID, "");


       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            check_permissions();
        } else {
            HalnderSplashScreen();
        }*/
        check_permissions();

    }




    @Override
    protected void onStart() {
        super.onStart();
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        enable_permission();
        Log.d("Variables.TAG", "Permission result" + grantResults);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public boolean check_permissions() {


        String[] PERMISSIONS = {
                Manifest.permission.CALL_PHONE,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
        };

        if (!hasPermissions(SplashScreen.this, PERMISSIONS)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(PERMISSIONS, 202);
            }
           // PermissionDialog(PERMISSIONS);
        } else {
            enable_permission();
            return true;
        }

        return false;
    }

    private void PermissionDialog(String[] PERMISSIONS) {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(this);

        builder.setMessage("Please allow the Location and Media Permissions." +
                "\nThis app collects location data to enable " +
                "[\"to measure user distance to destination\"], " +
                "[\"user can punchin in given range\"]," +
                " & [\" check The user in range to the given distance\"]" +
                " even when the app is closed or not in use.” ")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            requestPermissions(PERMISSIONS, 202);
                        }
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        finishAffinity();
                    }
                });

        AlertDialog alert = builder.create();
        alert.setTitle(R.string.app_name);
        alert.show();


    }


    public boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (permission != Manifest.permission.CALL_PHONE &&
                        permission != Manifest.permission.RECORD_AUDIO &&
                        ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;

    }


    private void enable_permission() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean GpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!GpsStatus) {

            new GpsUtils(this).turnGPSOn(isGPSEnable -> {

                enable_permission();

            });
        } else if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            //Bind_Location_service();
            HalnderSplashScreen();

        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                        },
                        123);
            }
        }


    }

    private void HalnderSplashScreen() {

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!user_id.equals("")) {
                    Intent i = new Intent(getApplicationContext(), SchedulePage_A.class);//SchedulePage_A
                    finish();
                    startActivity(i);
                } else {
                    Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                    finish();
                    startActivity(i);
                }

            }
        }, 2000);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        enable_permission();
    }

}