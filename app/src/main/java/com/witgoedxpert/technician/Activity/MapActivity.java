package com.witgoedxpert.technician.Activity;

import static com.witgoedxpert.technician.Forms.LoginActivity.SHARED_PREFERENCES_NAME;
import static com.witgoedxpert.technician.Forms.LoginActivity.USER;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
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
import com.witgoedxpert.technician.Helper.Constant;
import com.witgoedxpert.technician.Helper.routehelper.FetchURL;
import com.witgoedxpert.technician.Helper.routehelper.TaskLoadedCallback;
import com.witgoedxpert.technician.Helper.routehelper.TaskLoadedCallback_Pro;
import com.witgoedxpert.technician.Location_Services.LocationTrack;
import com.witgoedxpert.technician.R;
import com.witgoedxpert.technician.model.PhotoModel;
import com.witgoedxpert.technician.model.PlaceLocationModel;
import com.witgoedxpert.technician.model.PlaceModel;
import com.witgoedxpert.technician.model.ProLatLngModelDatum;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback, TaskLoadedCallback, TaskLoadedCallback_Pro {

    private GoogleMap mRouteMap;
    private double sLatitude = 0;
    private double sLongitude = 0;
    private ImageView mMyLocation;
    private LatLng origin;
    private Bundle bundle;

    private LocationTrack mLocationTrack;
    private ArrayList<PlaceModel> placeModelArrayList;
    private ArrayList<PhotoModel> photoModelArrayList;
    ArrayList<PlaceLocationModel> place_location_ArrayList = new ArrayList<>();
    ArrayList<PlaceLocationModel> pro_placeLocationModelArrayList = new ArrayList<>();
    private ArrayList<ProLatLngModelDatum> proLatLngModelDatumArrayList;
    private String routeID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pro_map);

        SupportMapFragment routeMap = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.routeMap);
        routeMap.getMapAsync(this);


        mMyLocation = (ImageView) findViewById(R.id.mMyLocation);
        mMyLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRouteMap != null && origin != null) {
                    mRouteMap.animateCamera(CameraUpdateFactory.newLatLngZoom(origin, 9));
                }
            }
        });

        bundle = getIntent().getExtras();
        if (bundle != null) {
            placeModelArrayList = bundle.getParcelableArrayList(Constant.DATA);
            photoModelArrayList = bundle.getParcelableArrayList(Constant.DATA_PHOTO);
            place_location_ArrayList = bundle.getParcelableArrayList(Constant.DATA_PLACE_LAT_LONG);
            routeID = bundle.getString(Constant.ROUTE_ID);
        }

/*        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.proLatLong, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("list_proLatLng", "onResponse: " + response);
                try {

                    Gson gson = new Gson();
                    Reader reader = new StringReader(response);
                    ProLatLngModel proLatLngModel = gson.fromJson(reader, ProLatLngModel.class);

                    proLatLngModelDatumArrayList = new ArrayList<>();
                    for (ProLatLngModelDatum proLatLngModelDatum : proLatLngModel.getData()) {
                        proLatLngModelDatumArrayList.add(proLatLngModelDatum);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap();
                params.put("route_id", "20"); // str_route_id
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
        requestQueue.add(stringRequest);*/
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mRouteMap = googleMap;
        ProGetDataLatLong("", "");
        if (mRouteMap != null) {

            if (ActivityCompat.checkSelfPermission(MapActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

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

            mLocationTrack = new LocationTrack(MapActivity.this);
            if (mLocationTrack.canGetLocation()) {
              /*  sLatitude = mLocationTrack.getLatitude();
                sLongitude = mLocationTrack.getLongitude();*/

                Log.d("SourceLatLng: ", Constant.LATITUDE_SEVER + " " + Constant.LONGITUDE_SERVER);

                origin = new LatLng(Double.parseDouble(Constant.LATITUDE_SEVER), Double.parseDouble(Constant.LONGITUDE_SERVER));

                MarkerOptions smarkerOptions = new MarkerOptions();
                smarkerOptions.position(origin);
                smarkerOptions.title("Current Location");
                smarkerOptions.icon(BitmapDescriptorFactory
                        .fromResource(R.mipmap.pin_current));
                mRouteMap.addMarker(smarkerOptions);


                //PRO completed route List
                /*
                if (place_location_ArrayList != null && place_location_ArrayList.size() > 0) {

                    PolylineOptions polylineOptions_place_lat = new PolylineOptions();
                    polylineOptions_place_lat.width(10);
                    List<PatternItem> pattern_latlong = Arrays.<PatternItem>asList(new Dot(), new Gap(5f));
                    polylineOptions_place_lat.pattern(pattern_latlong);

                    polylineOptions_place_lat.color(getResources().getColor(R.color.pink));
                    polylineOptions_place_lat.geodesic(true);

                    LatLngBounds.Builder latLngBounds_img = new LatLngBounds.Builder();
                    for (int i = 0; i < place_location_ArrayList.size(); i++) {
                        Log.d("", "onMapReady: " + place_location_ArrayList.get(i).getLatitude());
                        Log.d("", "onMapReady: " + place_location_ArrayList.get(i).getLongitude());
                        double placeLat = Double.parseDouble(place_location_ArrayList.get(i).getLongitude());
                        double placeLng = Double.parseDouble(place_location_ArrayList.get(i).getLatitude());

                        LatLng latLng = new LatLng(placeLng, placeLat);
                        MarkerOptions dmarkerOptions = new MarkerOptions();
                        dmarkerOptions.position(latLng);
                        dmarkerOptions.title(place_location_ArrayList.get(i).getCreatedDate());

                        dmarkerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.pin_pink));
                        mRouteMap.addMarker(dmarkerOptions);
                        latLngBounds_img.include(latLng);
                        polylineOptions_place_lat.add(latLng);

                    }

                    mRouteMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds_img.build(), 9));
                    mRouteMap.addPolyline(polylineOptions_place_lat);

                }
*/
                //this is Pro taking selfie lat long
                /* if (photoModelArrayList != null && photoModelArrayList.size() > 0) {

                    PolylineOptions polylineOptions_img = new PolylineOptions();
                    polylineOptions_img.width(10);
                    List<PatternItem> pattern_img = Arrays.<PatternItem>asList(new Dot(), new Gap(5f));
                    polylineOptions_img.pattern(pattern_img);

                    polylineOptions_img.color(getResources().getColor(R.color.colorPrimary));
                    polylineOptions_img.geodesic(true);

                    LatLngBounds.Builder latLngBounds_img = new LatLngBounds.Builder();
                    for (int i = 0; i < photoModelArrayList.size(); i++) {
                        Log.d("", "onMapReady: " + photoModelArrayList.get(i).getLatitude());
                        Log.d("", "onMapReady: " + photoModelArrayList.get(i).getLongitude());
                        double placeLat = Double.parseDouble(photoModelArrayList.get(i).getLatitude());
                        double placeLng = Double.parseDouble(photoModelArrayList.get(i).getLongitude());

                        LatLng latLng = new LatLng(placeLng, placeLat);
                        MarkerOptions dmarkerOptions = new MarkerOptions();
                        dmarkerOptions.position(latLng);
                        dmarkerOptions.title(photoModelArrayList.get(i).getCreatedDate());

                        dmarkerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.pin_blue));
                        mRouteMap.addMarker(dmarkerOptions);
                        latLngBounds_img.include(latLng);
                        polylineOptions_img.add(latLng);
//                        if (i == 0) {
//                            mRouteMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 9));
//                        }
                    }

                    mRouteMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds_img.build(), 9));
                    mRouteMap.addPolyline(polylineOptions_img);

                }*/
                //this is route that are Assign to PRO

                if (placeModelArrayList != null && placeModelArrayList.size() > 0) {
                    PolylineOptions polylineOptions = new PolylineOptions();
                    polylineOptions.width(10);
                    List<PatternItem> pattern = Arrays.<PatternItem>asList(new Dot(), new Gap(5f));
                    polylineOptions.pattern(pattern);

                    polylineOptions.color(getResources().getColor(R.color.bg_red));
                    polylineOptions.geodesic(true);

                    LatLngBounds.Builder latLngBounds = new LatLngBounds.Builder();
                    for (int i = 0; i < placeModelArrayList.size(); i++) {
                        double placeLat = Double.parseDouble(placeModelArrayList.get(i).getLatitude());
                        double placeLng = Double.parseDouble(placeModelArrayList.get(i).getLongitude());

                        LatLng latLng = new LatLng(placeLat, placeLng);
                        MarkerOptions dmarkerOptions = new MarkerOptions();
                        dmarkerOptions.position(latLng);
                        dmarkerOptions.title("(Route No:-" + placeModelArrayList.get(i).getSequence_id() + ")" + placeModelArrayList.get(i).getName() + "," + placeModelArrayList.get(i).getPhone());

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


                if (proLatLngModelDatumArrayList != null && proLatLngModelDatumArrayList.size() > 0) {
                    PolylineOptions polylineOptions1 = new PolylineOptions();
                    polylineOptions1.width(10);
                    List<PatternItem> pattern1 = Arrays.<PatternItem>asList(new Dot(), new Gap(5f));
                    polylineOptions1.pattern(pattern1);

                    polylineOptions1.color(getResources().getColor(R.color.green));
                    polylineOptions1.geodesic(true);

                    LatLngBounds.Builder latLngBounds1 = new LatLngBounds.Builder();
                    for (int i = 0; i < proLatLngModelDatumArrayList.size(); i++) {
                        double placeLat = Double.parseDouble(proLatLngModelDatumArrayList.get(i).getLatitude());
                        double placeLng = Double.parseDouble(proLatLngModelDatumArrayList.get(i).getLongitude());

                        LatLng latLng = new LatLng(placeLat, placeLng);
                        MarkerOptions dmarkerOptions = new MarkerOptions();
                        dmarkerOptions.position(latLng);
                        dmarkerOptions.title("Destination");
                        dmarkerOptions.icon(BitmapDescriptorFactory
                                .fromResource(R.mipmap.pin_destination));
                        mRouteMap.addMarker(dmarkerOptions);
                        latLngBounds1.include(latLng);
                        polylineOptions1.add(latLng);
//                    if (i == 0) {
//                        mRouteMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 9));
//                    }
                    }

                    mRouteMap.addPolyline(polylineOptions1);
                }


//                style = MapStyleOptions.loadRawResourceStyle(ProMapActivity.this, R.raw.mapstyle_silver);
//                mRouteMap.setMapStyle(style);

            } else {
                mLocationTrack.showSettingsAlert();
            }
        }
    }


    public void ProGetDataLatLong(String server_pro_id, String server_assign_date) {
        final ProgressDialog progressDialog = new ProgressDialog(MapActivity.this);
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
                        if (jsonArrayvideo != null && jsonArrayvideo.length() > 0) {
                            pro_placeLocationModelArrayList = new ArrayList<>();
                            JSONArray jsonArray = jsonObject.getJSONArray("location");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                PlaceLocationModel placeModel = new PlaceLocationModel();
                                JSONObject BMIReport = jsonArray.getJSONObject(i);

                                placeModel.id = BMIReport.getString("id");
                                placeModel.pro_id = BMIReport.getString("pro_id");
                                placeModel.latitude = BMIReport.getString("latitude");
                                placeModel.longitude = BMIReport.getString("longitude");
                                placeModel.isDeleted = BMIReport.getString("isDeleted");
                                pro_placeLocationModelArrayList.add(placeModel);

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
                //  params.put("pro_id", "433");
                SimpleDateFormat dateFormatterDesign = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                params.put("date", dateFormatterDesign.format(new Date()));
                //params.put("date", "2021-09-23");
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

    public static ArrayList<PlaceModel> sortLocations_assignroute(ArrayList<PlaceModel> locations, final double myLatitude, final double myLongitude) {
        Comparator comp = new Comparator<PlaceModel>() {
            @Override
            public int compare(PlaceModel o, PlaceModel o2) {
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

    private ProgressDialog progressDialog2;
    private ProgressDialog progressDialog4;

    private void On_Data_Map() {
        if (mRouteMap != null) {

            if (ActivityCompat.checkSelfPermission(MapActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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

            LocationTrack mLocationTrack = new LocationTrack(MapActivity.this);



            /*------------------------------------------------------------------------------------------------------------------------------------------------------------------*/
        /*    //this is for plot Line assign route/places to pro
            if (placeModelArrayList != null && placeModelArrayList.size() > 0) {
                String waypoints = "";
                int waypointCount = 0;

                if (placeModelArrayList.size() > 2) {
                    placeModelArrayList = sortLocations_assignroute(placeModelArrayList, Double.parseDouble(placeModelArrayList.get(0).latitude), Double.parseDouble(placeModelArrayList.get(0).longitude));
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


                    String url = getUrl(startLatLng, endLatLng, waypoints.length() > 0 ? waypoints : null, "driving");
                    Log.d("ProGetDataLatLong", "getParams: " + url);
                    new FetchURL_Pro(MapActivity.this).execute(url, "driving", "two");
                    // endLatLng = new LatLng(locationModel.getLocations().get(0).getLatitude(),  locationModel.getLocations().get(0).getLongitide());

                }
            }*/

            /*------------------------------------------------------------------------------------------------------------------------------------------------------------------*/

            //plot line to that pro completed route
            if (pro_placeLocationModelArrayList != null && pro_placeLocationModelArrayList.size() > 0) {
                String waypoints = "";
                int waypointCount = 0;



                if (pro_placeLocationModelArrayList.size() > 2) {
                    pro_placeLocationModelArrayList = sortLocations(pro_placeLocationModelArrayList, Double.parseDouble(pro_placeLocationModelArrayList.get(0).latitude), Double.parseDouble(pro_placeLocationModelArrayList.get(0).longitude));
                    for (int i = 1; i < pro_placeLocationModelArrayList.size() - 1; i++) {
                        double placeLat = Double.parseDouble(pro_placeLocationModelArrayList.get(i).latitude);
                        double placeLng = Double.parseDouble(pro_placeLocationModelArrayList.get(i).longitude);


                        Location startPoint = new Location("locationA");
                        startPoint.setLatitude(Double.parseDouble(pro_placeLocationModelArrayList.get(i - 1).latitude));
                        startPoint.setLongitude(Double.parseDouble(pro_placeLocationModelArrayList.get(i - 1).longitude));

                        Location endPoint = new Location("locationB");
                        endPoint.setLatitude(placeLat);
                        endPoint.setLongitude(placeLng);


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
                    LatLng startLatLng = new LatLng(Double.parseDouble(pro_placeLocationModelArrayList.get(0).latitude), Double.parseDouble(pro_placeLocationModelArrayList.get(0).longitude));
                    LatLng endLatLng = new LatLng(Double.parseDouble(pro_placeLocationModelArrayList.get(pro_placeLocationModelArrayList.size() - 1).latitude), Double.parseDouble(pro_placeLocationModelArrayList.get(pro_placeLocationModelArrayList.size() - 1).longitude));
                    progressDialog2 = new ProgressDialog(MapActivity.this);
                    progressDialog2.setCancelable(false);
                    progressDialog2.setMessage("Loading Pro Route..");
                    progressDialog2.show();

                    String url = getUrl(startLatLng, endLatLng, waypoints.length() > 0 ? waypoints : null, "driving");
                    Log.d("ProGetDataLatLong", "getParams: " + url);
                    new FetchURL(MapActivity.this).execute(url, "driving", "two");
                    // endLatLng = new LatLng(locationModel.getLocations().get(0).getLatitude(),  locationModel.getLocations().get(0).getLongitide());

                }
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

    Polyline currentPolyline2;

    @Override
    public void onTaskDone(String directionType, Object... values) {
        progressDialog2.dismiss();

        if (currentPolyline2 != null)
            currentPolyline2.remove();

        mRouteMap.addPolyline((PolylineOptions) values[0]).setColor(getResources().getColor(R.color.pink));
        LatLngBounds.Builder latLngBounds = new LatLngBounds.Builder();

        if (((PolylineOptions) values[0]).getPoints().size() > 2) {
            for (int i = 0; i < ((PolylineOptions) values[0]).getPoints().size(); i++) {
                LatLng latLng = ((PolylineOptions) values[0]).getPoints().get(i);
                latLngBounds.include(latLng);
            }

        }
        mRouteMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds.build(), 15));
    }

    @Override
    public void onError() {
     //   Toast.makeText(MapActivity.this, "Unable to draw  Pro route", Toast.LENGTH_LONG).show();
        progressDialog2.dismiss();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onTaskDone_Pro(String type, Object... values) {
        progressDialog2.dismiss();

        if (currentPolyline2 != null)
            currentPolyline2.remove();

        mRouteMap.addPolyline((PolylineOptions) values[0]).setColor(getResources().getColor(R.color.green));
        LatLngBounds.Builder latLngBounds = new LatLngBounds.Builder();

        if (((PolylineOptions) values[0]).getPoints().size() > 2) {
            for (int i = 0; i < ((PolylineOptions) values[0]).getPoints().size(); i++) {
                LatLng latLng = ((PolylineOptions) values[0]).getPoints().get(i);
                latLngBounds.include(latLng);
            }

        }
        mRouteMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds.build(), 15));
    }

    @Override
    public void onError_Pro() {
        Toast.makeText(MapActivity.this, "Unable to draw  Pro route", Toast.LENGTH_LONG).show();
        progressDialog2.dismiss();
    }
}

