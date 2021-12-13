package com.witgoedxpert.technician.Location_Services;

import static com.witgoedxpert.technician.Forms.LoginActivity.NAME;
import static com.witgoedxpert.technician.Forms.LoginActivity.SHARED_PREFERENCES_NAME;
import static com.witgoedxpert.technician.Forms.LoginActivity.USER_ID;
import static com.witgoedxpert.technician.Forms.LoginActivity.USER_LAT;
import static com.witgoedxpert.technician.Forms.LoginActivity.USER_LONG;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.Ringtone;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
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
import com.google.android.gms.maps.model.LatLng;

import com.witgoedxpert.technician.Forms.Functions;
import com.witgoedxpert.technician.Forms.LoginActivity;
import com.witgoedxpert.technician.Forms.Register_A;
import com.witgoedxpert.technician.Helper.Constant;
import com.witgoedxpert.technician.Helper.Methods;
import com.witgoedxpert.technician.Helper.SharePref;
import com.witgoedxpert.technician.Helper.Variables;
import com.witgoedxpert.technician.R;
import com.witgoedxpert.technician.SplashScreen;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class GetLocation_Service extends Service implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private final IBinder binder = new LocalBinder();
    private Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    // Location updates intervals in sec
    private int UPDATE_INTERVAL = 3000;
    private int FATEST_INTERVAL = 3000;
    private int DISPLACEMENT = 0;
    SharedPreferences.Editor editor;
    SharedPreferences sharedPreferences;
    public double latitude;
    public double longitude;
    LatLng latLng;
    Context context;
    String str_userid;
    NotificationManager notificationManager;
    public static boolean ismapopen;

    int app_icon = R.mipmap.ic_launcher;

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public class LocalBinder extends Binder {
        public GetLocation_Service getService() {
            // Return this instance of LocalService so clients can call public methods
            return GetLocation_Service.this;
        }
    }

    final class Mythreadclass implements Runnable {

        @Override
        public void run() {
            mLocationRequest.setInterval(UPDATE_INTERVAL);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            mLocationRequest.setSmallestDisplacement(DISPLACEMENT);

        }
    }

    @Override
    public void onCreate() {
        Log.d("GetLocationService", "running");
        buildGoogleApiClient();
        createLocationRequest();
        mGoogleApiClient.connect();
        context = getApplicationContext();


    }

    private LocationService_callback serviceCallbacks;

    public void setCallbacks(LocationService_callback callbacks) {
        serviceCallbacks = callbacks;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
            startLocationUpdates();
        }

        Thread thread = new Thread(new Mythreadclass());
        thread.start();

        if (Build.VERSION.SDK_INT > 25) {
//           startRunningInForeground();
        }

        return Service.START_STICKY;
    }


    protected synchronized void buildGoogleApiClient() {
        Log.d("pky", "in  build google client");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    protected void createLocationRequest() {
        Log.d("pky", "in createlocationRequest");
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    private FusedLocationProviderClient mFusedLocationClient;
    LocationCallback locationCallback;

    public void startLocationUpdates() {
        mGoogleApiClient.connect();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        mLastLocation = location;
                        latitude = mLastLocation.getLatitude();
                        longitude = mLastLocation.getLongitude();

                        latLng = new LatLng(latitude, longitude);
                        if (serviceCallbacks != null)
                            serviceCallbacks.onDataReceived(new LatLng(latitude, longitude));

                        Functions.LOGE("GetLocation", "longitude" + longitude + "-- latitude" + latitude);
                        sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);
                        str_userid = sharedPreferences.getString(USER_ID, "");
                        SendLatLong(latitude,longitude,str_userid);
                        Constant.LATITUDE_SEVER = String.valueOf(latitude);
                        Constant.LONGITUDE_SERVER = String.valueOf(longitude);

                        sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
                        editor = sharedPreferences.edit();
                        editor.putString(USER_LAT, String.valueOf(longitude));
                        editor.putString(USER_LONG, String.valueOf(latitude));
                        editor.apply();

                        if (!ismapopen)
//                             CallApi_Of_add_LOcation(latitude,longitude);
                            upload_to_sharedPreference(new LatLng(latitude, longitude));
                    }
                }
            }
        };

        mFusedLocationClient.requestLocationUpdates(mLocationRequest, locationCallback
                , Looper.myLooper());

    }

    private void SendLatLong(double latitude, double longitude, String str_userid) {



        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.Send_latlong,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("context_location", "onResponse: " + response);

                        String code, message, id, user_id;

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            message= jsonObject.getString("message");
                            if (jsonObject.getString("code").equals("200")) {


                            } else {

                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("", "onErrorResponse: " );
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("mechanic_id", str_userid);
                params.put("lat", String.valueOf(latitude));
                params.put("long", String.valueOf(longitude));
                Log.d("params_location", "getParams: " + params);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("token", Constant.Token);
                return headers;
            }
        };


        Volley.newRequestQueue(this).add(stringRequest);

    }


    protected void stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(locationCallback);
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // on connection failed this method will call
    }

    @Override
    public void onConnected(Bundle arg0) {
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();
    }


    public void upload_to_sharedPreference(LatLng latLng) {

        Log.d("", latLng.toString());

        double lat = (latLng.latitude);
        double lon = (latLng.longitude);

        if (Variables.address_id == null || Variables.address_id.equals("")) {
            Variables.latitude = lat;
            Variables.longitude = lon;

            SharedPreferences.Editor editor = SharePref.getInstance().edit();
            if (editor != null) {
                editor.putString(SharePref.lat, Double.toString(lat));
                editor.putString(SharePref.longe, Double.toString(lon));
                editor.commit();
            }

        }


    }

    @Override
    public void onDestroy() {
        if (mGoogleApiClient.isConnected()) {
            stopLocationUpdates();
            mGoogleApiClient.disconnect();
        }
        stopService(new Intent(getApplicationContext(), GetLocation_Service.class));
//        stopForeground(true);
        stopSelf();
        super.onDestroy();
    }


    boolean isNotificationon;


    public static String channelId = "channel-01";
    public static Ringtone r;

    public void NewshowNotification(Context context, String title, String body, Intent intent) {
        NotificationCompat.Builder mBuilder;
        Uri alarmSound = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.noo);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        int notificationId = 1;
        String channelName = "Channel Name";
        final AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM);
//
        audioManager.setStreamVolume(AudioManager.STREAM_ALARM, maxVolume, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);

        mBuilder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(app_icon)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(alarmSound, AudioManager.STREAM_ALARM)
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            if (alarmSound != null) {
                // Changing Default mode of notification_old
//                mBuilder.setDefaults(android.app.Notification.DEFAULT_VIBRATE);

                // Creating an Audio Attribute
                AudioAttributes audioAttributes = new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setFlags(Notification.FLAG_INSISTENT)
                        .build();


//                if(r != null && !r.isPlaying())
//                    r.play();
//
//                if(r == null)
//                {
//                    r = RingtoneManager.getRingtone(getApplicationContext(), alarmSound);
//                    r.setStreamType(AudioManager.STREAM_ALARM);
//                        r.play();
//                }

                // Creating Channel
                NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
                notificationChannel.setSound(alarmSound, audioAttributes);
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntent(intent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
                0,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
        mBuilder.setContentIntent(resultPendingIntent);

        notificationManager.notify(notificationId, mBuilder.build());

    }


    private void startRunningInForeground() {

        if (Build.VERSION.SDK_INT >= 26) {

            if (Build.VERSION.SDK_INT > 26) {
                String CHANNEL_ONE_ID = "Package.Service";
                String CHANNEL_ONE_NAME = "Screen service";
                NotificationChannel notificationChannel = null;
                notificationChannel = new NotificationChannel(CHANNEL_ONE_ID,
                        CHANNEL_ONE_NAME, NotificationManager.IMPORTANCE_MIN);
                notificationChannel.enableLights(true);
                notificationChannel.setLightColor(Color.RED);
                notificationChannel.setShowBadge(true);
                notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
                NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                if (manager != null) {
                    manager.createNotificationChannel(notificationChannel);
                }

                Notification notification = new Notification.Builder(getApplicationContext())
                        .setChannelId(CHANNEL_ONE_ID)
                        .setSmallIcon(app_icon)
                        .build();

                Intent notificationIntent = new Intent(getApplicationContext(), SplashScreen.class);
                notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                notification.contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, 0);

                startForeground(101, notification);
            } else {
                startForeground(101, updateNotification());
            }
        } else {
            Notification notification = new NotificationCompat.Builder(this)
                    .setSmallIcon(app_icon)
                    .setOngoing(true).build();

            startForeground(101, notification);
        }
    }

    private Notification updateNotification() {

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, SplashScreen.class), 0);

        return new NotificationCompat.Builder(this)
                .setSmallIcon(app_icon)
                .setContentIntent(pendingIntent)
                .setOngoing(true).build();
    }

}

