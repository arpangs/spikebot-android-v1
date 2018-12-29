package com.spike.bot.adapter.irblaster;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.spike.bot.R;
import com.spike.bot.model.IRRemoteListRes;

import java.nio.file.DirectoryStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sagar on 2/8/18.
 * Gmail : jethvasagar2@gmail.com
 */
public class IRRemoteBrandListAdapter extends RecyclerView.Adapter<IRRemoteBrandListAdapter.RemoteBrand> implements Filterable{

    List<IRRemoteListRes.Data.BrandList> brandLists;

    private List<String> filteredData = null;
    private List<String> originalData;
    private LayoutInflater mInflater;

    private IRRemoteListClickEvent irRemoteListClickEvent;

    private ItemFilter mFilter = new ItemFilter();

    public IRRemoteBrandListAdapter(List<IRRemoteListRes.Data.BrandList> brandLists,IRRemoteListClickEvent irRemoteListClickEvent){
        this.brandLists = brandLists;
        this.irRemoteListClickEvent = irRemoteListClickEvent;

      /*  for(IRRemoteListRes.Data.BrandList brands : brandLists){
            originalData.add(brands.getBrandType());
        }*/
    }

    @Override
    public RemoteBrand onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_ir_brand_list,parent,false);
        return new RemoteBrand(view);
    }

    @Override
    public void onBindViewHolder(RemoteBrand holder, final int position) {

        holder.ir_brand_name.setText(brandLists.get(position).getBrandType());
        holder.ir_brand_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                irRemoteListClickEvent.onClickRemoteList(brandLists.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return brandLists.size();
    }

    class RemoteBrand extends RecyclerView.ViewHolder{

        private RelativeLayout ir_brand_view;
        private TextView ir_brand_name;

        RemoteBrand(View itemView) {
            super(itemView);
            ir_brand_view = (RelativeLayout) itemView.findViewById(R.id.ir_brand_view);
            ir_brand_name = itemView.findViewById(R.id.ir_brand_name);
        }
    }

    public Filter getFilter() {
        return mFilter;
    }

    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String filterString = constraint.toString().toLowerCase();

            FilterResults results = new FilterResults();

            final List<String> list = originalData;

            int count = list.size();
            final ArrayList<String> nlist = new ArrayList<String>(count);

            String filterableString ;

            for (int i = 0; i < count; i++) {
                filterableString = list.get(i);
                if (filterableString.toLowerCase().contains(filterString)) {
                    nlist.add(filterableString);
                }
            }

            results.values = nlist;
            results.count = nlist.size();

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredData = (ArrayList<String>) results.values;
            notifyDataSetChanged();
        }
    }

    public interface IRRemoteListClickEvent{
        void onClickRemoteList(IRRemoteListRes.Data.BrandList brandList);
    }
}
