package com.witgoedxpert.technician.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.witgoedxpert.technician.Helper.Constant;
import com.witgoedxpert.technician.R;
import com.witgoedxpert.technician.model.ServiceModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class ServiceModelAdapter extends RecyclerView.Adapter<ServiceModelAdapter.MyViewHolder> {
    ArrayList<ServiceModel> conVideoArrayList;
    Context context;
    SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    SimpleDateFormat fmtOut = new SimpleDateFormat("dd-MM-yyyy");
    String new_date;
    SharedPreferences sharedPreferences;
    String str_userid, str_name, str_fname, str_lname;
    private ServiceModelAdapter.OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int positon, ServiceModel item, View view);
    }


    public ServiceModelAdapter(ArrayList<ServiceModel> conVideoArrayList, AppCompatActivity context) {
        this.conVideoArrayList = conVideoArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ServiceModelAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = (View) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_service, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceModelAdapter.MyViewHolder holder, final int position) {
        final ServiceModel classModel = conVideoArrayList.get(position);

        holder.name.setText(classModel.name );
        holder.price.setText("Rs. "+classModel.fee );

        Glide.with(context).load(Constant.image_url_ + classModel.image).placeholder(R.drawable.exo_ic_settings).into(holder.image);



    }

    @Override
    public int getItemCount() {
        return conVideoArrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView max_heart_rate_t, price, imdb_heart_rate_t, SPO2_t,name;
        ImageView image;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.image);
            price = itemView.findViewById(R.id.price);
            name = itemView.findViewById(R.id.name);


        }


    }


}