package com.androapp.technician.Location_Services;

import static com.androapp.technician.Forms.LoginActivity.SHARED_PREFERENCES_NAME;
import static com.androapp.technician.Forms.LoginActivity.USER;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.androapp.technician.Activity.Home.ProMainActivity;
import com.androapp.technician.Helper.Constant;
import com.androapp.technician.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class TrackingService extends Service implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {
    private static final String TAG = "TrackingService";
    public static int TRACKING_INTERVAL = 50 * 1000;
    private static Location oldLocation = null;
    private Location currentLocation = null;
    private static TrackingService instance = null;
    private GoogleApiClient mGoogleApiClient;

    public static boolean isInstanceCreated() {
        return instance != null;
    }

    private static Thread locationUpload = null;
    private SharedPreferences sharedPreferences;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private boolean isBetterLocation(Location oldLocation, Location newLocation) {
        // If there is no old location, of course the new location is better.
        if (oldLocation == null) {
            return true;
        }

        // Check if new location is newer in time.
        boolean isNewer = newLocation.getTime() > oldLocation.getTime();

        // Check if new location more accurate. Accuracy is radius in meters, so less is better.
        boolean isMoreAccurate = newLocation.getAccuracy() < oldLocation.getAccuracy();
        if (isMoreAccurate && isNewer) {
            // More accurate and newer is always better.
            return true;
        } else if (isMoreAccurate && !isNewer) {
            // More accurate but not newer can lead to bad fix because of user movement.
            // Let us set a threshold for the maximum tolerance of time difference.
            long timeDifference = newLocation.getTime() - oldLocation.getTime();
            // If time difference is not greater then allowed threshold we accept it.
            return timeDifference > -TRACKING_INTERVAL;
        }

        return false;
    }

    private Location doWorkWithNewLocation(Location location) {

        if (isBetterLocation(oldLocation, location)) {
            // If location is better, do some user preview.
            return location;
        }

        return oldLocation;
    }


    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.d(TAG, "onLowMemory");
    }


    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        Log.d(TAG, "onTrimMemory");
    }

    /**
     * Show notifications for background services
     *
     * @param service
     */
    public static void showNotificationForService(Service service) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(service);
            NotificationManager mNotificationManager = (NotificationManager) service.getSystemService(Context.NOTIFICATION_SERVICE);

            String CHANNEL_ID = "_BackgroundService";
            CharSequence name = service.getString(R.string.app_name);
            String Description = service.getString(R.string.app_name);
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            mChannel.setDescription(Description);
            mChannel.enableLights(false);
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(false);
            mChannel.setShowBadge(false);
            mNotificationManager.createNotificationChannel(mChannel);

            notificationBuilder = new NotificationCompat.Builder(service, CHANNEL_ID);

            Intent intent = new Intent(service, ProMainActivity.class);

            PendingIntent pendingIntent = PendingIntent.getActivity(service, 0 /* Request code */, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            notificationBuilder.setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(service.getString(R.string.app_name))
                    .setContentText("Tracking Service started")
                    .setStyle(new NotificationCompat.BigTextStyle().bigText("Tracking Service started"))
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent);

            Notification notification = notificationBuilder.build();
            notification.defaults = 0;
            service.startForeground(105, notification);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: Tracking ");
        instance = this;
        showNotificationForService(this);

        try {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this).build();

            mGoogleApiClient.connect();


            if (locationUpload != null) {
                try {
                    locationUpload.interrupt();
                    locationUpload = null;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            locationUpload = new Thread(LocationRunnable, "LocationRunnable");
            locationUpload.setPriority(Thread.MAX_PRIORITY);
            locationUpload.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (isGpsEnabled() == false) {
            Log.d(TAG, "onCreate: GPS is Off");
        }

        sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);
    }

    private boolean isGpsEnabled() {
        LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
        return service.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public static void stopService() {
        if (instance != null)
            instance.stopSelf();
    }

    @Override
    public void onLocationChanged(Location location) {

        String logMessage = location.getProvider();

        logMessage += "\nUpcoming Location:" + location.getLatitude() + "," + location.getLongitude() + "," + location.getProvider();
        if (oldLocation == null) {
            oldLocation = location;
        } else {
            Location newLocation = doWorkWithNewLocation(location);
            if (newLocation.getLongitude() > 0 && newLocation.getLongitude() > 0) {
                logMessage += "\nLocation:" + newLocation.getLatitude() + "," + newLocation.getLongitude();
                oldLocation = location;
                currentLocation = newLocation;
            } else {
                logMessage += "\nLocation:" + location.getLatitude() + "," + location.getLongitude();
                oldLocation = location;
                currentLocation = location;
            }
        }
        Log.d(TAG, "onLocationChanged: " + logMessage);

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {


        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(TRACKING_INTERVAL);
        mLocationRequest.setFastestInterval(TRACKING_INTERVAL);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "onConnected: No Permission");
        } else {
            FusedLocationProviderClient providerClient = LocationServices.getFusedLocationProviderClient(this);
            providerClient.getLastLocation().addOnSuccessListener(location -> {
            });
            providerClient.requestLocationUpdates(mLocationRequest, new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    super.onLocationResult(locationResult);
                    Location location = locationResult.getLastLocation();
                    String logMessage = "LocationCallBack-> " + location.getProvider();
                    if (oldLocation == null) {
                        oldLocation = location;
                    } else {
                        Location newLocation = doWorkWithNewLocation(location);
                        if (newLocation.getLongitude() > 0 && newLocation.getLongitude() > 0) {
                            logMessage += "\nLocation:" + newLocation.getLatitude() + "," + newLocation.getLongitude();
                            oldLocation = location;
                            currentLocation = newLocation;
                        } else {
                            logMessage += "\nLocation:" + location.getLatitude() + "," + location.getLongitude();
                            oldLocation = location;
                            currentLocation = location;
                        }
                    }
                    Log.d(TAG, "onLocationResult: " + logMessage);


                }
            }, null);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspended()->" + i);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed: " + connectionResult.getErrorMessage());
    }

    private Runnable LocationRunnable = new Runnable() {
        @Override
        public void run() {
            SharedPreferences preferences = getSharedPreferences("Settings", MODE_PRIVATE);
            String status = preferences.getString(Constant.KEY_STATUS, "");
            while (status.equalsIgnoreCase("start")) {
                try {
                    Thread.sleep(TRACKING_INTERVAL);
                    if (isGpsEnabled() == false) {
                        Log.d(TAG, "onCreate: GPS is Off");
                    } else {
                        Log.d(TAG, "CurrentLocation: " + currentLocation);
                        if (currentLocation != null) {

                            StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.updateLat,
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            Log.d("update_lat_lng", "onResponse: " + response);
                                            try {
                                                JSONObject jsonObject = new JSONObject(response);
                                                String message = jsonObject.getString("message");
                                                String code = jsonObject.getString("code");
                                                if (code.equals("200")) {
                                                    Log.d(TAG, "onResponse: " + "LatLng Updated!");
                                                } else {
                                                    Log.d(TAG, "onResponse Fail: " + "LatLng Not Updated!");
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    error.printStackTrace();
                                }
                            }) {

                                @Override
                                protected Map<String, String> getParams() throws AuthFailureError {
                                    HashMap<String, String> params = new HashMap();
                                    params.put("pro_id", sharedPreferences.getString(USER, ""));
                                    params.put("latitude", String.valueOf(currentLocation.getLatitude()));
                                    params.put("longitude", String.valueOf(currentLocation.getLongitude()));
                                    Log.d("params", "getParams: " + params);
                                    return params;
                                }

                                @Override
                                public Map<String, String> getHeaders() throws AuthFailureError {
                                    HashMap<String, String> header = new HashMap<>();
                                    header.put("token", Constant.Token);
                                    return header;
                                }

                            };
                            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                            RetryPolicy retryPolicy = new DefaultRetryPolicy(3000,
                                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                            stringRequest.setRetryPolicy(retryPolicy);
                            requestQueue.add(stringRequest);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    };


    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        stopSelf();
    }


    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        instance = null;
        try {
            if (mGoogleApiClient.isConnected()) {
                LocationServices.FusedLocationApi
                        .removeLocationUpdates(mGoogleApiClient, this);
                mGoogleApiClient.disconnect();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (locationUpload != null) {
                locationUpload.interrupt();
                locationUpload = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            stopForeground(true);
        }
        SharedPreferences preferences = getSharedPreferences("Settings", MODE_PRIVATE);
        String status = preferences.getString(Constant.KEY_STATUS, "");
        Log.d("TAG", "onDestroy: " + status);
        if (status.equalsIgnoreCase("start")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Intent intent = new Intent(this, TrackingService.class);
                startForegroundService(intent);
            } else {
                Intent intent = new Intent(this, TrackingService.class);
                startService(intent);
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub
        Log.d(TAG, "|onStartCommand|");
        return START_STICKY;
    }

}