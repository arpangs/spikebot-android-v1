package com.spike.bot.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.spike.bot.R;
import com.spike.bot.core.Common;
import com.spike.bot.model.UnassignedListRes;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sagar on 3/10/18.
 * Gmail : jethvasagar2@gmail.com
 */
public class AddUnassignedPanelAdapter extends RecyclerView.Adapter<AddUnassignedPanelAdapter.UnassignedHolder> {

    private List<UnassignedListRes.Data> roomdeviceList;
    private UnassignedClickEvent unassignedClickEvent;
    private Context mContext;

    public AddUnassignedPanelAdapter(Context context, ArrayList<UnassignedListRes.Data> roomdeviceList, UnassignedClickEvent unassignedClickEvent) {
        this.mContext = context;
        this.roomdeviceList = roomdeviceList;
        this.unassignedClickEvent = unassignedClickEvent;
    }

    @Override
    public UnassignedHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_unassigned_list, parent, false);
        return new UnassignedHolder(view);
    }

    @Override
    public void onBindViewHolder(UnassignedHolder holder, final int position) {

        holder.mModuleId.setText("[" + roomdeviceList.get(position).getModuleId() + "]");

        if (roomdeviceList.get(position).getModuleType().equalsIgnoreCase("door_sensor")) {
            holder.mImageIcon.setImageResource(Common.getIcon(0, roomdeviceList.get(position).getModuleType()));
        } else {
            holder.mImageIcon.setImageResource(Common.getIcon(0, roomdeviceList.get(position).getModuleType()));
        }

        if (roomdeviceList.get(position).getModuleType().equalsIgnoreCase("5f")) {
            holder.mDeviceName.setText("Switch Board");
            holder.mImageIcon.setImageResource(R.drawable.switchboard);
        } else {

            String deviceName = "";

            switch (roomdeviceList.get(position).getModuleType()) {

                case "heavy_load":
                case "double_heavy_load":
                    deviceName = "Heavy Load";
                    break;

                case "door_sensor":
                    deviceName = "Door Sensor";
                    break;

                case "gas_sensor":
                    deviceName = "Gas Sensor";
                    break;

                case "curtain":
                    deviceName = "Curtain";
                    break;

                case "water_detector":
                    deviceName = "Water Detector";
                    break;

                case "pir_detector":
                    deviceName = "Pir Detector";
                    break;

                case "pir_device":
                    deviceName = "Pir Device";
                    break;

                case "repeater":
                    deviceName = "Repeater";
                    break;

                case "yale_lock":
                    deviceName = "Yale Lock";
                    break;

                case "temp_sensor":
                    deviceName = "Temperature Sensor";
                    break;

                case "smart_remote":
                    deviceName = "Smart Remote";
                    break;

                case "beacon_scanner":
                    deviceName = "Beamer";
                    break;

                case "beacon":
                    deviceName = "Radar";
                    break;

                case "remote":
                    deviceName = "Remote";
                    break;

                case "ir_blaster":
                    deviceName = "IR Blaster";
                    break;


            }

            holder.mDeviceName.setText(deviceName);
        }


        holder.mImgAdd.setId(position);
        holder.mImgAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                unassignedClickEvent.onClick(position, roomdeviceList.get(holder.mImgAdd.getId()), "add");

               /* PopupMenu popup = new PopupMenu(mContext, v);
                @SuppressLint("RestrictedApi") Context wrapper = new ContextThemeWrapper(mContext, R.style.PopupMenu);
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                    popup = new PopupMenu(wrapper, v, Gravity.RIGHT);
                } else {
                    popup = new PopupMenu(wrapper, v);
                }
                popup.getMenuInflater().inflate(R.menu.menu_dots, popup.getMenu());
                popup.getMenu().findItem(R.id.action_edit_dots).setVisible(false);
                popup.getMenu().findItem(R.id.action_add_dots).setVisible(true);
                popup.getMenu().findItem(R.id.action_log).setVisible(false);

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_add_dots:
                                unassignedClickEvent.onClick(position,roomdeviceList.get(holder.mImgAdd.getId()), "add");
                                break;
                            case R.id.action_delete_dots:
                                unassignedClickEvent.onClick(position,roomdeviceList.get(holder.mImgAdd.getId()), "delete");
                                break;
                        }
                        return true;
                    }
                });
                popup.show();*/
            }
        });

      /*  holder.mImgAdd.setId(position);
        holder.mImgAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unassignedClickEvent.onClick(holder.mImgAdd.getId(), roomdeviceList.get(holder.mImgAdd.getId()));


            }
        });*/

    }

    @Override
    public int getItemCount() {
        return roomdeviceList.size();
    }

    public interface UnassignedClickEvent {
        void onClick(int position, UnassignedListRes.Data roomdeviceList, String action);
    }

    class UnassignedHolder extends RecyclerView.ViewHolder {

        private ImageView mImageIcon, mImgAdd;
        private TextView mDeviceName, mModuleId;

        UnassignedHolder(View itemView) {
            super(itemView);
            mImageIcon = itemView.findViewById(R.id.up_device_icon);
            mDeviceName = itemView.findViewById(R.id.up_device_name);
            mModuleId = itemView.findViewById(R.id.up_device_module);
            mImgAdd = itemView.findViewById(R.id.up_save);
        }
    }
}
