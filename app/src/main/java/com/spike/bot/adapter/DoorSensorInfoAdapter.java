package com.spike.bot.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import com.spike.bot.R;
import com.spike.bot.core.Common;
import com.spike.bot.core.Constants;
import com.spike.bot.customview.OnSwipeTouchListener;
import com.spike.bot.model.DoorSensorResModel;
import com.kp.core.DateHelper;

import java.text.ParseException;

/**
 * Created by Sagar on 7/5/18.
 * Gmail : jethvasagar2@gmail.com
 */

public class DoorSensorInfoAdapter extends RecyclerView.Adapter<DoorSensorInfoAdapter.SensorViewHolder>{

    DoorSensorResModel.DATA.DoorList.NotificationList[] notificationList;
    private boolean isCF;
    private OnNotificationContextMenu onNotificationContextMenu;
    private Context mContext;

    public DoorSensorInfoAdapter(DoorSensorResModel.DATA.DoorList.NotificationList[] notificationList, boolean cfType, OnNotificationContextMenu onNotificationContextMenu){
        this.notificationList = notificationList;
        this.isCF = cfType;
        this.onNotificationContextMenu = onNotificationContextMenu;
    }

    @Override
    public SensorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_door_sensor_info,parent,false);
        mContext = view.getContext();
        return new SensorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SensorViewHolder holder, final int position) {

       if(position==0){
           holder.viewLine.setVisibility(View.GONE);
       }else {
           holder.viewLine.setVisibility(View.VISIBLE);
       }
        final DoorSensorResModel.DATA.DoorList.NotificationList notification = notificationList[position];

        String startTime = notification.getmStartDateTime();
        String endTime   = notification.getmEndDateTime();

        if (Common.getPrefValue(mContext, Constants.USER_ADMIN_TYPE).equalsIgnoreCase("0")) {
            if( Common.getPrefValue(mContext, Constants.USER_ID).equalsIgnoreCase(notification.getUser_id())){
                holder.imgOptions.setVisibility(View.VISIBLE);
            }else {
                holder.imgOptions.setVisibility(View.INVISIBLE);
            }
        }

        try {

            String conDateStart = DateHelper.formateDate(DateHelper.parseTimeSimple(startTime,DateHelper.DATE_FROMATE_HH_MM),DateHelper.DATE_FROMATE_H_M_AMPM);
            String conDateEnd = DateHelper.formateDate(DateHelper.parseTimeSimple(endTime,DateHelper.DATE_FROMATE_HH_MM),DateHelper.DATE_FROMATE_H_M_AMPM);

            holder.txtMin.setText(conDateStart);
            holder.txtMax.setText(conDateEnd);


        } catch (final ParseException e) {
            holder.txtMin.setText(startTime);
            holder.txtMax.setText(endTime);
            e.printStackTrace();
        }

       // holder.txtMin.setText(startTime);
       // holder.txtMax.setText(endTime);

        if(!TextUtils.isEmpty(notification.getmIsActive()) && !notification.getmIsActive().equalsIgnoreCase("null")){
            holder.switchCompat.setChecked(Integer.parseInt(notification.getmIsActive()) > 0);
        }

        holder.imgOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayContextMenu(v,notification,position);
            }
        });

       // holder.switchCompat.setOnCheckedChangeListener(null);

        /*holder.switchCompat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.switchCompat.setChecked(!holder.switchCompat.isChecked());
                onNotificationContextMenu.onSwitchChanged(notification,holder.switchCompat,position,!holder.switchCompat.isChecked());
            }
        });*/

        holder.switchCompat.setOnTouchListener(new OnSwipeTouchListener(mContext){
            @Override
            public void onClick() {
                super.onClick();
                holder.switchCompat.setChecked(holder.switchCompat.isChecked());
                onNotificationContextMenu.onSwitchChanged(notification,holder.switchCompat,position,!holder.switchCompat.isChecked());
            }

            @Override
            public void onSwipeLeft() {
                super.onSwipeLeft();
                holder.switchCompat.setChecked(holder.switchCompat.isChecked());
                onNotificationContextMenu.onSwitchChanged(notification,holder.switchCompat,position,!holder.switchCompat.isChecked());
            }

            @Override
            public void onSwipeRight() {
                super.onSwipeRight();
                holder.switchCompat.setChecked(holder.switchCompat.isChecked());
                onNotificationContextMenu.onSwitchChanged(notification,holder.switchCompat,position,!holder.switchCompat.isChecked());
            }

        });


    }

    /**
     *
     * @param v
     * @param notification
     * @param position
     */
    private void displayContextMenu(View v, final DoorSensorResModel.DATA.DoorList.NotificationList notification, final int position) {

        PopupMenu popup = new PopupMenu(mContext, v);
        @SuppressLint("RestrictedApi") Context wrapper = new ContextThemeWrapper(mContext, R.style.PopupMenu);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            popup = new PopupMenu(wrapper, v, Gravity.RIGHT);
        } else {
            popup = new PopupMenu(wrapper, v);
        }
        popup.getMenuInflater().inflate(R.menu.menu_dots, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.action_edit_dots:
                        onNotificationContextMenu.onEditOpetion(notification,position,true);
                        break;
                    case R.id.action_delete_dots:
                        onNotificationContextMenu.onEditOpetion(notification,position,false);
                        break;
                }
                return true;
            }
        });

        popup.show();
    }

    @Override
    public int getItemCount() {
        return notificationList.length;
    }

    public class SensorViewHolder extends RecyclerView.ViewHolder{

        private AppCompatTextView txtMin,txtMax;
        private SwitchCompat switchCompat;
        private AppCompatImageView imgOptions;
        public View viewLine;

        public SensorViewHolder(View itemView) {
            super(itemView);

            txtMin = (AppCompatTextView) itemView.findViewById(R.id.txt_min);
            txtMax = (AppCompatTextView) itemView.findViewById(R.id.txt_max);

            switchCompat = (SwitchCompat) itemView.findViewById(R.id.switch_onoff);
            imgOptions = (AppCompatImageView) itemView.findViewById(R.id.img_options);
            viewLine = (View) itemView.findViewById(R.id.viewLine);
        }
    }

    public interface OnNotificationContextMenu{
        void onEditOpetion(DoorSensorResModel.DATA.DoorList.NotificationList notification, int position, boolean isEdit);
        void onSwitchChanged(DoorSensorResModel.DATA.DoorList.NotificationList notification, SwitchCompat swithcCompact, int position, boolean isActive);
    }
}
