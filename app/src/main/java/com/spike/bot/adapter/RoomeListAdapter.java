package com.spike.bot.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.spike.bot.R;
import com.spike.bot.core.Common;
import com.spike.bot.listener.SelectCamera;
import com.spike.bot.model.CameraVO;
import com.spike.bot.model.RoomVO;

import java.util.ArrayList;

/**
 * Created by Sagar on 6/3/19.
 * Gmail : jethvasagar2@gmail.com
 */
public class RoomeListAdapter extends RecyclerView.Adapter<RoomeListAdapter.SensorViewHolder>{

    private TempSensorInfoAdapter.OnNotificationContextMenu onNotificationContextMenu;
    private Context mContext;
    ArrayList<RoomVO> arrayListLog=new ArrayList<>();


    public RoomeListAdapter(Context context, ArrayList<RoomVO> arrayListLog1){
        this.mContext=context;
        this.arrayListLog=arrayListLog1;
    }

    @Override
    public SensorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_room_list,parent,false);
        return new SensorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SensorViewHolder holder, final int position) {

        holder.txtRoomName.setText(arrayListLog.get(position).getRoomName());

        if(arrayListLog.get(position).isDisable()){
           holder.checkbox.setChecked(true);
        }else {
            holder.checkbox.setChecked(false);
        }

        holder.checkbox.setId(position);
        holder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                arrayListLog.get(position).setDisable(isChecked);
            }
        });

        holder.txtRoomName.setId(position);
        holder.txtRoomName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.checkbox.performClick();
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

        public TextView txtRoomName;
        public CheckBox checkbox;

        public SensorViewHolder(View view) {
            super(view);
            checkbox =  itemView.findViewById(R.id.checkbox);
            txtRoomName =  itemView.findViewById(R.id.txtRoomName);
        }
    }

}
