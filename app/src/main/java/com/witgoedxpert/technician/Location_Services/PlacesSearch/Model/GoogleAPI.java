package com.witgoedxpert.technician.Location_Services.PlacesSearch.Model;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class GoogleAPI {
    public final static String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place/textsearch/json?query=";
    //    public static final String API_KEY = "AIzaSyBCj0qh8WApjfBjkkOQ-mnIIY0vWnMhNIo";
    public static final String API_KEY = "AIzaSyAhJ2jyMQywcUnCOp6_7dYsJ_f_qZsGyf8";
//    public static final String API_KEY = Variables.sharedPreferences.getString(Variables.PLACE_API_KEY, "0");

    //  public static final String API_KEY = "AIzaSyAL4erq8pOxd2Kbvi93rcHiVJa3k2EYLVQ";

    public static Address getGeocoder(Context context, LatLng latLng) {
        Address address = null;

        if (latLng != null) {
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            List<Address> addressArrayList = null;

            try {

                addressArrayList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (addressArrayList == null || addressArrayList.size() == 0) {
                if (address == null) {
//                    address = "No Address Found" ;
                }

            } else {
                Address address1 = addressArrayList.get(0);
                Double currentLattitude = 0.0;
                Double currentLongitude = 0.0;


                for (int i = 0; i <= address1.getMaxAddressLineIndex(); i++) {
                    address = address1;

                }


            }

        }

        return address;
    }

//    $URL='https://maps.google.com/maps/api/geocode/json?address='.urlencode($destination_address).'&key='.API_MAP_KEY.'&sensor=false';

}
