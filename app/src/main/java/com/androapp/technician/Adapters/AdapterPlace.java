package com.androapp.technician.Adapters;

import static android.content.Context.MODE_PRIVATE;
import static com.androapp.technician.Forms.LoginActivity.ROLE;
import static com.androapp.technician.Forms.LoginActivity.SHARED_PREFERENCES_NAME;
import static com.androapp.technician.Forms.LoginActivity.USER_ID;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.androapp.technician.Activity.PlaceDetails;
import com.androapp.technician.Activity.PlaceLists;
import com.androapp.technician.Helper.Constant;
import com.androapp.technician.R;
import com.androapp.technician.model.PlaceModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class AdapterPlace extends RecyclerView.Adapter<AdapterPlace.MyViewHolder> {
    ArrayList<PlaceModel> conVideoArrayList;
    Context context;
    SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat fmtOut = new SimpleDateFormat("dd-MM-yyyy");
    String new_date;
    SharedPreferences sharedPreferences;

    String str_userid, str_name, str_fname, str_lname;
    private AdapterPlace.OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int positon, PlaceModel item, View view);
    }


    public AdapterPlace(ArrayList<PlaceModel> conVideoArrayList, AppCompatActivity context) {
        this.conVideoArrayList = conVideoArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public AdapterPlace.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = (View) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_place, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterPlace.MyViewHolder holder, final int position) {
        final PlaceModel designModel = conVideoArrayList.get(position);

        sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        str_userid = sharedPreferences.getString(USER_ID, "");
        holder.title.setText(designModel.name);
        holder.Id.setText("" + designModel.sequence_id);
        String userRole = sharedPreferences.getString(ROLE, "");
        if (userRole.equalsIgnoreCase("BDM")) {
            holder.iv_delivered1.setVisibility(View.VISIBLE);
            holder.ll_del.setVisibility(View.VISIBLE);
        } else {
            holder.iv_delivered1.setVisibility(View.GONE);
            holder.ll_del.setVisibility(View.GONE);
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                String userRole = sharedPreferences.getString(ROLE, "");
//                if (userRole.equalsIgnoreCase("BDM")) {
                Intent yourIntent = new Intent(context, PlaceDetails.class);
                yourIntent.putExtra("id", designModel.id);
                yourIntent.putExtra("name", designModel.name);
                yourIntent.putExtra("photoCreatedDate", designModel.photoCreatedDate);
                yourIntent.putExtra("sequence_id", designModel.sequence_id);
                yourIntent.putExtra("latitude", designModel.latitude);
                yourIntent.putExtra("longitude", designModel.longitude);
                yourIntent.putExtra("branchAssigned", designModel.branchAssigned);
                yourIntent.putExtra("contactPerson", designModel.contactPerson);
                yourIntent.putExtra("mobile", designModel.mobile);
                yourIntent.putExtra("phone", designModel.phone);
                yourIntent.putExtra("email", designModel.email);
                yourIntent.putExtra("address", designModel.address);
                yourIntent.putExtra("state", designModel.state);
                yourIntent.putExtra("city", designModel.city);
                yourIntent.putExtra("category", designModel.category);
                yourIntent.putExtra("photo", Constant.image_url_ + designModel.photo);
                context.startActivity(yourIntent);
//                }
            }
        });

        holder.iv_delivered1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((PlaceLists) context).onClickCalled(designModel, position, "1");

            }
        });
        holder.ll_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((PlaceLists) context).onClickCalled_delete(designModel, position, "1");

            }
        });
        //  Glide.with(context).load(Constant.design_img + designModel.image).placeholder(R.drawable.logo_trans).into(holder.image);


    }

    @Override
    public int getItemCount() {
        return conVideoArrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title, city, Id;
        ImageView iv_delivered1;
        LinearLayout ll_del;
        RelativeLayout image_ac;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            Id = itemView.findViewById(R.id.Id);
            title = itemView.findViewById(R.id.title);
            city = itemView.findViewById(R.id.city);
            iv_delivered1 = itemView.findViewById(R.id.iv_delivered1);
            ll_del = itemView.findViewById(R.id.ll_del);
            image_ac = itemView.findViewById(R.id.image_ac);


        }
    }
}