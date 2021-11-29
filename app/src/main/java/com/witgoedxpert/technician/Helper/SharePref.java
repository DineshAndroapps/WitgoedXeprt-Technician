package com.witgoedxpert.technician.Helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.annotation.Nullable;


import com.witgoedxpert.technician.Forms.LoginActivity;

import java.util.Set;

public class SharePref {

    private volatile static SharePref mInstance;
    private Context mContext;


    private SharedPreferences mPref;

    public static final String pref_name="pref_name";
    public static final String u_id="u_id";
    public static final String shop_id="shop_id";
    public static final String shop_owner_number ="shop_owner_number";
    public static final String u_name="u_name";
    public static final String u_pic="u_pic";
    public static final String f_name="f_name";
    public static final String l_name="l_name";
    public static final String str_shop_name ="str_shop_name";
    public static final String str_shop_email ="shop_email";
    public static final String str_shop_type ="shop_type";
    public static final String str_shop_address ="str_shop_address";
    public static final String mobile="mobile";
    public static final String password ="password";
    public static final String otp ="otp";
    public static final String location="location";
    public static final String address="address";
    public static final String area="area";
    public static final String city="city";
    public static final String pincode="pincode";
    public static final String state="state";
    public static final String country="country";
    public static final String lat="latitude";
    public static final String longe="longitude";
    public static final String addr_id="address_id";
    public static final String created_at="created_at";


    public static final String gender="u_gender";
    public static final String dob ="dob";
    public static final String anniversary ="anniversary";
    public static final String whatsno ="whatsno";
    public static final String email ="email";
    public static final String email_verified_at ="email_verified_at";
    public static final String verified ="verified";

    public static final String accountObject ="accountObject";
    public static final String shopObject ="shopObject";
    public static final String employee_profile ="employee_profile";

    public static final String shop_types ="shop_types";
    public static final String paymentArray ="paymentArray";
    public static final String amenitiesArray ="amenitiesArray";
    public static final String categoriesArray ="categoriesArray";

    public static final String hide_info ="hide_info";


    public static final String islogin="is_login";
    public static final String device_token="device_token";
    public static final String api_token="api_token";
    public static final String authorization ="authorization";
    public static final String device_id="device_id";
    public static final String uploading_video_thumb="uploading_video_thumb";
    public static final String PLACE_API_KEY ="api_key";
    public static final String isNew_user ="newuser";
    public static final String isGuestMode ="Guestuser";
    public static final String isOTPVerified ="isOTPVerified";
    public static final String employee_count ="employee_count";
    public static final String service_count ="employee_count";
    public static final String isLoginBlock ="login_block";
    public static final String BlockTime ="block_time";
    public static final String isEmployelogin ="isEmployelogin";
    public static final String isSelfEmployee ="isSelfEmployee";


    public static final String isFingerPrintEnable ="isfingerprintenable";

    /**
     * A factory method for
     *
     * @return a instance of this class
     */
    public static SharePref getInstance() {
        if (null == mInstance) {
            synchronized (SharePref.class) {
                if (null == mInstance) {
                    mInstance = new SharePref();
                }
            }
        }
        return mInstance;
    }

    /**
     * initialization of context, use only first time later it will use this again and again
     *
     * @param context app context: first time
     */
    public void init(Context context) {
        if (mContext == null) {
            mContext = context;
        }
        if (mPref == null) {
//            mPref = PreferenceManager.getDefaultSharedPreferences(mContext);
            mPref = context.getSharedPreferences(pref_name,Context.MODE_PRIVATE);
        }
    }

    public void putString(String key, String value) {
        SharedPreferences.Editor editor = mPref.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public Set<String> getStringSet(String key, Set<String> def) {
        return mPref.getStringSet(key, def);
    }

    public void putLong(String key, long value) {
        SharedPreferences.Editor editor = mPref.edit();
        editor.putLong(key, value);
        editor.apply();
    }

    public void putInt(String key, int value) {
        SharedPreferences.Editor editor = mPref.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public void putBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = mPref.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }


    public boolean getBoolean(String key) {
        return mPref.getBoolean(key, false);
    }

    public boolean getBoolean(String key, boolean def) {
        return mPref.getBoolean(key, def);
    }

    @Nullable
    public String getString(String key) {
        return mPref.getString(key, "");
    }

    @Nullable
    public String getString(String key, String def) {
        return mPref.getString(key, def);
    }

    public long getLong(String key) {
        return mPref.getLong(key, 0);
    }

    public long getLong(String key, int defInt) {
        return mPref.getLong(key, defInt);
    }

    public int getInt(String key) {
        return mPref.getInt(key, 0);
    }

    public int getInt(String key, int defInt) {
        return mPref.getInt(key, defInt);
    }

    public boolean contains(String key) {
        return mPref.contains(key);
    }


    public void remove(String key) {
        SharedPreferences.Editor editor = mPref.edit();
        editor.remove(key);
        editor.apply();
    }

    public SharedPreferences.Editor edit() {

        if (mContext != null)
        {
            init(mContext);
        }

        if (mPref != null) {
//            mPref = PreferenceManager.getDefaultSharedPreferences(mContext);
            SharedPreferences.Editor editor = mPref.edit();
            return editor;
        }

        return null;
    }

    public void clear() {
        SharedPreferences.Editor editor = mPref.edit();
        editor.clear();
        editor.apply();
    }


    public static String getU_id() {
        return SharePref.getInstance().getString(SharePref.u_id,"");
    }

    public static String getShop_id() {
        return SharePref.getInstance().getString(SharePref.shop_id,"");
    }

    public static void setU_id(String value){
        SharePref.getInstance().putString(SharePref.u_id,value);
    }
    public static String getAuthorization() {
        return SharePref.getInstance().getString(SharePref.authorization,"");
    }

    public static void setAuthorization(String value){
        SharePref.getInstance().putString(SharePref.authorization,value);
    }

    public static boolean isLogin() {
        return SharePref.getInstance().getBoolean(SharePref.islogin,false);
    }

    public static boolean isEmployeeLogin() {
        return SharePref.getInstance().getBoolean(SharePref.isEmployelogin,false);
    }


    public static boolean isEmployeehave(){
       int count =  SharePref.getInstance().getInt(SharePref.employee_count,0);
       return count == 0 ? false : true;
    }

    public static boolean isNewUser(){
        return SharePref.getInstance().getBoolean(SharePref.isNew_user,true);
    }


    public static String first_name = "";
    public static void clearData(Activity context){

        first_name = SharePref.getInstance().getString(SharePref.f_name,"");
        SharePref.getInstance().clear();
        SharePref.getInstance().putBoolean(SharePref.isNew_user,false);
        SharePref.getInstance().putString(SharePref.f_name,first_name);
        context.startActivity(new Intent(context, LoginActivity.class));
        context.finishAffinity();
    }


}
