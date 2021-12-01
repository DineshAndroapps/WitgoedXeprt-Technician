package com.witgoedxpert.technician.Activity.Home;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;
import com.witgoedxpert.technician.Adapters.AdapterAppointment;
import com.witgoedxpert.technician.Forms.AddEnquiry;
import com.witgoedxpert.technician.Fragments.Complete_F;
import com.witgoedxpert.technician.Fragments.Pending_F;
import com.witgoedxpert.technician.Fragments.Process_F;
import com.witgoedxpert.technician.Helper.CustomViewPager;
import com.witgoedxpert.technician.Helper.RAdapterPager;
import com.witgoedxpert.technician.model.OrderModel;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.witgoedxpert.technician.Activity.Notification_Activity;
import com.witgoedxpert.technician.Activity.MyOrder;
import com.witgoedxpert.technician.Forms.Functions;
import com.witgoedxpert.technician.Forms.LoginActivity;
import com.witgoedxpert.technician.Helper.Callback;
import com.witgoedxpert.technician.Helper.Constant;
import com.witgoedxpert.technician.Helper.FileUtils;
import com.witgoedxpert.technician.Helper.MultiPartRequest;
import com.witgoedxpert.technician.Location_Services.GpsUtils;
import com.witgoedxpert.technician.R;

import androidx.annotation.NonNull;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.witgoedxpert.technician.Forms.LoginActivity.ADDRESS;
import static com.witgoedxpert.technician.Forms.LoginActivity.NAME;
import static com.witgoedxpert.technician.Forms.LoginActivity.PHONENO;
import static com.witgoedxpert.technician.Forms.LoginActivity.SHARED_PREFERENCES_NAME;
import static com.witgoedxpert.technician.Forms.LoginActivity.USER;
import static com.witgoedxpert.technician.Forms.LoginActivity.USER_ID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "MAIN_BDM";
    private ActionBar actionBar;
    private Toolbar toolbar;
    SharedPreferences sharedPreferences;
    String str_userid, str_name, str_email, str_branch_data = "";
    LinearLayout ll_bg;
    File str_image_1;
    public static int width;
    public static int height;
    SharedPreferences.Editor editor;
    View headerView;
    public static final int PERMISSIONS_DENIED = 1;
    private static final String PACKAGE_URL_SCHEME = "package:";

    private int GALLERY = 1, CAMERA = 2;
    String image_file = "";

    String token = "";


    private Pending_F fragmentOne;
    private Process_F fragmentTwo;
    private Complete_F fragmentThree;
    private TabLayout allTabs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        str_userid = sharedPreferences.getString(USER_ID, "");
        str_name = sharedPreferences.getString(NAME, "");

        initToolbar();
        initNavigationMenu();
        EnableGPS();

        allTabs = (TabLayout) findViewById(R.id.tabs);
        bindWidgetsWithAnEvent();
        setupTabLayout();


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
                        SendFCM(token);
                        //   getUser(token);

                        Log.d("TOKEN_", "onComplete: " + token);
                    }
                });
        findViewById(R.id.notification_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Notification_Activity.class);
                startActivity(intent);
            }
        });


    }


    @Override
    protected void onResume() {
        super.onResume();

    }


    void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        ((TextView) findViewById(R.id.title)).setText("Home");
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle("BDM");
    }


    /*
        private void initToolbar() {
            toolbar = findViewById(R.id.toolbar);
            toolbar.setNavigationIcon(R.drawable.ic_menu);
            toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);
            setSupportActionBar(toolbar);
            actionBar = getSupportActionBar();
            actionBar.setTitle(R.string.app_name);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            Tools.changeOverflowMenuIconColor(toolbar, getResources().getColor(R.color.black));
            Tools.setSmartSystemBar(this);
        }
    */
    private void SendFCM(String token) {

//    final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
//    progressDialog.setCancelable(false);
//    progressDialog.setMessage("Loading..");
//    progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.mechanicDetails,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        String code, message, id, user_id;

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            code = jsonObject.getString("code");
                            message = jsonObject.getString("message");

                            Log.d("resp", "onResponse: " + jsonObject + "===" + code);
                            if (code.equals("200")) {

                                JSONObject user_data = jsonObject.getJSONObject("Mechanic_data");

                                sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
                                editor = sharedPreferences.edit();
                                editor.putString(USER, user_data.getString("id"));
                                editor.putString(USER_ID, user_data.getString("id"));
                                editor.putString(NAME, user_data.getString("name"));
                                editor.putString(PHONENO, user_data.getString("mobile"));
                                editor.putString(ADDRESS, user_data.getString("address"));
                                editor.apply();
                            } else {
                                // Toast.makeText(MainActivity.this, "" + message, Toast.LENGTH_SHORT).show();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        // progressDialog.dismiss();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //  progressDialog.dismiss();
                // Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("mechanic_id", str_userid);
                params.put("fcm_id", token);
                params.put("fcm_flag", "1");// 1 -Update FCM and 0- Don't update fcm
                Log.d("params", "getParams: " + params);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("token", Constant.Token);
                return headers;
            }
        };

        Volley.newRequestQueue(MainActivity.this).add(stringRequest);


    }

    void initNavigationMenu() {
        NavigationView nav_view = (NavigationView) findViewById(R.id.navigationView);
        headerView = nav_view.getHeaderView(0);
        //TextView logout_date = (TextView) headerView.findViewById(R.id.logout_date);
        TextView navUsername = (TextView) headerView.findViewById(R.id.navUsername);
        TextView navUseremail = (TextView) headerView.findViewById(R.id.navUseremail);
        navUsername.setText(str_name);
        //logout_date.setText("20-09-2021 (1.0)");


        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.black));
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        nav_view.setNavigationItemSelectedListener(this);

        // open drawer at start
        drawer.closeDrawer(GravityCompat.START);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_home) {
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            finish();
            startActivity(i);


        } else if (id == R.id.nav_sefie) {
            Intent i = new Intent(getApplicationContext(), MyOrder.class);
            i.putExtra("flag", "1");
            i.putExtra("user_id", sharedPreferences.getString(USER, ""));
            startActivity(i);

        } else if (id == R.id.nav_help) {
            DialogForLogout();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void DialogForLogout() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
        dialog.setTitle(R.string.confirmation);
        dialog.setMessage(R.string.logout_confirmation_text);
        dialog.setNegativeButton(R.string.CANCEL, null);
        dialog.setPositiveButton(R.string.YES, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ClearAllSherf();
            }
        });
        dialog.setCancelable(false);
        dialog.show();
    }

    private void ClearAllSherf() {
        SharedPreferences sharedpreferences =
                getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.clear();
        editor.apply();
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    public void EnableGPS() {

        if (Functions.check_location_permissions(this)) {
            if (!GpsUtils.isGPSENABLE(MainActivity.this)) {
                Functions.showFragment(MainActivity.this);
            } else {
                enable_permission();
            }
        } else {
            Functions.showFragment(MainActivity.this);
        }
    }


    private void enable_permission() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean GpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!GpsStatus) {

            new GpsUtils(this).turnGPSOn(isGPSEnable -> {

                enable_permission();

            });
        } else if (Functions.check_location_permissions(this)) {
            Functions.Bind_Location_service(MainActivity.this);

            //  SetHomeScreen();

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) {
            return;
        }

        switch (requestCode) {
            case 3: {
                if (resultCode == RESULT_OK) {
                    Functions.Bind_Location_service(MainActivity.this);
                }
            }
            break;
        }

    }

    private void setupTabLayout() {
        fragmentOne = new Pending_F();
        fragmentTwo = new Process_F();
        fragmentThree = new Complete_F();
        allTabs.addTab(allTabs.newTab().setText("Pending"), true);
        allTabs.addTab(allTabs.newTab().setText("Process"));
        allTabs.addTab(allTabs.newTab().setText("Complete"));
    }

    private void bindWidgetsWithAnEvent() {
        allTabs.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                setCurrentTabFragment(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    private void setCurrentTabFragment(int tabPosition) {
        switch (tabPosition) {
            case 0:
                replaceFragment(fragmentOne);

                break;
            case 1:
                replaceFragment(fragmentTwo);

                break;
            case 2:
                replaceFragment(fragmentThree);

                break;

        }
    }

    public void replaceFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.frame_container, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commit();
    }
}