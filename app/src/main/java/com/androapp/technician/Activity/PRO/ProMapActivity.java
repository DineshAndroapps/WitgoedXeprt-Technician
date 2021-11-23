package com.androapp.technician.Activity.PRO;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

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
import com.google.android.gms.maps.model.PolylineOptions;
import com.androapp.technician.Helper.Constant;
import com.androapp.technician.Location_Services.LocationTrack;
import com.androapp.technician.R;
import com.androapp.technician.model.PlaceModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mRouteMap;
    private double sLatitude = 0;
    private double sLongitude = 0;
    private ImageView mMyLocation;
    private LatLng origin;
    private Bundle bundle;

    private LocationTrack mLocationTrack;
    private ArrayList<PlaceModel> placeModelArrayList;

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
        if (bundle != null)
            placeModelArrayList = bundle.getParcelableArrayList(Constant.DATA);
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mRouteMap = googleMap;
        if (mRouteMap != null) {

            if (ActivityCompat.checkSelfPermission(ProMapActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ProMapActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
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

            mLocationTrack = new LocationTrack(ProMapActivity.this);
            if (mLocationTrack.canGetLocation()) {
                sLatitude = mLocationTrack.getLatitude();
                sLongitude = mLocationTrack.getLongitude();

                Log.d("SourceLatLng: ", sLatitude + " " + sLongitude);

                origin = new LatLng(sLatitude, sLongitude);

                MarkerOptions smarkerOptions = new MarkerOptions();
                smarkerOptions.position(origin);
                smarkerOptions.title("Current Location");
                smarkerOptions.icon(BitmapDescriptorFactory
                        .fromResource(R.mipmap.pin_current));
                mRouteMap.addMarker(smarkerOptions);

//                ArrayList<LatLngModel> mTemp = new ArrayList<>();
//                LatLngModel latLngModel = new LatLngModel(30.7333, 76.7794);
//                LatLngModel latLngModel1 = new LatLngModel(19.0760, 72.8777);
//                LatLngModel latLngModel2 = new LatLngModel(28.7041, 77.1025);
//                LatLngModel latLngModel3 = new LatLngModel(24.9320, 73.8191);
//                mTemp.add(latLngModel);
//                mTemp.add(latLngModel1);
//                mTemp.add(latLngModel2);
//                mTemp.add(latLngModel3);
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
//                style = MapStyleOptions.loadRawResourceStyle(ProMapActivity.this, R.raw.mapstyle_silver);
//                mRouteMap.setMapStyle(style);

            } else {
                mLocationTrack.showSettingsAlert();
            }
        }
    }

    private class LatLngModel {
        double latitude;

        public double getLatitude() {
            return latitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }

        double longitude;

        public LatLngModel(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }
    }
}

