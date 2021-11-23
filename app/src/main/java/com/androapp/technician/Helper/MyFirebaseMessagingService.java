package com.androapp.technician.Helper;

import static android.media.AudioManager.STREAM_ALARM;
import static com.android.volley.VolleyLog.TAG;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.androapp.technician.R;
import com.androapp.technician.SplashScreen;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;


public class MyFirebaseMessagingService extends  FirebaseMessagingService {

    Ringtone ringtone;
    SharedPreferences sharedPreferences;
    String user_id;
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // ...

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d("Check_noti", "From: " + remoteMessage.getFrom());

//        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Uri alarmSound = Uri.parse("android.resource://" + this.getPackageName() + "/" + R.raw.notification_sound);


        ringtone = RingtoneManager.getRingtone(getApplicationContext(), alarmSound);
        ringtone.setStreamType(AudioManager.STREAM_ALARM);

        /*sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        user_id = sharedPreferences.getString(USER_ID, "");*/

        if (remoteMessage.getData().size() > 0) {
            Log.d("remote", "Message_data_payload: " + remoteMessage.getData());
            Map<String, String> params = remoteMessage.getData();
            String title = "";
            String msg = "";
            String content = "";
            String detail = "";
            String detail_js = "";
           //{message={"msg":"Shape","user_id":"19,16,14","added_date":"2021-05-29 11:36:24","class_id":"7","division_id":"-1","detail":"<p>hello<\/p>\r\n","title":"test"}}
            try {
                JSONObject jsonObject = new JSONObject(remoteMessage.getData());
                msg = jsonObject.getString("message");
                /*JSONObject jsonObject1 = new JSONObject(msg);
                title = jsonObject1.getString("title");
                detail_js = jsonObject1.getString("detail");
                Document document = Jsoup.parse(title);
                Document document_detail = Jsoup.parse(detail_js);
                content = document.body().text();
                detail = document_detail.body().text();*/
                NoficationManager(this,msg,detail);
//                sendNotification(content, table_id);
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("remote_error", "Message: "+e);

            }


        }


    }
    public void NoficationManager( String title, String table_id) {
        Intent intent;
        if (table_id.length() > 0) {

            intent = new Intent(this, SplashScreen.class);

        } else {

            intent = new Intent(this, SplashScreen.class);
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        NotificationCompat.Builder mBuilder = null;


        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        long[] vibrate = new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400};


        int notificationId = 1;
        String channelId = "channel-01";
        String channelName = "Channel Name";


        try {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                //                Uri alarmSound = Uri.parse("android.resource://" + this.getPackageName() + "/" + R.raw.notification);
                Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                mBuilder = new NotificationCompat.Builder(MyFirebaseMessagingService.this, channelId)
                        .setSmallIcon(R.drawable.app_trans)
                        .setContentTitle(title)
                        .setContentText(title)
                        .setAutoCancel(true)
                        .setSound(alarmSound, STREAM_ALARM);


                AudioAttributes attributes = new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                        .build();

                if (notificationManager != null) {
                    List<NotificationChannel> channelList = notificationManager.getNotificationChannels();

                    for (int i = 0; channelList != null && i < channelList.size(); i++) {
                        notificationManager.deleteNotificationChannel(channelList.get(i).getId());
                    }
                }

                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel mChannel = new NotificationChannel(channelId, channelName, importance);
                mChannel.enableLights(true);
                mChannel.setLightColor(Color.RED);
                mChannel.enableVibration(true);
                mChannel.setVibrationPattern(vibrate);
                mChannel.setSound(alarmSound, attributes);


                notificationManager.createNotificationChannel(mChannel);
            }

            notificationManager.notify(notificationId, mBuilder.build());


        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    public void NoficationManager(Context context, String title, String table_id) {

        NotificationCompat.Builder mBuilder = null;


        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        long[] vibrate = new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400};


        int notificationId = 1;
        String channelId = "channel-01";
        String channelName = "Channel Name";


        try {

            Intent intent;
            if (table_id.length() > 0) {

                intent = new Intent(this, SplashScreen.class);

            } else {

                intent = new Intent(this, SplashScreen.class);
            }


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                PendingIntent detailsPendingIntent = PendingIntent.getActivity(
                        getApplicationContext(),
                        0,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

//                Uri alarmSound = Uri.parse("android.resource://" + this.getPackageName() + "/" + R.raw.notification);
                Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                mBuilder = new NotificationCompat.Builder(context, channelId)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(title)
                        .setContentText(table_id)
                        .setAutoCancel(true)
//                        .setSound(alarmSound, STREAM_ALARM)
                        .setContentIntent(detailsPendingIntent);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mBuilder.setSmallIcon(R.mipmap.ic_launcher);
                 //   mBuilder.setColor(getResources().getColor(R.color.colorPrimary));
                } else {
                    mBuilder.setSmallIcon(R.mipmap.ic_launcher);
                }

                AudioAttributes attributes = new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                        .build();

                if (notificationManager != null) {
                    List<NotificationChannel> channelList = notificationManager.getNotificationChannels();

                    for (int i = 0; channelList != null && i < channelList.size(); i++) {
                        notificationManager.deleteNotificationChannel(channelList.get(i).getId());
                    }
                }

                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel mChannel = new NotificationChannel(channelId, channelName, importance);


                mChannel.enableLights(true);
                mChannel.setLightColor(Color.RED);
                mChannel.enableVibration(true);
                mChannel.setVibrationPattern(vibrate);
//                mChannel.setSound(alarmSound, attributes);


                notificationManager.createNotificationChannel(mChannel);
            }

            notificationManager.notify(notificationId, mBuilder.build());

            if (ringtone != null) {
                ringtone.play();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        if (ringtone != null) {
                            ringtone.stop();
                        }

                    }
                }, 5000);
            }


        } catch (Exception e) {
            Log.d(TAG, "NoficationManager: "+e.toString());
            e.printStackTrace();
        }
    }


    private void sendNotification(String messageBody, String table_id) {
        Intent intent;
        if (table_id.length() > 0) {

            intent = new Intent(this, SplashScreen.class);

        } else {

            intent = new Intent(this, SplashScreen.class);
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        String channelId = getString(R.string.default_notification_channel_id);
        final AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM);

        audioManager.setStreamVolume(AudioManager.STREAM_ALARM, maxVolume, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.app_trans)
                        .setContentTitle(getString(R.string.app_name))
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setChannelId(channelId)
                        .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                        .setLights(Color.RED, 3000, 3000)
                        .setSound(alarmSound, STREAM_ALARM)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
    }
}
