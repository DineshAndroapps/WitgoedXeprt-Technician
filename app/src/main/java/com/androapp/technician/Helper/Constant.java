package com.androapp.technician.Helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.util.Base64;

import java.io.ByteArrayOutputStream;


public class Constant {


    public Constant() {

    }

    public static String LATITUDE_SEVER = "LAT_SER";
    public static String LONGITUDE_SERVER = "LONG_SER";

    public static final String MODE = "MODE";
    public static final String DATA = "DATA";
    public static final String HOME_PLACE = "H_DATA";
    public static final String DATA_PLACE_LAT_LONG = "DATA_LAT_LONG";
    public static final String DATA_PHOTO = "DATA_PIC";
    public static final String ROUTE_ID = "ROUTE_ID";
    public static final String KEY_ALLOW_AUTO_START = "allow_auto_start";
    public static final String KEY_STATUS = "status";


    //Live
    public static final String MAIN = "https://androappstech.in/WitgoedXpert/api/";
    public static final String image_url_ = "https://androappstech.in/WitgoedXpert/data/Product/";


    //12
    public static String register = MAIN + "User/Registration";
    public static String login = MAIN + "Mechanic/login";
    public static String ListAllProduct = MAIN + "Mechanic/AssignMechanic";
    public static String addEnquiry = MAIN + "Mechanic/Invoice";
    public static String viewEnquiry = MAIN + "Enquiry/viewEnquiry";



    public static String Token = "c7d3965d49d4a59b0da80e90646aee77548458b3377ba3c0fb43d5ff91d54ea28833080e3de6ebd4fde36e2fb7175cddaf5d8d018ac1467c3d15db21c11b6909";

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        android.net.NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    public static String getStringImageBase64(Bitmap bitmap) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }


    /*---------------------------------------------------------------------------------------------------------*/
    public static final String BDE_URL = "";
    public static final String PRO_URL ="";
    public static String formList = MAIN + "ProContactSource";
    public static String pro_common_data = MAIN + "pro_common_data";
    public static String branchwisepro = MAIN + "branchwisepro/?branch_id=";
    public static String proEvent = MAIN + "retrieve_pro_events/?source_id=";
    public static String submitForm = MAIN + "LeadCreateMultiple/";
    public static String pro_image_upload = MAIN + "pro_image_upload/";
    public static String pro_image_list = MAIN + "pro_image_list/";
    public static String pro_lead_performance_detail_report = MAIN + "pro_lead_performance_detail_report/";
    public static String branch_wise_pro_data_collection = MAIN + "branch_wise_pro_data_collection/?branch=";
    public static String combine_pro_lead_performance_report = MAIN + "combine_pro_lead_performance_report/?branch=";
    public static String ProLeadPerformanceCustomFilter = MAIN + "ProLeadPerformanceCustomFilter/?date=";

    //23
    public static String routeByBde = BDE_URL + "routeByBde";
    public static String route = BDE_URL + "route";
    public static String placeByRoute = BDE_URL + "placeByRoute";
    public static String place = BDE_URL + "place";
    public static String assignRoute = BDE_URL + "assignRoute";
    public static String checkAssignRoute = BDE_URL + "checkAssignRoute";
    public static String deleteAssignRoute = BDE_URL + "deleteAssignRoute";
    public static String proLatLong = BDE_URL + "proLatLong";
    public static String bde_fcm = BDE_URL + "bde";
    public static String notification = BDE_URL + "notification";
    public static String allCategory = BDE_URL + "allCategory";
    public static String updateSequenceId = BDE_URL + "updateSequenceId";
    public static String deletePlace = BDE_URL + "deletePlace";

    public static String checkIn = PRO_URL + "checkin";
    public static String checkOut = PRO_URL + "checkOut";
    public static String checkInStatus = PRO_URL + "checkInStatus";
    public static String ProListRoute = PRO_URL + "ProListRoute";
    public static String ProListPlace = PRO_URL + "ProListPlace";
    public static String FatchPlaceByRoute = PRO_URL + "FatchPlaceByRoute";
    public static String updateLat = PRO_URL + "updateLat";
    public static String locationPhoto = PRO_URL + "locationPhoto";
    public static String upcommingAssigned = PRO_URL + "upcommingAssigned";
    public static String pro_fcm = PRO_URL + "pro";
    public static String proLocationList = PRO_URL + "proLocationList";


    /*=======================================================================================================================================*/


}
