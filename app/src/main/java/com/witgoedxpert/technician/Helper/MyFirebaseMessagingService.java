package com.witgoedxpert.technician.Helper;

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
import com.witgoedxpert.technician.Activity.Home.MainActivity;
import com.witgoedxpert.technician.R;
import com.witgoedxpert.technician.SplashScreen;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

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
            String type = "";
            String detail = "";
            String user_data = "";
            //{message={"msg":"Shape","user_id":"19,16,14","added_date":"2021-05-29 11:36:24","class_id":"7","division_id":"-1","detail":"<p>hello<\/p>\r\n","title":"test"}}
            try {
                JSONObject jsonObject = new JSONObject(remoteMessage.getData());
                msg = jsonObject.getString("message");
                type = jsonObject.getString("type");
                user_data = jsonObject.getString("user_data");
                Log.e(TAG, "onMessageReceived: " + user_data);
                Log.e("remote_type", "Message: " + type);

                NotificationsManager_Data(this, msg, user_data, type);
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("remote_error", "Message: " + e);

            }


        }


    }


    public void NotificationsManager_Data(Context context, String title, String details, String type) {

        NotificationCompat.Builder mBuilder = null;
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        long[] vibrate = new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400};
        int notificationId = 1;
        String channelId = "channel-01";
        String channelName = "Channel Name";
        try {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra(Constant.DETAILS, details);
            intent.putExtra(Constant.MSG_NOTIFICATION, title);
            intent.putExtra(Constant.MSG_TYPE, type);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                    PendingIntent.FLAG_ONE_SHOT);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                Uri alarmSound = Uri.parse("android.resource://" + this.getPackageName() + "/" + R.raw.notification);
                Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                mBuilder = new NotificationCompat.Builder(context, channelId)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("New Order")
                        .setContentText(title)
                        .setAutoCancel(true)
//                        .setSound(alarmSound, STREAM_ALARM)
                        .setContentIntent(pendingIntent);
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
            Log.d(TAG, "NoficationManager: " + e.toString());
            e.printStackTrace();
        }
    }



    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
    }
}
