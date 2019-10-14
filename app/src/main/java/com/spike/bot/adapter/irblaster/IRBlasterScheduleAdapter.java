package com.spike.bot.adapter.irblaster;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.kp.core.DateHelper;
import com.spike.bot.R;
import com.spike.bot.core.Common;
import com.spike.bot.model.RemoteSchListRes;

import java.text.ParseException;
import java.util.List;

import static com.spike.bot.R.drawable.blue_border_yellow_bottom;
import static com.spike.bot.R.drawable.blue_border_yellow_top;
import static com.spike.bot.R.drawable.yellow_border_fill_rectangle;

/**
 * Created by Sagar on 8/8/18.
 * Gmail : jethvasagar2@gmail.com
 */
public class IRBlasterScheduleAdapter extends RecyclerView.Adapter<IRBlasterScheduleAdapter.IRBlasterSch> {

    private List<RemoteSchListRes.Data.RemoteScheduleList> remoteScheduleList;
    private Context mContext;
    private RemoteSchClickEvent remoteSchClickEvent;
    String lastAMPMStart = "", lastAMPMEnd = "";

    public IRBlasterScheduleAdapter(List<RemoteSchListRes.Data.RemoteScheduleList> remoteScheduleList, RemoteSchClickEvent remoteSchClickEvent) {
        this.remoteScheduleList = remoteScheduleList;
        this.remoteSchClickEvent = remoteSchClickEvent;
    }

    @Override
    public IRBlasterSch onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_remote_schedule, parent, false);
        mContext = view.getContext();
        return new IRBlasterSch(view);
    }

    @Override
    public void onBindViewHolder(IRBlasterSch holder, int position) {
        final RemoteSchListRes.Data.RemoteScheduleList remote = remoteScheduleList.get(position);

        holder.ir_tv_name.setText(remote.getIrScheduleName());
        holder.ir_tv_schedule_days.setText(Html.fromHtml(Common.getDaysString(remote.getScheduleDay())));

        try {

            if (remote.getStartTemperature() > 0) {
                holder.ir_tv_on_time.setText(remote.getStartTemperature() + "" + Common.getC() + " " + remote.getStartMode() + " " + DateHelper.formateDate(DateHelper.parseTimeSimple(remote.getStartTime(),
                        DateHelper.DATE_FROMATE_HH_MM), DateHelper.DATE_FROMATE_H_M_AMPM));
            }
            if (remote.getEndTemperature() > 0) {
                holder.ir_tv_off_time.setText(remote.getEndTemperature() + "" + Common.getC() + " " + remote.getEndMode() + "" + " " + DateHelper.formateDate(DateHelper.parseTimeSimple(remote.getEndTime(),
                        DateHelper.DATE_FROMATE_HH_MM), DateHelper.DATE_FROMATE_H_M_AMPM));
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (remote.getIsActive() == 1) {
            holder.ir_onoff_view.setBackgroundResource(R.drawable.blue_border_fill_rectangle);
            holder.ir_tv_on_time.setTextColor(mContext.getResources().getColor(R.color.automation_white));
            holder.ir_tv_on.setTextColor(mContext.getResources().getColor(R.color.automation_white));
            holder.ir_tv_off_time.setTextColor(mContext.getResources().getColor(R.color.automation_white));
            holder.ir_tv_off.setTextColor(mContext.getResources().getColor(R.color.automation_white));

            try {
                lastAMPMStart = holder.ir_tv_on_time.getText().toString().trim().substring(
                        holder.ir_tv_on_time.length() - 2, holder.ir_tv_on_time.length());

                lastAMPMEnd = holder.ir_tv_off_time.getText().toString().trim().substring(
                        holder.ir_tv_off_time.length() - 2, holder.ir_tv_off_time.length());


                if (lastAMPMStart.equalsIgnoreCase("AM") || lastAMPMStart.equalsIgnoreCase("am")) {
                    holder.ir_on_view.setBackgroundResource(blue_border_yellow_top);
                }

                if ((lastAMPMStart.equalsIgnoreCase("AM") || lastAMPMStart.equalsIgnoreCase("am")) &&
                        remote.getStartTime().equalsIgnoreCase("")) {
                    holder.ir_on_view.setBackgroundResource(yellow_border_fill_rectangle);
                }

                if (lastAMPMEnd.equalsIgnoreCase("AM") || lastAMPMEnd.equalsIgnoreCase("am")) {
                    holder.ir_off_view.setBackgroundResource(blue_border_yellow_bottom);
                }

                if ((lastAMPMEnd.equalsIgnoreCase("AM") || lastAMPMEnd.equalsIgnoreCase("am")) &&
                        remote.getEndTime().equalsIgnoreCase("")) {
                    holder.ir_off_view.setBackgroundResource(yellow_border_fill_rectangle);
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }

        } else {
            holder.ir_on_view.setBackgroundResource(0);
            holder.ir_off_view.setBackgroundResource(0);

            holder.ir_onoff_view.setBackgroundResource(R.drawable.blue_border_rectangle);
        }

        holder.ir_iv_schedule_dots.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayOptionMenu(v, remote, remoteSchClickEvent);
            }
        });

        holder.ir_onoff_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                remoteSchClickEvent.onClickActive(remote);
            }
        });


    }

    @Override
    public int getItemCount() {
        return remoteScheduleList.size();
    }

    class IRBlasterSch extends RecyclerView.ViewHolder {

        private LinearLayout ir_onoff_view,ir_on_view,ir_off_view;
        private TextView ir_tv_on_time,ir_tv_off_time,ir_tv_on,ir_tv_off,ir_tv_name,ir_tv_schedule_days;
        private ImageView ir_iv_schedule_dots;

        IRBlasterSch(View itemView) {
            super(itemView);

            ir_onoff_view = (LinearLayout) itemView.findViewById(R.id.ir_onoff_view);
            ir_on_view = (LinearLayout) itemView.findViewById(R.id.ir_on_view);
            ir_off_view = (LinearLayout) itemView.findViewById(R.id.ir_off_view);

            ir_tv_on_time = (TextView) itemView.findViewById(R.id.ir_tv_schedule_on_time);
            ir_tv_off_time = (TextView) itemView.findViewById(R.id.ir_tv_schedule_off_time);

            ir_tv_on = (TextView) itemView.findViewById(R.id.ir_tv_schedule_on);
            ir_tv_off = (TextView) itemView.findViewById(R.id.ir_tv_schedule_off);

            ir_tv_name = (TextView) itemView.findViewById(R.id.ir_tv_name);
            ir_tv_schedule_days = (TextView) itemView.findViewById(R.id.ir_tv_schedule_days);

            ir_iv_schedule_dots = (ImageView) itemView.findViewById(R.id.ir_iv_schedule_dots);

        }
    }

    public interface RemoteSchClickEvent {
        void onClickActive(RemoteSchListRes.Data.RemoteScheduleList remoteList);

        void onClickEdit(RemoteSchListRes.Data.RemoteScheduleList remoteList);

        void onClickDelete(RemoteSchListRes.Data.RemoteScheduleList remoteList);
    }


    /**
     * Edit, Delete context option menu
     *
     * @param v
     * @param remoteList
     * @param remoteSchClickEvent
     */
    private void displayOptionMenu(View v, final RemoteSchListRes.Data.RemoteScheduleList remoteList,
                                   final RemoteSchClickEvent remoteSchClickEvent) {

        PopupMenu popup = new PopupMenu(mContext, v);
        @SuppressLint("RestrictedApi") Context wrapper = new ContextThemeWrapper(mContext, R.style.PopupMenu);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            popup = new PopupMenu(wrapper, v, Gravity.RIGHT);
        } else {
            popup = new PopupMenu(wrapper, v);
        }
        popup.getMenuInflater().inflate(R.menu.menu_room_edit_option_popup, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_edit:
                        remoteSchClickEvent.onClickEdit(remoteList);
                        break;
                    case R.id.action_delete:
                        remoteSchClickEvent.onClickDelete(remoteList);
                        break;
                }
                return true;
            }
        });

        popup.show();
    }
}
