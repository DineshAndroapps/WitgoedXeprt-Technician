package com.witgoedxpert.technician.Adapters;

import static android.content.Context.MODE_PRIVATE;
import static com.witgoedxpert.technician.Forms.LoginActivity.ROLE;
import static com.witgoedxpert.technician.Forms.LoginActivity.SHARED_PREFERENCES_NAME;
import static com.witgoedxpert.technician.Forms.LoginActivity.USER_ID;

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

import com.witgoedxpert.technician.Activity.ListOfRoute;
import com.witgoedxpert.technician.Activity.PlaceLists;
import com.witgoedxpert.technician.Activity.PRO.ProPlaceLists;
import com.witgoedxpert.technician.R;
import com.witgoedxpert.technician.model.RouteModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AdapterDesign extends RecyclerView.Adapter<AdapterDesign.MyViewHolder> {
    ArrayList<RouteModel> conVideoArrayList;
    Context context;
    SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    SimpleDateFormat fmtOut = new SimpleDateFormat("dd-MM-yyyy");

    SimpleDateFormat fmt1 = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat fmtOut1 = new SimpleDateFormat("dd-MM-yyyy");

    String new_date;
    SharedPreferences sharedPreferences;

    String str_userid, str_name, str_fname, str_lname;
    private AdapterDesign.OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int positon, RouteModel item, View view);
    }


    public AdapterDesign(ArrayList<RouteModel> conVideoArrayList, AppCompatActivity context) {
        this.conVideoArrayList = conVideoArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public AdapterDesign.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = (View) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_route, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterDesign.MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final RouteModel designModel = conVideoArrayList.get(position);

        sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        str_userid = sharedPreferences.getString(USER_ID, "");
        holder.title.setText("Route Name: " + designModel.routeName);


        String userRole = sharedPreferences.getString(ROLE, "");

        if (userRole.equalsIgnoreCase("BDM")) {
            holder.name.setVisibility(View.GONE);
            holder.created_date.setVisibility(View.GONE);
            holder.assign_date.setVisibility(View.GONE);
            holder.branchAssigned.setVisibility(View.VISIBLE);
        } else {

            if (designModel.pro_name.equals("") || designModel.pro_name.equals("null")) {

                holder.name.setText("PRO Name: - ");
            } else {
                holder.name.setText("PRO Name: " + designModel.pro_name);
            }
            Date date = null;
            Date date2 = null;
            try {
                date = fmt.parse(designModel.createdDate);
                date2 = fmt1.parse(designModel.assign_date);
                holder.created_date.setText("Created Date: " + fmtOut.format(date));
                holder.assign_date.setText("Assign Date: " + fmtOut1.format(date2));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            holder.branchAssigned.setVisibility(View.GONE);
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String userRole = sharedPreferences.getString(ROLE, "");
                if (userRole.equalsIgnoreCase("BDM")) {
                    Intent yourIntent = new Intent(context, PlaceLists.class);
                    yourIntent.putExtra("id", designModel.id);
                    context.startActivity(yourIntent);
                    holder.branchAssigned.setVisibility(View.VISIBLE);
                } else {

                    Intent yourIntent = new Intent(context, ProPlaceLists.class);
                    yourIntent.putExtra("id", designModel.route_id);
                    context.startActivity(yourIntent);
                    holder.branchAssigned.setVisibility(View.GONE);
                }
            }
        });
        holder.branchAssigned.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ListOfRoute) context).onClickCalled(designModel, position, "1");
            }
        });

        //  Glide.with(context).load(Constant.design_img + designModel.image).placeholder(R.drawable.logo_trans).into(holder.image);


    }

    @Override
    public int getItemCount() {
        return conVideoArrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title, assign_date, name, created_date;
        ImageView branchAssigned;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            branchAssigned = itemView.findViewById(R.id.branchAssigned);
            title = itemView.findViewById(R.id.title);
            created_date = itemView.findViewById(R.id.created_date);
            name = itemView.findViewById(R.id.name);
            assign_date = itemView.findViewById(R.id.assign_date);


        }
    }
}