package com.witgoedxpert.technician.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.witgoedxpert.technician.Activity.PRO.ProFormActivity;
import com.witgoedxpert.technician.R;
import com.witgoedxpert.technician.model.FormListModel;
import com.witgoedxpert.technician.model.RouteModel;

import java.util.ArrayList;

public class AdapterFormList extends RecyclerView.Adapter<AdapterFormList.MyViewHolder> {
    static ArrayList<FormListModel> formListModelArrayList;
    Context context;
  //  private int lastSelectedPosition = -1;

    private AdapterFormList.OnItemClickListener listener;
    public interface OnItemClickListener {
        void onItemClick(int positon, RouteModel item, View view);
    }

    public AdapterFormList(ArrayList<FormListModel> formListModelArrayList, Context context) {
        this.formListModelArrayList = formListModelArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public AdapterFormList.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = (View) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_form_list_lyt, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterFormList.MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final FormListModel designModel = formListModelArrayList.get(position);
        holder.bind(formListModelArrayList.get(position),position);




      //  holder.tvFormName.setChecked(lastSelectedPosition == position);

/*
        holder.tvFormName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lastSelectedPosition = holder.getAdapterPosition();
                notifyDataSetChanged();

                ((ProFormActivity) context).onClickCalled(designModel, position, "1");

                */
/*Intent intent = new Intent(context, ProFormActivity.class);
                intent.putExtra("form_name", designModel.getSubName());
                intent.putExtra("form_id", designModel.getId());
                context.startActivity(intent);*//*

            }
        });
*/
    }




    @Override
    public int getItemCount() {
        return formListModelArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
      //  RadioButton tvFormName;
        LinearLayout ll_data;
        TextView tvFormName;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFormName = itemView.findViewById(R.id.name);
            ll_data = itemView.findViewById(R.id.ll_data);
        }

        public void bind(FormListModel formListModel, int position) {
            if (((ProFormActivity) context).checkPosition == -1) {
                ll_data.setBackground(context.getDrawable(R.drawable.d_white_round_bkg));
            } else {
                if (((ProFormActivity) context).checkPosition == getAdapterPosition()) {
                    ll_data.setBackground(context.getDrawable(R.drawable.d_blue_round_bkg));
                } else {
                    ll_data.setBackground(context.getDrawable(R.drawable.d_white_round_bkg));
                }
            }
            tvFormName.setText(formListModel.getSubName());
           // txt_class_name.setText(bookModel.getName());

            ll_data.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onClick(View v) {
                    Log.d("TAG_TAG_TAG_ad", "onClick: "+((ProFormActivity) context).checkPosition);
                    ll_data.setBackground(context.getDrawable(R.drawable.d_blue_round_bkg));
                    if (((ProFormActivity) context).checkPosition != getAdapterPosition()) {
                        notifyItemChanged(((ProFormActivity) context).checkPosition);
                        ((ProFormActivity) context).checkPosition = getAdapterPosition();
                    }
                    ((ProFormActivity) context).onClickCalled(formListModel, position, "1");

                }
            });
        }




    }
    public FormListModel getSelected() {
        if (((ProFormActivity) context).checkPosition != -1) {
            return formListModelArrayList.get(((ProFormActivity) context).checkPosition);
        }
        return null;
    }
}