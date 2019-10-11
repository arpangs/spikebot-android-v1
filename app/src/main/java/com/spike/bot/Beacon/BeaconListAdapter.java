package com.spike.bot.Beacon;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.adapter.TempSensorInfoAdapter;
import com.spike.bot.core.Common;
import com.spike.bot.listener.SelectCamera;
import com.spike.bot.model.CameraVO;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sagar on 27/11/18.
 * Gmail : jethvasagar2@gmail.com
 */
public class BeaconListAdapter extends RecyclerView.Adapter<BeaconListAdapter.SensorViewHolder>{

    private Context mContext;
    List<LeDeviceItem> arrayListLog=new ArrayList<>();


    public BeaconListAdapter(Context context, List<LeDeviceItem> arrayListLog1){
        this.mContext=context;
        this.arrayListLog=arrayListLog1;
    }

    @Override
    public SensorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_beacon,parent,false);
        return new SensorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SensorViewHolder holder, final int position) {

        Log.d("System out","mLeScanCallback is 22 adapter "+arrayListLog.get(position).getDevice().getScanRecord());
        holder.txtMacAddress.setText("Address : "+arrayListLog.get(position).getDevice().getAddress());
        holder.txtName.setText("Name : "+arrayListLog.get(position).getDevice().getName());
        holder.txtRss.setText("RSSI : "+arrayListLog.get(position).getDevice().getRssi());
    }


    @Override
    public int getItemCount() {
        return arrayListLog.size();
    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public class SensorViewHolder extends RecyclerView.ViewHolder{

        public AppCompatTextView txtName,txtMacAddress,txtRss,txtPower;

        public SensorViewHolder(View view) {
            super(view);
            txtMacAddress =  itemView.findViewById(R.id.txtMacAddress);
            txtName =  itemView.findViewById(R.id.txtName);
            txtRss =  itemView.findViewById(R.id.txtRss);
            txtPower =  itemView.findViewById(R.id.txtPower);
        }
    }
}
