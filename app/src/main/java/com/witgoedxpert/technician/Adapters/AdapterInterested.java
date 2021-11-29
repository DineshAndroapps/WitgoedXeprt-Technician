package com.witgoedxpert.technician.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.witgoedxpert.technician.Activity.PRO.ProFormActivity;
import com.witgoedxpert.technician.R;
import com.witgoedxpert.technician.model.StatusModel;
import com.witgoedxpert.technician.model.RouteModel;

import java.util.ArrayList;

public class AdapterInterested extends RecyclerView.Adapter<AdapterInterested.MyViewHolder> {
    ArrayList<StatusModel> formListModelArrayList;
    Context context;
    private int lastSelectedPosition = -1;
    int row_index = -1;

    private AdapterInterested.OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int positon, RouteModel item, View view);
    }

    public AdapterInterested(ArrayList<StatusModel> formListModelArrayList, Context context) {
        this.formListModelArrayList = formListModelArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public AdapterInterested.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = (View) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_form_list, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterInterested.MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final StatusModel designModel = formListModelArrayList.get(position);

        holder.tvFormName.setText(designModel.getStatusName());
        holder.tvFormName.setChecked(lastSelectedPosition == position);

        holder.tvFormName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lastSelectedPosition = holder.getAdapterPosition();
                notifyDataSetChanged();

                ((ProFormActivity) context).onClickCalled_status(designModel, position, 1);
//code for select and deselect Radio button
/*
                if (formListModelArrayList.get(position).getId().toString().equalsIgnoreCase(String.valueOf(row_index))) {
                    holder.tvFormName.setChecked(false);
                    row_index = -1;
                    ((ProFormActivity) context).onClickCalled_status(designModel, position,row_index);

                } else {
                    holder.tvFormName.setChecked(true);
                    row_index = (formListModelArrayList.get(position).getId());
                    notifyDataSetChanged();
                    ((ProFormActivity) context).onClickCalled_status(designModel, position,row_index);

                }*/
            }
        });


/*
        if (row_index == (formListModelArrayList.get(position).getId())) {
            holder.tvFormName.setChecked(true);
        } else {
            holder.tvFormName.setChecked(false);
        }*/
    }

    @Override
    public int getItemCount() {
        return formListModelArrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        RadioButton tvFormName;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFormName = itemView.findViewById(R.id.tvFormName);
        }
    }
}