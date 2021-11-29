package com.witgoedxpert.technician.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.witgoedxpert.technician.R;
import com.witgoedxpert.technician.model.MainRouteModel;
import com.witgoedxpert.technician.model.RouteHomeModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AdapterHomeRoute
        extends RecyclerView.Adapter<AdapterHomeRoute.MyViewHolder> {
    ArrayList<RouteHomeModel> formListModelArrayList;
    Context context;
    private int lastSelectedPosition = -1;
    ArrayList<MainRouteModel> subServiceArrayList;

    private AdapterHomeRoute.OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int positon, RouteHomeModel item, View view);
    }

    public AdapterHomeRoute(ArrayList<RouteHomeModel> formListModelArrayList, Context context) {
        this.formListModelArrayList = formListModelArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public AdapterHomeRoute.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = (View) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_route_home, parent, false);
        return new AdapterHomeRoute.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterHomeRoute.MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final RouteHomeModel designModel = formListModelArrayList.get(position);
        subServiceArrayList = new ArrayList<>();
        holder.day.setText(designModel.day);

        holder.day.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // ((ProFormActivity) context).onClickCalled_status(designModel, position, "1");


            }
        });

        JSONArray jsonArray = formListModelArrayList.get(position).jsonArray;
        for (int j = 0; j < jsonArray.length(); j++) {
            JSONObject jsonObject2 = null;
            try {
                jsonObject2 = jsonArray.getJSONObject(j);
                MainRouteModel subServicemodel = new MainRouteModel();
                subServicemodel.route_id= (jsonObject2.getString("route_id"));
                subServicemodel.routeName =(jsonObject2.getString("routeName"));

                subServiceArrayList.add(subServicemodel);
                holder.rv_subService.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
                SubServiceAdapter adapter = new SubServiceAdapter(context, subServiceArrayList, "4");
                holder.rv_subService.setAdapter(adapter);
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }

    }

    @Override
    public int getItemCount() {
        return formListModelArrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        Button day;
        RecyclerView rv_subService;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            day = itemView.findViewById(R.id.day);
            rv_subService = itemView.findViewById(R.id.rv_subService);
        }
    }
}