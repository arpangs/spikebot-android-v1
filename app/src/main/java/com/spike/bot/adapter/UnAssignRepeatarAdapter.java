package com.spike.bot.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.spike.bot.R;
import com.spike.bot.core.Common;
import com.spike.bot.model.SensorUnassignedRes;

import java.util.List;

/**
 * Created by Sagar on 23/9/19.
 * Gmail : vipul patel
 */
public class UnAssignRepeatarAdapter extends RecyclerView.Adapter<UnAssignRepeatarAdapter.SensorViewHolder>{

    List<SensorUnassignedRes.Data.UnassigendSensorList> unassigendSensorLists;

    public UnAssignRepeatarAdapter(Context context,List<SensorUnassignedRes.Data.UnassigendSensorList> unassigendSensorLists){
        this.unassigendSensorLists = unassigendSensorLists;
    }

    @Override
    public SensorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.row_sensor_unassigned_item,parent,false);
        return new SensorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SensorViewHolder holder, final int position) {

        final SensorUnassignedRes.Data.UnassigendSensorList unassigendSensorList = unassigendSensorLists.get(position);

        holder.sensorName.setText(unassigendSensorList.getSensorName());
//        if(unassigendSensorList.getSensorIcon().equals("doorsensor")){
//
//            if(unassigendSensorList.getLock_subtype().equals("1")){
//                holder.sensorImage.setImageResource(R.drawable.off_door);
//            }else if(unassigendSensorList.getLock_subtype().equals("2")){
//                holder.sensorImage.setImageResource(R.drawable.lock_only_grey);
//            }else {
//                holder.sensorImage.setImageResource(R.drawable.door_locked);
//            }
//
//        }else {
            holder.sensorImage.setImageResource(R.drawable.gray_repeater);
//        }

        if(unassigendSensorList.isChecked){
            holder.sensorChecked.setImageResource(R.drawable.icn_circle_grey_checked);
        }else{
            holder.sensorChecked.setImageResource(R.drawable.icn_circle_grey);
        }

        holder.sensorChecked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for (int i = 0; i < unassigendSensorLists.size(); i++) {
                    SensorUnassignedRes.Data.UnassigendSensorList noti = unassigendSensorLists.get(i);
                    if(i==position){
                        noti.setChecked(!noti.isChecked());
                    }else{
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
                    SensorUnassignedRes.Data.UnassigendSensorList noti = unassigendSensorLists.get(i);
                    if(i==position){
                        noti.setChecked(!noti.isChecked());
                    }else{
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

    public class SensorViewHolder extends RecyclerView.ViewHolder{

        private ImageView sensorImage,sensorChecked;
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

    public SensorUnassignedRes.Data.UnassigendSensorList  getSelectedSensor(){

        for (int i = 0; i < unassigendSensorLists.size(); i++) {
            SensorUnassignedRes.Data.UnassigendSensorList noti = unassigendSensorLists.get(i);
            if(noti.isChecked){
                return noti;
            }
        }
        return null;

    }

}
