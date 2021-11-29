package com.witgoedxpert.technician.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.witgoedxpert.technician.R;
import com.witgoedxpert.technician.model.FormEventModel;

import java.util.ArrayList;
import java.util.List;

public class AdapterFormEventList extends RecyclerView.Adapter<AdapterFormEventList.MyViewHolder> implements Filterable {
    ArrayList<FormEventModel> mItem;
    private MFilter mFilter;
    Context context;

    private OnItemClickListener mItemClickListener;

    public void setOnItemClickListener(OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    @Override
    public Filter getFilter() {
        if (mFilter == null) {
            mFilter = new MFilter(mItem, this);
        }
        return mFilter;
    }

    public class MFilter extends Filter {
        private AdapterFormEventList adapter;
        private List<FormEventModel> filterList;

        public MFilter(List<FormEventModel> filterList, AdapterFormEventList adapter) {
            this.adapter = adapter;
            this.filterList = filterList;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

//            if (constraint.toString().isEmpty()) {
//                mSearchText = "";
//            } else {
//                mSearchText = (String) constraint;
//            }

            if (constraint != null && constraint.length() > 0) {
                constraint = constraint.toString().toUpperCase();
                ArrayList<FormEventModel> filteredPlayers = new ArrayList<>();
                for (int i = 0; i < filterList.size(); i++) {
                    if (filterList.get(i).getEventName().toUpperCase().contains(constraint)) {
                        filteredPlayers.add(filterList.get(i));
                    }
                }
                results.count = filteredPlayers.size();
                results.values = filteredPlayers;
            } else {
                results.count = filterList.size();
                results.values = filterList;
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mItem = (ArrayList<FormEventModel>) results.values;
            adapter.notifyDataSetChanged();
        }
    }


    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public AdapterFormEventList(ArrayList<FormEventModel> formListModelArrayList, Context context) {
        this.mItem = formListModelArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public AdapterFormEventList.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = (View) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_form_list_new, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterFormEventList.MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final FormEventModel designModel = mItem.get(position);

        holder.tvFormName.setText(designModel.getEventName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mItemClickListener != null) {
                    mItemClickListener.onItemClick(v, position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mItem.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvFormName;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFormName = itemView.findViewById(R.id.tvFormName);
        }
    }
}