package com.androapp.technician.Location_Services.MapUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Address;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.androapp.technician.Forms.Functions;
import com.androapp.technician.Helper.Variables;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.androapp.technician.R;


public class MapsFragment extends Fragment implements GoogleMap.OnMarkerClickListener, View.OnClickListener, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, OnMapReadyCallback, OnMapInterTouch  {

    SupportMapFragment mapFragment;

    public static boolean mMapIsTouched = false;
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    int ConstZoomValue = 15;
    double source_address_lat, source_address_long;
    public static boolean mIntentInProgress;
    public static ConnectionResult mConnectionResult;
    private static final int RC_SIGN_IN = 0;
    String source_address;
    Activity context;

    View view;

    @Nullable
    @Override
    public View onCreateView( LayoutInflater inflater,
                              ViewGroup container,
                              Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_maps, container, false);
        context = getActivity();

        mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(this::onMapReady);


        Initialization();

        return view;
    }

    private void Initialization() {
        view.findViewById(R.id.imgMyLocation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myCurrentLocation();
            }
        });

        view.findViewById(R.id.imgback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        view.findViewById(R.id.lnr_searresult).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String place_address = Functions.getStringFromTextview(((TextView)view.findViewById(R.id.tv_result)));

                if(!Functions.checkStringisValid(place_address))
                    return;

                Variables.select_latitude = source_address_lat;
                Variables.select_longitude = source_address_long;
                Variables.select_address = place_address;
                String address = Variables.select_address;
                if(address != null && !address.equals(""))
                {
                    Intent intent = new Intent();
                    intent.putExtra("latitude",Variables.select_latitude);
                    intent.putExtra("longitude",Variables.select_longitude);
                    intent.putExtra("address",""+address);
                    context.setResult(Activity.RESULT_OK,intent);
                    context.finish();
                }

            }
        });

        TypeFace();

    }

    private void TypeFace() {

    /*    StyleDrawable crossDrawable = Functions.setFontIcon(context,null,R.string.icon_crosshairs);
        StyleDrawable closeDrawable = Functions.setFontIcon(context,null,R.string.icon_close);
        crossDrawable.setTextColor(Functions.getColorThin(R.color.white,context));
        closeDrawable.setTextColor(Functions.getColorThin(R.color.white,context));

        ((ImageView) view.findViewById(R.id.imgback)).setImageDrawable(closeDrawable);
        ((ImageView) view.findViewById(R.id.imgMyLocation)).setImageDrawable(crossDrawable);*/

    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
          }

    @Override
    public void onResume() {
        super.onResume();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                myCurrentLocation();
            }
        },1000);

    }

    @Override
    public void onClick(View v) {
        switch (view.getId()){

            case R.id.imgMyLocation:
                Toast.makeText(context, "My Location", Toast.LENGTH_SHORT).show();
                myCurrentLocation();
                break;

        }

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (!mIntentInProgress) {
            mConnectionResult = connectionResult;
            resolveSignInError(context);
        }
    }

    public void resolveSignInError(Context cc) {
        if (mConnectionResult.hasResolution()) {
            try {
                mIntentInProgress = true;
                ((Activity) cc).startIntentSenderForResult(mConnectionResult.getResolution()
                        .getIntentSender(), RC_SIGN_IN, null, 0, 0, 0);
            } catch (IntentSender.SendIntentException e) {

                mIntentInProgress = false;
                mGoogleApiClient.connect();
            }
        }
    }


    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        if (marker.isInfoWindowShown()) {
            marker.hideInfoWindow();
        } else {
            marker.showInfoWindow();
        }
        return true;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        mMap = googleMap;
        mMap.getUiSettings().setAllGesturesEnabled(false);
        mMap.getUiSettings().setScrollGesturesEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);


//        if (source_address_lat != 0.0 && source_address_long != 0.0) {
//
//            setAddress(source_address_lat, source_address_long,context);
//            setupDefaultScreen(source_address_lat,source_address_long);
//        }

        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                myCurrentLocation();
            }
        });


        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                try {

                    LatLng latlong = googleMap.getProjection().getVisibleRegion().latLngBounds.getCenter();
                    if (latlong.latitude != 0) {
                        source_address_lat = latlong.latitude;
                        source_address_long = latlong.longitude;
                        setAddress(source_address_lat, source_address_long,context);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        myCurrentLocation();
    }

    private String setAddress(double lat, double long_temp,Context context) {

        String addressstr = "Select location";
        try {
            Address address = Functions.getAddressFromLatLong(lat,long_temp,context);

            if(address != null)
            {
                String pincode = address.getPostalCode();
                addressstr = Functions.getLocationFromGeoAddress(address);
                source_address = addressstr;
                ((TextView)view.findViewById(R.id.tv_result)).setText(""+addressstr);
            }

            } catch (Exception e) {
                e.printStackTrace();

            }

        return addressstr;
    }

    String lastTouch = "";
    @Override
    public void OnInterCeptMap(String isMapTap) {
        Log.d("gyr", "isMapTap" + isMapTap);
        lastTouch = isMapTap;
        Log.d("gyr", "isMapTap" + isMapTap);
    }

    Double returnLat = 0.0,returnLong = 0.0;
    public void setupDefaultScreen(Double latitude,Double longitude) {
        try {


            Variables.currentLatlang = new LatLng(latitude,longitude);

            LatLng latLng1 = Variables.currentLatlang;

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng1, ConstZoomValue), 1000, null);
            mMap.getUiSettings().setRotateGesturesEnabled(false);



        } catch (Exception e) {
            // enableProvider();
        }


    }

    private void myCurrentLocation() {
        try {

            if(Variables.latitude <= 0 && Variables.longitude <= 0)
            {
                if(Functions.Bind_Location_service(context))
                {
                    myCurrentLocation();
                }
            }

            Variables.currentLatlang = new LatLng(Variables.latitude, Variables.longitude);
            LatLng latLng1 = Variables.currentLatlang;

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng1, ConstZoomValue), 1000, null);
            mMap.getUiSettings().setRotateGesturesEnabled(false);


        } catch (Exception e) {
            // enableProvider();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode)
        {
            case 90:
                if(resultCode == Activity.RESULT_OK){


                    returnLat = data.getDoubleExtra("lat",0);
                    returnLong = data.getDoubleExtra("long",0);

                    setupDefaultScreen(returnLat,returnLong);

                }
                break;
        }

    }


}