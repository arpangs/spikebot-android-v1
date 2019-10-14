package com.spike.bot.adapter.SmartDeviceAdapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.spike.bot.R;
import com.spike.bot.activity.SmartDevice.PhilipsHueBridgeDeviceListActivity;
import com.spike.bot.model.SmartBrandModel;

import java.util.ArrayList;

/**
 * Created by Sagar on 2/8/19.
 * Gmail : vipul patel
 */
public class BridgeListAdapter extends RecyclerView.Adapter<BridgeListAdapter.SensorViewHolder> {

    private Context mContext;
    public int type = 0;
    ArrayList<SmartBrandModel> arrayListLog = new ArrayList<>();

    public BridgeListAdapter(Context context, ArrayList<SmartBrandModel> arrayList, int type) {
        this.mContext = context;
        this.type = type;
        this.arrayListLog = arrayList;
    }

    @Override
    public SensorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_bridge_list, parent, false);
        return new SensorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SensorViewHolder holder, final int position) {

        holder.txtTitle.setVisibility(View.GONE);
        holder.txtBridgeName.setText(arrayListLog.get(position).getBridge_name());

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, PhilipsHueBridgeDeviceListActivity.class);
                intent.putExtra("host_ip", "" + arrayListLog.get(position).getHost_ip());
                intent.putExtra("getBridge_name", "" + arrayListLog.get(position).getBridge_name());
                intent.putExtra("bridge_id", "" + arrayListLog.get(position).getBridge_id());
                mContext.startActivity(intent);
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

        public View view;
        public TextView txtBridgeName, txtTitle;

        public SensorViewHolder(View view) {
            super(view);
            this.view = view;
            txtBridgeName = itemView.findViewById(R.id.txtBridgeName);
            txtTitle = itemView.findViewById(R.id.txtTitle);

        }
    }
}
