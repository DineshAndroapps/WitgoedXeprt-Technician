package com.androapp.technician.Location_Services;

import static com.androapp.technician.Forms.LoginActivity.SHARED_PREFERENCES_NAME;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;


public class UpdateLocationService extends Service {
    private static final String TAG = UpdateLocationService.class.getSimpleName();
    public Context context;
    private SharedPreferences sharedPreferences;
    private LocationTrack mLocationTrack;
    private double currentLatitude, currentLongitude;

    private Handler handler = new Handler();
    private Runnable runnable;
    private int delay = 50000;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);

        mLocationTrack = new LocationTrack(UpdateLocationService.this);
        if (mLocationTrack.canGetLocation()) {
            currentLatitude = mLocationTrack.getLatitude();
            currentLongitude = mLocationTrack.getLongitude();

        } else{
            mLocationTrack.showSettingsAlert();
        }

        handler.postDelayed(runnable = new Runnable() {
            public void run() {
                Log.d(TAG, "run: " + "API Call:");
                handler.postDelayed(runnable, delay);
                updateLatLng();
            }
        }, delay);

//        if (intent != null) {
//            if(intent.getAction().equals("configEPG")) {
//                String token = intent.getStringExtra("token");
//                String timezone = intent.getStringExtra("timezone");
//                String filter = intent.getStringExtra("filter");
//                configEPG(token, timezone, filter);
//            }
//        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void updateLatLng() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("AppService", "onDestroy: ");
    }
}