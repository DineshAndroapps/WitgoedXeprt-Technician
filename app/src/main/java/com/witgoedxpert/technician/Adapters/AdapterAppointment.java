package com.witgoedxpert.technician.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.witgoedxpert.technician.Activity.MyOrderDetails;
import com.witgoedxpert.technician.Helper.Constant;
import com.witgoedxpert.technician.R;
import com.witgoedxpert.technician.model.OrderModel;
import com.bumptech.glide.Glide;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.content.Context.MODE_PRIVATE;
import static com.witgoedxpert.technician.Forms.LoginActivity.SHARED_PREFERENCES_NAME;
import static com.witgoedxpert.technician.Forms.LoginActivity.USER_ID;

public class AdapterAppointment extends RecyclerView.Adapter<AdapterAppointment.MyViewHolder> {
    ArrayList<OrderModel> conVideoArrayList;
    Context context;
    SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat fmtOut = new SimpleDateFormat("dd-MM-yyyy");
    String new_date;
    SharedPreferences sharedPreferences;
    String str_userid, str_name, str_fname, str_lname;
    private AdapterAppointment.OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int positon, OrderModel item, View view);
    }


    public AdapterAppointment(ArrayList<OrderModel> conVideoArrayList, AppCompatActivity context) {
        this.conVideoArrayList = conVideoArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public AdapterAppointment.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = (View) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_selfie, parent, false);
        return new AdapterAppointment.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterAppointment.MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final OrderModel bookModel = conVideoArrayList.get(position);

        sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        str_userid = sharedPreferences.getString(USER_ID, "");

        //holder.qualification.setText( bookModel.createdDate);

        holder.event_name.setText(bookModel.product_name);
        holder.description.setText(bookModel.description);

        Glide.with(context).load(Constant.image_url_ + bookModel.image).placeholder(R.drawable.ic_baseline_settings_24).into(holder.image);

        String date = bookModel.added_date;
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm a");
        Date parsedDate = null;
        try {
            parsedDate = inputFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String formattedDate = outputFormat.format(parsedDate);
        System.out.println(formattedDate);
        holder.create_date.setText(formattedDate);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MyOrderDetails.class);
                intent.putExtra("MyClass", bookModel);
                context.startActivity(intent);
            }
        });
        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //((SelfieList) context).ShowAnswerDialogImg(bookModel.image);

            }
        });


    }

    @Override
    public int getItemCount() {
        return conVideoArrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView event_name, description, create_date;
        ImageView image;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            event_name = itemView.findViewById(R.id.event_name);
            create_date = itemView.findViewById(R.id.created_date);
            description = itemView.findViewById(R.id.description);

        }
    }
}
