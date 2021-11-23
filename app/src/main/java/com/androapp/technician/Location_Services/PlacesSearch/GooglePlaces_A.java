package com.androapp.technician.Location_Services.PlacesSearch;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.androapp.technician.R;


public class GooglePlaces_A extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_places_);

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.container_google,new GooglePlace_Fragment()).
                commit();

    }
}