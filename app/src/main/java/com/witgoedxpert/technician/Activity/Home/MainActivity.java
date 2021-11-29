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
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.witgoedxpert.technician.Adapters.AdapterAppointment;
import com.witgoedxpert.technician.Forms.AddEnquiry;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

    SharedPreferences.Editor editor;
    View headerView;
    public static final int PERMISSIONS_DENIED = 1;
    private static final String PACKAGE_URL_SCHEME = "package:";

    private int GALLERY = 1, CAMERA = 2;
    String image_file = "";

    String token = "";

    ArrayList<OrderModel> home_data_list = new ArrayList<>();
    RecyclerView rv_list;
    String type;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        str_userid = sharedPreferences.getString(USER_ID, "");
        str_name = sharedPreferences.getString(NAME, "");

        rv_list = findViewById(R.id.rv_list);
        //rv_list.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
        rv_list.setLayoutManager((new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false)));
        initToolbar();
        initNavigationMenu();
        EnableGPS();
        Log.d(TAG, "onCreate: " + Constant.NEW_ORDER);

        if (getIntent().hasExtra(Constant.MSG_TYPE)) {
            type = getIntent().getStringExtra(Constant.MSG_TYPE);
            if (type.equals("1")) {
                if (getIntent().hasExtra(Constant.MSG_NOTIFICATION) && getIntent().hasExtra(Constant.DETAILS)) {
                    try {
                        String str_msg = getIntent().getStringExtra(Constant.MSG_NOTIFICATION);
                        String str_DETAILS = getIntent().getStringExtra(Constant.DETAILS);
                        JSONObject jsonObject = new JSONObject(str_DETAILS);
                        Log.e(TAG, "onCreate: "+jsonObject.getString("address") );
                        String address = jsonObject.getString("address");
                        String mobile = jsonObject.getString("mobile");
                        ((TextView) findViewById(R.id.name)).setText(str_msg);
                        ((TextView) findViewById(R.id.address)).setText(address);
                        ((TextView) findViewById(R.id.mobile)).setText(mobile);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }

        }
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
        if (Constant.isNetworkAvailable(getApplicationContext())) {
            CallHomeData();
        } else {
            Toast.makeText(getApplicationContext(), "Internet Connection Not Available", Toast.LENGTH_SHORT).show();
        }


    }


    public void CallHomeData() {
        home_data_list.clear();
        final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading..");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.ListAllProduct, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("list_cat", "onResponse: " + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String message = jsonObject.getString("message");
                    String code = jsonObject.getString("code");
                    if (code.equals("200")) {
                        progressDialog.dismiss();
                        findViewById(R.id.no_slot).setVisibility(View.GONE);


                        JSONArray jsonArrayvideo = jsonObject.getJSONArray("Assign_data");

                        for (int i = 0; i < jsonArrayvideo.length(); i++) {
                            OrderModel placeModel = new OrderModel();
                            JSONObject BMIReport = jsonArrayvideo.getJSONObject(i);

                            placeModel.id = BMIReport.getString("id");
                            placeModel.user_id = BMIReport.getString("user_id");
                            placeModel.product_id = BMIReport.getString("product_id");
                            placeModel.description = BMIReport.getString("description");
                            placeModel.error_code = BMIReport.getString("error_code");
                            placeModel.type_of_machine = BMIReport.getString("type_of_machine");
                            placeModel.name = BMIReport.getString("name");
                            placeModel.age_machine = BMIReport.getString("age_machine");
                            placeModel.address = BMIReport.getString("address");
                            placeModel.isDeleted = BMIReport.getString("isDeleted");
                            placeModel.phone = BMIReport.getString("phone");
                            placeModel.additional_info = BMIReport.getString("additional_info");
                            placeModel.added_date = BMIReport.getString("added_date");
                            placeModel.product_name = BMIReport.getString("product_name");
                            placeModel.image = BMIReport.getString("image");

                            home_data_list.add(placeModel);
                        }
                        rv_list.setAdapter(new AdapterAppointment(home_data_list, MainActivity.this));


                    } else {
                        progressDialog.dismiss();
                        findViewById(R.id.no_slot).setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                    findViewById(R.id.no_slot).setVisibility(View.VISIBLE);
                }

                progressDialog.dismiss();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                progressDialog.dismiss();
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap();
                params.put("mechanic_id", str_userid);
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


    @Override
    protected void onResume() {
        super.onResume();
        //   get_user_data();
        //SendFCM(token);
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
        if (requestCode == GALLERY) {
            if (data != null) {
                Uri selectedImage = data.getData();
                if (resultCode == RESULT_OK) {
                    lnr_imageverification.setVisibility(View.VISIBLE);
                    lnr_imageverification.setImageURI(selectedImage);

                    InputStream imageStream = null;
                    try {
                        imageStream = MainActivity.this.getContentResolver().openInputStream(selectedImage);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    final Bitmap imagebitmap = BitmapFactory.decodeStream(imageStream);
                    Bitmap rotatedBitmap = Bitmap.createBitmap(imagebitmap, 0, 0, imagebitmap.getWidth(), imagebitmap.getHeight(), null, true);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);

                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    rotatedBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                    String strDocument = Constant.getStringImageBase64(rotatedBitmap);
                    Log.d("IMAGE_1sstrDocument_ga", "onActivityResult: " + strDocument);
                    try {
                        str_image_1 = FileUtils.getFileFromUri(getApplicationContext(), selectedImage);
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }

                }
            }
        } else if (requestCode == CAMERA) {
            // Uri selectedImage = (Uri.fromFile(new File(imageFilePath)));

            File image_file = new File(imageFilePath);
            Uri selectedImage = (Uri.fromFile(image_file));
            if (resultCode == RESULT_OK) {
                lnr_imageverification.setVisibility(View.VISIBLE);
                //  selfie_imageview.setImageURI(selectedImage);

                Glide.with(MainActivity.this)
                        .load(selectedImage)
                        .into(lnr_imageverification);

                InputStream imageStream = null;
                try {
                    imageStream = MainActivity.this.getContentResolver().openInputStream(selectedImage);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                final Bitmap imagebitmap = BitmapFactory.decodeStream(imageStream);
                Bitmap rotatedBitmap = Bitmap.createBitmap(imagebitmap, 0, 0, imagebitmap.getWidth(), imagebitmap.getHeight(), null, true);
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());

                File file = new File(String.valueOf(getExternalFilesDir(null) + "img" + timeStamp + ".jpg"));

                OutputStream os = null;
                try {
                    os = new BufferedOutputStream(new FileOutputStream(file));
                    rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
                    os.close();
                    str_image_1 = file;
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }


                try {
                    str_image_1 = FileUtils.getFileFromUri(getApplicationContext(), selectedImage);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }

            }


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


    String strSelIdType = "";
    String sp_verification_id = "";
    ImageView lnr_imageverification;
    Dialog dialog;

    private void Dialog_VerifyShop() {

        dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setTitle("");
        dialog.setContentView(R.layout.dialog_pic);
        lnr_imageverification = dialog.findViewById(R.id.lnr_imageverification);

        dialog.findViewById(R.id.rlt_verificationupload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestMultiplePermissions();
            }
        });
        dialog.findViewById(R.id.bt_update_profile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (str_image_1 != null && !str_image_1.equals("") && !str_image_1.equals("null")) {
                    if (Constant.isNetworkAvailable(getApplicationContext())) {
                        selfie_bdm();
                    } else {
                        Toast.makeText(getApplicationContext(), "Internet Connection Not Available", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Select Selfie", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialog.findViewById(R.id.bt_no).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                str_image_1 = null;
            }
        });

        dialog.findViewById(R.id.bt_reset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                str_image_1 = null;
                dialog.dismiss();
            }
        });

        dialog.setCancelable(true);
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

    }

    private void selfie_bdm() {
        final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading..");
        progressDialog.show();
        MultiPartRequest request = new MultiPartRequest(MainActivity.this, Constant.pro_image_upload, new Callback() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
            @Override
            public void Responce(String resp) {
                Functions.LOGE("EditProfile", "Upload_Service " + resp);
                if (!resp.equals("null")) {
                    dialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Image Uploaded Successfully", Toast.LENGTH_SHORT).show();
                    str_image_1 = null;
                    progressDialog.dismiss();
                } else {
                    Toast.makeText(getApplicationContext(), "Failed To  Upload", Toast.LENGTH_SHORT).show();
                }

/*
                try {



                } catch (JSONException e) {
                    e.printStackTrace();
                }*/
                progressDialog.dismiss();

            }
        });

        Geocoder geocoder;
        List<Address> addresses;
        String str_server_address = "";
        geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(Double.parseDouble(Constant.LATITUDE_SEVER), Double.parseDouble(Constant.LONGITUDE_SERVER), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName(); // Only i
            str_server_address = address + "," + city + "," + state + "," + country + "," + postalCode;

        } catch (
                IOException e) {
            e.printStackTrace();
        }


        Log.d("image_file", "selfie_bdm: " + str_image_1);
        Log.d("image_file", "selfie_bdm: " + image_file);
        if (str_image_1 != null && !str_image_1.equals("") && !str_image_1.equals("null"))
            request.addImageFile("image", String.valueOf(str_image_1), Functions.getRandomString() + ".png");

        //  request.addString("_method","POST");
        request.addString("bde_id", sharedPreferences.getString(USER, ""));
        request.addString("latitude", Constant.LATITUDE_SEVER);
        request.addString("longitude", Constant.LONGITUDE_SERVER);
        request.addString("address", str_server_address);

        Log.d("Upload_Service", "request=> \n" + request);
        request.execute();


    }

    private void requestMultiplePermissions() {
        Dexter.withActivity(MainActivity.this).withPermissions(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                if (report.areAllPermissionsGranted()) {
                    openCameraIntent();
                }
                if (report.isAnyPermissionPermanentlyDenied()) {
                    showMissingPermissionDialog();
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                token.continuePermissionRequest();
            }
        }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toast.makeText(MainActivity.this, "Some Error! ", Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }

    private void showMissingPermissionDialog() {
        android.app.AlertDialog.Builder dialogBuilder = new android.app.AlertDialog.Builder(MainActivity.this);
        dialogBuilder.setTitle(R.string.string_permission_help);
        dialogBuilder.setMessage(R.string.string_permission_help_text);
        dialogBuilder.setNegativeButton(R.string.string_permission_quit, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MainActivity.this.setResult(PERMISSIONS_DENIED);
                MainActivity.this.finish();
            }
        });
        dialogBuilder.setPositiveButton(R.string.string_settings, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.parse(PACKAGE_URL_SCHEME + MainActivity.this.getPackageName()));
                startActivity(intent);
            }
        });
        dialogBuilder.show();
    }

    private void openCameraIntent() {
        Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (pictureIntent.resolveActivity(MainActivity.this.getPackageManager()) != null) {
            //Create a file to store the image
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(MainActivity.this, MainActivity.this.getPackageName() + ".fileprovider", photoFile);
                pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(pictureIntent, CAMERA);
            }
        }
    }

    String imageFilePath;

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir = MainActivity.this.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  // prefix /
                ".jpg",         // suffix /
                storageDir      // directory /
        );
        imageFilePath = image.getAbsolutePath();
        return image;
    }

    public void onClickCalled(OrderModel designModel, int position, String s) {
        Intent intent = new Intent(getApplicationContext(), AddEnquiry.class);
        intent.putExtra("id", designModel.id);
        startActivity(intent);
    }
}