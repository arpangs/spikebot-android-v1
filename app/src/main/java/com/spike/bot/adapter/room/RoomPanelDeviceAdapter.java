package com.spike.bot.adapter.room;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.spike.bot.R;
import com.spike.bot.core.Common;
import com.spike.bot.customview.recycle.ItemClickListener;
import com.spike.bot.model.CameraVO;
import com.spike.bot.model.DeviceVO;

import java.util.List;

/**
 * Created by Sagar on 27/2/18.
 * Gmail : jethvasagar2@gmail.com
 */

public class RoomPanelDeviceAdapter extends RecyclerView.Adapter<RoomPanelDeviceAdapter.RoomPanelDevice>{

    private List<DeviceVO> deviceVOs;
    private List<CameraVO> cameraVOs;
    private Context context;
    private ItemClickListener itemClickListener;
    private boolean isCameraAdapter = false;

    public RoomPanelDeviceAdapter(List<DeviceVO> deviceVOs1, List<CameraVO> list, ItemClickListener itemClickListener, boolean isCameraAdapter){
        this.deviceVOs = deviceVOs1;
        this.itemClickListener = itemClickListener;
        this.isCameraAdapter = isCameraAdapter;
        this.cameraVOs = list;
    }


    @Override
    public RoomPanelDevice onCreateViewHolder(ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        return new RoomPanelDevice(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_device_dashboard,parent,false));

    }

    @Override
    public void onBindViewHolder(RoomPanelDevice holder, final int position) {

        if(!isCameraAdapter){

            final DeviceVO deviceVO = deviceVOs.get(position);

            holder.text_item.setText(deviceVO.getDeviceName());
            holder.iv_icon.setImageResource(Common.getIcon(deviceVO.getDeviceStatus(),String.valueOf(deviceVO.getDevice_icon())));

            holder.iv_icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("LongClick","1 = deviceID : " + deviceVO.getDeviceId() + " Type : " + deviceVO.getDeviceType());
                    itemClickListener.itemClicked(deviceVO,"itemclick",position);
                }
            });

            if(Integer.parseInt(deviceVO.getDeviceId())==1 && Integer.parseInt(deviceVO.getDeviceType())==1){
                holder.iv_icon.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        Log.d("LongClick","onLongClick");
                        itemClickListener.itemClicked(deviceVO,"longclick",position);
                        return true;
                    }
                });
            }
            else{
                holder.iv_icon.setOnLongClickListener(null);
            }
        }else{

            final CameraVO cameraVO = (CameraVO) cameraVOs.get(position);
            holder.text_item.setText(cameraVO.getCamera_name());
            holder.iv_icon.setImageResource(Common.getIcon(1,cameraVO.getCamera_icon()));

            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //   DeviceVO itemCopy = (DeviceVO) item.clone();
                    if(itemClickListener!=null){
                        itemClickListener.itemClicked(cameraVO,"cameraClick");
                    }
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        if(isCameraAdapter){
            return cameraVOs.size();
        }else{
            return deviceVOs.size();
        }
    }

    class RoomPanelDevice extends RecyclerView.ViewHolder{

        TextView text_item;
        ImageView iv_icon;
        private View view;

        RoomPanelDevice(View itemView) {
            super(itemView);
            this.view = itemView;
            text_item = (TextView)itemView.findViewById(R.id.text_item);
            iv_icon = (ImageView)itemView.findViewById(R.id.iv_icon);
        }
    }
}
