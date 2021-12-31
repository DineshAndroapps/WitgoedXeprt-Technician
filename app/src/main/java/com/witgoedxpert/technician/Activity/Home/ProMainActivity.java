package com.witgoedxpert.technician.Activity.Home;

import static com.witgoedxpert.technician.Forms.LoginActivity.BRANCH_DATA;
import static com.witgoedxpert.technician.Forms.LoginActivity.NAME;
import static com.witgoedxpert.technician.Forms.LoginActivity.SERVER_TOKEN;
import static com.witgoedxpert.technician.Forms.LoginActivity.SHARED_PREFERENCES_NAME;
import static com.witgoedxpert.technician.Forms.LoginActivity.USER;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.common.reflect.TypeToken;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.witgoedxpert.technician.Activity.PRO.CustomCollection;
import com.witgoedxpert.technician.Activity.PRO.ProFormActivity;
import com.witgoedxpert.technician.Activity.MyOrder;
import com.witgoedxpert.technician.Forms.Functions;
import com.witgoedxpert.technician.Forms.LoginActivity;
import com.witgoedxpert.technician.Helper.Callback;
import com.witgoedxpert.technician.Helper.Constant;
import com.witgoedxpert.technician.Helper.FileUtils;
import com.witgoedxpert.technician.Helper.MultiPartRequest;
import com.witgoedxpert.technician.Helper.Tools;
import com.witgoedxpert.technician.Helper.routehelper.FetchURL;
import com.witgoedxpert.technician.Helper.routehelper.TaskLoadedCallback;
import com.witgoedxpert.technician.Location_Services.LocationTrack;
import com.witgoedxpert.technician.Location_Services.TrackingService;
import com.witgoedxpert.technician.R;
import com.witgoedxpert.technician.model.BranchModel;
import com.witgoedxpert.technician.model.EmpModel;
import com.witgoedxpert.technician.model.PlaceLocationModel;
import com.witgoedxpert.technician.model.RouteHomeModel;
import com.witgoedxpert.technician.model.RoutePlaceModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class ProMainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, TaskLoadedCallback {
    private ActionBar actionBar;
    private Toolbar toolbar;
    SharedPreferences sharedPreferences;
    String str_userid, str_name, accessToken;
    LinearLayout ll_bg;
    CardView card_main;
    private String TAG = ProMainActivity.class.getSimpleName();
    ArrayList<EmpModel> designModelArrayList = new ArrayList<>();
    ArrayList<RouteHomeModel> routeHomeModelArrayList = new ArrayList<>();

    ImageButton bt_menu;
    SharedPreferences.Editor editor;
    View headerView;
    public static final int PERMISSIONS_DENIED = 1;
    private static final String PACKAGE_URL_SCHEME = "package:";

    private int GALLERY = 1, CAMERA = 2;
    File str_image_1;


    private RelativeLayout btnCheckIn, btnCheckOut;
    private String halfday, checkInIdentifier;
    //  private LocationTrack mLocationTrack;
    private double latitude, longitude;
    private String parsedDistance;
    String token = "";
    // For background service ...
    private static final int PERMISSIONS_REQUEST = 1;
    SharedPreferences preferences;
    RecyclerView recyclerView_route;
    String str_server_address = "";
    String str_server_lat = "";
    String str_server_long = "";
    /*Map Plots*/

    private GoogleMap mRouteMap;
    private double sLatitude = 0;
    private double sLongitude = 0;
    private ImageView mMyLocation;
    private LatLng origin;
    private Bundle bundle;

    private LocationTrack mLocationTrack;
    private ArrayList<RoutePlaceModel> placeModelArrayList = new ArrayList<>();
    //  ArrayList<RouteModel> proListRoute = new ArrayList<>();
    SimpleDateFormat dateFormatterDesign;
    Calendar calendar;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    ArrayList<PlaceLocationModel> placeLocationModelArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        str_userid = sharedPreferences.getString(USER, "");
        str_name = sharedPreferences.getString(NAME, "");
        accessToken = sharedPreferences.getString(SERVER_TOKEN, "");

        dateFormatterDesign = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        calendar = Calendar.getInstance();
        // ProGetData(dateFormatterDesign.format(calendar.getTime()));

        findViewById(R.id.ll_btm_ll).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), CustomCollection.class);
                startActivity(i);
            }
        });

        Log.d(TAG, "onCreate_size: " + placeModelArrayList.size());
        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);

        if (timeOfDay >= 0 && timeOfDay < 12) {
            ((TextView) findViewById(R.id.txr_proname)).setText("Good Morning, " + str_name);
        } else if (timeOfDay >= 12 && timeOfDay < 16) {
            ((TextView) findViewById(R.id.txr_proname)).setText("Good Afternoon, " + str_name);

        } else if (timeOfDay >= 16 && timeOfDay < 21) {
            ((TextView) findViewById(R.id.txr_proname)).setText("Good Evening, " + str_name);

        } else if (timeOfDay >= 21 && timeOfDay < 24) {
            ((TextView) findViewById(R.id.txr_proname)).setText("Good Night, " + str_name);
        }
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ProMainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            getCurrentLocation();
        }

        findViewById(R.id.fullView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProMainActivity.this, FullMapActivity.class);
                // intent.putExtra("PRO_ID",)
                startActivity(intent);
            }
        });

        SupportMapFragment routeMap = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.routeMap);
        routeMap.getMapAsync(this);


        mMyLocation = (ImageView) findViewById(R.id.mMyLocation);
        mMyLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRouteMap != null && origin != null) {
                    mRouteMap.getUiSettings().setZoomControlsEnabled(true);
                    mRouteMap.animateCamera(CameraUpdateFactory.newLatLngZoom(origin, 9));
                }
            }
        });


        initToolbar();
        initNavigationMenu();
        findViewById(R.id.notification_btn).setVisibility(View.GONE);

        recyclerView_route = findViewById(R.id.recyclerView_route);
        recyclerView_route.setLayoutManager((new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false)));


        //  recyclerView_design.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));

        btnCheckIn = findViewById(R.id.btnCheckIn);
        btnCheckOut = findViewById(R.id.btnCheckOut);


        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.w("", "Fetching FCM registration token failed", task.getException());
                return;
            }

            // Get new FCM registration token
            token = task.getResult();
            SendFCM(token);
            //   getUser(token);

            Log.d("TOKEN_", "onComplete: " + token);
        });

        if (Constant.isNetworkAvailable(getApplicationContext())) {
            ProGetDataCount();
            CheckINStatus();
        } else {
            Toast.makeText(getApplicationContext(), "Internet Connection Not Available", Toast.LENGTH_SHORT).show();
        }

        findViewById(R.id.take_selfie).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog_VerifyShop();
            }
        });
        findViewById(R.id.ll_route).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), PRONextRoute.class);
                startActivity(i);
            }
        });
        findViewById(R.id.ll_today_live).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProGetDataLatLong("", "");
            }
        });
/*
        findViewById(R.id.route_tb).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), ProListOfRoute.class);
                startActivity(i);
            }
        });
*/

        findViewById(R.id.form_tb).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), ProFormActivity.class);
                startActivity(i);
            }
        });

        btnCheckIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckIN();
               /* int newDistance = distance();
                if (newDistance > 1000) {
                    Toast.makeText(getApplicationContext(), "Total Distance: " + newDistance + " Meter", Toast.LENGTH_SHORT).show();
                } else {
                    CheckIN();
                }*/
            }
        });

        btnCheckOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckOUT("0", checkInIdentifier);
               /* int newDistance = distance();
                if (newDistance > 1000) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(ProMainActivity.this);
                    dialog.setTitle("Are you not in range");
                    dialog.setMessage("Are you sure you want to proceed!");
                    dialog.setNegativeButton(R.string.CANCEL, null);
                    dialog.setPositiveButton(R.string.YES, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            CheckOUT("1", checkInIdentifier);
                        }
                    });
                    dialog.setCancelable(false);
                    dialog.show();

                } else {
                    CheckOUT("0", checkInIdentifier);
                }*/
            }
        });


        // For Background service ...
        preferences = getSharedPreferences("Settings", MODE_PRIVATE);
        String status = preferences.getString(Constant.KEY_STATUS, "");

        // restartTrackingService();
    }


    private void getCurrentLocation() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.getFusedLocationProviderClient(ProMainActivity.this)
                .requestLocationUpdates(locationRequest, new LocationCallback() {

                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        LocationServices.getFusedLocationProviderClient(getApplicationContext())
                                .removeLocationUpdates(this);
                        if (locationResult != null && locationResult.getLocations().size() > 0) {
                            int latestlocIndex = locationResult.getLocations().size() - 1;
                            double lati = locationResult.getLocations().get(latestlocIndex).getLatitude();
                            double longi = locationResult.getLocations().get(latestlocIndex).getLongitude();
                            // textLatLong.setText(String.format("Latitude : %s\n Longitude: %s", lati, longi));
                            Log.d(TAG, "onLocationResult: " + lati);
                            Log.d(TAG, "onLocationResult: " + longi);
                            Location location = new Location("providerNA");
                            location.setLongitude(longi);
                            location.setLatitude(lati);
                            fetchaddressfromlocation(location);

                        } else {
                            // progressBar.setVisibility(View.GONE);

                        }
                    }
                }, Looper.getMainLooper());

    }

    private void fetchaddressfromlocation(Location location) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        if (!String.valueOf(sLongitude).equals("0.0") || !String.valueOf(sLongitude).equals("0")) {
            try {
                addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

                String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                String postalCode = addresses.get(0).getPostalCode();
                String knownName = addresses.get(0).getFeatureName(); // Only i
                str_server_address = address + "," + city + "," + state + "," + country + "," + postalCode;
                str_server_lat = String.valueOf(location.getLatitude());
                str_server_long = String.valueOf(location.getLongitude());
                //  Toast.makeText(getApplicationContext(), " ON_CREATE=>Lat:-" + str_server_lat + "Long:-" + str_server_long, Toast.LENGTH_SHORT).show();
            } catch (
                    IOException e) {
                e.printStackTrace();
            }
        }
    }

    void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle("PRO");
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

    void initNavigationMenu() {
        NavigationView nav_view = (NavigationView) findViewById(R.id.nav_view);
        headerView = nav_view.getHeaderView(0);
        TextView navUsername = (TextView) headerView.findViewById(R.id.navUsername);
        TextView navUseremail = (TextView) headerView.findViewById(R.id.navUseremail);
        navUsername.setText(str_name);
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
            Intent i = new Intent(getApplicationContext(), ProMainActivity.class);
            finish();
            startActivity(i);


        } else if (id == R.id.nav_sefie) {
            Intent i = new Intent(getApplicationContext(), MyOrder.class);
            i.putExtra("flag", "0");
            i.putExtra("user_id", str_userid);
            startActivity(i);
            /*
             * */
        } else if (id == R.id.nav_help) {
            DialogForLogout();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void DialogForLogout() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(ProMainActivity.this);
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

    private int distance() {
        String branchData = sharedPreferences.getString(BRANCH_DATA, "");

        Gson gson = new Gson();
        Type type = new TypeToken<List<BranchModel>>() {
        }.getType();
        List<BranchModel> branchModelList = gson.fromJson(branchData, type);

        double branchLat = Double.parseDouble(branchModelList.get(0).getBranchLatitude());
        double branchLng = Double.parseDouble(branchModelList.get(0).getBranchLongitude());
        LatLng StartP = new LatLng(branchLat, branchLng);

        mLocationTrack = new LocationTrack(ProMainActivity.this);
        if (mLocationTrack.canGetLocation()) {
            latitude = mLocationTrack.getLongitude();
            longitude = mLocationTrack.getLatitude();
        } else {
            mLocationTrack.showSettingsAlert();
        }

        LatLng EndP = new LatLng(latitude, longitude);

        int Radius = 6371;// radius of earth in Km
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double km = valueResult / 1;
        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec = Integer.valueOf(newFormat.format(km));
        double meter = valueResult % 1000;
        int meterInDec = Integer.valueOf(newFormat.format(meter));
        Log.i("Radius Value", "" + valueResult + "   KM  " + kmInDec
                + " Meter   " + meterInDec);

        return meterInDec;
    }

    private void CheckINStatus() {
        final ProgressDialog progressDialog = new ProgressDialog(ProMainActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading..");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.checkInStatus,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("response_status", "onResponse: " + response);

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String code = jsonObject.getString("code");
                            String status = jsonObject.getString("status");

                            if (code.equalsIgnoreCase("200")) {
                                checkInIdentifier = jsonObject.optString("check_in_id");

                                if (status.equalsIgnoreCase("true")) {
                                    btnCheckIn.setVisibility(View.GONE);
                                    btnCheckOut.setVisibility(View.VISIBLE);
                                    //findViewById(R.id.route_tb).setVisibility(View.VISIBLE);

                                } else {
                                    btnCheckIn.setVisibility(View.VISIBLE);
                                    btnCheckOut.setVisibility(View.GONE);
                                    // findViewById(R.id.route_tb).setVisibility(View.GONE);
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        progressDialog.dismiss();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                //  Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<String, String>();
                map.put("pro_id", sharedPreferences.getString(USER, ""));
                Log.d("params", "getParams: " + map);
                return map;
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

    private void CheckIN() {

        final ProgressDialog progressDialog = new ProgressDialog(ProMainActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading..");
        progressDialog.show();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("check_in_latitude", str_server_lat);
            jsonObject.put("check_in_longitude", str_server_long);
            jsonObject.put("check_in_address", str_server_address);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.e(TAG, "jsonObject_parameter: " + jsonObject);


        String url = Constant.MAIN + "pro_check_in/";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, url, jsonObject, new com.android.volley.Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "pro_check_in: " + response);
                        try {
                            //TODO: Handle your response here
                            if (!response.equals("null")) {
                                Toast.makeText(ProMainActivity.this, "" + response.getString("Message"), Toast.LENGTH_LONG).show();
                                // Toast.makeText(getApplicationContext(), " CHECK_IN=>Lat:-" + str_server_lat + "Long:-" + str_server_long, Toast.LENGTH_SHORT).show();

                            } else {
                                progressDialog.dismiss();
                                // Toast.makeText(ProMainActivity.this, "Username or password not valid", Toast.LENGTH_LONG).show();
                            }

                        } catch (Exception e) {
                            progressDialog.dismiss();
                            // Toast.makeText(ProMainActivity.this, "Email or password is Wrong", Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                        System.out.print(response);

                    }
                }, new com.android.volley.Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        error.printStackTrace();
                        progressDialog.dismiss();
                        //  Toast.makeText(LoginActivity.this, "Email or password is Wrong", Toast.LENGTH_LONG).show();

                    }


                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> header = new HashMap<>();
                header.put("Authorization", "Bearer " + accessToken);
                return header;
            }

        };

        Volley.newRequestQueue(this).add(jsonObjectRequest);

        /*-------------------------------------------------androapps_server--------------------------------------------------*/
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.checkIn,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("response_checkin", "onResponse: " + response);

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String code = jsonObject.getString("code");

                            if (code.equalsIgnoreCase("200")) {
                                CheckINStatus();

                                preferences.edit().putString(Constant.KEY_STATUS, "start").apply();
                                restartTrackingService();
                                checkForAutoStartPermission();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        progressDialog.dismiss();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                //  Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<String, String>();
                map.put("pro_id", sharedPreferences.getString(USER, ""));
                map.put("check_in_latitude", str_server_lat);
                map.put("check_in_longitude", str_server_long);
                Log.d("params", "getParams: " + map);
                return map;
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

    private void CheckOUT(String halfday, String checkInId) {
        final ProgressDialog progressDialog = new ProgressDialog(ProMainActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading..");
        progressDialog.show();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("check_out_latitude", str_server_lat);
            jsonObject.put("check_out_longitude", str_server_long);
            jsonObject.put("check_out_address", str_server_address);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.e(TAG, "onResponse: " + jsonObject);


        String url = Constant.MAIN + "pro_check_out/";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, url, jsonObject, new com.android.volley.Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "pro_check_in: " + response);

                        try {
                            //TODO: Handle your response here
                            if (!response.equals("null")) {
                                Toast.makeText(ProMainActivity.this, "" + response.getString("Message"), Toast.LENGTH_LONG).show();
                                // Toast.makeText(ProMainActivity.this, " CHECK_OUT=>Lat:-" + str_server_lat + "Long:-" + str_server_long, Toast.LENGTH_SHORT).show();

                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(ProMainActivity.this, "" + response.getString("Message"), Toast.LENGTH_LONG).show();
                            }

                        } catch (Exception e) {
                            progressDialog.dismiss();
                            // Toast.makeText(ProMainActivity.this, "Email or password is Wrong", Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                        System.out.print(response);

                    }
                }, new com.android.volley.Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        error.printStackTrace();
                        progressDialog.dismiss();
                        //  Toast.makeText(LoginActivity.this, "Email or password is Wrong", Toast.LENGTH_LONG).show();

                    }


                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> header = new HashMap<>();
                header.put("Authorization", "Bearer " + accessToken);
                return header;
            }

        };
        ;

        Volley.newRequestQueue(this).add(jsonObjectRequest);


        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.checkOut,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("response_checkout", "onResponse: " + response);

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String code = jsonObject.getString("code");

                            if (code.equalsIgnoreCase("200")) {
                                CheckINStatus();

                                preferences.edit().putString(Constant.KEY_STATUS, "stop").apply();
                                restartTrackingService();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        progressDialog.dismiss();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                // Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<String, String>();
                map.put("check_in_id", checkInId);
                map.put("halfday", halfday);
                map.put("pro_id", sharedPreferences.getString(USER, ""));
                map.put("check_out_latitude", str_server_lat);
                map.put("check_out_longitude", str_server_long);
                Log.d("params", "getParams: " + map);
                return map;
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

    // Background service ...
    private void restartTrackingService() {
        if (TrackingService.isInstanceCreated()) {
            TrackingService.stopService();
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Intent intent = new Intent(ProMainActivity.this, TrackingService.class);
                startForegroundService(intent);
            } else {
                Intent intent = new Intent(ProMainActivity.this, TrackingService.class);
                startService(intent);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        ProGetDataCount();
        // checkForAutoStartPermission();
        SendFCM(token);
    }


    private void checkForAutoStartPermission() {
        SharedPreferences preferences = getSharedPreferences("Settings", MODE_PRIVATE);
        if (checkAutoStartRequired() &&
                preferences.getBoolean(Constant.KEY_ALLOW_AUTO_START, true)) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(Constant.KEY_ALLOW_AUTO_START, false);
            editor.commit();

            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this)
                    .setMessage("App should run in background to receive alerts so please allow auto start permission.")
                    .setCancelable(false)
                    .setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                            addAutoStartup();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            builder.create().show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else {
                Toast.makeText(this, "Permission is denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean checkAutoStartRequired() {
        String manufacturer = android.os.Build.MANUFACTURER.toLowerCase();
        Log.d("TAG", "checkAutoStartRequired: " + manufacturer);
        if ("xiaomi".equalsIgnoreCase(manufacturer)) {
            return true;
        } else if ("oppo".equalsIgnoreCase(manufacturer)) {
            return true;
        } else if ("vivo".equalsIgnoreCase(manufacturer)) {
            return true;
        } else if ("Letv".equalsIgnoreCase(manufacturer)) {
            return true;
        } else return "Honor".equalsIgnoreCase(manufacturer);
    }

    private void addAutoStartup() {

        try {
            Intent intent = new Intent();
            String manufacturer = android.os.Build.MANUFACTURER;
            if ("xiaomi".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"));
            } else if ("oppo".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity"));
            } else if ("vivo".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"));
            } else if ("Letv".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.AutobootManageActivity"));
            } else if ("Honor".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity"));
            }

            List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
            if (list.size() > 0) {
                startActivity(intent);
            }
        } catch (Exception e) {
            Log.e("exc", String.valueOf(e));
        }
    }

    private void SendFCM(String token) {

//    final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
//    progressDialog.setCancelable(false);
//    progressDialog.setMessage("Loading..");
//    progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.pro_fcm,
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

                                //  Toast.makeText(MainActivity.this, "" + message, Toast.LENGTH_SHORT).show();

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
                params.put("pro_id", sharedPreferences.getString(USER, ""));
                params.put("pro_fcm_id", token);
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

        Volley.newRequestQueue(ProMainActivity.this).add(stringRequest);


    }


    public void ProGetDataCount() {
        final ProgressDialog progressDialog = new ProgressDialog(ProMainActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading..");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                Constant.pro_lead_performance_detail_report, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("pro_lead_performance_", "onResponse: " + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    ((TextView) findViewById(R.id.todays_count)).setText(jsonObject.getString("today_collection"));


                    JSONObject yesterday_data = jsonObject.getJSONObject("yesterday_data");
                    ((TextView) findViewById(R.id.yest_count)).setText("Yesterdays :" + yesterday_data.getString("lead_collected"));
                    JSONArray yesterday_data_array = yesterday_data.getJSONArray("status_data");

                    String CounsellingDone = yesterday_data_array.getJSONObject(0).getString("value");
                    String AdmissionDone = yesterday_data_array.getJSONObject(1).getString("value");
                    String ApplicationDone = yesterday_data_array.getJSONObject(2).getString("value");
                    String FollowUp = yesterday_data_array.getJSONObject(3).getString("value");
                    String CallNotMade = yesterday_data_array.getJSONObject(4).getString("value");
                    String CallNotResponding = yesterday_data_array.getJSONObject(5).getString("value");
                    String NotInterested = yesterday_data_array.getJSONObject(6).getString("value");
                    String WrongNumber = yesterday_data_array.getJSONObject(7).getString("value");
                    String DuplicateLead = yesterday_data_array.getJSONObject(8).getString("value");


                    ((TextView) findViewById(R.id.AdmissionDone)).setText(AdmissionDone);
                    ((TextView) findViewById(R.id.ApplicationDone)).setText(ApplicationDone);
                    ((TextView) findViewById(R.id.CounsellingDone)).setText(CounsellingDone);
                    ((TextView) findViewById(R.id.CallNotMade)).setText(CallNotMade);
                    ((TextView) findViewById(R.id.CallNotResponding)).setText(CallNotResponding);
                    ((TextView) findViewById(R.id.NotInterested)).setText(NotInterested);
                    ((TextView) findViewById(R.id.WrongNumber)).setText(WrongNumber);
                    ((TextView) findViewById(R.id.Duplicate)).setText(DuplicateLead);
                    ((TextView) findViewById(R.id.walk_in)).setText(FollowUp);

                    ((TextView) findViewById(R.id.AdmissionDone_n)).setText(yesterday_data_array.getJSONObject(0).getString("key"));
                    ((TextView) findViewById(R.id.ApplicationDone_n)).setText(yesterday_data_array.getJSONObject(1).getString("key"));
                    ((TextView) findViewById(R.id.CounsellingDone_n)).setText(yesterday_data_array.getJSONObject(2).getString("key"));
                    ((TextView) findViewById(R.id.walk_in_n)).setText(yesterday_data_array.getJSONObject(3).getString("key"));
                    ((TextView) findViewById(R.id.CallNotMade_n)).setText(yesterday_data_array.getJSONObject(4).getString("key"));
                    ((TextView) findViewById(R.id.CallNotResponding_n)).setText(yesterday_data_array.getJSONObject(5).getString("key"));
                    ((TextView) findViewById(R.id.NotInterested_n)).setText(yesterday_data_array.getJSONObject(6).getString("key"));
                    ((TextView) findViewById(R.id.WrongNumber_n)).setText(yesterday_data_array.getJSONObject(7).getString("key"));
                    ((TextView) findViewById(R.id.Duplicate_n)).setText(yesterday_data_array.getJSONObject(8).getString("key"));

                    Log.d(TAG, "onResponse: " + CounsellingDone);
                    /*-----------------------------------------------------------------------------------------------------------------------*/


                    JSONObject one_to_seven_day_data = jsonObject.getJSONObject("one_to_seven_day_data");
                    ((TextView) findViewById(R.id.last_seven)).setText("Last 7 Days :" + one_to_seven_day_data.getString("lead_collected"));
                    JSONArray one_to_seven_day_data_array = one_to_seven_day_data.getJSONArray("status_data");

                    String CounsellingDone_1 = one_to_seven_day_data_array.getJSONObject(0).getString("value");
                    String AdmissionDone_1 = one_to_seven_day_data_array.getJSONObject(1).getString("value");
                    String ApplicationDone_1 = one_to_seven_day_data_array.getJSONObject(2).getString("value");
                    String FollowUp_1 = one_to_seven_day_data_array.getJSONObject(3).getString("value");
                    String CallNotMade_1 = one_to_seven_day_data_array.getJSONObject(4).getString("value");
                    String CallNotResponding_1 = one_to_seven_day_data_array.getJSONObject(5).getString("value");
                    String NotInterested_1 = one_to_seven_day_data_array.getJSONObject(6).getString("value");
                    String WrongNumber_1 = one_to_seven_day_data_array.getJSONObject(7).getString("value");
                    String DuplicateLead_1 = one_to_seven_day_data_array.getJSONObject(8).getString("value");

                    ((TextView) findViewById(R.id.AdmissionDone_1)).setText(AdmissionDone_1);
                    ((TextView) findViewById(R.id.ApplicationDone_1)).setText(ApplicationDone_1);
                    ((TextView) findViewById(R.id.CounsellingDone_1)).setText(CounsellingDone_1);
                    ((TextView) findViewById(R.id.CallNotMade_1)).setText(CallNotMade_1);
                    ((TextView) findViewById(R.id.CallNotResponding_1)).setText(CallNotResponding_1);
                    ((TextView) findViewById(R.id.NotInterested_1)).setText(NotInterested_1);
                    ((TextView) findViewById(R.id.WrongNumber_1)).setText(WrongNumber_1);
                    ((TextView) findViewById(R.id.Duplicate_1)).setText(DuplicateLead_1);
                    ((TextView) findViewById(R.id.walk_in_1)).setText(FollowUp_1);


                    ((TextView) findViewById(R.id.AdmissionDone_1_n)).setText(one_to_seven_day_data_array.getJSONObject(0).getString("key"));
                    ((TextView) findViewById(R.id.ApplicationDone_1_n)).setText(one_to_seven_day_data_array.getJSONObject(1).getString("key"));
                    ((TextView) findViewById(R.id.CounsellingDone_1_n)).setText(one_to_seven_day_data_array.getJSONObject(2).getString("key"));
                    ((TextView) findViewById(R.id.walk_in_1_n)).setText(one_to_seven_day_data_array.getJSONObject(3).getString("key"));
                    ((TextView) findViewById(R.id.CallNotMade_1_n)).setText(one_to_seven_day_data_array.getJSONObject(4).getString("key"));
                    ((TextView) findViewById(R.id.CallNotResponding_1_n)).setText(one_to_seven_day_data_array.getJSONObject(5).getString("key"));
                    ((TextView) findViewById(R.id.NotInterested_1_n)).setText(one_to_seven_day_data_array.getJSONObject(6).getString("key"));
                    ((TextView) findViewById(R.id.WrongNumber_1_n)).setText(one_to_seven_day_data_array.getJSONObject(7).getString("key"));
                    ((TextView) findViewById(R.id.Duplicate_1_n)).setText(one_to_seven_day_data_array.getJSONObject(8).getString("key"));


                    /*-----------------------------------------------------------------------------------------------------------------------*/


                    JSONObject thirty_data = jsonObject.getJSONObject("30_day");
                    ((TextView) findViewById(R.id.lst_thirty)).setText("Last 30 Days :" + thirty_data.getString("lead_collected"));
                    JSONArray thirty_data_array = thirty_data.getJSONArray("status_data");

                    String CounsellingDone_2 = thirty_data_array.getJSONObject(0).getString("value");
                    String AdmissionDone_2 = thirty_data_array.getJSONObject(1).getString("value");
                    String ApplicationDone_2 = thirty_data_array.getJSONObject(2).getString("value");
                    String FollowUp_2 = thirty_data_array.getJSONObject(3).getString("value");
                    String CallNotMade_2 = thirty_data_array.getJSONObject(4).getString("value");
                    String CallNotResponding_2 = thirty_data_array.getJSONObject(5).getString("value");
                    String NotInterested_2 = thirty_data_array.getJSONObject(6).getString("value");
                    String WrongNumber_2 = thirty_data_array.getJSONObject(7).getString("value");
                    String DuplicateLead_2 = thirty_data_array.getJSONObject(8).getString("value");


                    ((TextView) findViewById(R.id.AdmissionDone_2)).setText(AdmissionDone_2);
                    ((TextView) findViewById(R.id.ApplicationDone_2)).setText(ApplicationDone_2);
                    ((TextView) findViewById(R.id.CounsellingDone_2)).setText(CounsellingDone_2);
                    ((TextView) findViewById(R.id.CallNotMade_2)).setText(CallNotMade_2);
                    ((TextView) findViewById(R.id.CallNotResponding_2)).setText(CallNotResponding_2);
                    ((TextView) findViewById(R.id.NotInterested_2)).setText(NotInterested_2);
                    ((TextView) findViewById(R.id.WrongNumber_2)).setText(WrongNumber_2);
                    ((TextView) findViewById(R.id.Duplicate_2)).setText(DuplicateLead_2);
                    ((TextView) findViewById(R.id.walk_in_2)).setText(FollowUp_2);

                    ((TextView) findViewById(R.id.AdmissionDone_2_n)).setText(thirty_data_array.getJSONObject(0).getString("key"));
                    ((TextView) findViewById(R.id.ApplicationDone_2_n)).setText(thirty_data_array.getJSONObject(1).getString("key"));
                    ((TextView) findViewById(R.id.CounsellingDone_2_n)).setText(thirty_data_array.getJSONObject(2).getString("key"));
                    ((TextView) findViewById(R.id.CallNotMade_2_n)).setText(thirty_data_array.getJSONObject(4).getString("key"));
                    ((TextView) findViewById(R.id.CallNotResponding_2_n)).setText(thirty_data_array.getJSONObject(5).getString("key"));
                    ((TextView) findViewById(R.id.NotInterested_2_n)).setText(thirty_data_array.getJSONObject(6).getString("key"));
                    ((TextView) findViewById(R.id.WrongNumber_2_n)).setText(thirty_data_array.getJSONObject(7).getString("key"));
                    ((TextView) findViewById(R.id.Duplicate_2_n)).setText(thirty_data_array.getJSONObject(8).getString("key"));
                    ((TextView) findViewById(R.id.walk_in_2_n)).setText(thirty_data_array.getJSONObject(3).getString("key"));


                } catch (Exception e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
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
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> header = new HashMap<>();
                header.put("Authorization", "Bearer " + accessToken);
                Log.d(TAG, "getHeaders: " + header);
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY) {
            if (data != null) {
                Uri selectedImage = data.getData();
                if (resultCode == RESULT_OK) {
                    selfie_imageview.setVisibility(View.VISIBLE);
                    selfie_imageview.setImageURI(selectedImage);

                    InputStream imageStream = null;
                    try {
                        imageStream = ProMainActivity.this.getContentResolver().openInputStream(selectedImage);
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
                selfie_imageview.setVisibility(View.VISIBLE);
                //  selfie_imageview.setImageURI(selectedImage);

                Glide.with(ProMainActivity.this)
                        .load(selectedImage)
                        .into(selfie_imageview);

                InputStream imageStream = null;
                try {
                    imageStream = ProMainActivity.this.getContentResolver().openInputStream(selectedImage);
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
                    Functions.Bind_Location_service(ProMainActivity.this);
                }
            }
            break;
        }

    }


    ImageView selfie_imageview;
    Dialog dialog;

    private void Dialog_VerifyShop() {

        dialog = new Dialog(ProMainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setTitle("");
        dialog.setContentView(R.layout.dialog_pic);
        selfie_imageview = dialog.findViewById(R.id.lnr_imageverification);

        dialog.findViewById(R.id.lnrUploadedImage).setOnClickListener(v -> {
            if (Tools.check_permissions(ProMainActivity.this))
                openCameraIntent();

            //  selectImage();
        });
        dialog.findViewById(R.id.card_view).setOnClickListener(v -> {
            if (Tools.check_permissions(ProMainActivity.this))
                openCameraIntent();

            // selectImage();
        });
        dialog.findViewById(R.id.rlt_verificationupload).setOnClickListener(v -> {
            if (Tools.check_permissions(ProMainActivity.this))
                openCameraIntent();

            // selectImage();
        });

        dialog.findViewById(R.id.bt_update_profile).setOnClickListener(v -> {
            Log.e(TAG, "str_image_1: " + str_image_1);
            if (str_image_1 != null && !str_image_1.equals("") && !str_image_1.equals("null")) {
                if (Constant.isNetworkAvailable(getApplicationContext())) {
                    selfie_pro();
                } else {
                    Toast.makeText(getApplicationContext(), "Internet Connection Not Available", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(ProMainActivity.this, "Select Selfie", Toast.LENGTH_SHORT).show();
            }

        });

        dialog.findViewById(R.id.bt_no).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                str_image_1 = null;
                Log.d(TAG, "onClick: " + str_image_1);
                dialog.dismiss();
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

    private void selectImage() {

        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};


        AlertDialog.Builder builder = new AlertDialog.Builder(ProMainActivity.this, R.style.AlertDialogCustom);
        builder.setTitle("Add Photo!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals("Take Photo")) {
                    if (Tools.check_permissions(ProMainActivity.this))
                        openCameraIntent();
                } else if (options[item].equals("Choose from Gallery")) {

                    if (Tools.check_permissions(ProMainActivity.this)) {
                        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(intent, GALLERY);
                    }
                } else if (options[item].equals("Cancel")) {

                    dialog.dismiss();

                }

            }

        });

        builder.show();

    }



    private void openCameraIntent() {
        Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (pictureIntent.resolveActivity(ProMainActivity.this.getPackageManager()) != null) {
            //Create a file to store the image
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(ProMainActivity.this, ProMainActivity.this.getPackageName() + ".fileprovider", photoFile);
                pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(pictureIntent, CAMERA);
            }
        }
    }

    String imageFilePath;

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir = ProMainActivity.this.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  // prefix /
                ".jpg",         // suffix /
                storageDir      // directory /
        );

        imageFilePath = image.getAbsolutePath();
        return image;
    }

    private void selfie_pro() {

        final ProgressDialog progressDialog = new ProgressDialog(ProMainActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading..");
        progressDialog.show();
        MultiPartRequest request = new MultiPartRequest(ProMainActivity.this, Constant.pro_image_upload, new Callback() {
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
            }
        });
        Log.d(TAG, "selfie_pro: " + sLongitude);


        Log.d("image_file", "selfie_bdm: " + str_image_1);
        if (str_image_1 != null && !str_image_1.equals("") && !str_image_1.equals("null"))
            request.addImageFile("image", String.valueOf(str_image_1), Functions.getRandomString() + ".png");

        request.addString("pro_id", sharedPreferences.getString(USER, ""));
        request.addString("latitude", str_server_lat);
        request.addString("longitude", str_server_long);
        request.addString("address", str_server_address);

        Log.d("Upload_Service", "request=> \n" + request);
        request.execute();

    }

    /*----------------------------------------------------------------------------------------------------------------------------------------------------*/
    @Override
    public void onMapReady(final GoogleMap googleMap) {
        ProGetData(dateFormatterDesign.format(calendar.getTime()));
        //  ProGetDataLatLong("", "");
        mRouteMap = googleMap;
        On_Data_Map();
    }

    private void On_Data_Map() {
        if (mRouteMap != null) {

            if (ActivityCompat.checkSelfPermission(ProMainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ProMainActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
//            mRouteMap.setTrafficEnabled(true);
//            mRouteMap.setIndoorEnabled(true);
            mRouteMap.setMyLocationEnabled(true); // Default My Location icon Hide ...

            mRouteMap.getUiSettings().setCompassEnabled(true);
            mRouteMap.getUiSettings().setRotateGesturesEnabled(true);
            mRouteMap.getUiSettings().setMyLocationButtonEnabled(false);
            mRouteMap.getUiSettings().setRotateGesturesEnabled(true);
            mRouteMap.getUiSettings().setMapToolbarEnabled(false);

            mLocationTrack = new LocationTrack(ProMainActivity.this);
            if (mLocationTrack.canGetLocation()) {
                if (placeModelArrayList != null && placeModelArrayList.size() > 0) {
                    String waypoints = "";
                    int waypointCount = 0;
                    Log.d(TAG, "onMapReady: " + placeModelArrayList.size());
                    Log.d(TAG, "placeLocationModelArrayList: " + placeLocationModelArrayList.size());


                    if (placeModelArrayList.size() > 2) {
                        for (int i = 1; i < placeModelArrayList.size() - 1; i++) {
                            double placeLat = Double.parseDouble(placeModelArrayList.get(i).latitude);
                            double placeLng = Double.parseDouble(placeModelArrayList.get(i).longitude);

                            Location startPoint = new Location("locationA");
                            startPoint.setLatitude(Double.parseDouble(placeModelArrayList.get(i - 1).latitude));
                            startPoint.setLongitude(Double.parseDouble(placeModelArrayList.get(i - 1).longitude));

                            Location endPoint = new Location("locationB");
                            endPoint.setLatitude(placeLat);
                            endPoint.setLongitude(placeLng);
                            double distance = startPoint.distanceTo(endPoint);
                           /*    MarkerOptions smarkerOptions2 = new MarkerOptions();
                            smarkerOptions2.position(new LatLng(placeLat,placeLng));
                            smarkerOptions2.title("Destination");
                            smarkerOptions2.icon(BitmapDescriptorFactory
                                    .fromResource(R.mipmap.pin_pink));
                            mRouteMap.addMarker(smarkerOptions2);*/
                            if (distance > 100) {

                                if (waypointCount <= 23) {
                                    if (i == 1) {
                                        waypoints = waypoints + placeLat + "," + placeLng;
                                    } else {
                                        waypoints = waypoints + "|" + placeLat + "," + placeLng;
                                    }
                                }
                                waypointCount++;

                            }

                        }
                        LatLng startLatLng = new LatLng(Double.parseDouble(placeModelArrayList.get(0).latitude), Double.parseDouble(placeModelArrayList.get(0).longitude));
                        LatLng endLatLng = new LatLng(Double.parseDouble(placeModelArrayList.get(placeModelArrayList.size() - 1).latitude), Double.parseDouble(placeModelArrayList.get(placeModelArrayList.size() - 1).longitude));

                      /*  MarkerOptions smarkerOptions = new MarkerOptions();
                        smarkerOptions.position(startLatLng);
                        smarkerOptions.title("Source");
                        smarkerOptions.icon(BitmapDescriptorFactory
                                .fromResource(R.mipmap.pin_current));
                        mRouteMap.addMarker(smarkerOptions);

                        MarkerOptions smarkerOptions2 = new MarkerOptions();
                        smarkerOptions2.position(endLatLng);
                        smarkerOptions2.title("Destination");
                        smarkerOptions2.icon(BitmapDescriptorFactory
                                .fromResource(R.mipmap.pin_destination));
                        mRouteMap.addMarker(smarkerOptions2);*/

                        String url = getUrl(startLatLng, endLatLng, waypoints.length() > 0 ? waypoints : null, "driving");
                        new FetchURL(ProMainActivity.this).execute(url, "driving", "one");
                        // endLatLng = new LatLng(locationModel.getLocations().get(0).getLatitude(),  locationModel.getLocations().get(0).getLongitide());

                    }
                }
                if (placeLocationModelArrayList != null && placeLocationModelArrayList.size() > 0) {
                    String waypoints = "";
                    int waypointCount = 0;


                    if (placeLocationModelArrayList.size() > 2) {
                        placeLocationModelArrayList = sortLocations(placeLocationModelArrayList, Double.parseDouble(placeLocationModelArrayList.get(0).latitude), Double.parseDouble(placeLocationModelArrayList.get(0).longitude));
                        for (int i = 1; i < placeLocationModelArrayList.size() - 1; i++) {
                            double placeLat = Double.parseDouble(placeLocationModelArrayList.get(i).latitude);
                            double placeLng = Double.parseDouble(placeLocationModelArrayList.get(i).longitude);


                            Location startPoint = new Location("locationA");
                            startPoint.setLatitude(Double.parseDouble(placeLocationModelArrayList.get(i - 1).latitude));
                            startPoint.setLongitude(Double.parseDouble(placeLocationModelArrayList.get(i - 1).longitude));

                            Location endPoint = new Location("locationB");
                            endPoint.setLatitude(placeLat);
                            endPoint.setLongitude(placeLng);

                            /*MarkerOptions smarkerOptions2 = new MarkerOptions();
                            smarkerOptions2.position(new LatLng(placeLat,placeLng));
                            smarkerOptions2.title("Destination");
                            smarkerOptions2.icon(BitmapDescriptorFactory
                                    .fromResource(R.mipmap.pin_pink));
                            mRouteMap.addMarker(smarkerOptions2);*/

                            double distance = startPoint.distanceTo(endPoint);
                            if (distance > 100) {

                                if (waypointCount <= 23) {
                                    if (i == 1) {
                                        waypoints = waypoints + placeLat + "," + placeLng;
                                    } else {
                                        waypoints = waypoints + "|" + placeLat + "," + placeLng;
                                    }
                                }
                                waypointCount++;

                            }
                        }
                        LatLng startLatLng = new LatLng(Double.parseDouble(placeLocationModelArrayList.get(0).latitude), Double.parseDouble(placeLocationModelArrayList.get(0).longitude));
                        LatLng endLatLng = new LatLng(Double.parseDouble(placeLocationModelArrayList.get(placeLocationModelArrayList.size() - 1).latitude), Double.parseDouble(placeLocationModelArrayList.get(placeLocationModelArrayList.size() - 1).longitude));


                        String url = getUrl(startLatLng, endLatLng, waypoints.length() > 0 ? waypoints : null, "driving");
                        new FetchURL(ProMainActivity.this).execute(url, "driving", "two");
                        // endLatLng = new LatLng(locationModel.getLocations().get(0).getLatitude(),  locationModel.getLocations().get(0).getLongitide());

                    }
                }

                if (placeModelArrayList != null && placeModelArrayList.size() > 0) {
                    PolylineOptions polylineOptions = new PolylineOptions();
                    polylineOptions.width(10);
                    List<PatternItem> pattern = Arrays.<PatternItem>asList(new Dot(), new Gap(5f));
                    polylineOptions.pattern(pattern);

                    polylineOptions.color(getResources().getColor(R.color.bg_red));
                    polylineOptions.geodesic(true);

                    LatLngBounds.Builder latLngBounds = new LatLngBounds.Builder();
                    for (int i = 0; i < placeModelArrayList.size(); i++) {
                        double placeLat = Double.parseDouble(placeModelArrayList.get(i).latitude);
                        double placeLng = Double.parseDouble(placeModelArrayList.get(i).longitude);
                        Log.d(TAG, "onMapReady: " + placeLat);
                        Log.d(TAG, "onMapReady: " + placeLng);

                        LatLng latLng = new LatLng(placeLat, placeLng);
                        MarkerOptions dmarkerOptions = new MarkerOptions();
                        dmarkerOptions.position(latLng);
                        dmarkerOptions.title(placeModelArrayList.get(i).name);
                        dmarkerOptions.icon(BitmapDescriptorFactory
                                .fromResource(R.mipmap.pin_destination));
                        mRouteMap.addMarker(dmarkerOptions);
                        latLngBounds.include(latLng);
                        polylineOptions.add(latLng);
                        if (i == 0) {
                            mRouteMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 9));
                        }
                    }

                    mRouteMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds.build(), 9));
                    mRouteMap.addPolyline(polylineOptions);
                }
              /*  if (placeLocationModelArrayList != null && placeLocationModelArrayList.size() > 0) {
                    PolylineOptions polylineOptions = new PolylineOptions();
                    List<PatternItem> pattern = Arrays.<PatternItem>asList(new Dash(30));
                    polylineOptions.pattern(pattern);

                    polylineOptions.color(getResources().getColor(R.color.green));
                    polylineOptions.geodesic(true);
                    polylineOptions.width(10);


                    LatLngBounds.Builder latLngBounds = new LatLngBounds.Builder();
                    for (int i = 0; i < placeLocationModelArrayList.size(); i++) {
                        double placeLat = Double.parseDouble(placeLocationModelArrayList.get(i).latitude);
                        double placeLng = Double.parseDouble(placeLocationModelArrayList.get(i).longitude);
                        Log.d(TAG, "onMapReady: " + placeLat);
                        Log.d(TAG, "onMapReady: " + placeLng);

                        LatLng latLng = new LatLng(placeLat, placeLng);

                       *//* MarkerOptions dmarkerOptions = new MarkerOptions();
                        dmarkerOptions.position(latLng);
                        dmarkerOptions.title("");
                        dmarkerOptions.icon(BitmapDescriptorFactory
                                .fromResource(R.mipmap.pin_pink));
                        mRouteMap.addMarker(dmarkerOptions);*//*

                        latLngBounds.include(latLng);
                        polylineOptions.add(latLng);
                        if (i == 0) {
                            mRouteMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                        }
                    }

                    mRouteMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds.build(), 15));
                    mRouteMap.addPolyline(polylineOptions);
                }*/
//                style = MapStyleOptions.loadRawResourceStyle(ProMainActivity.this, R.raw.mapstyle_silver);
//                mRouteMap.setMapStyle(style);

            } else {
                mLocationTrack.showSettingsAlert();
            }
        }

    }

    private String getUrl(LatLng origin, LatLng dest, String waypoints, String directionMode) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Mode
        String mode = "mode=" + directionMode;
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        if (waypoints != null) {
            String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&waypoints=" + waypoints + "&key=AIzaSyAhJ2jyMQywcUnCOp6_7dYsJ_f_qZsGyf8";
            return url;
        } else {
            String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=AIzaSyAhJ2jyMQywcUnCOp6_7dYsJ_f_qZsGyf8";
            return url;
        }

    }

    Polyline currentPolyline1;
    Polyline currentPolyline2;

    @Override
    public void onTaskDone(String directionType, Object... values) {
        if (directionType.equalsIgnoreCase("one")) {
            if (currentPolyline1 != null)
                currentPolyline1.remove();

            currentPolyline1 = mRouteMap.addPolyline((PolylineOptions) values[0]);
            LatLngBounds.Builder latLngBounds = new LatLngBounds.Builder();
            for (LatLng latLng : ((PolylineOptions) values[0]).getPoints()) {
                latLngBounds.include(latLng);
            }
            mRouteMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds.build(), 15));
        } else {
            if (currentPolyline2 != null)
                currentPolyline2.remove();

            currentPolyline2 = mRouteMap.addPolyline((PolylineOptions) values[0]);
            LatLngBounds.Builder latLngBounds = new LatLngBounds.Builder();
            if (((PolylineOptions) values[0]).getPoints().size() > 2) {

                for (int i = 0; i < ((PolylineOptions) values[0]).getPoints().size(); i++) {
                    LatLng latLng = ((PolylineOptions) values[0]).getPoints().get(i);
                 /*  MarkerOptions smarkerOptions = new MarkerOptions();
                   if(i==0) {
                       smarkerOptions.position(new LatLng(latLng.latitude,latLng.longitude));
                       smarkerOptions.title("Source");
                       smarkerOptions.icon(BitmapDescriptorFactory
                               .fromResource(R.mipmap.pin_current));
                   }else if(i==((PolylineOptions)values[0]).getPoints().size()-1) {
                       smarkerOptions.position(new LatLng(latLng.latitude,latLng.longitude));
                       smarkerOptions.title("Destination");
                       smarkerOptions.icon(BitmapDescriptorFactory
                               .fromResource(R.mipmap.pin_destination));
                   }else {
                       smarkerOptions.position(new LatLng(latLng.latitude,latLng.longitude));
                       smarkerOptions.title("Waypoint"+i);
                       smarkerOptions.icon(BitmapDescriptorFactory
                               .fromResource(R.mipmap.pin_pink));
                   }
                   mRouteMap.addMarker(smarkerOptions);*/
                    latLngBounds.include(latLng);
                }


            }

            mRouteMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds.build(), 15));
        }

    }

    @Override
    public void onError() {
        Toast.makeText(ProMainActivity.this, "Unable to draw route", Toast.LENGTH_LONG).show();
    }


    public void ProGetData(String selectedDate) {
        placeModelArrayList.clear();
        final ProgressDialog progressDialog = new ProgressDialog(ProMainActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading..");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.ProListPlace, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("list_route_pro", "onResponse: " + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String message = jsonObject.getString("message");
                    String code = jsonObject.getString("code");
                    if (code.equals("200")) {
                        progressDialog.dismiss();

                        JSONArray jsonArrayvideo = jsonObject.optJSONArray("list");
                        if (jsonArrayvideo != null && jsonArrayvideo.length() > 0) {
                            for (int i = 0; i < jsonArrayvideo.length(); i++) {
                                RoutePlaceModel bmi_model = new RoutePlaceModel();
                                JSONObject BMIReport = jsonArrayvideo.getJSONObject(i);

                                bmi_model.id = BMIReport.getString("id");
                                bmi_model.bde_id = BMIReport.getString("bde_id");
                                bmi_model.route_id = BMIReport.getString("route_id");
                                bmi_model.latitude = BMIReport.getString("latitude");
                                bmi_model.longitude = BMIReport.getString("longitude");
                                bmi_model.name = BMIReport.getString("name");
                                bmi_model.createdDate = BMIReport.getString("createdDate");

                                placeModelArrayList.add(bmi_model);
                            }
                            On_Data_Map();
                            Log.d(TAG, "onResponse_array_size: " + placeModelArrayList.size());
                        }
                    } else {

                        progressDialog.dismiss();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
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
                params.put("pro_id", str_userid);
                // params.put("date", selectedDate);
                params.put("date", selectedDate);
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

    public void ProGetDataLatLong(String server_pro_id, String server_assign_date) {
        final ProgressDialog progressDialog = new ProgressDialog(ProMainActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading..");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.proLocationList, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("ProGetDataLatLong", "onResponse: " + response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String message = jsonObject.getString("message");
                    String code = jsonObject.getString("code");
                    if (code.equals("200")) {
                        progressDialog.dismiss();

                        JSONArray jsonArrayvideo = jsonObject.optJSONArray("location");
                        //JSONArray jsonArrayvideo = jsonObject.optJSONArray("location");
                        Log.d(TAG, "onResponse: " + jsonArrayvideo);
                        if (jsonArrayvideo != null && jsonArrayvideo.length() > 0) {
                            placeLocationModelArrayList = new ArrayList<>();
                            JSONArray jsonArray = jsonObject.getJSONArray("location");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                PlaceLocationModel placeModel = new PlaceLocationModel();
                                JSONObject BMIReport = jsonArray.getJSONObject(i);

                                placeModel.id = BMIReport.getString("id");
                                placeModel.pro_id = BMIReport.getString("pro_id");
                                placeModel.latitude = BMIReport.getString("latitude");
                                placeModel.longitude = BMIReport.getString("longitude");
                                placeModel.isDeleted = BMIReport.getString("isDeleted");
                                placeLocationModelArrayList.add(placeModel);

                            }
                            On_Data_Map();
                            //Adapter add here if needed


                        } else {
                            //   findViewById(R.id.no_slot).setVisibility(View.VISIBLE);
                        }

                    } else {
                        progressDialog.dismiss();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
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
                SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);
                String str_userid = sharedPreferences.getString(USER, "");
                params.put("pro_id", str_userid);
                SimpleDateFormat dateFormatterDesign = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                params.put("date", dateFormatterDesign.format(new Date()));
                Log.d("ProGetDataLatLong", "getParams: " + params);
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

    public static ArrayList<PlaceLocationModel> sortLocations(ArrayList<PlaceLocationModel> locations, final double myLatitude, final double myLongitude) {
        Comparator comp = new Comparator<PlaceLocationModel>() {
            @Override
            public int compare(PlaceLocationModel o, PlaceLocationModel o2) {
                float[] result1 = new float[3];
                android.location.Location.distanceBetween(myLatitude, myLongitude, Double.parseDouble(o.getLatitude()), Double.parseDouble(o.getLongitude()), result1);
                Float distance1 = result1[0];

                float[] result2 = new float[3];
                android.location.Location.distanceBetween(myLatitude, myLongitude, Double.parseDouble(o2.getLatitude()), Double.parseDouble(o2.getLongitude()), result2);
                Float distance2 = result2[0];

                return distance1.compareTo(distance2);
            }
        };


        Collections.sort(locations, comp);
        return locations;
    }
}