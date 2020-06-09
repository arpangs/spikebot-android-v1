package com.spike.bot.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.spike.bot.R;
import com.spike.bot.model.DeviceBrandRemoteList;

import java.util.ArrayList;

/**
 * Created by Sagar on 22/1/19.
 * Gmail : jethvasagar2@gmail.com
 */
public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SensorViewHolder> {

    private TempSensorInfoAdapter.OnNotificationContextMenu onNotificationContextMenu;
    private Context mContext;
    ArrayList<DeviceBrandRemoteList> arrayListLog = new ArrayList<>();
    public SearchClick searchClick;


    public SearchAdapter(Context context, ArrayList<DeviceBrandRemoteList> arrayListLog1, SearchClick selectCamera) {
        this.mContext = context;
        this.arrayListLog = arrayListLog1;
        this.searchClick = selectCamera;
    }

    @Override
    public SensorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_search, parent, false);
        return new SensorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SensorViewHolder holder, final int position) {

        holder.txtNameSearch.setText(arrayListLog.get(position).getModelNumber());

        holder.txtNameSearch.setId(position);
        holder.txtNameSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchClick.searchItemClick(arrayListLog.get(v.getId()));
            }
        });
    }


    @Override
    public int getItemCount() {
        return arrayListLog.size();
    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public class SensorViewHolder extends RecyclerView.ViewHolder {

        public TextView txtNameSearch;

        public SensorViewHolder(View view) {
            super(view);
            txtNameSearch = (TextView) itemView.findViewById(R.id.txtNameSearch);
        }
    }
}
