package com.witgoedxpert.technician.Adapters;

import static android.content.Context.MODE_PRIVATE;
import static com.witgoedxpert.technician.Forms.LoginActivity.SHARED_PREFERENCES_NAME;
import static com.witgoedxpert.technician.Forms.LoginActivity.USER_ID;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.witgoedxpert.technician.Activity.Home.SchedulePage_A;
import com.witgoedxpert.technician.Activity.MyOrder;
import com.witgoedxpert.technician.Activity.MyOrderDetails;
import com.witgoedxpert.technician.Forms.AddEnquiry;
import com.witgoedxpert.technician.Helper.Constant;
import com.witgoedxpert.technician.R;
import com.witgoedxpert.technician.model.OrderModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AdapterSchedule extends RecyclerView.Adapter<AdapterSchedule.MyViewHolder> {
    ArrayList<OrderModel> conVideoArrayList;
    Context context;
    SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat fmtOut = new SimpleDateFormat("dd-MM-yyyy");
    String new_date;
    SharedPreferences sharedPreferences;
    String str_userid, str_slot_time, str_slot_date, str_slot_id;
    private AdapterSchedule.OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int positon, OrderModel item, View view);
    }


    public AdapterSchedule(ArrayList<OrderModel> conVideoArrayList, AppCompatActivity context) {
        this.conVideoArrayList = conVideoArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public AdapterSchedule.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = (View) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_slot, parent, false);
        return new AdapterSchedule.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterSchedule.MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final OrderModel bookModel = conVideoArrayList.get(position);

        sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        str_userid = sharedPreferences.getString(USER_ID, "");
        Log.e("position", "onBindViewHolder: " + position);
        if (position == 0) {
            if (bookModel.flag.equals("1")) {
                holder.on_way.setVisibility(View.GONE);
            } else {
                holder.on_way.setVisibility(View.VISIBLE);
            }
            Log.e("position_true", "onBindViewHolder: " + position);

        } else {
            Log.e("position_false", "onBindViewHolder: " + position);
            holder.on_way.setVisibility(View.GONE);
        }

        holder.on_way.setOnClickListener(v -> {
            ((SchedulePage_A) context).onClickCalled(bookModel, position, "1");

        });

        //holder.qualification.setText( bookModel.createdDate);

        Log.e("slot_start_time", "onBindViewHolder: " + bookModel.slot_start_time);
        holder.slot_time.setText(bookModel.slot_start_time);
        holder.name.setText(bookModel.name + "\n" + "(" + bookModel.description + ")");


        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, MyOrderDetails.class);
            intent.putExtra("MyClass", bookModel);
            context.startActivity(intent);
        });


    }

    @Override
    public int getItemCount() {
        return conVideoArrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name, slot_time, on_way;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            slot_time = itemView.findViewById(R.id.time);
            name = itemView.findViewById(R.id.name);
            on_way = itemView.findViewById(R.id.on_way);


        }
    }
}
