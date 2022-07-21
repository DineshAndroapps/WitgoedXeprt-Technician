package com.witgoedxpert.technician.Adapters;

import static android.content.Context.MODE_PRIVATE;

import static com.witgoedxpert.technician.Forms.LoginActivity.SHARED_PREFERENCES_NAME;
import static com.witgoedxpert.technician.Forms.LoginActivity.USER_ID;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.witgoedxpert.technician.Forms.AddEnquiry;
import com.witgoedxpert.technician.R;
import com.witgoedxpert.technician.model.SlotModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class AdapterSlot extends RecyclerView.Adapter<AdapterSlot.MyViewHolder> {
    ArrayList<SlotModel> conVideoArrayList;
    Context context;
    SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat fmtOut = new SimpleDateFormat("dd-MM-yyyy");
    String new_date;
    SharedPreferences sharedPreferences;
    String str_userid, str_name, str_fname, str_lname;
    private AdapterSlot.OnItemClickListener listener;
    private int lastSelectedPosition = -1;

    public interface OnItemClickListener {
        void onItemClick(int positon, SlotModel item, View view);
    }


    public AdapterSlot(ArrayList<SlotModel> conVideoArrayList, AppCompatActivity context) {
        this.conVideoArrayList = conVideoArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public AdapterSlot.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = (View) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_time_slot, parent, false);
        return new AdapterSlot.MyViewHolder(v);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull AdapterSlot.MyViewHolder holder, final int position) {
        final SlotModel designModel = conVideoArrayList.get(position);

        sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        str_userid = sharedPreferences.getString(USER_ID, "");
        holder.bind(conVideoArrayList.get(position), position);

        holder.name.setText(designModel.start_time + " - " + designModel.end_time);


    }


    @Override
    public int getItemCount() {
        return conVideoArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView qualification, level, name, fees;
        RelativeLayout rl_lyt, image_ac;
        ImageView video, day;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            rl_lyt = itemView.findViewById(R.id.rl_lyt);
            name = itemView.findViewById(R.id.name);
            day = itemView.findViewById(R.id.day);


        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        public void bind(SlotModel formListModel, int position) {
            //inactive = UnBook
            //active = booked
            if (formListModel.bookingStatus.equals("true")) { //booking Done
                rl_lyt.setBackground(context.getDrawable(R.drawable.yellow_slot));
                name.setTextColor(context.getResources().getColor(R.color.gray7));
                rl_lyt.setOnClickListener(view -> Toast.makeText(context, "This Slot is not Available ", Toast.LENGTH_SHORT).show());
            } else {
                rl_lyt.setBackground(context.getDrawable(R.drawable.d_dashed_border_cate));
                name.setTextColor(context.getResources().getColor(R.color.black));


                if (((AddEnquiry) context).checkPosition == -1) {
                    rl_lyt.setBackground(context.getDrawable(R.drawable.yellow_slot));
                } else {
                    if (((AddEnquiry) context).checkPosition == getAdapterPosition()) {
                        rl_lyt.setBackground(context.getDrawable(R.drawable.gray_slot));
                        name.setTextColor(context.getResources().getColor(R.color.white));
                    } else {
                        rl_lyt.setBackground(context.getDrawable(R.drawable.yellow_slot));
                    }
                }

                rl_lyt.setOnClickListener(new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onClick(View v) {
                        Log.d("TAG_TAG_TAG_ad", "onClick: " + ((AddEnquiry) context).checkPosition);
                        rl_lyt.setBackground(context.getDrawable(R.drawable.gray_slot));
                        name.setTextColor(context.getResources().getColor(R.color.white));
                        if (((AddEnquiry) context).checkPosition != getAdapterPosition()) {
                            notifyItemChanged(((AddEnquiry) context).checkPosition);
                            ((AddEnquiry) context).checkPosition = getAdapterPosition();
                        }
                        ((AddEnquiry) context).onClickCalled(formListModel, position, "1");

                    }
                });

            }


            // txt_class_name.setText(bookModel.getName());

        }

    }

    public SlotModel getSelected() {
        if (((AddEnquiry) context).checkPosition != -1) {
            return conVideoArrayList.get(((AddEnquiry) context).checkPosition);
        }
        return null;
    }
}