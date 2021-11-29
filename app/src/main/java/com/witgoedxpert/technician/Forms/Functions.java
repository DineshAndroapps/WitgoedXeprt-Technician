package com.witgoedxpert.technician.Forms;

import static android.content.Context.ACTIVITY_SERVICE;
import static android.os.Build.VERSION.SDK_INT;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.witgoedxpert.technician.Helper.Variables;
import com.witgoedxpert.technician.Location_Services.GetLocation_Service;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class Functions {

    public static String getRandomString() {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 10) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;

    }

    public static File createImageFile(Context context) throws IOException {
        String timeStamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss",
                        Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir =
                context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        return image;
    }
    public static void LOGE(String classname, String message) {

        if (!Variables.IS_LOG_ENABLE)
            return;

        if (message.length() > 4000) {

            Log.e("" + classname, "" + message.substring(0, 4000));

            LOGE(classname, message.substring(4000));
        } else
            Log.e("" + classname, "" + message);
    }

    public static boolean checkStringisValid(String text) {

        if (text != null && !text.equals("") && !text.equals("null") && !text.equals("0")) {
            return true;
        }

        return false;
    }

    public static String[] LOCATION_PERMISSIONS;

    public static boolean check_location_permissions(Activity context) {

        LOCATION_PERMISSIONS = new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
//                Manifest.permission.ACCESS_BACKGROUND_LOCATION,
        };

        if (!hasPermissions(context, LOCATION_PERMISSIONS)) {
            if (SDK_INT >= Build.VERSION_CODES.M) {
//                context.requestPermissions(PERMISSIONS, 123);
            }
        } else {

            return true;
        }

        return false;
    }

    public static boolean isServiceRunning(Context context, String service_name) {
        ActivityManager manager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (service_name.equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static boolean Bind_Location_service(Activity context) {

        if (!isServiceRunning(context, Variables.LOCATION_SERVICE)) {
            Intent mServiceIntent = new Intent(context, GetLocation_Service.class);
            context.startService(mServiceIntent);
            return false;
        } else {
            return true;
        }

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

    public static String getStringFromTextview(TextView textView) {

        return textView.getText().toString().trim();
    }

    public static String getStringFromEdit(EditText editText) {

        return editText.getText().toString().trim();
    }
    public static Address getAddressFromLatLong(double lat, double long_temp, Context context) {
        Address address = null;
        try {
            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(context, Locale.getDefault());

            addresses = geocoder.getFromLocation(lat, long_temp, 1);
            address = addresses.get(0);

        } catch (Exception e) {
            e.printStackTrace();
            // new GetClass().execute();
        }

        return address;
    }

    public static String getLocationFromGeoAddress(Address address) {
        String addressstr = "Select location";
        try {

            if (address.getMaxAddressLineIndex() == 0) {
                addressstr = address.getAddressLine(0);


            } else if (address.getMaxAddressLineIndex() == 1) {
                String localAddress = address.getAddressLine(1);
                String place2 = localAddress + " , " + address.getLocality() + "," + address.getCountryName();
            } else if (address.getMaxAddressLineIndex() == 2) {

                String localAddress = address.getAddressLine(1);
                String MiddleAddress = address.getAddressLine(2);
                addressstr = localAddress + " , " + MiddleAddress;

            } else {
                String localAddress = address.getAddressLine(1);
                String[] partsaa = localAddress.split(",");
                String partd = partsaa[1];
                String MiddleAddress = address.getAddressLine(2);
                String[] parts = MiddleAddress.split(",");
                String part = parts[0];
                addressstr = partd + " , " + part;
            }


        } catch (Exception e) {
            e.printStackTrace();

        }

        return addressstr;
    }
    public static void CustomeToast(Activity context, int imgValue, String Text) {
        Toast.makeText(context, Text, Toast.LENGTH_SHORT).show();


    }
    public static void showFragment(Activity context) {
        //Toast.makeText(context, "Turn On GPS", Toast.LENGTH_SHORT).show();
/*

        if (Functions.enableGPS_bf != null)
            Functions.enableGPS_bf.show(((MainActivity) context).getSupportFragmentManager(), Functions.enableGPS_bf.getTag());
*/
    }
}
