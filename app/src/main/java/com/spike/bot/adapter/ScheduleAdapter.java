package com.spike.bot.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.spike.bot.R;
import com.spike.bot.activity.DeviceLogActivity;
import com.spike.bot.core.Common;
import com.spike.bot.model.ScheduleVO;
import com.kp.core.DateHelper;

import java.util.ArrayList;

import static com.spike.bot.R.drawable.blue_border_yellow_top;
import static com.spike.bot.R.drawable.yellow_border_fill_rectangle;

/**
 * Created by kaushal on 27/12/17.
 */

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ViewHolder> {

    Activity mContext;
    ArrayList<ScheduleVO> scheduleArrayList;
    ScheduleClickListener scheduleClickListener;
    private boolean isMoodAdapter;

    public ScheduleAdapter(Activity context,ArrayList<ScheduleVO> scheduleArrayList , ScheduleClickListener scheduleClickListener,boolean isMood) {
        this.scheduleArrayList = scheduleArrayList;
        this.mContext = context;
        this.scheduleClickListener = scheduleClickListener;
        this.isMoodAdapter = isMood;
        Log.d("", "AutoModeAdapter AutoModeAdapter ");
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

    public boolean isClickable = true;
    public void setClickable(boolean isClickable){
        this.isClickable = isClickable;
    }

    /**
     * change schedule mood/room status using socketIO response
     *
     * @param schedule_id       :   1520069936773_ryYNpyOuM
     * @param schedule_status   :   1/0
     */

    public void chandeScheduleStatus(String schedule_id, String schedule_status){

        for (int i = 0; i < scheduleArrayList.size(); i++) {
            ScheduleVO scheduleVO = scheduleArrayList.get(i);

             if (scheduleVO.getSchedule_id().equalsIgnoreCase(schedule_id)) {
                Log.d("SCH_STATUS","name  : " + schedule_status);
                scheduleVO.setSchedule_status(Integer.parseInt(schedule_status));
                notifyItemChanged(i,scheduleVO);
            }
        }
    }

    @Override
    public int getItemCount() {
      //  Log.d("RoomParse","getItemCount : " + scheduleArrayList.size());

        return scheduleArrayList.size();
    }
    String url;

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int listPosition) {
        holder.setIsRecyclable(false);
        final ScheduleVO scheduleVO = scheduleArrayList.get(listPosition);

      //  Log.d("RoomParse","name : " + scheduleVO.getSchedule_name());

        try {

            if(listPosition==0){
               // holder.view_line_top.setVisibility(View.GONE);
            }


            if(scheduleVO.getIs_timer() == 1){

                if(scheduleVO.getSchedule_status()==1) {
                    holder.iv_sch_type.setImageResource(R.drawable.ic_timer_new);
                }else {
                    holder.iv_sch_type.setImageDrawable(mContext.getResources().getDrawable(R.drawable.timer_gray));
                }

                holder.tv_schedule_days.setVisibility(View.GONE);

                if(!TextUtils.isEmpty(scheduleVO.getTimer_on_date())){
                    holder.tv_auto_on.setVisibility(View.VISIBLE);//008BE0
                    String color = "#FFFFFF";
                    if(scheduleVO.getSchedule_status()==1){
                        color = "#FFFFFF";
                    }else{
                       // color = "#808080";
                        color = "#808080";
                    }
                    holder.tv_auto_on.setText(Html.fromHtml("<font color=\""+color+"\">"+scheduleVO.getTimer_on_date()+ "</font>"));
                }else{
                    holder.tv_auto_on.setVisibility(View.GONE);
                }
                if(!TextUtils.isEmpty(scheduleVO.getTimer_off_date())){
                    holder.tv_auto_off.setVisibility(View.VISIBLE);
                    holder.tv_auto_off.setText(Html.fromHtml("<font color=\"\"+color+\"\">"+scheduleVO.getTimer_off_date()+ "</font>"));
                }else{
                    holder.tv_auto_off.setVisibility(View.GONE);
                }

            }else{
                if(scheduleVO.getSchedule_status()==1){
                    holder.iv_sch_type.setImageResource(R.drawable.ic_scheduler_new);

                }else {
                    holder.iv_sch_type.setImageDrawable(mContext.getResources().getDrawable(R.drawable.schedule_grey_temp));
                }

                holder.tv_auto_on.setVisibility(View.GONE);
                holder.tv_auto_off.setVisibility(View.GONE);
                holder.tv_schedule_days.setVisibility(View.VISIBLE);
                holder.tv_schedule_days.setText(Html.fromHtml(Common.getDaysString(scheduleVO.getSchedule_device_day())));

            }

            // for timer
            if(scheduleVO.getIs_timer() == 1){
                holder.ll_schedule_on_off.setOrientation(LinearLayout.VERTICAL);
            }else{
                holder.ll_schedule_on_off.setOrientation(LinearLayout.HORIZONTAL);
            }


            if(!scheduleVO.getSchedule_device_on_time().equalsIgnoreCase("")){

                holder.ll_on.setVisibility(View.VISIBLE);

                holder.tv_schedule_on_time.setText("- "+DateHelper.formateDate(DateHelper.parseTimeSimple(scheduleVO.getSchedule_device_on_time(),
                        DateHelper.DATE_FROMATE_HH_MM),DateHelper.DATE_FROMATE_H_M_AMPM));
                holder.tv_schedule_on.setText("On : ");
                holder.tv_schedule_on_time.setVisibility(View.VISIBLE);
                holder.tv_schedule_on.setVisibility(View.VISIBLE);

                String lastAMPM = "";
                try{
                    lastAMPM = holder.tv_schedule_on_time.getText().toString().trim().substring(
                            holder.tv_schedule_on_time.length()-2,holder.tv_schedule_on_time.length());
                }catch (Exception ex){ ex.printStackTrace(); }

                if(lastAMPM.equalsIgnoreCase("AM") || lastAMPM.equalsIgnoreCase("am")){
                    holder.ll_on.setBackgroundResource(R.drawable.blue_border_yellow_top);
                }

                if((lastAMPM.equalsIgnoreCase("AM") || lastAMPM.equalsIgnoreCase("am") ) && scheduleVO.getSchedule_device_off_time().equalsIgnoreCase("")){
                    holder.ll_on.setBackgroundResource(R.drawable.yellow_border_fill_rectangle);
                }

                //android:background="@drawable/blue_border_yellow_bottom"

            } else {
                holder.ll_on.setVisibility(View.GONE); //VISIBLE

                holder.tv_schedule_on_time.setVisibility(View.GONE);
                holder.tv_schedule_on.setVisibility(View.GONE);
            }

            if(!scheduleVO.getSchedule_device_off_time().equalsIgnoreCase("")) {

                holder.ll_off.setVisibility(View.VISIBLE);

              //  String off_date = scheduleVO.getTimer_off_date()+" " +scheduleVO.getSchedule_device_off_time();
              //  Log.d("SchEdit","adapter : " + off_date);

                holder.tv_schedule_off_time.setText("- " + DateHelper.formateDate(
                        DateHelper.parseTimeSimple(scheduleVO.getSchedule_device_off_time(),DateHelper.DATE_FROMATE_HH_MM), DateHelper.DATE_FROMATE_H_M_AMPM));
                holder.tv_schedule_off_time.setVisibility(View.VISIBLE);
                holder.tv_schedule_off.setText("Off : ");
                holder.tv_schedule_off.setVisibility(View.VISIBLE);

                String lastAMPM = "";
                try{
                    lastAMPM = holder.tv_schedule_off_time.getText().toString().trim().substring(
                            holder.tv_schedule_off_time.length()-2,holder.tv_schedule_off_time.length());
                }catch (Exception ex){ ex.printStackTrace(); }

                if(lastAMPM.equalsIgnoreCase("AM") || lastAMPM.equalsIgnoreCase("am")){
                    holder.ll_off.setBackgroundResource(R.drawable.blue_border_yellow_bottom);
                }

                if((lastAMPM.equalsIgnoreCase("AM") || lastAMPM.equalsIgnoreCase("am")) && scheduleVO.getSchedule_device_on_time().equalsIgnoreCase("")){
                    holder.ll_off.setBackgroundResource(R.drawable.yellow_border_fill_rectangle);
                }

            } else{

                holder.ll_off.setVisibility(View.GONE);

                holder.tv_schedule_off_time.setVisibility(View.GONE);
                holder.tv_schedule_off.setVisibility(View.GONE);
            }

            holder.tv_schedule_name.setText(scheduleVO.getSchedule_name());
            holder.tv_name.setText(scheduleVO.getRoom_name());

            holder.tv_schedule_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                  //  if(!isClickable)
                   //     return;

                    PopupMenu popup = new PopupMenu(mContext, v);
                    @SuppressLint("RestrictedApi") Context wrapper = new ContextThemeWrapper(mContext, R.style.PopupMenu);
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                        popup = new PopupMenu(wrapper, v, Gravity.RIGHT);
                    } else {
                        popup = new PopupMenu(wrapper, v);
                    }
                    //popup.getMenuInflater().inflate(R.menu.menu_room_add_popup, popup.getMenu());
                    popup.getMenu().add(1, listPosition, 1, scheduleVO.getSchedule_name());
                    popup.show();

                }
            });

            if(scheduleVO.getSchedule_status()==1){
                holder.tv_schedule_name.setTextColor(mContext.getResources().getColor(R.color.sensor_button));
                holder.ll_schedule_on_off.setBackgroundResource(R.drawable.blue_border_fill_rectangle);
                holder.tv_schedule_on_time.setTextColor(mContext.getResources().getColor(R.color.automation_white));
                holder.tv_schedule_on.setTextColor(mContext.getResources().getColor(R.color.automation_white));
                holder.tv_schedule_off_time.setTextColor(mContext.getResources().getColor(R.color.automation_white));
                holder.tv_schedule_off.setTextColor(mContext.getResources().getColor(R.color.automation_white));

                holder.linearRoomSchedule.setBackgroundColor(mContext.getResources().getColor(R.color.automation_white));

                holder.tv_schedule_on.setTextColor(mContext.getResources().getColor(R.color.automation_white));
               // holder.tv_auto_on.setTextColor(mContext.getResources().getColor(R.color.automation_white));
                holder.tv_schedule_on_time.setTextColor(mContext.getResources().getColor(R.color.automation_white));
                holder.tv_schedule_off.setTextColor(mContext.getResources().getColor(R.color.automation_white));
                holder.tv_auto_off.setTextColor(mContext.getResources().getColor(R.color.automation_white));
                holder.tv_schedule_off_time.setTextColor(mContext.getResources().getColor(R.color.automation_white));

                holder.iv_schedule_dots.setImageDrawable(mContext.getResources().getDrawable(R.drawable.more));
            }
            else{

                //Remove background resources
                holder.ll_on.setBackgroundResource(0);
                holder.ll_off.setBackgroundResource(0);

               // holder.ll_schedule_on_off.setBackgroundResource(R.drawable.blue_border_rectangle);
                holder.ll_schedule_on_off.setBackgroundResource(R.drawable.drawable_off_schedule);
               // holder.linearRoomSchedule.setBackgroundColor(mContext.getResources().getColor(R.color.automation_dark_gray));
          //      holder.iv_sch_type.setImageDrawable(mContext.getResources().getDrawable(R.drawable.schedule_grey_new));
              //  holder.tv_schedule_on_time.setTextColor(mContext.getResources().getColor(R.color.automation_white));
              //  holder.tv_schedule_on.setTextColor(mContext.getResources().getColor(R.color.automation_white));
                //holder.tv_schedule_off_time.setTextColor(mContext.getResources().getColor(R.color.automation_white));
              //  holder.tv_schedule_off.setTextColor(mContext.getResources().getColor(R.color.automation_white));

                holder.iv_schedule_dots.setImageDrawable(mContext.getResources().getDrawable(R.drawable.more_grey));
                holder.tv_schedule_name.setTextColor(mContext.getResources().getColor(R.color.device_button));
                holder.tv_schedule_days.setText(Html.fromHtml(Common.getDaysStringGray(scheduleVO.getSchedule_device_day())));

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
        holder.ll_schedule_on_off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!isClickable)
                    return;

                // mItemClickListener.itemClicked(item);
                scheduleVO.setIs_active(scheduleVO.getIs_active()==1?0:1);
                notifyDataSetChanged();

              //  scheduleClickListener.itemClicked(scheduleVO,"active");
                scheduleClickListener.itemClicked(scheduleVO,"active",isMoodAdapter);
            }
        });

        holder.iv_sch_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!isClickable)
                    return;

                // mItemClickListener.itemClicked(item);
                scheduleVO.setIs_active(scheduleVO.getIs_active()==1?0:1);
                notifyDataSetChanged();

              //  scheduleClickListener.itemClicked(scheduleVO,"active");
                scheduleClickListener.itemClicked(scheduleVO,"active",isMoodAdapter);
            }
        });


        holder.iv_schedule_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isClickable)
                    return;
                // mItemClickListener.itemClicked(item);
             //   Log.d("", " setOnClickListener iv_auto_mode_delete = ");
             //   scheduleClickListener.itemClicked(scheduleVO,"edit");
                scheduleClickListener.itemClicked(scheduleVO,"edit",isMoodAdapter);
            }
        });
        holder.iv_schedule_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isClickable)
                    return;
                // mItemClickListener.itemClicked(item);
              //  Log.d("", " setOnClickListener tv_auto_mode_on_off = ");
             //   scheduleClickListener.itemClicked(scheduleVO,"delete");
                scheduleClickListener.itemClicked(scheduleVO,"delete",isMoodAdapter);
            }
        });

        holder.iv_schedule_dots.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!isClickable)
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
                        switch (item.getItemId()){
                            case R.id.action_edit_dots:
                              //  scheduleClickListener.itemClicked(scheduleVO,"edit");
                                scheduleClickListener.itemClicked(scheduleVO,"edit",isMoodAdapter);
                                break;
                            case R.id.action_delete_dots:
                              //  scheduleClickListener.itemClicked(scheduleVO,"delete");
                                scheduleClickListener.itemClicked(scheduleVO,"delete",isMoodAdapter);
                                break;

                            case R.id.action_log:
                                Intent intent = new Intent(mContext, DeviceLogActivity.class);
                                intent.putExtra("Schedule_id",""+scheduleVO.getSchedule_id());
                                intent.putExtra("activity_type",""+scheduleVO.getIs_timer());
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
        View view;
        int viewType;
        View view_line_top;
        TextView tv_schedule_name, tv_schedule_days,tv_name;

        ImageView iv_schedule_edit, iv_schedule_delete;
        ImageView iv_schedule_dots;
        ImageView iv_sch_type;

        LinearLayout ll_schedule_on_off,linearRoomSchedule;
        LinearLayout ll_on,ll_off;
        TextView tv_schedule_on,tv_schedule_off;
        TextView tv_auto_on,tv_auto_off,tv_schedule_on_time,tv_schedule_off_time;

        public ViewHolder(View view, int viewType) {
            super(view);
            this.viewType = viewType;
            this.view = view;
            view_line_top = (View) view.findViewById(R.id.view_line_top);
            tv_schedule_name = (TextView) view.findViewById(R.id.tv_schedule_name);
            tv_schedule_days = (TextView) view.findViewById(R.id.tv_schedule_days);


            tv_name = (TextView) view.findViewById(R.id.tv_name);
            iv_schedule_edit = (ImageView) view.findViewById(R.id.iv_schedule_edit);
            iv_schedule_delete = (ImageView) view.findViewById(R.id.iv_schedule_delete);
            iv_schedule_dots = (ImageView) view.findViewById(R.id.iv_schedule_dots);
            iv_sch_type = (ImageView) view.findViewById(R.id.iv_sche_type);

            ll_schedule_on_off = (LinearLayout) view.findViewById(R.id.ll_schedule_on_off );
            linearRoomSchedule = (LinearLayout) view.findViewById(R.id.linearRoomSchedule );
            ll_on = (LinearLayout)view.findViewById(R.id.ll_on);
            ll_off = (LinearLayout)view.findViewById(R.id.ll_off);
            tv_schedule_on = (TextView) view.findViewById(R.id.tv_schedule_on);
            tv_schedule_off = (TextView) view.findViewById(R.id.tv_schedule_off);
            tv_auto_on = (TextView) view.findViewById(R.id.tv_schedule_on_auto);
            tv_auto_off = (TextView) view.findViewById(R.id.tv_schedule_off_auto);
            tv_schedule_on_time = (TextView) view.findViewById(R.id.tv_schedule_on_time);
            tv_schedule_off_time = (TextView) view.findViewById(R.id.tv_schedule_off_time);


            //  itemTextView.setLayoutParams(new LinearLayout.LayoutParams(mContext));


        }
    }
}