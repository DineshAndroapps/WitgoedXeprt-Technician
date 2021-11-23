package com.androapp.technician.Location_Services.PlacesSearch.Inteface;




import com.androapp.technician.Location_Services.PlacesSearch.Model.PlacePredicationModel;

import java.util.ArrayList;

public interface GoogleLocationInterface
{
    public void onLocationGetting(ArrayList<PlacePredicationModel> placePredicationModels);
}
