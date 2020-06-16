package com.spike.bot.Beacon;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.core.BeaconDiffCallBack;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import no.nordicsemi.android.support.v18.scanner.ScanResult;


public class BeaconScanListAdapter extends RecyclerView.Adapter<BeaconScanListAdapter.SensorViewHolder> {

    private Context mContext;
    List<ScanResult> arrayListscanresult = new ArrayList<>();
    BeaconListClickEvent beaconListClickEvent;

    public BeaconScanListAdapter(List<ScanResult> arrayListscanresult1, BeaconListClickEvent beaconclickevent) {
        this.arrayListscanresult = arrayListscanresult1;
        this.beaconListClickEvent = beaconclickevent;

       // this.arrayListscanresult.addAll(arrayListscanresult1);
    }




    @Override
    public SensorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_beacon, parent, false);
        return new SensorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SensorViewHolder holder, int position) {
        holder.txtMacAddress.setText("Address : " + arrayListscanresult.get(position).getDevice().getAddress());
        if (TextUtils.isEmpty(arrayListscanresult.get(position).getScanRecord().getDeviceName()) || arrayListscanresult.get(position).getScanRecord().getDeviceName() == null) {
            holder.txtName.setText("Name : " + "- -");
            //  holder.txtName.setVisibility(View.GONE);
        } else {
            holder.txtName.setVisibility(View.VISIBLE);
            holder.txtName.setText("Name : " + arrayListscanresult.get(position).getScanRecord().getDeviceName());
        }
        int Measured_Power = -65;
        int rssi = arrayListscanresult.get(position).getRssi();
        String range = String.valueOf(Measured_Power - rssi);
        double finaldis = Double.parseDouble(range) / Double.parseDouble("20");
        double Distance = (float) Math.pow(10,finaldis);
        try {

            holder.txtRss.setText("Rssi : " + arrayListscanresult.get(position).getRssi());
        //    holder.txtDistance.setText("Distance : " + String.format("%.1f", Distance + "m"));

            DecimalFormat form = new DecimalFormat("0.0");
            holder.txtDistance.setText("Distance : " + form.format(Distance) + "m");
        }catch (Exception e){
            e.printStackTrace();
        }

        ChatApplication.logDisplay("Distance " + Distance);

        holder.view_beacon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                beaconListClickEvent.onClickBeaconList(arrayListscanresult.get(position));
            }
        });

    }

/*    @Override
    public void onBindViewHolder(final SensorViewHolder holder, final int position,List<Object> payloads) {

        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads);
        } else {
            Bundle o = (Bundle) payloads.get(position);
            for (String key : o.keySet()) {
                if (key.equals("address")) {
                    holder.txtRss.setText("RSSI : " + arrayListscanresult.get(position).getRssi());
                }
            }
        }
    }*/


    @Override
    public int getItemCount() {
        return arrayListscanresult.size();
    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public class SensorViewHolder extends RecyclerView.ViewHolder {

        public AppCompatTextView txtName, txtMacAddress, txtRss, txtPower,txtDistance;
        LinearLayout view_beacon;

        public SensorViewHolder(View view) {
            super(view);
            txtMacAddress = itemView.findViewById(R.id.txtMacAddress);
            txtName = itemView.findViewById(R.id.txtName);
            txtRss = itemView.findViewById(R.id.txtRss);
            txtPower = itemView.findViewById(R.id.txtPower);
            txtDistance= itemView.findViewById(R.id.txtDistance);
            view_beacon = itemView.findViewById(R.id.view_beacon);
        }
    }

    public interface BeaconListClickEvent {
        void onClickBeaconList(ScanResult scanresultlist);
    }

    public void updatebeaconlistitem(List<ScanResult> results) {
        final BeaconDiffCallBack diffCallback = new BeaconDiffCallBack(this.arrayListscanresult, results);
        final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);
        this.arrayListscanresult.clear();
        this.arrayListscanresult.addAll(results);
        diffResult.dispatchUpdatesTo(this);
    }

}
