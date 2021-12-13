package com.witgoedxpert.technician.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.witgoedxpert.technician.Activity.MyOrderDetails;
import com.witgoedxpert.technician.Helper.Adapter_ClickListener;
import com.witgoedxpert.technician.Helper.Constant;
import com.witgoedxpert.technician.R;
import com.witgoedxpert.technician.model.OrderModel;
import com.bumptech.glide.Glide;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    Adapter_ClickListener adapter_clickListener;


    public AdapterAppointment(ArrayList<OrderModel> conVideoArrayList, AppCompatActivity context, Adapter_ClickListener adapter_clickListener) {
        this.conVideoArrayList = conVideoArrayList;
        this.context = context;
        this.adapter_clickListener = adapter_clickListener;

    }

    @NonNull
    @Override
    public AdapterAppointment.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = (View) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_orders_layout, parent, false);
        return new AdapterAppointment.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterAppointment.MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final OrderModel bookModel = conVideoArrayList.get(position);

        sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        str_userid = sharedPreferences.getString(USER_ID, "");

        holder.tvOrderNo.setText("" + bookModel.id);
        ((TextView) holder.itemView.findViewById(R.id.tv_name)).setText("" + bookModel.name);
        ((TextView) holder.itemView.findViewById(R.id.tv_mobile)).setText("" + bookModel.phone);
        ((TextView) holder.itemView.findViewById(R.id.tv_delivery)).setText("" + bookModel.address);
        ((TextView) holder.itemView.findViewById(R.id.tv_product_name)).setText("" + bookModel.product_name);
        ((TextView) holder.itemView.findViewById(R.id.tv_service_charge)).setText("Rs." + bookModel.service_charge);
        //Glide.with(context).load(Constant.design_img + designModel.image).placeholder(R.drawable.logo_trans).into(holder.image);
        String date = bookModel.added_date;
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyy");
        Date parsedDate = null;
        try {
            parsedDate = inputFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String formattedDate = outputFormat.format(parsedDate);
        System.out.println(formattedDate);
        ((TextView) holder.itemView.findViewById(R.id.tv_last_date)).setText("" + formattedDate);
        ;//book_now=0 book_later=1
        if (bookModel.status.equals("1")) {
            ((TextView) holder.itemView.findViewById(R.id.tv_actual_Delivery_time)).setText("" + bookModel.date + " " + bookModel.time);
        } else {
            String date_ = bookModel.added_date;
            SimpleDateFormat inputFormat_ = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat outputFormat_ = new SimpleDateFormat("dd-MM-yyy HH:mm");
            Date parsedDate_ = null;
            try {
                parsedDate_ = inputFormat_.parse(date_);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            String formattedDate_ = outputFormat_.format(parsedDate_);
            ((TextView) holder.itemView.findViewById(R.id.tv_actual_Delivery_time)).setText(formattedDate_);

        }

        if (bookModel.service_staus.equals("pending")) {
            holder.div_accept.setVisibility(View.VISIBLE);
            holder.div_process.setVisibility(View.GONE);
            holder.rlt_complete_div.setVisibility(View.GONE);

        } else if (bookModel.service_staus.equals("process")) {
            if (bookModel.service_status.equals("1")){
                holder.btn_start.setVisibility(View.GONE);
            }else{
                holder.btn_start.setVisibility(View.VISIBLE);
            }
            holder.div_accept.setVisibility(View.GONE);
            holder.div_process.setVisibility(View.VISIBLE);
            holder.rlt_complete_div.setVisibility(View.GONE);
        } else {
            holder.div_accept.setVisibility(View.GONE);
            holder.div_process.setVisibility(View.VISIBLE);
            // holder.rlt_complete_div.setVisibility(View.VISIBLE);
            holder.btn_start.setVisibility(View.GONE);
            holder.btn_end.setVisibility(View.GONE);
            holder.start_rlt.setVisibility(View.VISIBLE);
            holder.start_time.setText(bookModel.start_time);
            holder.end_time.setText(bookModel.end_time);

        }

        holder.btn_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //((SelfieList) context).ShowAnswerDialogImg(bookModel.image);

            }
        });
        holder.btn_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        holder.btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        holder.btn_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        holder.btn_details.setOnClickListener(v -> {
            Intent intent = new Intent(context, MyOrderDetails.class);
            intent.putExtra("MyClass", bookModel);
            context.startActivity(intent);
        });
        holder.btn_details_p.setOnClickListener(v -> {
            Intent intent = new Intent(context, MyOrderDetails.class);
            intent.putExtra("MyClass", bookModel);
            context.startActivity(intent);
        });
        holder.bind(position, bookModel, adapter_clickListener);

    }

    @Override
    public int getItemCount() {
        return conVideoArrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderNo, btn_start,end_time,start_time, btn_end, tv_service_charge, tvDelivery, tvDate, tvActualDeliveryTime, tvActualPickupLocation, tvOrderStatus;
        TextView btn_yes, btn_no, btn_details, btn_details_p, btn_paid, btn_rec, btn_details_c;
        RelativeLayout rlt_complete_div;
        LinearLayout div_accept, div_process;
        LinearLayout histroy_rlt, div_process_c, ll_date,start_rlt;
        ImageView image;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderNo = itemView.findViewById(R.id.tv_order_no);
            tvDelivery = itemView.findViewById(R.id.tv_delivery);
            tvActualDeliveryTime = itemView.findViewById(R.id.tv_actual_Delivery_time);
            tvActualPickupLocation = itemView.findViewById(R.id.tv_actual_Pickup_location);
            btn_yes = itemView.findViewById(R.id.btn_yes);
            btn_start = itemView.findViewById(R.id.btn_start);
            btn_end = itemView.findViewById(R.id.btn_end);
            histroy_rlt = itemView.findViewById(R.id.histroy_rlt);
            btn_no = itemView.findViewById(R.id.btn_no);
            btn_details = itemView.findViewById(R.id.btn_details);
            tvDate = itemView.findViewById(R.id.tv_last_date);
            rlt_complete_div = itemView.findViewById(R.id.rlt_complete_div);
            div_accept = itemView.findViewById(R.id.div_accept);
            div_process = itemView.findViewById(R.id.div_process);
            btn_details_p = itemView.findViewById(R.id.btn_details_p);
            tvOrderStatus = itemView.findViewById(R.id.tv_order_status);
            tv_service_charge = itemView.findViewById(R.id.tv_service_charge);
            btn_paid = itemView.findViewById(R.id.btn_paid);
            btn_rec = itemView.findViewById(R.id.btn_rec);
            div_process_c = itemView.findViewById(R.id.div_process_c);
            btn_details_c = itemView.findViewById(R.id.btn_details_c);
            ll_date = itemView.findViewById(R.id.ll_date);
            image = itemView.findViewById(R.id.image);
            start_time = itemView.findViewById(R.id.start_time);
            end_time = itemView.findViewById(R.id.end_time);
            start_rlt = itemView.findViewById(R.id.start_rlt);

        }

        public void bind(final int item, final OrderModel my_orders_model_, final Adapter_ClickListener listener) {

            itemView.setOnClickListener(v -> {
                listener.On_Item_Click(item, my_orders_model_, v);
            });

            btn_details.setOnClickListener(v -> {
                listener.On_Item_Click(item, my_orders_model_, v);
            });

            btn_yes.setOnClickListener(v -> {
                listener.On_Item_Click(item, my_orders_model_, v);

            });

            btn_no.setOnClickListener(v -> {

                listener.On_Item_Click(item, my_orders_model_, v);


            });

            btn_details_p.setOnClickListener(v -> {
                listener.On_Item_Click(item, my_orders_model_, v);


            });
            div_process_c.setOnClickListener(v -> {
                listener.On_Item_Click(item, my_orders_model_, v);


            });
            btn_start.setOnClickListener(v -> {
                listener.On_Item_Click(item, my_orders_model_, v);


            });
            btn_end.setOnClickListener(v -> {
                listener.On_Item_Click(item, my_orders_model_, v);


            });
            btn_paid.setOnClickListener(v -> {
                listener.On_Item_Click(item, my_orders_model_, v);


            });
            btn_rec.setOnClickListener(v -> {
                listener.On_Item_Click(item, my_orders_model_, v);


            });
            btn_details_c.setOnClickListener(v -> {
                listener.On_Item_Click(item, my_orders_model_, v);

            });

        }

    }

}
