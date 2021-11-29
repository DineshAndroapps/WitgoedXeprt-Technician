package com.witgoedxpert.technician.Location_Services.PlacesSearch.Model;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import  com.witgoedxpert.technician.Location_Services.PlacesSearch.Inteface.GoogleLocationInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;


import static  com.witgoedxpert.technician.Location_Services.PlacesSearch.Model.GoogleAPI.API_KEY;
import static  com.witgoedxpert.technician.Location_Services.PlacesSearch.Model.GoogleAPI.PLACES_API_BASE;


public class GoogleLocation
{
    Activity activity ;
    GoogleLocationInterface googleLocation;
    PlacesClient placesClient;
    String latitude = "19.023490", longitude = "73.039280";



    public GoogleLocation(Activity activity  , GoogleLocationInterface googleLocation) {
        this.activity = activity;
        this.googleLocation = googleLocation;

        if (!Places.isInitialized()) {
            Places.initialize(activity, API_KEY);
        }
        placesClient = Places.createClient(activity);
    }


    @SuppressLint("StaticFieldLeak")
    public void getCurrentLocation(final String listenerGetCurrentLocation) {
        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... voids) {


                ArrayList<String> resultList = null;
                ArrayList<String> descriptionList = null;
                HttpURLConnection conn = null;
                StringBuilder jsonResults = new StringBuilder();
                try {

                    StringBuilder sb;

                    if (latitude != null && !latitude.equals("0.0")) {
                        sb = new StringBuilder(PLACES_API_BASE);
                        sb.append(URLEncoder.encode(listenerGetCurrentLocation));
//                        sb.append("&types=establishment");
                        sb.append("&country=IN");
                        sb.append("&radius=7516.6");
                        sb.append("&country=IN");
                        sb.append("&location=" + latitude + "," + longitude);
                        sb.append("&key=" + API_KEY);

                    } else {
                        sb = new StringBuilder(PLACES_API_BASE);
                        sb.append("&country=IN");
                        sb.append("&key=" + API_KEY);

                    }


                    URL url = new URL(String.valueOf(sb));
                    conn = (HttpURLConnection) url.openConnection();
                    InputStreamReader in = new InputStreamReader(conn.getInputStream());

                    // Load the results into a StringBuilder
                    int read;
                    char[] buff = new char[1024];
                    while ((read = in.read(buff)) != -1) {
                        jsonResults.append(buff, 0, read);
                    }
                } catch (MalformedURLException e) {

                } catch (IOException e) {
//            Log.e(LOG_TAG, "Error connecting to Places API", e);

                } finally {
                    if (conn != null) {
                        conn.disconnect();
                    }
                }

                try {
                    // Create a JSON object hierarchy from the results
                    Log.d("yo", jsonResults.toString());
                    JSONObject jsonObj = new JSONObject(jsonResults.toString());


                    ArrayList<PlacePredicationModel> placePredicationModels = new ArrayList<>();

                    JSONArray predsJsonArray = jsonObj.getJSONArray("results");

                    // Extract the Place descriptions from the results
                     for (int i = 0; i < predsJsonArray.length(); i++) {
                        PlacePredicationModel placePredicationModel = new PlacePredicationModel();
                        placePredicationModel.setDescription(predsJsonArray.getJSONObject(i).getString("formatted_address"));
                        placePredicationModel.setHighLigt(predsJsonArray.getJSONObject(i).getString("name"));


                        JSONObject geoMatrcObj = predsJsonArray.getJSONObject(i).getJSONObject("geometry");
                        placePredicationModel.setLat(geoMatrcObj.getJSONObject("location").getString("lat"));
                        placePredicationModel.setLng(geoMatrcObj.getJSONObject("location").getString("lng"));




                        placePredicationModels.add(placePredicationModel);
                    }


                     googleLocation.onLocationGetting(placePredicationModels);

//            saveArray(resultList.toArray(new String[resultList.size()]), "predictionsArray", getContext());
                } catch (JSONException e) {
                }


                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);


            }
        }.execute();
    }





}
