package com.spike.bot.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.spike.bot.R;
import com.spike.bot.core.Common;
import com.spike.bot.model.SensorUnassignedRes;

import java.util.List;

/**
 * Created by Sagar on 17/5/18.
 * Gmail : jethvasagar2@gmail.com
 */

public class SensorUnassignedAdapter extends RecyclerView.Adapter<SensorUnassignedAdapter.SensorViewHolder> {

    List<SensorUnassignedRes.Data.UnassigendSensorList> unassigendSensorLists;
    SensorUnassignedRes.Data.UnassigendSensorList unassigendSensorList;
    SensorUnassignedRes.Data.UnassigendSensorList noti;

    public SensorUnassignedAdapter(List<SensorUnassignedRes.Data.UnassigendSensorList> unassigendSensorLists) {
        this.unassigendSensorLists = unassigendSensorLists;
    }

    @Override
    public SensorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.row_sensor_unassigned_item, parent, false);
        return new SensorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SensorViewHolder holder, final int position) {

        unassigendSensorList = unassigendSensorLists.get(position);

        holder.sensorName.setText(unassigendSensorList.getSensorName());
        if (unassigendSensorList.getSensorIcon().equals("doorsensor")) {

            if (unassigendSensorList.getLock_subtype().equals("1")) {
                holder.sensorImage.setImageResource(R.drawable.off_door);
            } else if (unassigendSensorList.getLock_subtype().equals("2")) {
                holder.sensorImage.setImageResource(R.drawable.lock_only_grey);
            } else {
                holder.sensorImage.setImageResource(R.drawable.door_locked);
            }

        } else {
            holder.sensorImage.setImageResource(Common.getIcon(1, unassigendSensorList.getSensorIcon()));
        }

        if (unassigendSensorList.isChecked) {
            holder.sensorChecked.setImageResource(R.drawable.icn_circle_grey_checked);
        } else {
            holder.sensorChecked.setImageResource(R.drawable.icn_circle_grey);
        }

        holder.sensorChecked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for (int i = 0; i < unassigendSensorLists.size(); i++) {
                    noti = unassigendSensorLists.get(i);
                    if (i == position) {
                        noti.setChecked(!noti.isChecked());
                    } else {
                        noti.setChecked(false);
                    }
                }
                notifyDataSetChanged();
            }
        });

        holder.ll_sensor_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for (int i = 0; i < unassigendSensorLists.size(); i++) {
                    noti = unassigendSensorLists.get(i);
                    if (i == position) {
                        noti.setChecked(!noti.isChecked());
                    } else {
                        noti.setChecked(false);
                    }
                }
                notifyDataSetChanged();
            }
        });

    }

    @Override
    public int getItemCount() {
        return unassigendSensorLists.size();
    }

    public class SensorViewHolder extends RecyclerView.ViewHolder {

        private ImageView sensorImage, sensorChecked;
        private TextView sensorName;
        private LinearLayout ll_sensor_view;

        public SensorViewHolder(View itemView) {
            super(itemView);

            sensorImage = (ImageView) itemView.findViewById(R.id.img_un_sensor_type);
            sensorName = (TextView) itemView.findViewById(R.id.txt_un_sensor_name_un);
            sensorChecked = (ImageView) itemView.findViewById(R.id.img_un_sensor_selected);
            ll_sensor_view = (LinearLayout) itemView.findViewById(R.id.ll_sensor_view);
        }
    }

    public SensorUnassignedRes.Data.UnassigendSensorList getSelectedSensor() {

        for (int i = 0; i < unassigendSensorLists.size(); i++) {
            noti = unassigendSensorLists.get(i);
            if (noti.isChecked) {
                return noti;
            }
        }
        return null;

    }

}
