package com.witgoedxpert.technician.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.witgoedxpert.technician.Activity.PRO.ProFormActivity;
import com.witgoedxpert.technician.R;
import com.witgoedxpert.technician.model.PlaceModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class AdapterChild extends RecyclerView.Adapter<AdapterChild.MyViewHolder> {
    ArrayList<ProFormActivity.ChildMaster> mChildMasterList;
    Context context;
    SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat fmtOut = new SimpleDateFormat("dd-MM-yyyy");
    String new_date;
    SharedPreferences sharedPreferences;

    String str_userid, str_name, str_fname, str_lname;
    private AdapterChild.OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int positon, PlaceModel item, View view);
    }


    public AdapterChild(ArrayList<ProFormActivity.ChildMaster> list, Context context) {
        this.mChildMasterList = list;
        this.context = context;
    }

    @NonNull
    @Override
    public AdapterChild.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = (View) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_child, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterChild.MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final ProFormActivity.ChildMaster designModel = mChildMasterList.get(position);

        holder.close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mChildMasterList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, mChildMasterList.size());
            }
        });

        holder.name.setText("Name: " + designModel.getChildName());
        holder.age.setText("Age: " + designModel.getChildAge());
        holder.grade.setText("Grade: " + designModel.getGrade());
        holder.school.setText("School Name: " + designModel.getSchoolName());
    }

    @Override
    public int getItemCount() {
        return mChildMasterList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name, age, grade, school;
        ImageView close;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            close = itemView.findViewById(R.id.iv_card_child_close);
            name = itemView.findViewById(R.id.tv_card_child_name);
            age = itemView.findViewById(R.id.tv_card_child_age);
            grade = itemView.findViewById(R.id.tv_card_child_grade);
            school = itemView.findViewById(R.id.tv_card_child_school);
        }
    }
}