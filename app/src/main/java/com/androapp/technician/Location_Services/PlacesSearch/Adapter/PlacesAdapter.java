package com.androapp.technician.Location_Services.PlacesSearch.Adapter;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.gms.maps.model.LatLng;
import com.androapp.technician.Location_Services.PlacesSearch.Model.GoogleAPI;
import com.androapp.technician.Location_Services.PlacesSearch.Model.PlacePredicationModel;
import com.androapp.technician.R;


import java.util.ArrayList;

public class PlacesAdapter extends RecyclerView.Adapter<PlacesAdapter.myholder> {

    Context context;
    ArrayList<PlacePredicationModel> placePredicationModels;
    public interface OnLocationSelect
    {
        void onLocationSelect(PlacePredicationModel placePredicationModel);
    }
    OnLocationSelect onLocationSelect ;

    public PlacesAdapter(Context context, ArrayList<PlacePredicationModel> placePredicationModels) {
        this.context = context;
        this.placePredicationModels = placePredicationModels;
    }


    public void setLocationListener(OnLocationSelect locationListener)
    {
        this.onLocationSelect = locationListener ;
    }

    @NonNull
    @Override
    public myholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.location_item , parent , false);

        return new myholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myholder holder, int position) {

        final PlacePredicationModel placePredicationModel = placePredicationModels.get(position);

        Log.v("PlacesAdapter",""+placePredicationModel.toString());

        holder.txtHighlight.setText(placePredicationModel.getHighLigt());
        holder.txtFullAddress.setText(placePredicationModel.getDescription());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (onLocationSelect != null)
                {

                    Address address = GoogleAPI.getGeocoder(context , new LatLng(Double.parseDouble(placePredicationModel.getLat()) , Double.parseDouble(placePredicationModel.getLng())));

                    if (address.getLocality() != null)
                    {
                        placePredicationModel.setCity(address.getLocality());
                    }
                    else
                    {
                        placePredicationModel.setCity("");
                    }

                    if (address.getCountryName() != null )
                    {
                        placePredicationModel.setCountry(address.getCountryName());
                    }
                    else
                    {
                        placePredicationModel.setCountry("");

                    }

                    if (address.getAdminArea() != null)
                    {
                        placePredicationModel.setState(address.getAdminArea());
                    }
                    else
                    {
                        placePredicationModel.setState("");
                    }


//                    placePredicationModel.setDescription(address.getAddressLine(0));

                    onLocationSelect.onLocationSelect(placePredicationModel);
                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return placePredicationModels.size();
    }

    class myholder extends RecyclerView.ViewHolder{

//        CardView parentCrd ;
        TextView txtHighlight , txtFullAddress ;

        public myholder(@NonNull View itemView) {
            super(itemView);

//            parentCrd = itemView.findViewById(R.id.parentCrd);
            txtHighlight = itemView.findViewById(R.id.txtHighlight);
            txtFullAddress = itemView.findViewById(R.id.txtFullAddress);

        }
    }


    public void filter(ArrayList<PlacePredicationModel> placePredicationModels)
    {
        this.placePredicationModels.clear();
        this.placePredicationModels.addAll(placePredicationModels);

        ((Activity)context).runOnUiThread(new Runnable() {

            @Override
            public void run() {

                // Stuff that updates the UI
                notifyDataSetChanged();

            }
        });

    }

}
