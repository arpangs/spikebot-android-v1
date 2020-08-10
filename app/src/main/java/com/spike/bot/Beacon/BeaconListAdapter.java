package com.spike.bot.Beacon;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.spike.bot.R;
import com.spike.bot.adapter.TempSensorInfoAdapter;
import com.spike.bot.model.CameraVO;
import com.spike.bot.model.IRBlasterAddRes;

import java.util.ArrayList;
import java.util.List;

public class BeaconListAdapter extends RecyclerView.Adapter<BeaconListAdapter.SensorViewHolder> {

    List<IRBlasterAddRes.Datum> arrayListbeacon = new ArrayList<>();
    public BeaconClickListener clickListener;


    public BeaconListAdapter(List<IRBlasterAddRes.Datum> arrayListbeacon1, BeaconClickListener clickListener) {
        this.arrayListbeacon = arrayListbeacon1;
        this.clickListener = clickListener;
    }

    @Override
    public BeaconListAdapter.SensorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_repeater, parent, false);
        return new BeaconListAdapter.SensorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final BeaconListAdapter.SensorViewHolder holder, final int position) {

        holder.txtName.setText(arrayListbeacon.get(position).getDeviceName());

        holder.imgBeacon.setImageResource(R.drawable.beaconsearch);

        if(arrayListbeacon.get(position).getRange() > 0)
        holder.mBeaconRange.setText(arrayListbeacon.get(position).getRange() + " m");

        holder.imgEditBeacon.setId(position);
        holder.imgEditBeacon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.editClicked(arrayListbeacon.get(holder.imgEditBeacon.getId()), holder.imgEditBeacon.getId(), 1);
            }
        });

    }


    @Override
    public int getItemCount() {
        return arrayListbeacon.size();
    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public class SensorViewHolder extends RecyclerView.ViewHolder {

        public AppCompatTextView txtName;
        public ImageView imgEditBeacon, imgBeacon;
        public TextView mBeaconRange;

        public SensorViewHolder(View view) {
            super(view);
            txtName = itemView.findViewById(R.id.txtName);
            imgBeacon = itemView.findViewById(R.id.imgRepeatar);
            imgEditBeacon = itemView.findViewById(R.id.imgEditRepeater);
            mBeaconRange = itemView.findViewById(R.id.txt_beacon_range);
        }
    }

    public interface BeaconClickListener {
        void editClicked(IRBlasterAddRes.Datum beaconmodel, int position, int type);
    }
}
