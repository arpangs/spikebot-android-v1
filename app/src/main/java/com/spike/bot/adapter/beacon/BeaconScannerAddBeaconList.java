package com.spike.bot.adapter.beacon;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.spike.bot.R;
import com.spike.bot.model.IRBlasterAddRes;

import java.util.List;

public class BeaconScannerAddBeaconList extends RecyclerView.Adapter<BeaconScannerAddBeaconList.BeaconScannerViewHolder> {

    private Context mContext;
    private List<IRBlasterAddRes.RemoteList> remoteList;
    IRBlasterAddRes.RemoteList remote;

    public BeaconScannerAddBeaconList(List<IRBlasterAddRes.RemoteList> remoteList) {
       this.remoteList = remoteList;
    }

    @Override
    public BeaconScannerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_room_edit_device_v2, parent, false);
        mContext = view.getContext();
        return new BeaconScannerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BeaconScannerViewHolder holder, int position) {
        try {
            remote = remoteList.get(position);
            holder.mBeaconName.setText(remote.getDeviceName());
            //TODO code here for change remote icon
            holder.mBeaconIcon.setImageResource(R.drawable.beaconsearch);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        if (remoteList.size() == 0) {
            return 1;
        } else {
            return remoteList.size();
        }
    }

    class BeaconScannerViewHolder extends RecyclerView.ViewHolder {

        private TextView mBeaconName;
        private ImageView mBeaconIcon;

        BeaconScannerViewHolder(View itemView) {
            super(itemView);
            mBeaconName =  itemView.findViewById(R.id.text_item);
            mBeaconIcon =  itemView.findViewById(R.id.iv_icon);
        }
    }
}
