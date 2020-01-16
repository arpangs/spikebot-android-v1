package com.spike.bot.adapter;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.spike.bot.R;
import com.spike.bot.adapter.filter.CameraNotificationAdapter;
import com.spike.bot.core.Common;
import com.spike.bot.model.CameraAlertList;
import com.spike.bot.model.CameraViewModel;

import java.util.ArrayList;

/**
 * Created by Sagar on 26/11/18.
 * Gmail : jethvasagar2@gmail.com
 */
public class CameraNotiListAdapter extends RecyclerView.Adapter<CameraNotiListAdapter.SensorViewHolder>{

    private TempSensorInfoAdapter.OnNotificationContextMenu onNotificationContextMenu;
    private Context mContext;
    ArrayList<CameraViewModel> arrayListLog=new ArrayList<>();


    public CameraNotiListAdapter(Context context, ArrayList<CameraViewModel> arrayListLog1){
        this.mContext=context;
        this.arrayListLog=arrayListLog1;
    }

    @Override
    public SensorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_room_camera_item,parent,false);
        return new SensorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SensorViewHolder holder, final int position) {

        if(arrayListLog.get(position).getIsActivite().equalsIgnoreCase("0")){
            holder.iv_icon.setImageResource(Common.getIconInActive(0,"camera"));
        }else{
            holder.iv_icon.setImageResource(Common.getIcon(1,"camera"));
        }


        holder.text_item.setText(""+arrayListLog.get(position).getName());
        holder.iv_icon_text.setVisibility(View.GONE);
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

        public TextView text_item;
        public ImageView iv_icon_text,iv_icon_active_camera,iv_icon;

        public SensorViewHolder(View view) {
            super(view);
            text_item =  itemView.findViewById(R.id.text_item);
            iv_icon_text =  itemView.findViewById(R.id.iv_icon_text);
            iv_icon_active_camera =  itemView.findViewById(R.id.iv_icon_active_camera);
            iv_icon =  itemView.findViewById(R.id.iv_icon);
        }
    }
}
