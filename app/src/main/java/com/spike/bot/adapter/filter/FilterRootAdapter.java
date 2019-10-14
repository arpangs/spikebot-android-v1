package com.spike.bot.adapter.filter;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.spike.bot.R;
import com.spike.bot.listener.FilterMarkAll;
import com.spike.bot.model.Filter;

import java.util.ArrayList;

/**
 * Created by Sagar on 3/4/18.
 * Gmail : jethvasagar2@gmail.com
 */

public class FilterRootAdapter extends RecyclerView.Adapter<FilterRootAdapter.FilterHolder> implements FilterSubAdapter.SubFilterEvent {

    ArrayList<Filter> filters;
    ArrayList<Filter.SubFilter> subFilterArrayList;

    private Context context;
    FilterMarkAll filterMarkAll;

    boolean isChecked = false;

    public FilterRootAdapter(ArrayList<Filter> filters1, FilterMarkAll filterMarkAll) {
        this.filters = filters1;
        this.filterMarkAll = filterMarkAll;
    }

    @Override
    public FilterHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_filter_root, parent, false);
        context = view.getContext();
        return new FilterHolder(view);
    }

    @Override
    public void onBindViewHolder(final FilterHolder holder, final int position) {

        final Filter filter = filters.get(position);

        if (filter.isChecked()) {
            holder.rootCheck.setChecked(true);
        } else {
            holder.rootCheck.setChecked(false);
        }


        holder.rootTitle.setText(filter.getName());

        holder.root_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (filter.isExpanded()) {
                    holder.subList.setVisibility(View.VISIBLE);
                } else {
                    holder.subList.setVisibility(View.GONE);
                }
                filter.setExpanded(!filter.isExpanded());
                filter.setChecked(filter.isChecked());
                notifyItemChanged(position, filter);
            }
        });

        holder.rootCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (filter.isChecked() || filter.isExpanded()) {
                    holder.subList.setVisibility(View.VISIBLE);

                } else {

                    holder.subList.setVisibility(View.GONE);
                    for (Filter.SubFilter subFilter : filter.getSubFilters()) {
                        subFilter.setChecked(true);
                    }
                }
                if (filter.isChecked()) {
                    for (Filter.SubFilter subFilter : filter.getSubFilters()) {
                        subFilter.setChecked(false);
                    }
                } else {
                    for (Filter.SubFilter subFilter : filter.getSubFilters()) {
                        subFilter.setChecked(true);
                    }
                }
                if (!filter.isExpanded()) {
                    filter.setExpanded(true);
                } else {
                    filter.setExpanded(filter.isExpanded());
                }
                filter.setChecked(!filter.isChecked());
                notifyItemChanged(position, filter);

                filterMarkAll.filterAllMark(filters);
            }
        });
        holder.subList.setVisibility(View.GONE);
        holder.root_arrow.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return filters.size();
    }

    @Override
    public void eventClick(int pos) {
        for (int i = 0; i < filters.size(); i++) {
            Filter filter = filters.get(i);
            if (i == pos) {
                subFilterArrayList = filter.getSubFilters();
                for (Filter.SubFilter subFilter : subFilterArrayList) {
                    if (subFilter.isChecked()) {
                        isChecked = true;
                    }
                }
                filter.setChecked(isChecked);
                notifyItemChanged(pos, filter);
            }
        }
    }

    class FilterHolder extends RecyclerView.ViewHolder {

        private CheckBox rootCheck;
        private TextView rootTitle;
        private RecyclerView subList;
        private LinearLayout root_ll;
        private ImageView root_arrow;


        public FilterHolder(View itemView) {
            super(itemView);

            rootCheck = (CheckBox) itemView.findViewById(R.id.root_checked);
            rootTitle = (TextView) itemView.findViewById(R.id.root_title);
            root_ll = (LinearLayout) itemView.findViewById(R.id.root_ll);
            root_arrow = (ImageView) itemView.findViewById(R.id.root_arrow);
            subList = (RecyclerView) itemView.findViewById(R.id.list_sub_filter);
            subList.setLayoutManager(new GridLayoutManager(context, 1));
        }
    }

}
