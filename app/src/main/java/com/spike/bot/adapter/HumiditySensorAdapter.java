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
import com.spike.bot.listener.OnHumitySensorContextMenu;
import com.spike.bot.model.SensorResModel;

import java.util.ArrayList;

/**
 * Created by Sagar on 5/6/19.
 * Gmail : vipul patel
 */
public class HumiditySensorAdapter extends RecyclerView.Adapter<HumiditySensorAdapter.SensorViewHolder> {

    ArrayList<SensorResModel.DATA.TempList.HumidityNotificationList> notificationList = new ArrayList<>();
    SensorResModel.DATA.TempList.HumidityNotificationList notification;
    private boolean isCF;
    private OnHumitySensorContextMenu onNotificationContextMenu;
    private Context mContext;

    public HumiditySensorAdapter(ArrayList<SensorResModel.DATA.TempList.HumidityNotificationList> arrayList, boolean cfType, OnHumitySensorContextMenu onNotificationContextMenu) {
        this.notificationList = arrayList;
        this.isCF = cfType;
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
        notification = notificationList.get(position);

        holder.txtMin.setText("" + notification.getMinHumidity() + " %");
        holder.txtMax.setText("" + notification.getMaxHumidity() + " %");

        holder.switchCompat.setChecked(notification.getIsActive() == 0 ? false : true);

        if (!TextUtils.isEmpty(notification.getDays()) && !notification.getDays().equalsIgnoreCase("null")) {
            holder.txtDays.setText(Html.fromHtml(Common.htmlDaysFormat(notification.getDays())));
        }

        if (Common.getPrefValue(mContext, Constants.USER_ADMIN_TYPE).equalsIgnoreCase("0")) {
            if (Common.getPrefValue(mContext, Constants.USER_ID).equalsIgnoreCase(notification.getUserId())) {
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
                onNotificationContextMenu.onSwitchHumityChanged(notification, holder.switchCompat, position, !holder.switchCompat.isChecked());
            }

            @Override
            public void onSwipeLeft() {
                super.onSwipeLeft();
                holder.switchCompat.setChecked(holder.switchCompat.isChecked());
                onNotificationContextMenu.onSwitchHumityChanged(notification, holder.switchCompat, position, !holder.switchCompat.isChecked());
            }

            @Override
            public void onSwipeRight() {
                super.onSwipeRight();
                holder.switchCompat.setChecked(holder.switchCompat.isChecked());
                onNotificationContextMenu.onSwitchHumityChanged(notification, holder.switchCompat, position, !holder.switchCompat.isChecked());
            }

        });


    }

    /**
     * @param v
     * @param notification
     * @param position
     */
    private void displayContextMenu(View v, final SensorResModel.DATA.TempList.HumidityNotificationList notification, final int position) {

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
                        onNotificationContextMenu.onEditHumityOpetion(notification, position, true);
                        break;
                    case R.id.action_delete_dots:
                        onNotificationContextMenu.onEditHumityOpetion(notification, position, false);
                        break;
                }
                return true;
            }
        });

        popup.show();
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
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
}
