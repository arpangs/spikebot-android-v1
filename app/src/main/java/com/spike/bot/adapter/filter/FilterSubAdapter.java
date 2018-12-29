package com.spike.bot.adapter.filter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.spike.bot.R;
import com.spike.bot.model.Filter;

import java.util.ArrayList;

/**
 * Created by Sagar on 3/4/18.
 * Gmail : jethvasagar2@gmail.com
 */

public class FilterSubAdapter extends RecyclerView.Adapter<FilterSubAdapter.SubFilterHolder>{

    ArrayList<Filter.SubFilter> subFilters;
    SubFilterEvent subFilterEvent;
    int pos;

    public FilterSubAdapter(int position, SubFilterEvent isFilter, ArrayList<Filter.SubFilter> subFilterArrayList){
        this.subFilters = subFilterArrayList;
        this.subFilterEvent = isFilter;
        this.pos = position;
    }

    @Override
    public SubFilterHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_filter_sublist,parent,false);
        return new SubFilterHolder(view);
    }

    @Override
    public void onBindViewHolder(SubFilterHolder holder, final int position) {

        final Filter.SubFilter subFilter = subFilters.get(position);
        holder.subTitle.setText(subFilter.getName());


        if(subFilter.isChecked()){
            holder.subCheckbox.setChecked(true);
        }else{
            holder.subCheckbox.setChecked(false);
        }
        holder.subroot_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isCheck = !subFilter.isChecked();
                subFilter.setChecked(isCheck);
                subFilterEvent.eventClick(pos);
                notifyItemChanged(position,subFilter);
            }
        });

        holder.subCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isCheck = !subFilter.isChecked();
                subFilter.setChecked(isCheck);
                subFilterEvent.eventClick(pos);
                notifyItemChanged(position,subFilter);
            }
        });
    }

    @Override
    public int getItemCount() {
        return subFilters.size();
    }

    class SubFilterHolder extends RecyclerView.ViewHolder{

        private CheckBox subCheckbox;
        private TextView subTitle;
        private LinearLayout subroot_ll;

        public SubFilterHolder(View itemView) {
            super(itemView);

            subCheckbox = (CheckBox) itemView.findViewById(R.id.subroot_checked);
            subTitle = (TextView) itemView.findViewById(R.id.subroot_title);
            subroot_ll = (LinearLayout) itemView.findViewById(R.id.subroot_ll);
        }
    }

    public interface SubFilterEvent{
        void eventClick(int pos);
    }

}
