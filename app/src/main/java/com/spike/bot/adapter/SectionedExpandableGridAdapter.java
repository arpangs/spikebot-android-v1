package com.spike.bot.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
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

import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.activity.CameraDeviceLogActivity;
import com.spike.bot.core.Common;
import com.spike.bot.core.Constants;
import com.spike.bot.customview.recycle.ItemClickListener;
import com.spike.bot.customview.recycle.SectionStateChangeListener;
import com.spike.bot.listener.OnSmoothScrollList;
import com.spike.bot.listener.TempClickListener;
import com.spike.bot.model.CameraVO;
import com.spike.bot.model.DeviceVO;
import com.spike.bot.model.PanelVO;
import com.spike.bot.model.RoomVO;
import com.sun.mail.pop3.POP3Folder;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by lenovo on 2/23/2016.
 */
public class SectionedExpandableGridAdapter extends RecyclerView.Adapter<SectionedExpandableGridAdapter.ViewHolder> {

    //data array
    private ArrayList<Object> mDataArrayList;

    //context
    private final Context mContext;

    //listeners
    private final ItemClickListener mItemClickListener;
    private CameraClickListener mCameraClickListener;
    private final SectionStateChangeListener mSectionStateChangeListener;
    private OnSmoothScrollList onSmoothScrollList;
    private TempClickListener tempClickListener;

    //view type
    private static final int VIEW_TYPE_SECTION = R.layout.row_room_home_v2;
    private static final int VIEW_TYPE_PANEL = R.layout.row_room_panel;
    private static final int VIEW_TYPE_ITEM = R.layout.row_room_switch_item; //TODO : change this
    private static final int VIEW_TYPE_CAMERA = R.layout.row_camera_listview;

    public GridLayoutManager gridLayoutManager;

    public void setCameraClickListener(CameraClickListener mCameraClickListener) {
        this.mCameraClickListener = mCameraClickListener;
    }

        SectionedExpandableGridAdapter(Context context, ArrayList<Object> dataArrayList,
                                   final GridLayoutManager gridLayout, ItemClickListener itemClickListener,
                                   OnSmoothScrollList onSmoothScroll, TempClickListener tempClickListener, SectionStateChangeListener sectionStateChangeListener) {
        mContext = context;
        mItemClickListener = itemClickListener;
        mSectionStateChangeListener = sectionStateChangeListener;
        mDataArrayList = dataArrayList;
        onSmoothScrollList = onSmoothScroll;
        this.tempClickListener = tempClickListener;
        this.gridLayoutManager=gridLayout;

       setGridView();

    }

    public void setGridView() {
        try {

            /* grid view set as per view */
            this.gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    int ffposition=1;

                    if(isCamera(position) ){
                        ffposition = isCamera(position)?gridLayoutManager.getSpanCount() : 1;
                    } else {
                        ffposition=isSection(position) || isPanel(position) ? gridLayoutManager.getSpanCount() : 1;
                    }
                    return ffposition;
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    private boolean isSection(int position) {
        try {
            return mDataArrayList.get(position) instanceof RoomVO;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    private boolean isPanel(int position) {
        return mDataArrayList.get(position) instanceof PanelVO;
    }

    private boolean isCamera(int position) {
        return mDataArrayList.get(position) instanceof CameraVO;
    }

    private boolean isSwitch(int position) {
        return mDataArrayList.get(position) instanceof DeviceVO;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new ViewHolder(mView, viewType);

    }

    public boolean isClickable = true;

    /**
     * if swipe refresh is enable then disable the click event on recycler item
     *
     * @param isClickable
     */

    public void setClickable(boolean isClickable) {
        this.isClickable = isClickable;
    }

    public int sectionPosition = 0;

    @SuppressLint("ResourceType")
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        switch (holder.viewType) {
            case VIEW_TYPE_SECTION:
                sectionPosition = position;
                try {

                    if (position == 0) {
                        holder.view_line_top.setVisibility(View.GONE);
                    } else {
                        holder.view_line_top.setVisibility(View.VISIBLE);
                    }

                    final RoomVO section = (RoomVO) mDataArrayList.get(position);

                    String roomName = section.getRoomName();

                    holder.txtTotalDevices.setVisibility(View.GONE);

                    /*99 + notification than show */
                    if(Integer.parseInt(section.getDevice_count())>99){
                        holder.txtTotalDevices.setText("" + "99+ devices");
                    }else {
                        holder.txtTotalDevices.setText("" + section.getDevice_count() + " devices");
                    }

                    if (section.isExpanded()) {
                        holder.mImgIcnLog.setVisibility(View.VISIBLE);
                        holder.sectionTextView.setMaxLines(2);
                        if (section.getRoomName().length() >= 16) {
                            roomName = "";
                            String str[] = section.getRoomName().split(" ");
                            int i = 0;
                            for (String strVal : str) {
                                i++;
                                if (i <= 3) {
                                    roomName += strVal + " ";
                                    if (i == 3) {
                                        roomName += "\n";
                                    }
                                } else {
                                    roomName += strVal + " ";
                                }
                            }
                        }
                    } else {
                        holder.mImgIcnLog.setVisibility(View.VISIBLE);
                        if (section.getRoomName().length() >= 16) {
                            roomName = section.getRoomName().substring(0, 16);
                            roomName += "...";
                        }
                        holder.sectionTextView.setMaxLines(1);
                    }


                    String styledText = "<u><font color='#0098C0'>"+roomName+"</font></u>";
                    holder.sectionTextView.setText(Html.fromHtml(styledText), TextView.BufferType.SPANNABLE);

                    if (section.getRoom_status() == 1) {
                        holder.text_section_on_off.setBackground(mContext.getResources().getDrawable(R.drawable.room_on));
                        holder.text_section_on_off.setTextColor(mContext.getResources().getColor(R.color.automation_white));
                    } else {
                        holder.text_section_on_off.setBackground(mContext.getResources().getDrawable(R.drawable.room_off));
                        holder.text_section_on_off.setTextColor(mContext.getResources().getColor(R.color.sky_blue));
                    }

                    holder.sectionToggleButton.setChecked(section.isExpanded);

                    if (section.getRoomId().equalsIgnoreCase("camera")) {
                        holder.textShowCamera.setVisibility(View.VISIBLE);
                        holder.textRefreshCamera.setVisibility(View.VISIBLE);
                        holder.text_section_edit.setVisibility(View.GONE);
                        holder.text_section_on_off.setVisibility(View.GONE);
                        holder.img_room_delete.setVisibility(View.GONE);
                        holder.linearPanelList.setVisibility(View.VISIBLE);
                        holder.text_section_on_off.setBackgroundResource(R.drawable.icn_camera_power);
                        //     holder.mImgSch.setImageResource(R.drawable.recoeding_new_camera);
                        holder.mImgSch.setImageResource(R.drawable.rec_48);
                        holder.img_setting_badge.setVisibility(View.VISIBLE);
                        holder.text_section_on_off.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (!isClickable)
                                    return;
                                mItemClickListener.itemClicked(section, "cameraopen");
                            }
                        });
                    } else {
                        holder.textShowCamera.setVisibility(View.GONE);
                        holder.textRefreshCamera.setVisibility(View.GONE);
                        holder.text_section_on_off.setVisibility(View.VISIBLE);
                        holder.img_room_delete.setVisibility(View.VISIBLE);
                        holder.linearPanelList.setVisibility(View.VISIBLE);
                        holder.mImgSch.setImageResource(R.drawable.icn_schedule_v2);

                        if (section.isExpanded && !Common.getPrefValue(mContext, Constants.USER_ADMIN_TYPE).equalsIgnoreCase("0")) {
                            holder.text_section_edit.setVisibility(View.VISIBLE);
                            holder.img_room_delete.setVisibility(View.VISIBLE);
                        } else {
                            holder.text_section_edit.setVisibility(View.GONE);
                            holder.img_room_delete.setVisibility(View.GONE);
                        }

                        if(section.isExpanded){
                            mItemClickListener.itemClicked(section, "heavyloadSocketon");
                        }else {
                            mItemClickListener.itemClicked(section, "heavyloadSocketoff");
                        }

                        holder.textShowCamera.setVisibility(View.GONE);
                        holder.textRefreshCamera.setVisibility(View.GONE);
                    }

                    holder.text_section_on_off.setId(position);
                    holder.text_section_on_off.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!isClickable)
                                return;

                            /*
                             * if panel list found 1 or more so change room icon immediately
                             * */
                            if (section.getPanelList().size() > 0) {
                                section.setOld_room_status(section.getRoom_status());
                                section.setRoom_status(section.getRoom_status() == 0 ? 1 : 0);
                                notifyItemChanged(position);
                            }
                            mItemClickListener.itemClicked(section, "onoffclick");
                        }
                    });

                    //all camera list
                    holder.textShowCamera.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!isClickable)
                                return;
                            if (section.getRoomId().equalsIgnoreCase("camera")) {
                                mItemClickListener.itemClicked(section, "showGridCamera");
                            }
                        }
                    });

                    holder.textRefreshCamera.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!isClickable)
                                return;
                            if (section.getRoomId().equalsIgnoreCase("camera")) {
                                mItemClickListener.itemClicked(section, "refreshCamera");
                            }
                        }
                    });

                    holder.text_section_edit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!isClickable)
                                return;
                            if (section.getRoomId().equalsIgnoreCase("camera")) {
                                mItemClickListener.itemClicked(section, "editclick_true");
                            } else {
                                mItemClickListener.itemClicked(section, "editclick_false");
                            }
                        }
                    });
                    holder.sectionTextView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!isClickable)
                                return;

                            if (section.getRoomId().equalsIgnoreCase("camera")) {
                                onSmoothScrollList.onPoisitionClick(sectionPosition);
                            }

                            mItemClickListener.itemClicked(section, "expandclick");

                            mSectionStateChangeListener.onSectionStateChanged(section, !section.isExpanded);

                        }
                    });

                    holder.linearRowRoom.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!isClickable)
                                return;

                            if (section.getRoomId().equalsIgnoreCase("camera")) {
                                onSmoothScrollList.onPoisitionClick(sectionPosition);
                            }

                            mItemClickListener.itemClicked(section, "expandclick");

                            mSectionStateChangeListener.onSectionStateChanged(section, !section.isExpanded);
                        }
                    });

                    holder.ll_root_view_section.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!isClickable)
                                return;

                            if (section.getRoomId().equalsIgnoreCase("camera")) {
                                onSmoothScrollList.onPoisitionClick(sectionPosition);
                            }

                            mItemClickListener.itemClicked(section, "expandclick");

                            mSectionStateChangeListener.onSectionStateChanged(section, !section.isExpanded);
                        }
                    });

                    holder.sectionToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (!isClickable)
                                return;

                            mSectionStateChangeListener.onSectionStateChanged(section, isChecked);
                            if(section.isExpanded){
                                mItemClickListener.itemClicked(section, "heavyloadSocketon");
                            }else {
                                mItemClickListener.itemClicked(section, "heavyloadSocketoff");
                            }
                        }
                    });

                    holder.img_room_delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!isClickable)
                                return;
                            mItemClickListener.itemClicked(section, "deleteRoom");
                        }
                    });

                    holder.img_room_delete.setVisibility(View.GONE);
                    holder.mImgIcnLog.setVisibility(View.VISIBLE);

                    //Image Icon Log Click
                    holder.mImgIcnLog.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (section.getRoomId().equalsIgnoreCase("Camera")) {
                                /*camera device log*/
                                mItemClickListener.itemClicked(section, "cameraDevice");
                            } else {
                                mItemClickListener.itemClicked(section, "icnLog");
                            }
                        }
                    });
                    holder.mImgSch.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (section.getRoomId().equalsIgnoreCase("Camera")) {
                                if (!isClickable)
                                    return;
                                mItemClickListener.itemClicked(new RoomVO(), "cameraopen");
                            } else {
                                mItemClickListener.itemClicked(section, "icnSch");
                            }
                        }
                    });
                    holder.img_setting_badge.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (section.getRoomId().equalsIgnoreCase("camera")) {
                                mItemClickListener.itemClicked(section, "cameraNotification");
                            } else {
                                mItemClickListener.itemClicked(section, "icnSensorLog");
                            }
                        }
                    });

                    boolean flag = false;
                    for (int i = 0; i < section.getPanelList().size(); i++) {
                        for (int j = 0; j < section.getPanelList().get(i).getDeviceList().size(); j++) {
                            if (!TextUtils.isEmpty(section.getPanelList().get(i).getDeviceList().get(j).getSensor_type())) {
                                if (section.getPanelList().get(i).getDeviceList().get(j).getSensor_type().equalsIgnoreCase("temp") ||
                                        section.getPanelList().get(i).getDeviceList().get(j).getSensor_type().equalsIgnoreCase("irblaster") ||
                                        section.getPanelList().get(i).getDeviceList().get(j).getSensor_type().equalsIgnoreCase("multisensor") ||
                                        section.getPanelList().get(i).getDeviceList().get(j).getSensor_type().equalsIgnoreCase("gassensor") ||
                                        section.getPanelList().get(i).getDeviceList().get(j).getSensor_type().equalsIgnoreCase("door")) {

                                    flag = true;
                                }
                            }
                        }
                    }

                    String isunRead = section.getIs_unread();
                    ChatApplication.logDisplay("isunRead is "+isunRead);
                    if (!TextUtils.isEmpty(isunRead) && Integer.parseInt(isunRead)>0) {
                        holder.img_setting_badge_count.setVisibility(View.VISIBLE);
                        if (Integer.parseInt(isunRead) > 99) {
                            holder.img_setting_badge_count.setText("99+");
                            holder.img_setting_badge_count.getLayoutParams().width = Common.dpToPx(mContext, 27);
                            holder.img_setting_badge_count.getLayoutParams().height = Common.dpToPx(mContext, 27);
                        } else if(Integer.parseInt(isunRead)>0) {
                            holder.img_setting_badge_count.setText(""+isunRead);
                            holder.img_setting_badge_count.getLayoutParams().width = Common.dpToPx(mContext, 27);
                            holder.img_setting_badge_count.getLayoutParams().height = Common.dpToPx(mContext, 27);

                        }else {
                            holder.img_setting_badge_count.setVisibility(View.GONE);
                        }

                    } else {
                        holder.img_setting_badge_count.setVisibility(View.GONE);
                    }


                    /* notification show only camera & sensor panel */
                    if (flag || section.getRoomId().equalsIgnoreCase("Camera")) {
                        holder.img_setting_badge.setVisibility(View.VISIBLE);

                        if(TextUtils.isEmpty(section.getIs_unread())){
                            holder.img_setting_badge_count.setVisibility(View.GONE);
                        } else if (section.getIs_unread().equalsIgnoreCase("0")) {
                            holder.img_setting_badge_count.setVisibility(View.GONE);
                        } else {
                            holder.img_setting_badge_count.setVisibility(View.VISIBLE);
                            if (!TextUtils.isEmpty(isunRead)&&Integer.parseInt(isunRead) > 99) {
                                holder.img_setting_badge_count.setText("99+");
                            }else {
                                holder.img_setting_badge_count.setText(""+isunRead);
                            }
                        }
                    } else {
                        holder.img_setting_badge_count.setVisibility(View.GONE);
                        holder.img_setting_badge.setVisibility(View.GONE);
                    }


                        if (section.isExpanded) {
                            holder.ll_root_view_section.setBackground(mContext.getDrawable(R.drawable.background_shadow_bottom_side));
                        } else {
                            holder.ll_root_view_section.setBackground(mContext.getDrawable(R.drawable.background_shadow));
                        }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                break;
            case VIEW_TYPE_PANEL:

                ChatApplication.logDisplay("status update panel adapter");
                if (position == 0) {
                    holder.view_line_top.setVisibility(View.GONE);
                } else {
                    holder.view_line_top.setVisibility(View.VISIBLE);
                }

                final PanelVO panel1 = (PanelVO) mDataArrayList.get(position);

                if(panel1.getDeviceList().size()==0){
                    holder.ll_background.setVisibility(View.GONE);
                }else {
                    holder.ll_background.setVisibility(View.VISIBLE);
                    holder.sectionTextView.setText(panel1.getPanelName());
                }

                if (panel1.getPanel_status() == 1) {
                    holder.iv_room_panel_onoff.setImageResource(R.drawable.room_on);
                } else {
                    holder.iv_room_panel_onoff.setImageResource(R.drawable.room_off);
                }

                holder.iv_room_panel_onoff.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!isClickable)
                            return;

                        if (panel1.isActivePanel()) {
                            panel1.setOldStatus(panel1.getPanel_status());
                            panel1.setPanel_status(panel1.getPanel_status() == 0 ? 1 : 0);
                            notifyItemChanged(position);
                            mItemClickListener.itemClicked(panel1, "onOffclick");
                        }
                    }
                });


                //5 for curtain
                if (panel1.getType().equalsIgnoreCase("camera") || panel1.getPanel_type()==5) {
                    holder.txt_recording.setVisibility(View.GONE);
                    holder.txt_recording.setText(Html.fromHtml("<font color=\"red\">◉</font> <font color=\"#FFFFFF\">RECORDINGS</font>"));
                    holder.iv_room_panel_onoff.setVisibility(View.GONE);//INVISIBLE

                    holder.txt_recording.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!isClickable)
                                return;
                            mItemClickListener.itemClicked(new RoomVO(), "cameraopen");
                        }
                    });

                } else {
                    holder.sectionTextView.setVisibility(View.VISIBLE);
                    holder.txt_recording.setVisibility(View.GONE);
                    holder.iv_room_panel_onoff.setVisibility(View.VISIBLE);

                    if(panel1.isSensorPanel()){
                        if(panel1.isRemoteAvabile()){
                            holder.iv_room_panel_onoff.setVisibility(View.VISIBLE);
                        }else {
                            holder.iv_room_panel_onoff.setVisibility(View.GONE);
                        }
                    }
                }
                break;
            case VIEW_TYPE_ITEM:
                final DeviceVO item = (DeviceVO) mDataArrayList.get(position);

                String itemDeviceName = "";
                int itemIcon = 0;
                ChatApplication.logDisplay("status update device adapter "+item.getDeviceStatus()+" "+item.getDeviceId());
                /* for device */
                if (!item.isSensor()) {
//                    ChatApplication.logDisplay("status update device adapter "+item.getDeviceStatus()+" "+item.getDeviceId());
                    holder.txt_temp_in_cf.setVisibility(View.GONE);
                    holder.iv_icon_badge.setVisibility(View.GONE);
                    itemDeviceName = item.getDeviceName();
                    itemIcon = Common.getIcon(item.getDeviceStatus(), item.getDevice_icon());
                } else {
                    /*--Sensor type start--*/

                    //onlydoor, subtype=1
                    //only lock, subtype=2
                    //door+lock, subtype=3
                    if (item.getSensor_type().equalsIgnoreCase("irblaster")) {
                        holder.txt_temp_in_cf.setVisibility(View.INVISIBLE);
                        holder.iv_icon.setVisibility(View.VISIBLE);
                        itemIcon = Common.getIcon(item.getRemote_status().equalsIgnoreCase("ON") ? 1 : 0, item.getSensor_icon()); //AC off icon

                        holder.itemTextView.setVisibility(View.VISIBLE);
                        holder.itemTextView.setText(item.getSensor_name());

                        itemDeviceName = item.getSensor_name();

                    }else if(item.getSensor_type().equalsIgnoreCase(mContext.getResources().getString(R.string.door_sensor))){

                        //onlydoor, subtype=1
                        //only lock, subtype=2
                        //door+lock, subtype=3

                        if(item.getDoor_subtype()==1){
                            if(TextUtils.isEmpty(item.getDoor_sensor_status())){
                                item.setDoor_lock_status(1);
                            }
                            itemIcon = Common.getIcon(item.getDoor_sensor_status().equals("1") ? 1 : 0, item.getSensor_icon()); //AC off icon

                            if(item.getIs_door_active()==-1){
                                itemIcon = R.drawable.door_off_inactive;
                            }

                        }else if(item.getDoor_subtype()==2){
                            if(TextUtils.isEmpty(item.getDoor_lock_status()+"")){
                                item.setDoor_lock_status(0);
                            }
                            itemIcon = Common.getIcon(item.getDoor_lock_status()==1 ? 1 : 0, "lockOnly"); //AC off icon

                            if(item.getIs_lock_active()== -1){
                                itemIcon = R.drawable.gray_lock_disabled;
                            }
                        }else {
                            if(TextUtils.isEmpty(item.getDoor_sensor_status()+"")){
                                item.setDoor_lock_status(1);
                            }
                            itemIcon = Common.getIcon(item.getDoor_sensor_status().equals("1") ? 1 : 0, "lockWithDoor");

                            if(item.getIs_lock_active() == -1 && item.getIs_door_active() == -1){
                                itemIcon=R.drawable.green_lock_gray_door_both_disabled;
                            }else if(item.getIs_lock_active() == -1 && item.getIs_door_active() ==1){
                                if(item.getDoor_sensor_status().equals("1")){
                                    itemIcon=R.drawable.yellow_door_gray_lock_disabled;
                                }else {
                                    itemIcon=R.drawable.gray_door_gray_lock_disabled;
                                }
                                ChatApplication.logDisplay("door status is "+item.getDoor_sensor_status());
                            }else if(item.getIs_lock_active() == 1 && item.getIs_door_active() == -1){
                                if(item.getDoor_lock_status()==1){
                                    itemIcon=R.drawable.red_lock_gray_door_disabled;
                                }else {
                                    itemIcon=R.drawable.green_lock_gray_door_disabled;
                                }
                            }else if(item.getIs_lock_active() == 1 && item.getIs_door_active() == 1){

                                if(item.getDoor_sensor_status().equals("1") && item.getDoor_lock_status()==1){
                                    itemIcon=R.drawable.door_unlocked;
                                }else if(item.getDoor_sensor_status().equals("1") && item.getDoor_lock_status()==0){
                                    itemIcon=R.drawable.green_lock;
                                }else if(item.getDoor_sensor_status().equals("0") && item.getDoor_lock_status()==1){
                                    itemIcon=R.drawable.red_lock;
                                }else if(item.getDoor_sensor_status().equals("0") && item.getDoor_lock_status()==0){
                                    itemIcon=R.drawable.door_locked;

                                }

                            }
                        }

                        holder.itemTextView.setText(item.getSensor_name());
                        holder.txt_temp_in_cf.setVisibility(View.INVISIBLE);

                        itemDeviceName = item.getSensor_name();

                        if (!TextUtils.isEmpty(item.getIs_unread())) {
                            if (Integer.parseInt(item.getIs_unread()) > 0) {
                                holder.iv_icon_badge.setVisibility(View.VISIBLE);
                                holder.iv_icon_badge.setText(item.getIs_unread());

                                if (Integer.parseInt(item.getIs_unread()) > 99) {
                                    holder.iv_icon_badge.setText("99+");
                                    holder.iv_icon_badge.getLayoutParams().width = Common.dpToPx(mContext, 27);
                                    holder.iv_icon_badge.getLayoutParams().height = Common.dpToPx(mContext, 27);
                                } else {
                                    holder.iv_icon_badge.getLayoutParams().width = Common.dpToPx(mContext, 27);
                                    holder.iv_icon_badge.getLayoutParams().height = Common.dpToPx(mContext, 27);
                                }

                            } else {
                                holder.iv_icon_badge.setVisibility(View.GONE);
                            }
                        } else {
                            holder.iv_icon_badge.setVisibility(View.GONE);
                        }

                    }else if(item.getSensor_type().equals("gas_sensor")){
                        holder.txt_temp_in_cf.setVisibility(View.INVISIBLE);
                        holder.iv_icon.setVisibility(View.VISIBLE);
                        itemIcon = Common.getIcon(1, item.getSensor_icon()); //AC off icon

                        holder.itemTextView.setVisibility(View.VISIBLE);
                        holder.itemTextView.setText(item.getSensor_name());

                        itemDeviceName = item.getSensor_name();

                        if (!TextUtils.isEmpty(item.getIs_unread())) {
                            if (Integer.parseInt(item.getIs_unread()) > 0) {
                                holder.iv_icon_badge.setVisibility(View.VISIBLE);
                                holder.iv_icon_badge.setText(item.getIs_unread());

                                if (Integer.parseInt(item.getIs_unread()) > 99) {
                                    holder.iv_icon_badge.setText("99+");
                                    holder.iv_icon_badge.getLayoutParams().width = Common.dpToPx(mContext, 27);
                                    holder.iv_icon_badge.getLayoutParams().height = Common.dpToPx(mContext, 27);
                                } else {
                                    holder.iv_icon_badge.getLayoutParams().width = Common.dpToPx(mContext, 27);
                                    holder.iv_icon_badge.getLayoutParams().height = Common.dpToPx(mContext, 27);
                                }

                            } else {
                                holder.iv_icon_badge.setVisibility(View.GONE);
                            }
                        } else {
                            holder.iv_icon_badge.setVisibility(View.GONE);
                        }

                    } else {

                        holder.iv_icon_badge.setVisibility(View.VISIBLE);
                        holder.txt_temp_in_cf.setVisibility(View.VISIBLE);

                        String tempInCF = "";
                        String cf = "";

                        if (item.getIs_in_c().equalsIgnoreCase("1")) {

                            if (TextUtils.isEmpty(item.getTemp_in_c()) || item.getTemp_in_c().equalsIgnoreCase("null")) {
                                tempInCF = "-- ";
                            } else {
                                tempInCF = item.getTemp_in_c();
                            }
                            cf = Common.getC();

                        } else {

                            if (TextUtils.isEmpty(item.getTemp_in_f()) || item.getTemp_in_f().equalsIgnoreCase("null")) {
                                tempInCF = "-- ";
                            } else {
                                tempInCF = item.getTemp_in_f();
                            }
                            cf = Common.getF();
                        }

                        String humility="";

                        if(!TextUtils.isEmpty(item.getHumidity()) && item.getHumidity()!=null){
                            humility=item.getHumidity();
                        }
                        if(TextUtils.isEmpty(humility)){
                            humility="";
                        }
                        if(humility.equalsIgnoreCase("null")){
                            tempInCF=tempInCF+" "+cf;
                        }else {
                            tempInCF=tempInCF+" "+cf+" / "+item.getHumidity()+"%";
                        }


                        if(item.getIsActive()!=-1){
                            holder.txt_temp_in_cf.setText(Html.fromHtml("<b>" + tempInCF+ "</b>"));
                        }else {
                            holder.txt_temp_in_cf.setText(Html.fromHtml("<b>" + "-- "+cf+ "</b>"));
                        }

                        if (item.getSensor_type().equalsIgnoreCase("door")) {
                            holder.txt_temp_in_cf.setVisibility(View.INVISIBLE); //INVISIBLE
                        }

                        if (!TextUtils.isEmpty(item.getIs_unread())) {
                            if (Integer.parseInt(item.getIs_unread()) > 0) {
                                holder.iv_icon_badge.setVisibility(View.VISIBLE);
                                holder.iv_icon_badge.setText(item.getIs_unread());

                                if (Integer.parseInt(item.getIs_unread()) > 99) {
                                    holder.iv_icon_badge.setText("99+");
                                    holder.iv_icon_badge.getLayoutParams().width = Common.dpToPx(mContext, 27);
                                    holder.iv_icon_badge.getLayoutParams().height = Common.dpToPx(mContext, 27);
                                } else {
                                    holder.iv_icon_badge.getLayoutParams().width = Common.dpToPx(mContext, 27);
                                    holder.iv_icon_badge.getLayoutParams().height = Common.dpToPx(mContext, 27);
                                }

                            } else {
                                holder.iv_icon_badge.setVisibility(View.GONE);
                            }
                        } else {
                            holder.iv_icon_badge.setVisibility(View.GONE);
                        }

                        itemDeviceName = item.getSensor_name();
                        int status = 1;
                        if (!item.getDoor_sensor_status().equalsIgnoreCase("null") && !TextUtils.isEmpty(item.getDoor_sensor_status())) {
                            status = Integer.parseInt(item.getDoor_sensor_status());
                        } else {
                            status = 1;
                        }

                        // } else if (item.getIsActive() == 1) {
                        if (item.getIsActive() == -1) {
                            if (item.getSensor_type() != null && item.getSensor_type().equalsIgnoreCase(mContext.getResources().getString(R.string.temp_sensor))) {
                                itemIcon = Common.getIconInActive(status, item.getSensor_type());
                            } else if (item.getSensor_type() != null && item.getSensor_type().equalsIgnoreCase("multisensor")) {
                                itemIcon = Common.getIconInActive(status, item.getSensor_type());
                            } else {
                                itemIcon = Common.getIconInActive(status, item.getDevice_icon()); //unavailable means temp or dead sensor is on dead mode
                            }
                        } else{
                            itemIcon = Common.getIcon(status, item.getSensor_icon());

                        }

                    }

                }

                /*--End is sensor--*/

                String itemData;
                if (itemDeviceName.length() > 9) {
                    itemData = itemDeviceName.substring(0, 9) + "...";
                } else {
                    itemData = itemDeviceName;
                }

                if (!item.isSensor()) {
                    holder.itemTextView.setText(itemDeviceName);
                } else {
                    holder.itemTextView.setText(itemData);
                }

                holder.iv_icon.setImageResource(itemIcon);

                holder.iv_icon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!isClickable)
                            return;

                        ChatApplication.logDisplay("postion click is "+position);
                        if(!item.getDevice_icon().equals(mContext.getResources().getString(R.string.curtain))){
                            item.setOldStatus(item.getDeviceStatus());
                            item.setDeviceStatus(item.getDeviceStatus() == 0 ? 1 : 0);
                            notifyItemChanged(position, item);
                        }

                        if (!item.isSensor()) {
                            if(item.getDeviceType().equalsIgnoreCase("3")){
                                mItemClickListener.itemClicked(item, "philipsClick", position);
                            }else if(item.getDevice_icon().equalsIgnoreCase("curtain")){
                                mItemClickListener.itemClicked(item, "curtain", position);
                            }else {
                                mItemClickListener.itemClicked(item, "itemclick", position);
                            }
                        } else {
                            if (item.getSensor_type().equalsIgnoreCase("irblaster")) {
                                tempClickListener.itemClicked(item, "isIRSensorClick", true, position);
                            } else {
                                tempClickListener.itemClicked(item, "isSensorClick", true, position);
                            }
                        }
                    }
                });

                //onlydoor, subtype=1
                //only lock, subtype=2
                //door+lock, subtype=3

                holder.iv_icon.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if (!isClickable)
                            return false;

                        item.setOldStatus(item.getDeviceStatus());
                        if(item.getIs_locked()==1){
                            item.setDeviceStatus(item.getDeviceStatus() == 0 ? 1 : 0);
                        }

                        notifyItemChanged(position, item);

                        if (!item.isSensor()) {
                            if(item.getDeviceType().equalsIgnoreCase("heavyload")){
                                tempClickListener.itemClicked(item, "heavyloadlongClick", true, position);
                            }else if(item.getDeviceType().equalsIgnoreCase("3")){
                                tempClickListener.itemClicked(item, "philipslongClick", true, position);
                            }
                        } else {
                            if (item.getSensor_type().equalsIgnoreCase("irblaster")) {
                                tempClickListener.itemClicked(item, "isIRSensorLongClick", true, position);
                            }
                        }
                        return false;
                    }
                });


                if (item.getDevice_identifier().equalsIgnoreCase("1")|| item.getDeviceType().equalsIgnoreCase("-1")|| item.getDeviceType().equalsIgnoreCase("2") || item.getDeviceType().equalsIgnoreCase("3")) {
                    holder.imgLongClick.setVisibility(View.VISIBLE);
                }else {
                    if(!item.getDeviceType().equalsIgnoreCase("1")){
                        holder.imgLongClick.setVisibility(View.GONE);
                    }else {
                        holder.imgLongClick.setVisibility(View.GONE);
                    }
                }

//                if (!TextUtils.isEmpty(item.getDeviceId()) && Integer.parseInt(item.getDeviceId()) == 1 && Integer.parseInt(item.getDeviceType()) == 1) {

//                ChatApplication.logDisplay("device type is "+item.getDeviceName()+" "+item.getDeviceType());
                if ((!TextUtils.isEmpty(item.getDeviceType()) && item.getDeviceType().equals(mContext.getResources().getString(R.string.fan)))) {
                    holder.iv_icon.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            if(item.getIs_locked()==1){
                                Toast.makeText(mContext,mContext.getResources().getString(R.string.fan_error),Toast.LENGTH_LONG).show();
                            }else {
                                mItemClickListener.itemClicked(item, "longclick", position);
                            }
                            return true;
                        }
                    });
                } else {
                    holder.view.setOnLongClickListener(null);
                }


                holder.iv_icon_text.setVisibility(View.GONE);
                holder.ll_room_item.setOnClickListener(null);

                break;
            case VIEW_TYPE_CAMERA:
                final CameraVO cameraVO = (CameraVO) mDataArrayList.get(position);
                holder.itemTextView.setText(cameraVO.getCamera_name());

                holder.ll_room_item.setOnClickListener(null);

                if (cameraVO.getIsActive() == 0) {
                    holder.iv_icon.setImageResource(Common.getIconInActive(1, cameraVO.getCamera_icon()));
                } else {
                    holder.iv_icon.setImageResource(Common.getIcon(1, cameraVO.getCamera_icon()));
                }

                if(!TextUtils.isEmpty(cameraVO.getIs_unread())&&!cameraVO.getIs_unread().equalsIgnoreCase("0")){
                    holder.txtCameraCount.setVisibility(View.VISIBLE);
                    holder.txtCameraCount.setText(""+cameraVO.getIs_unread());
                }else {
                    holder.txtCameraCount.setVisibility(View.INVISIBLE);
                }
                holder.iv_icon_text.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mItemClickListener.itemClicked(cameraVO, "editcamera");
                    }
                });

                holder.iv_icon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mCameraClickListener != null && cameraVO.getIsActive()==1) {
                            mCameraClickListener.itemClicked(cameraVO, "editclick_true");
                        } else {
                            Common.showToast("Camera not active");
                        }
                    }
                });
                holder.itemTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mCameraClickListener != null && cameraVO.getIsActive()==1) {
                            mCameraClickListener.itemClicked(cameraVO, "editclick_true");
                        } else {
                            Common.showToast("Camera not active");
                        }
                    }
                });
                holder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mCameraClickListener != null && cameraVO.getIsActive()==1) {
                            mCameraClickListener.itemClicked(cameraVO, "editclick_true");
                        } else {
                            Common.showToast("Camera not active");
                        }
                    }
                });


                holder.imgLogCamera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mItemClickListener.itemClicked(cameraVO, "cameraLog");
                    }
                });

                break;
        }
    }

    @Override
    public int getItemCount() {
        return mDataArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (isSection(position)) {
            return VIEW_TYPE_SECTION;
        } else if (isPanel(position)) {
            return VIEW_TYPE_PANEL;
        } else if (isCamera(position)) {
            return VIEW_TYPE_CAMERA;
        } else if (isSwitch(position)) {
            return VIEW_TYPE_ITEM;
        } else {
            return position;
        }
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        View view;
        int viewType;
        ImageView iv_room_panel_onoff,view_line_top,mImgSch, img_setting_badge,mImgIcnLog,img_room_delete,iv_icon,imgLongClick,iv_icon_text,mImgCameraActive,imgLogCamera;
        ToggleButton sectionToggleButton;
        TextView sectionTextView,text_section_on_off,text_section_edit,iv_icon_badge_room,img_setting_badge_count,
                itemTextView,iv_icon_badge, txt_temp_in_cf, txtTotalDevices,txtCameraCount,txt_recording,textRefreshCamera,textShowCamera;
        RelativeLayout rel_main_view;
        LinearLayout ll_background,ll_room_item, linearRowRoom, linearPanelList,ll_root_view_section;

        public ViewHolder(View view, int viewType) {
            super(view);
            this.viewType = viewType;
            this.view = view;
            if (viewType == VIEW_TYPE_ITEM) {
                itemTextView = view.findViewById(R.id.text_item);
                iv_icon =  view.findViewById(R.id.iv_icon);
                iv_icon_text =  view.findViewById(R.id.iv_icon_text);
                ll_room_item =  view.findViewById(R.id.ll_room_item);
                iv_icon_badge =  view.findViewById(R.id.iv_icon_badge);
                txt_temp_in_cf =  view.findViewById(R.id.txt_temp_in_cf);
                imgLongClick =  view.findViewById(R.id.imgLongClick);
                mImgCameraActive =  view.findViewById(R.id.iv_icon_active_camera);

            } else if (viewType == VIEW_TYPE_PANEL) {

                view_line_top =  view.findViewById(R.id.view_line_top);

                itemTextView =  view.findViewById(R.id.heading);
                sectionTextView = itemTextView;
                iv_room_panel_onoff =  view.findViewById(R.id.iv_room_panel_onoff);
                ll_background =  view.findViewById(R.id.ll_background);
                txt_recording =  view.findViewById(R.id.txt_recording);
            }else if(viewType == VIEW_TYPE_CAMERA){
                itemTextView =  view.findViewById(R.id.text_item);
                iv_icon = view.findViewById(R.id.iv_icon);
                iv_icon_text = view.findViewById(R.id.iv_icon_text);
                ll_room_item =  view.findViewById(R.id.ll_room_item);
                imgLogCamera =  view.findViewById(R.id.imgLogCamera);
                txtCameraCount =  view.findViewById(R.id.txtCameraCount);

            } else {
                textShowCamera =  view.findViewById(R.id.textShowCamera);
                textRefreshCamera =  view.findViewById(R.id.textRefreshCamera);
                rel_main_view = view.findViewById(R.id.rel_main_view);
                linearRowRoom = view.findViewById(R.id.linearRowRoom);
                linearPanelList = view.findViewById(R.id.linearPanelList);

                view_line_top = view.findViewById(R.id.view_line_top);
                sectionTextView = view.findViewById(R.id.text_section);

                text_section_on_off =  view.findViewById(R.id.text_section_on_off);
                text_section_edit =  view.findViewById(R.id.text_section_edit);

                img_room_delete = view.findViewById(R.id.iv_room_delete);

                sectionToggleButton =  view.findViewById(R.id.toggle_button_section);//toggle_button_section
                ll_root_view_section =  view.findViewById(R.id.ll_root_view_section);
                iv_icon_badge_room =  view.findViewById(R.id.iv_icon_badge_room);

                mImgIcnLog =  view.findViewById(R.id.img_icn_log);
                mImgSch = view.findViewById(R.id.icn_schedule_v2);
                img_setting_badge =  view.findViewById(R.id.img_setting_badge);
                img_setting_badge_count =  view.findViewById(R.id.img_setting_badge_count);
                txtTotalDevices =  view.findViewById(R.id.txtTotalDevices);

            }
        }
    }

    public interface CameraClickListener {
        void itemClicked(CameraVO item, String action);
    }
}
