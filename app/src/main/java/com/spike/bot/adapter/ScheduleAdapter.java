package com.spike.bot.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
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
import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.activity.DeviceLogActivity;
import com.spike.bot.core.Common;
import com.spike.bot.core.Constants;
import com.spike.bot.model.ScheduleVO;

import java.util.ArrayList;

/**
 * Created by kaushal on 27/12/17.
 */

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ViewHolder> {

    Activity mContext;
    ArrayList<ScheduleVO> scheduleArrayList;
    ScheduleClickListener scheduleClickListener;
    private boolean isMoodAdapter;
    ScheduleVO scheduleVO;
    String lastAMPM = "", color;
    public boolean isClickable = true;

    public ScheduleAdapter(Activity context, ArrayList<ScheduleVO> scheduleArrayList, ScheduleClickListener scheduleClickListener, boolean isMood, boolean isType) {
        this.scheduleArrayList = scheduleArrayList;
        this.mContext = context;
        this.scheduleClickListener = scheduleClickListener;
        this.isMoodAdapter = isMood;
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.row_room_schedule;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new ViewHolder(view, viewType);
    }

    public void setClickable(boolean isClickable) {
        this.isClickable = isClickable;
    }

    /**
     * change schedule mood/room status using socketIO response
     *
     * @param schedule_id     :   1520069936773_ryYNpyOuM
     * @param schedule_status :   1/0
     */

    public void chandeScheduleStatus(String schedule_id, String schedule_status) {

        for (int i = 0; i < scheduleArrayList.size(); i++) {
            scheduleVO = scheduleArrayList.get(i);

            if (scheduleVO.getSchedule_id().equalsIgnoreCase(schedule_id)) {
                scheduleVO.setSchedule_status(Integer.parseInt(schedule_status));
                notifyItemChanged(i, scheduleVO);
            }
        }
    }

    @Override
    public int getItemCount() {
        return scheduleArrayList.size();
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int listPosition) {
        holder.setIsRecyclable(false);
        scheduleVO = scheduleArrayList.get(listPosition);

        try {
            if (scheduleVO.getSchedule_type()==1) {

                if (scheduleVO.getIs_active()==1) {
                    holder.iv_sch_type.setImageResource(R.drawable.ic_timer_new);
                } else {
                    holder.iv_sch_type.setImageDrawable(mContext.getResources().getDrawable(R.drawable.timer_gray));
                }

                holder.tv_schedule_days.setVisibility(View.GONE);

                if (!TextUtils.isEmpty(scheduleVO.getTimer_on_date())) {
                    holder.tv_auto_on.setVisibility(View.VISIBLE);//008BE0
                    color = "#FFFFFF";
                    if (scheduleVO.getSchedule_status() == 1) {
                        color = "#FFFFFF";
                    } else {
                        color = "#808080";
                    }
                    holder.tv_auto_on.setText(Html.fromHtml("<font color=\"" + color + "\">" + scheduleVO.getTimer_on_date() + "</font>"));
                } else {
                    holder.tv_auto_on.setVisibility(View.GONE);
                }
                if (!TextUtils.isEmpty(scheduleVO.getTimer_off_date())) {
                    holder.tv_auto_off.setVisibility(View.VISIBLE);
                    holder.tv_auto_off.setText(Html.fromHtml("<font color=\"\"+color+\"\">" + scheduleVO.getTimer_off_date() + "</font>"));
                } else {
                    holder.tv_auto_off.setVisibility(View.GONE);
                }

            } else {
                if (scheduleVO.getIs_active() == 1) {
                    holder.iv_sch_type.setImageResource(R.drawable.ic_scheduler_new);

                } else {
                    holder.iv_sch_type.setImageDrawable(mContext.getResources().getDrawable(R.drawable.schedule_grey_temp));
                }

                holder.tv_auto_on.setVisibility(View.GONE);
                holder.tv_auto_off.setVisibility(View.GONE);
                holder.tv_schedule_days.setVisibility(View.VISIBLE);

                if (TextUtils.isEmpty(scheduleVO.getSchedule_device_day())) {
                    if (scheduleVO.getSchedule_status() == 1) {
                        holder.tv_schedule_days.setText(ChatApplication.getCurrentDateOnly(true, scheduleVO.getSchedule_device_on_time(), scheduleVO.getSchedule_device_off_time()));

                    } else {
                        holder.tv_schedule_days.setText(ChatApplication.getCurrentDateOnly(false, "", ""));
                    }
                } else {
                    holder.tv_schedule_days.setText(Html.fromHtml(Common.getDaysString(scheduleVO.getSchedule_device_day())));
                }

            }

            // for timer
            if (scheduleVO.getSchedule_type()==1) {
                holder.ll_schedule_on_off.setOrientation(LinearLayout.VERTICAL);
            } else {
                holder.ll_schedule_on_off.setOrientation(LinearLayout.HORIZONTAL);
            }


            /* on time */
            if (!scheduleVO.getSchedule_device_on_time().equalsIgnoreCase("")) {

                holder.ll_on.setVisibility(View.VISIBLE);

                if (scheduleVO.getSchedule_type()==1) {
                    holder.tv_schedule_on_time.setText("- " + DateHelper.formateDate(DateHelper.parseTimeSimple(scheduleVO.getSchedule_device_on_time(), DateHelper.DATE_FROMATE_H_M_AMPM1), DateHelper.DATE_FROMATE_H_M_AMPM12));
                }else {
                    holder.tv_schedule_on_time.setText("- " + Common.getConvertDateForScheduleHour(scheduleVO.getSchedule_device_on_time()));
                }

                holder.tv_schedule_on.setText("On : ");
                holder.tv_schedule_on_time.setVisibility(View.VISIBLE);
                holder.tv_schedule_on.setVisibility(View.VISIBLE);

                try {
                    lastAMPM = holder.tv_schedule_on_time.getText().toString().trim().substring(holder.tv_schedule_on_time.length() - 2, holder.tv_schedule_on_time.length());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                if (lastAMPM.equalsIgnoreCase("AM") || lastAMPM.equalsIgnoreCase("am")) {
                    holder.ll_on.setBackgroundResource(R.drawable.blue_border_yellow_top);
                }

                if ((lastAMPM.equalsIgnoreCase("AM") || lastAMPM.equalsIgnoreCase("am")) && scheduleVO.getSchedule_device_off_time().equalsIgnoreCase("")) {
                    holder.ll_on.setBackgroundResource(R.drawable.yellow_border_fill_rectangle);
                }

            } else {
                holder.ll_on.setVisibility(View.GONE); //VISIBLE

                holder.tv_schedule_on_time.setVisibility(View.GONE);
                holder.tv_schedule_on.setVisibility(View.GONE);
            }

            /*off time*/
            if (!TextUtils.isEmpty(scheduleVO.getSchedule_device_off_time()) && !scheduleVO.getSchedule_device_off_time().equalsIgnoreCase("null")) {

                holder.ll_off.setVisibility(View.VISIBLE);
                if (scheduleVO.getSchedule_type()==1) {
                    holder.tv_schedule_off_time.setText("- " + DateHelper.formateDate(DateHelper.parseTimeSimple(scheduleVO.getSchedule_device_off_time(), DateHelper.DATE_FROMATE_H_M_AMPM1), DateHelper.DATE_FROMATE_H_M_AMPM12));
                }else {
                    holder.tv_schedule_off_time.setText("- " + Common.getConvertDateForScheduleHour(scheduleVO.getSchedule_device_off_time()));
                }


                holder.tv_schedule_off_time.setVisibility(View.VISIBLE);
                holder.tv_schedule_off.setText("Off : ");
                holder.tv_schedule_off.setVisibility(View.VISIBLE);

                String lastAMPM = "";
                try {
                    lastAMPM = holder.tv_schedule_off_time.getText().toString().trim().substring(
                            holder.tv_schedule_off_time.length() - 2, holder.tv_schedule_off_time.length());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                if (lastAMPM.equalsIgnoreCase("AM") || lastAMPM.equalsIgnoreCase("am")) {
                    holder.ll_off.setBackgroundResource(R.drawable.blue_border_yellow_bottom);
                }

                if ((lastAMPM.equalsIgnoreCase("AM") || lastAMPM.equalsIgnoreCase("am")) && scheduleVO.getSchedule_device_on_time().equalsIgnoreCase("")) {
                    holder.ll_off.setBackgroundResource(R.drawable.yellow_border_fill_rectangle);
                }

            } else {

                holder.ll_off.setVisibility(View.GONE);

                holder.tv_schedule_off_time.setVisibility(View.GONE);
                holder.tv_schedule_off.setVisibility(View.GONE);
            }

            holder.tv_schedule_name.setText(scheduleVO.getSchedule_name());
            holder.tv_name.setText(scheduleVO.getRoom_name());

            holder.tv_schedule_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popup = new PopupMenu(mContext, v);
                    @SuppressLint("RestrictedApi") Context wrapper = new ContextThemeWrapper(mContext, R.style.PopupMenu);
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                        popup = new PopupMenu(wrapper, v, Gravity.RIGHT);
                    } else {
                        popup = new PopupMenu(wrapper, v);
                    }
                    popup.getMenu().add(1, listPosition, 1, scheduleVO.getSchedule_name());
                    popup.show();

                }
            });

            // YELLOW background means AM timeing
            // Grey BAckground means PM timening
            if (scheduleVO.getIs_active() == 1) {
                holder.tv_schedule_name.setTextColor(mContext.getResources().getColor(R.color.sensor_button));
                holder.ll_schedule_on_off.setBackgroundResource(R.drawable.blue_border_fill_rectangle);
                holder.tv_schedule_on_time.setTextColor(mContext.getResources().getColor(R.color.automation_white));
                holder.tv_schedule_on.setTextColor(mContext.getResources().getColor(R.color.automation_white));
                holder.tv_schedule_off_time.setTextColor(mContext.getResources().getColor(R.color.automation_white));
                holder.tv_schedule_off.setTextColor(mContext.getResources().getColor(R.color.automation_white));
                holder.tv_schedule_on.setTextColor(mContext.getResources().getColor(R.color.automation_white));
                holder.tv_schedule_on_time.setTextColor(mContext.getResources().getColor(R.color.automation_white));
                holder.tv_schedule_off.setTextColor(mContext.getResources().getColor(R.color.automation_white));
                holder.tv_auto_off.setTextColor(mContext.getResources().getColor(R.color.automation_white));
                holder.tv_schedule_off_time.setTextColor(mContext.getResources().getColor(R.color.automation_white));

                holder.iv_schedule_dots.setImageDrawable(mContext.getResources().getDrawable(R.drawable.more));
            } else {

                //Remove background resources
                holder.ll_on.setBackgroundResource(0);
                holder.ll_off.setBackgroundResource(0);
                holder.ll_schedule_on_off.setBackgroundResource(R.drawable.drawable_off_schedule);

                holder.iv_schedule_dots.setImageDrawable(mContext.getResources().getDrawable(R.drawable.more_grey));
                holder.tv_schedule_name.setTextColor(mContext.getResources().getColor(R.color.txtPanal));

                if (TextUtils.isEmpty(scheduleVO.getSchedule_device_day())) {
                    if (scheduleVO.getIs_active() == 1) {
                        holder.tv_schedule_days.setText(ChatApplication.getCurrentDateOnly(true, scheduleVO.getSchedule_device_on_time(), scheduleVO.getSchedule_device_off_time()));
                    } else {

                        holder.tv_schedule_days.setText(ChatApplication.getCurrentDateOnly(false, "", ""));
                    }
                } else {
                    holder.tv_schedule_days.setText(Html.fromHtml(Common.getDaysString(scheduleVO.getSchedule_device_day())));
                }

                holder.tv_schedule_on.setTextColor(mContext.getResources().getColor(R.color.txtPanal));
                holder.tv_auto_on.setTextColor(mContext.getResources().getColor(R.color.txtPanal));
                holder.tv_schedule_on_time.setTextColor(mContext.getResources().getColor(R.color.txtPanal));
                holder.tv_schedule_off.setTextColor(mContext.getResources().getColor(R.color.txtPanal));
                holder.tv_auto_off.setTextColor(mContext.getResources().getColor(R.color.txtPanal));
                holder.tv_schedule_off_time.setTextColor(mContext.getResources().getColor(R.color.txtPanal));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        if (!Common.getPrefValue(mContext, Constants.USER_ADMIN_TYPE).equals("1")) {
            if (Common.getPrefValue(mContext, Constants.USER_ID).equals(scheduleVO.getUser_id())) {
                holder.iv_schedule_dots.setVisibility(View.VISIBLE);
            } else {
                holder.iv_schedule_dots.setVisibility(View.INVISIBLE);
            }
        } else {
            holder.iv_schedule_dots.setVisibility(View.VISIBLE);
        }

        holder.ll_schedule_on_off.setId(listPosition);
        holder.ll_schedule_on_off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!isClickable)
                    return;

                if(scheduleArrayList.get(holder.iv_sch_type.getId()).getIs_active()==1){
                    scheduleArrayList.get(holder.iv_sch_type.getId()).setIs_active(0);
                }else {
                    scheduleArrayList.get(holder.iv_sch_type.getId()).setIs_active(1);
                }

                notifyDataSetChanged();
                scheduleClickListener.itemClicked(scheduleArrayList.get(holder.ll_schedule_on_off.getId()), "active", isMoodAdapter);
            }
        });

        holder.iv_sch_type.setId(listPosition);
        holder.iv_sch_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!isClickable)
                    return;
                if(scheduleArrayList.get(holder.iv_sch_type.getId()).getIs_active()==1){
                    scheduleArrayList.get(holder.iv_sch_type.getId()).setIs_active(0);
                }else {
                    scheduleArrayList.get(holder.iv_sch_type.getId()).setIs_active(1);
                }

                notifyDataSetChanged();
                scheduleClickListener.itemClicked(scheduleArrayList.get(holder.iv_sch_type.getId()), "active", isMoodAdapter);
            }
        });


        holder.iv_schedule_edit.setId(listPosition);
        holder.iv_schedule_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isClickable)
                    return;
                scheduleClickListener.itemClicked(scheduleArrayList.get(holder.iv_schedule_edit.getId()), "edit", isMoodAdapter);
            }
        });

        holder.iv_schedule_delete.setId(listPosition);
        holder.iv_schedule_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isClickable)
                    return;
                scheduleClickListener.itemClicked(scheduleArrayList.get(holder.iv_schedule_delete.getId()), "delete", isMoodAdapter);
            }
        });

        holder.iv_schedule_dots.setId(listPosition);
        holder.iv_schedule_dots.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!isClickable)
                    return;

                PopupMenu popup = new PopupMenu(mContext, v);
                @SuppressLint("RestrictedApi") Context wrapper = new ContextThemeWrapper(mContext, R.style.PopupMenu);
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                    popup = new PopupMenu(wrapper, v, Gravity.RIGHT);
                } else {
                    popup = new PopupMenu(wrapper, v);
                }
                popup.getMenuInflater().inflate(R.menu.menu_dots, popup.getMenu());

                popup.getMenu().findItem(R.id.action_log).setVisible(true);

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_edit_dots:
                                scheduleClickListener.itemClicked(scheduleArrayList.get(holder.iv_schedule_dots.getId()), "edit", isMoodAdapter);
                                break;
                            case R.id.action_delete_dots:
                                scheduleClickListener.itemClicked(scheduleArrayList.get(holder.iv_schedule_dots.getId()), "delete", isMoodAdapter);
                                break;
                            case R.id.action_log:
                                Intent intent = new Intent(mContext, DeviceLogActivity.class);
                                intent.putExtra("Schedule_id", "" + scheduleArrayList.get(holder.iv_schedule_dots.getId()).getSchedule_id());
                                intent.putExtra("ROOM_ID", "" + scheduleArrayList.get(holder.iv_schedule_dots.getId()).getSchedule_id());
                                intent.putExtra("activity_type", "" + scheduleArrayList.get(holder.iv_schedule_dots.getId()).getIs_timer());
                                intent.putExtra("isRoomName", "" + scheduleArrayList.get(holder.iv_schedule_dots.getId()).getSchedule_name());
                                if (scheduleArrayList.get(holder.iv_schedule_dots.getId()).getIs_timer() == 0) {
                                    intent.putExtra("isCheckActivity", "schedule");
                                } else {
                                    intent.putExtra("isCheckActivity", "Timer");
                                }
                                mContext.startActivity(intent);
                                break;
                        }
                        return true;
                    }
                });
                popup.show();
            }
        });


    }


    protected static class ViewHolder extends RecyclerView.ViewHolder {
        //common
        View view, view_line_top;
        int viewType;
        TextView tv_schedule_name, tv_schedule_days, tv_name, tv_schedule_on, tv_schedule_off, tv_auto_on, tv_auto_off, tv_schedule_on_time, tv_schedule_off_time;
        ImageView iv_schedule_edit, iv_schedule_delete, iv_schedule_dots, iv_sch_type;
        LinearLayout ll_schedule_on_off, linearRoomSchedule, ll_on, ll_off;

        public ViewHolder(View view, int viewType) {
            super(view);
            this.viewType = viewType;
            this.view = view;
            view_line_top =  view.findViewById(R.id.view_line_top);
            tv_schedule_name =  view.findViewById(R.id.tv_schedule_name);
            tv_schedule_days =  view.findViewById(R.id.tv_schedule_days);


            tv_name = view.findViewById(R.id.tv_name);
            iv_schedule_edit =  view.findViewById(R.id.iv_schedule_edit);
            iv_schedule_delete =  view.findViewById(R.id.iv_schedule_delete);
            iv_schedule_dots =  view.findViewById(R.id.iv_schedule_dots);

            iv_sch_type =  view.findViewById(R.id.iv_sche_type);

            ll_schedule_on_off =  view.findViewById(R.id.ll_schedule_on_off);
            linearRoomSchedule =  view.findViewById(R.id.linearRoomSchedule);
            ll_on =  view.findViewById(R.id.ll_on);
            ll_off =  view.findViewById(R.id.ll_off);
            tv_schedule_on =  view.findViewById(R.id.tv_schedule_on);
            tv_schedule_off =  view.findViewById(R.id.tv_schedule_off);
            tv_auto_on =  view.findViewById(R.id.tv_schedule_on_auto);
            tv_auto_off =  view.findViewById(R.id.tv_schedule_off_auto);
            tv_schedule_on_time = view.findViewById(R.id.tv_schedule_on_time);
            tv_schedule_off_time =  view.findViewById(R.id.tv_schedule_off_time);
        }
    }
}