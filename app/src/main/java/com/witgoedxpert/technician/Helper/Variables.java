package com.witgoedxpert.technician.Helper;

import android.os.Environment;

import com.google.android.gms.maps.model.LatLng;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class Variables {

    public String push_by = "Dinesh";

    public static final String MEN = "men";
    public static final String WOMEN = "women";
    public static final String MEN_WOMEN = "men,women";
    public static final String UNISEX = "unisex";
    public static final String PET = "pet";
    public static final String DOG_CAT = "Dog / Cat";

    public static LatLng currentLatlang;

    public static final String LOCATION_SERVICE = "com.ship.stylebook.Location_Services.GetLocation_Service";

    public static String address_id = "";

    public static final boolean is_secure_info=false;

    public static boolean IS_DASHBOARD_VISIBLE = true;

    public static final boolean IS_TOAST_ENABLE= true;

    public static final boolean IS_LOG_ENABLE= true;

    public static String isSignupFrom = "" ;

    public static final String device="android";
    public static final String TAG = "LOG_E";

    public static int screen_width;
    public static int screen_height;

    public static final int REQUESTCODE_MAP = 456;
    public static final int REQUESTCODE_LOCATION = 987;
    public static final int REQUESTCODE_CAMERA = 123;

    public static final int second_in_hr = 60;

    public static double latitude = 0.0;
    public static double longitude = 0.0;

    public static double select_latitude = 0.0;
    public static double select_longitude = 0.0;

    public static String select_address = "";

    public static String google_link = "https://play.google.com/store/apps/details?id=";
    public static String app_name ;

    public static final String root= Environment.getExternalStorageDirectory().toString();
    //    public static final String app_hided_folder =root+"/.HidedTicTic/";
    public static final String app_showing_folder =root+"/FragmentLayout/";
    public static final String app_folder =root+"/FragmentLayout/";
    public static final String draft_app_folder= app_showing_folder +"Draft/";

    public static final String ACTIVE = "active";
    public static final String INACTIVE = "inactive";

    public static final String EMPLOYEE = "employee";
    public static final String OWNER = "owner";




    public static String tag="AppLog_";



    public static final SimpleDateFormat df =
            new SimpleDateFormat("dd-MM-yyyy HH:mm:ssZZ", Locale.ENGLISH);

    public static final SimpleDateFormat df2 =
            new SimpleDateFormat("dd-MM-yyyy HH:mmZZ", Locale.ENGLISH);


    public static final SimpleDateFormat df_defult =
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);

    public static final SimpleDateFormat df_normal =
            new SimpleDateFormat("yyyy-dd-MM", Locale.ENGLISH);

    public static final SimpleDateFormat df_middle_month =
            new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

    public static final SimpleDateFormat df_viewformat =
            new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);

    public static final SimpleDateFormat df_Hourviewformat =
            new SimpleDateFormat("hh:mm aa");


    public static final SimpleDateFormat df_franh_format =
            new SimpleDateFormat("dd MMM yy", Locale.ENGLISH);



}

