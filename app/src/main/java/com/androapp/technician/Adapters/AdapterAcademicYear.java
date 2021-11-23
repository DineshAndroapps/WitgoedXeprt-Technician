package com.androapp.technician.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.androapp.technician.Activity.PRO.ProFormActivity;
import com.androapp.technician.R;
import com.androapp.technician.model.RouteModel;
import com.androapp.technician.model.AcademicYearModel;

import java.util.ArrayList;



public class AdapterAcademicYear extends RecyclerView.Adapter<AdapterAcademicYear.MyViewHolder> {
    ArrayList<AcademicYearModel> formListModelArrayList;
    Context context;
    private int lastSelectedPosition = -1;

    private AdapterAcademicYear.OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int positon, RouteModel item, View view);
    }

    public AdapterAcademicYear(ArrayList<AcademicYearModel> formListModelArrayList, Context context) {
        this.formListModelArrayList = formListModelArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public AdapterAcademicYear.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = (View) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_form_list, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterAcademicYear.MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final AcademicYearModel designModel = formListModelArrayList.get(position);

        holder.tvFormName.setText(designModel.getSessionYear());
        holder.tvFormName.setChecked(lastSelectedPosition == position);

        holder.tvFormName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lastSelectedPosition = holder.getAdapterPosition();
                notifyDataSetChanged();

                ((ProFormActivity) context).onClickCalled_aca_year(designModel, position,"1");


            }
        });
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