package com.androapp.technician.Activity.Home;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.androapp.technician.Helper.Constant;
import com.androapp.technician.Helper.routehelper.FetchURL;
import com.androapp.technician.Helper.routehelper.TaskLoadedCallback;
import com.androapp.technician.Location_Services.LocationTrack;
import com.androapp.technician.R;
import com.androapp.technician.model.PlaceLocationModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.androapp.technician.Forms.LoginActivity.SHARED_PREFERENCES_NAME;
import static com.androapp.technician.Forms.LoginActivity.USER;

public class FullMapActivity extends AppCompatActivity implements OnMapReadyCallback, TaskLoadedCallback {

    private GoogleMap mRouteMap;
    ArrayList<PlaceLocationModel> placeLocationModelArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_map);
        SupportMapFragment routeMap = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.routeMap);
        routeMap.getMapAsync(this);
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mRouteMap = googleMap;
        ProGetDataLatLong("", "");
    }

    public void ProGetDataLatLong(String server_pro_id, String server_assign_date) {
        final ProgressDialog progressDialog = new ProgressDialog(FullMapActivity.this);
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
                        // Log.d(TAG, "onResponse: " + jsonArrayvideo);
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

    private ProgressDialog progressDialog2;

    private void On_Data_Map() {
        if (mRouteMap != null) {

            if (ActivityCompat.checkSelfPermission(FullMapActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(FullMapActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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

            LocationTrack mLocationTrack = new LocationTrack(FullMapActivity.this);

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
                    progressDialog2 = new ProgressDialog(FullMapActivity.this);
                    progressDialog2.setCancelable(false);
                    progressDialog2.setMessage("Loading..");
                    progressDialog2.show();


                    String url = getUrl(startLatLng, endLatLng, waypoints.length() > 0 ? waypoints : null, "driving");
                    Log.d("ProGetDataLatLong", "getParams: " + url);
                    new FetchURL(FullMapActivity.this).execute(url, "driving", "two");
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

    @Override
    public void onError() {
        Toast.makeText(FullMapActivity.this, "Unable to draw route", Toast.LENGTH_LONG).show();
        progressDialog2.dismiss();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}