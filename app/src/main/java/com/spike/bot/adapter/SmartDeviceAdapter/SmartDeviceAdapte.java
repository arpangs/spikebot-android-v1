package com.spike.bot.adapter.SmartDeviceAdapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.activity.SmartColorPickerActivity;
import com.spike.bot.customview.OnSwipeTouchListener;
import com.spike.bot.listener.LIsterSmartDevice;
import com.spike.bot.model.SmartDeviceModel;

import java.util.ArrayList;

public class SmartDeviceAdapte extends RecyclerView.Adapter<SmartDeviceAdapte.SensorViewHolder>{

    private Context mContext;
    ArrayList<SmartDeviceModel> arrayListLog=new ArrayList<>();
    public LIsterSmartDevice lIsterSmartDevice;


    public SmartDeviceAdapte(Context context, ArrayList<SmartDeviceModel> arrayListLog1,LIsterSmartDevice lIsterSmartDevice){
        this.mContext=context;
        this.arrayListLog=arrayListLog1;
        this.lIsterSmartDevice=lIsterSmartDevice;
    }

    @Override
    public SensorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_smart_device,parent,false);
        return new SensorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SensorViewHolder holder, final int position) {

        holder.txtName.setText(arrayListLog.get(position).getName());

        holder.linearView.setId(position);
        holder.linearView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(arrayListLog.get(v.getId()).getOnfff().equalsIgnoreCase("true")){
                    Intent intent=new Intent(mContext, SmartColorPickerActivity.class);
                    intent.putExtra("arraylist",arrayListLog.get(v.getId()));
                    mContext.startActivity(intent);
                }else {
                    ChatApplication.showToast(mContext,"bulb is off. please check");
                }

            }
        });

        if(arrayListLog.get(position).getOnfff().equalsIgnoreCase("true")){
            holder.switchOnOff.setChecked(true);
        }else {
            holder.switchOnOff.setChecked(false);
        }


        holder.switchOnOff.setId(position);
        holder.switchOnOff.setOnTouchListener(new OnSwipeTouchListener(mContext){
            @Override
            public void onClick() {
                super.onClick();
                holder.switchOnOff.setChecked(holder.switchOnOff.isChecked());
                lIsterSmartDevice.listeronoff(arrayListLog.get(position),holder.switchOnOff.getId());
            }

            @Override
            public void onSwipeLeft() {
                super.onSwipeLeft();
                holder.switchOnOff.setChecked(holder.switchOnOff.isChecked());
                lIsterSmartDevice.listeronoff(arrayListLog.get(position),holder.switchOnOff.getId());
            }

            @Override
            public void onSwipeRight() {
                super.onSwipeRight();
                holder.switchOnOff.setChecked(holder.switchOnOff.isChecked());
                lIsterSmartDevice.listeronoff(arrayListLog.get(position),holder.switchOnOff.getId());
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

    public class SensorViewHolder extends RecyclerView.ViewHolder{

        public TextView txtName;
        public SwitchCompat switchOnOff;
        public LinearLayout linearView;

        public SensorViewHolder(View view) {
            super(view);
            txtName =  itemView.findViewById(R.id.txtName);
            switchOnOff =  itemView.findViewById(R.id.switchOnOff);
            linearView =  itemView.findViewById(R.id.linearView);
        }
    }
}
