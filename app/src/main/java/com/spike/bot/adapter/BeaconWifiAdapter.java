package com.spike.bot.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.spike.bot.R;
import com.spike.bot.listener.WifiListner;
import com.spike.bot.model.WifiModel;

import java.util.ArrayList;

public class BeaconWifiAdapter extends RecyclerView.Adapter<BeaconWifiAdapter.SensorViewHolder> {

    private Context mContext;
    public ArrayList<WifiModel.WiFiList> arrayList = new ArrayList<>();
    public WifiListner wifiListner;


    public BeaconWifiAdapter(Context context, ArrayList<WifiModel.WiFiList> arrayList, WifiListner wifiListner) {
        this.mContext = context;
        this.arrayList = arrayList;
        this.wifiListner = wifiListner;

    }

    @Override
    public SensorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_wifi_list, parent, false);
        return new SensorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SensorViewHolder holder, final int position) {

        holder.txtWifiName.setText(arrayList.get(position).getSSID());

        holder.linearWifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wifiListner.wifiClick(arrayList.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public class SensorViewHolder extends RecyclerView.ViewHolder {

        public TextView txtWifiName;
        public LinearLayout linearWifi;

        public SensorViewHolder(View view) {
            super(view);
            txtWifiName = (TextView) itemView.findViewById(R.id.txtWifiName);
            linearWifi = (LinearLayout) itemView.findViewById(R.id.linearWifi);
        }
    }

}
