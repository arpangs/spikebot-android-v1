package com.spike.bot.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.text.Html;
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
import com.spike.bot.model.SensorResModel;

/**
 * Created by Sagar on 7/5/18.
 * Gmail : jethvasagar2@gmail.com
 */

public class TempSensorInfoAdapter extends RecyclerView.Adapter<TempSensorInfoAdapter.SensorViewHolder> {

    SensorResModel.DATA.TempList.NotificationList[] notificationList;
    private OnNotificationContextMenu onNotificationContextMenu;
    private Context mContext;
    SensorResModel.DATA.TempList.NotificationList notification;
    String minVal = "", maxVal = "";

    public TempSensorInfoAdapter(SensorResModel.DATA.TempList.NotificationList[] notificationList, boolean cfType, OnNotificationContextMenu onNotificationContextMenu) {
        this.notificationList = notificationList;
        this.onNotificationContextMenu = onNotificationContextMenu;
    }

    @Override
    public SensorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_temp_sensor_info, parent, false);
        mContext = view.getContext();
        return new SensorViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final SensorViewHolder holder, final int position) {

        if (position == 0) {
            holder.viewLine.setVisibility(View.GONE);
        } else {
            holder.viewLine.setVisibility(View.VISIBLE);
        }
        //\u2109 \u2103 (℉ & ℃)
        notification = notificationList[position];

        if (notification.isCFActive()) {

            if (!TextUtils.isEmpty(notification.getMinValueC()) && !notification.getMinValueC().equalsIgnoreCase("null") && !TextUtils.isEmpty(notification.getMaxValueC()) && !notification.getMaxValueC().equalsIgnoreCase("null")) {
                minVal = Common.parseCelsius(notification.getMinValueC());
                maxVal = Common.parseCelsius(notification.getMaxValueC());
            }
        } else {
            if (!TextUtils.isEmpty(notification.getMinValueF()) && !notification.getMinValueF().equalsIgnoreCase("null") && !TextUtils.isEmpty(notification.getMaxValueF()) && !notification.getMaxValueF().equalsIgnoreCase("null")) {
                minVal = Common.parseFahrenheit(notification.getMinValueF());
                maxVal = Common.parseFahrenheit(notification.getMaxValueF());
            }
        }

        holder.txtMin.setText(minVal);
        holder.txtMax.setText(maxVal);

        if (!TextUtils.isEmpty(notification.getIsActive()) && !notification.getIsActive().equalsIgnoreCase("null")) {
            holder.switchCompat.setChecked(Integer.parseInt(notification.getIsActive()) > 0);
        }

        if (!TextUtils.isEmpty(notification.getDays()) && !notification.getDays().equalsIgnoreCase("null")) {
            holder.txtDays.setText(Html.fromHtml(Common.htmlDaysFormat(notification.getDays())));
        }

        if (Common.getPrefValue(mContext, Constants.USER_ADMIN_TYPE).equalsIgnoreCase("0")) {
            if (Common.getPrefValue(mContext, Constants.USER_ID).equalsIgnoreCase(notification.getUser_id())) {
                holder.imgOptions.setVisibility(View.VISIBLE);
            } else {
                holder.imgOptions.setVisibility(View.INVISIBLE);
            }
        }
        holder.imgOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayContextMenu(v, notification, position);
            }
        });

        holder.switchCompat.setOnTouchListener(new OnSwipeTouchListener(mContext) {
            @Override
            public void onClick() {
                super.onClick();
                holder.switchCompat.setChecked(holder.switchCompat.isChecked());
                onNotificationContextMenu.onSwitchChanged(notification, holder.switchCompat, position, !holder.switchCompat.isChecked());
            }

            @Override
            public void onSwipeLeft() {
                super.onSwipeLeft();
                holder.switchCompat.setChecked(holder.switchCompat.isChecked());
                onNotificationContextMenu.onSwitchChanged(notification, holder.switchCompat, position, !holder.switchCompat.isChecked());
            }

            @Override
            public void onSwipeRight() {
                super.onSwipeRight();
                holder.switchCompat.setChecked(holder.switchCompat.isChecked());
                onNotificationContextMenu.onSwitchChanged(notification, holder.switchCompat, position, !holder.switchCompat.isChecked());
            }

        });


    }

    /**
     * @param v
     * @param notification
     * @param position
     */
    private void displayContextMenu(View v, final SensorResModel.DATA.TempList.NotificationList notification, final int position) {

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
                switch (item.getItemId()) {
                    case R.id.action_edit_dots:
                        onNotificationContextMenu.onEditOpetion(notification, position, true);
                        break;
                    case R.id.action_delete_dots:
                        onNotificationContextMenu.onEditOpetion(notification, position, false);
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

    public class SensorViewHolder extends RecyclerView.ViewHolder {

        private AppCompatTextView txtMin, txtMax, txtDays;
        private SwitchCompat switchCompat;
        private AppCompatImageView imgOptions;
        public View viewLine;

        public SensorViewHolder(View itemView) {
            super(itemView);

            txtMin = (AppCompatTextView) itemView.findViewById(R.id.txt_min);
            txtMax = (AppCompatTextView) itemView.findViewById(R.id.txt_max);
            txtDays = (AppCompatTextView) itemView.findViewById(R.id.txt_days);

            switchCompat = (SwitchCompat) itemView.findViewById(R.id.switch_onoff);
            imgOptions = (AppCompatImageView) itemView.findViewById(R.id.img_options);
            viewLine = (View) itemView.findViewById(R.id.viewLine);
        }
    }

    public interface OnNotificationContextMenu {
        void onEditOpetion(SensorResModel.DATA.TempList.NotificationList notification, int position, boolean isEdit);

        void onSwitchChanged(SensorResModel.DATA.TempList.NotificationList notification, SwitchCompat swithcCompact, int position, boolean isActive);
    }
}
