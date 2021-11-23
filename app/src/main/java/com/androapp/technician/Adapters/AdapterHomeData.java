package com.androapp.technician.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.androapp.technician.Forms.AddEnquiry;
import com.androapp.technician.Helper.Constant;
import com.androapp.technician.R;
import com.androapp.technician.model.MainRouteModel;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class AdapterHomeData extends RecyclerView.Adapter<AdapterHomeData.MyViewHolder> {
    ArrayList<HomaDataModel> formListModelArrayList;
    Context context;
    private int lastSelectedPosition = -1;
    ArrayList<MainRouteModel> subServiceArrayList;

    private AdapterHomeData.OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int positon, HomaDataModel item, View view);
    }

    public AdapterHomeData(ArrayList<HomaDataModel> formListModelArrayList, Context context) {
        this.formListModelArrayList = formListModelArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public AdapterHomeData.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = (View) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_list, parent, false);
        return new AdapterHomeData.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterHomeData.MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final HomaDataModel designModel = formListModelArrayList.get(position);
        subServiceArrayList = new ArrayList<>();
        holder.bind(designModel);
        Glide.with(context).load(Constant.image_url_ + designModel.image).into(holder.image);
        holder.name.setText(designModel.name);

        holder.lyt_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //((MainActivity) context).onClickCalled(designModel, position, "1");
                Intent intent = new Intent(context, AddEnquiry.class);
                intent.putExtra("str_product_id",designModel.id);
                intent.putExtra("str_name_pro",designModel.name);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return formListModelArrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        ImageView image;
        LinearLayout lyt_parent;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            image = itemView.findViewById(R.id.image);
            lyt_parent = itemView.findViewById(R.id.lyt_parent);


        }

        public void bind(HomaDataModel designModel) {

            name.setText(designModel.name);


        }
    }
}