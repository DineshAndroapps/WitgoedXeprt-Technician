package com.witgoedxpert.technician.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.witgoedxpert.technician.Activity.PRO.ProPlaceLists;
import com.witgoedxpert.technician.R;
import com.witgoedxpert.technician.model.MainRouteModel;

import java.util.ArrayList;

class SubServiceAdapter extends RecyclerView.Adapter<SubServiceAdapter.ViewHolder> {
    Context context;
    ArrayList<MainRouteModel> arrayList;
    String count;

    public SubServiceAdapter(Context context, ArrayList<MainRouteModel> arrayList, String count) {
        this.context = context;
        this.arrayList = arrayList;
        this.count = count;
    }

    @NonNull
    @Override
    public SubServiceAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.sub_service_item, parent, false);

        return new SubServiceAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubServiceAdapter.ViewHolder holder, int position) {
        final MainRouteModel model = arrayList.get(position);
        holder.bind(model, position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                context.startActivity(new Intent(context, ProPlaceLists.class)
                        .putExtra("id", model.route_id)
                        // .putExtra("id",model.getId())
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });


    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView text_car_Service, txtDuration, txtAmount, txtSubscribed;

        public ViewHolder(@NonNull View itemView) {

            super(itemView);
            text_car_Service = itemView.findViewById(R.id.city);
            

        }

        public void bind(MainRouteModel model, int position) {

            text_car_Service.setText(model.routeName);


        }

    }


}