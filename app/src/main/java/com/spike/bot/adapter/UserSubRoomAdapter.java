package com.spike.bot.adapter;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.spike.bot.R;
import com.spike.bot.core.Common;
import com.spike.bot.customview.recycle.ItemClickMoodListener;
import com.spike.bot.customview.recycle.MoodStateChangeListener;
import com.spike.bot.listener.NotifityData;
import com.spike.bot.model.DeviceVO;
import com.spike.bot.model.PanelVO;
import com.spike.bot.model.RoomVO;

import java.util.ArrayList;

import static android.view.View.GONE;

/**
 * Created by Sagar on 13/3/19.
 * Gmail : jethvasagar2@gmail.com
 */
public class UserSubRoomAdapter extends RecyclerView.Adapter<UserSubRoomAdapter.ViewHolder> {

    //data array
    private ArrayList<Object> mDataArrayList;

    //context
    private final Context mContext;
    public static boolean flagIs=false;

    //listeners
    private final ItemClickMoodListener mItemClickListener;
    private final MoodStateChangeListener mSectionStateChangeListener;
    public NotifityData notifityData;

    //view type
    private static final int VIEW_TYPE_SECTION = R.layout.row_user_child_room;
    private static final int VIEW_TYPE_PANEL = R.layout.row_mood_panel;
    private static final int VIEW_TYPE_ITEM = R.layout.row_room_switch_item_mood; //TODO : change this

    public UserSubRoomAdapter(Context context, ArrayList<Object> dataArrayList,
                                     final GridLayoutManager gridLayoutManager,
                                     ItemClickMoodListener itemClickListener,
                                     MoodStateChangeListener sectionStateChangeListener,
                                     NotifityData notifityData) {
        mContext = context;
        mItemClickListener = itemClickListener;
        mSectionStateChangeListener = sectionStateChangeListener;
        mDataArrayList = dataArrayList;
        this.notifityData=notifityData;

        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                //return isSection(position)?gridLayoutManager.getSpanCount():1;
                //    Log.d("","getSpanSize position     " + position);
                //     Log.d("","getSpanSize getSpanCount " + gridLayoutManager.getSpanCount());
                return isSection(position) || isPanel(position) ? gridLayoutManager.getSpanCount():1 ;
                //return isSection(position)?gridLayoutManager.getSpanCount():1;
            }
        });
    }

    private boolean isSection(int position) {
        return mDataArrayList.get(position) instanceof RoomVO;
    }
    private boolean isPanel(int position) {
        return mDataArrayList.get(position) instanceof PanelVO;
    }

    public boolean isClickable = true;
    public void setClickabl(boolean isClickable){
        this.isClickable = isClickable;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new ViewHolder(mView,viewType);
        // return new ViewHolder(LayoutInflater.from(mContext).inflate(viewType, parent, false), viewType);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        switch (holder.viewType) {
            case VIEW_TYPE_SECTION :

                final RoomVO section = (RoomVO) mDataArrayList.get(position);

                holder.view_line_top.setVisibility(GONE);
                holder.iv_icon.setVisibility(GONE);
                holder.imgLog.setVisibility(GONE);
                holder.icnSchedule.setVisibility(GONE);
                holder.sectionTextView.setText(section.getRoomName());

                if(section.isExpanded()){
                    holder.sectionTextView.setSingleLine(false);
                }else{
                    holder.sectionTextView.setSingleLine(true);
                }


//                holder.iv_icon.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        if(!isClickable)
//                            return;
//                        section.setOld_room_status(section.getRoom_status());
//                        section.setRoom_status(section.getRoom_status()==0?1:0);
//                        notifyItemChanged(position);
//                        //   Log.d("panelMood","pid : " + section.getPanel_id());
//                        mItemClickListener.itemClicked(section,"onoffclick");
//                    }
//                });

                holder.ll_top_section.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!isClickable)
                            return;
                        mItemClickListener.itemClicked(section,"expandclick");
                        mSectionStateChangeListener.onSectionStateChanged(section, !section.isExpanded);
                    }
                });



                holder.iv_mood_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!isClickable)
                            return;
                        mItemClickListener.itemClicked(section,"deleteclick");
                        //section.setRoom_status(section.getRoom_status()==1?0:1);
                    }
                });
                holder.iv_mood_edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!isClickable)
                            return;
                        mItemClickListener.itemClicked(section,"editclick");
                    }
                });


                holder.sectionToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        mSectionStateChangeListener.onSectionStateChanged(section, isChecked);
                    }
                });

                holder.sectionToggleButton.setChecked(section.isExpanded);

//                if(section.isExpanded){
//                    holder.iv_mood_delete.setVisibility(View.VISIBLE);
//                    holder.iv_mood_edit.setVisibility(View.VISIBLE);
//                }
//                else{
//                    //  holder.text_section_edit.setVisibility(View.GONE);
//                    holder.iv_mood_delete.setVisibility(View.GONE);
//                    holder.iv_mood_edit.setVisibility(View.GONE);
//                }

                //image log
                holder.imgLog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mItemClickListener.itemClicked(section,"imgLog");
                    }
                });

                holder.icnSchedule.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mItemClickListener.itemClicked(section,"imgSch");
                    }
                });

                holder.txtTotalDevices.setText(""+section.getDevice_count() + " devices");

                if(section.isExpanded){
                    holder.ll_top_section.setBackground(mContext.getDrawable(R.drawable.background_shadow_bottom_side));
                }else {
                    holder.ll_top_section.setBackground(mContext.getDrawable(R.drawable.background_shadow));
                }

                break;
            case VIEW_TYPE_PANEL :

                final PanelVO panel1 = (PanelVO) mDataArrayList.get(position);
                if(position==0){
                    holder.txtLine.setVisibility(GONE);
                }else{
                    holder.txtLine.setVisibility(View.VISIBLE);
                }

                holder.itemTextView.setText(panel1.getPanelName());

                holder.iv_mood_panel_schedule_click.setVisibility(GONE);

                holder.iv_mood_panel_schedule_click.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!isClickable)
                            return;
                        mItemClickListener.itemClicked(panel1,"scheduleclick");
                        //mSectionStateChangeListener.onSectionStateChanged(section, !section.isExpanded);
                    }
                });
                //holder.iv_mood_panel_schedule_click.setImageResource(R.drawable.timeschedule);
                holder.iv_mood_panel_schedule_click.setImageResource(R.drawable.ic_scheduler_new);
               /* if(panel1.getPanel_status()==1 ){ //|| position%2 ==1
                }*/

                break;
            case VIEW_TYPE_ITEM :
                final DeviceVO item = (DeviceVO) mDataArrayList.get(position);

                holder.itemTextView.setText(item.getDeviceName());

                if(item.getDeviceType().equalsIgnoreCase("2")){
                    if(item.getIsActive() == 0){
                        holder.iv_icon.setImageResource(Common.getIconInActive(item.getDeviceStatus(),item.getDevice_icon()));
                    }else{
                        holder.iv_icon.setImageResource(Common.getIcon(item.getDeviceStatus(),item.getDevice_icon()));
                    }
                }else{
                    holder.iv_icon.setImageResource(Common.getIcon(item.getDeviceStatus(),item.getDevice_icon()));
                }

                holder.iv_icon_text.setVisibility(View.VISIBLE);
                holder.ll_room_item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(!isClickable || item.getDevice_icon().equalsIgnoreCase("Remote_AC"))
                            return;
                        Log.d("itemPanelClick","get : " + item.getPanel_name());

                        mItemClickListener.itemClicked(item,"textclick");
                    }
                });

//                holder.iv_icon.setId(position);
//                holder.iv_icon.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//                        if(!isClickable)
//                            return;
//
//                        Log.d("System out","mood postion is "+position);
//
//                        if(item.getDevice_icon().equalsIgnoreCase("Remote_AC")){ //click on remote device id
//                            // mItemClickListener.itemClicked(item,"isIRSensorClick");
//                            item.setPower(item.getPower().equalsIgnoreCase("ON")? "OFF":"ON" );
//                            item.setDeviceType("0");
//                            item.setDeviceStatus(item.getDeviceStatus() == 0 ? 1:0 );
////                            notifyItemChanged(v.getId(),item);
//                            // notifyItemChanged(position);
//                            mItemClickListener.itemClicked(item,"isIRSensorOnClick");
//                            notifityData.notifyData();
//                        }else{
//
//                            item.setOldStatus(item.getDeviceStatus());
//                            item.setDeviceStatus(item.getDeviceStatus() == 0 ? 1:0 );
//                            //notifyItemChanged(v.getId(),item);
////                            notifyItemChanged(position);
//
//                            mItemClickListener.itemClicked(item,"itemOnOffclick");
//                            notifityData.notifyData();
//                        }
//                        Log.d("System out","postion is "+position);
//                    }
//                });

//                holder.iv_icon.setOnLongClickListener(new View.OnLongClickListener() {
//                    @Override
//                    public boolean onLongClick(View v) {
//                        if(item.getDevice_icon().equalsIgnoreCase("Remote_AC")){ //click on remote device id
//                            mItemClickListener.itemClicked(item,"isIRSensorClick");
//                        }
//                        return false;
//                    }
//                });

//                if(item.getDeviceType().equalsIgnoreCase("1")){
//                    if(Integer.parseInt(item.getDeviceId()) == 1 && Integer.parseInt(item.getDeviceType()) == 1){
//                        holder.iv_icon.setOnLongClickListener(new View.OnLongClickListener() {
//                            @Override
//                            public boolean onLongClick(View view) {
//                                if(item.getIs_locked()==1){
//                                    Toast.makeText(mContext,mContext.getResources().getString(R.string.fan_error),Toast.LENGTH_LONG).show();
//                                }else {
//                                    mItemClickListener.itemClicked(item, "longclick");
//                                }
//                                return true;
//                            }
//                        });
//                    }else{
//                        holder.iv_icon.setOnLongClickListener(null);
//                    }
//                }

                if(item.getDevice_icon().equalsIgnoreCase("Remote_AC")){
                    holder.iv_icon_text.setVisibility(View.GONE);
                } else {
                    holder.iv_icon_text.setVisibility(View.VISIBLE);
                }

                // holder.itemTextView.setText(item.getDevice_nameTemp());
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mDataArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (isSection(position)){
            return VIEW_TYPE_SECTION;
        }else if(isPanel(position)){
            return VIEW_TYPE_PANEL;
        }else {
            return VIEW_TYPE_ITEM;
        }
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {

        //common
        View view;
        int viewType;
        ImageView iv_mood_panel_schedule_click;
        ImageView view_line_top;
        //for section
        TextView sectionTextView,txtLine,headingTemp;
        ToggleButton sectionToggleButton;
        TextView text_section_on_off;
        TextView text_section_edit;
        ImageView iv_mood_edit;
        ImageView iv_mood_delete;
        View ll_top_section;
        ImageView imgLog,icnSchedule;
        TextView txtTotalDevices;

        //for item
        TextView itemTextView;
        ImageView iv_icon;
        LinearLayout ll_room_item;
        ImageView iv_icon_text;
        RelativeLayout view_rel;

        View vi_test;

        public ViewHolder(View view, int viewType) {
            super(view);
            this.viewType = viewType;
            this.view = view;
            if (viewType == VIEW_TYPE_ITEM) {
                itemTextView = (TextView) view.findViewById(R.id.text_item);
                iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
                iv_icon_text = (ImageView) view.findViewById(R.id.iv_icon_text );
                ll_room_item = (LinearLayout) view.findViewById(R.id.ll_room_item );
                view_rel = (RelativeLayout) view.findViewById(R.id.view_rel);
                vi_test = view.findViewById(R.id.vi_test);

            } else if (viewType == VIEW_TYPE_PANEL) {
                itemTextView = (TextView) view.findViewById(R.id.heading);
                txtLine = (TextView) view.findViewById(R.id.txtLine);
                sectionTextView = itemTextView;
                iv_mood_panel_schedule_click = (ImageView) view.findViewById(R.id.iv_mood_panel_schedule_click);
            } else {
                view_line_top = (ImageView) view.findViewById(R.id.view_line_top);
                sectionTextView = (TextView) view.findViewById(R.id.text_section);
                iv_icon = (ImageView) view.findViewById(R.id.iv_icon);

                text_section_on_off = (TextView) view.findViewById(R.id.text_section_on_off);
                text_section_edit = (TextView) view.findViewById(R.id.text_section_edit);
                iv_mood_edit = (ImageView) view.findViewById(R.id.iv_mood_edit);
                iv_mood_delete = (ImageView) view.findViewById(R.id.iv_mood_delete);
                sectionToggleButton = (ToggleButton) view.findViewById(R.id.toggle_button_section);
                ll_top_section = view.findViewById(R.id.ll_top_section);

                imgLog = (ImageView) view.findViewById(R.id.img_icn_log);
                icnSchedule = (ImageView) view.findViewById(R.id.icn_schedule_v2);
                txtTotalDevices = (TextView) view.findViewById(R.id.txt_total_devices);
            }
        }
    }
}
