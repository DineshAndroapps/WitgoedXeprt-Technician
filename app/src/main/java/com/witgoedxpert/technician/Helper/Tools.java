package com.witgoedxpert.technician.Helper;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.witgoedxpert.technician.R;

public class Tools {

    private static String token = "";


    public static boolean check_permissions(Activity context) {

        String[] PERMISSIONS = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
        };

        if (!hasPermissions(context, PERMISSIONS)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                context.requestPermissions(PERMISSIONS, 2);
            }
        }else {

            return true;
        }

        return false;
    }


    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

   /* public static String Bitmap_to_base64(Activity activity, Bitmap imagebitmap){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imagebitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
        byte[] byteArray = baos .toByteArray();
        String base64= Base64.encodeToString(byteArray, Base64.DEFAULT);
        return base64;
    }*/
    public static int getFeaturedNewsImageHeight(Activity activity) {
        float w_ratio = 2, h_ratio = 1; // we use 2:1 ratio
        Display display = activity.getWindowManager().getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getMetrics(displayMetrics);
        float screenWidth = displayMetrics.widthPixels - 10;
        float resHeight = (screenWidth * h_ratio) / w_ratio;
        return Math.round(resHeight);
    }

    public static void changeOverflowMenuIconColor(Toolbar toolbar, @ColorInt int color) {
        try {
            Drawable drawable = toolbar.getOverflowIcon();
            drawable.mutate();
            drawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        } catch (Exception e) {
        }
    }

    public static void setSmartSystemBar(Activity act) {
        setSystemBarColor(act, R.color.colorPrimary);
    }

    public static void setSystemBarColor(Activity act, @ColorRes int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = act.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(act.getResources().getColor(color));
        }
    }


    public static String GetFCMToken() {

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w("", "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        token = task.getResult();

                        //   getUser(token);

                        Log.d("TOKEN_", "onComplete: " + token);
                    }
                });
        return token;
    }
}
