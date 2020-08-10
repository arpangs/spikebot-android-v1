package com.spike.bot.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.activity.Camera.CameraGridActivity;
import com.spike.bot.activity.RoomDetailActivity;
import com.spike.bot.core.Common;
import com.spike.bot.core.Constants;
import com.spike.bot.customview.recycle.ItemClickListener;
import com.spike.bot.customview.recycle.SectionStateChangeListener;
import com.spike.bot.listener.OnSmoothScrollList;
import com.spike.bot.listener.TempClickListener;
import com.spike.bot.model.CameraCounterModel;
import com.spike.bot.model.CameraVO;
import com.spike.bot.model.DeviceVO;
import com.spike.bot.model.PanelVO;
import com.spike.bot.model.RoomVO;

import java.util.ArrayList;

/**
 * Created by lenovo on 2/23/2016.
 */
public class SectionedExpandableGridAdapter extends RecyclerView.Adapter<SectionedExpandableGridAdapter.ViewHolder> {

    //view type
    private static final int VIEW_TYPE_SECTION = R.layout.row_room_home_v2;
    private static final int VIEW_TYPE_PANEL = R.layout.row_room_panel;
    private static final int VIEW_TYPE_ITEM = R.layout.row_room_switch_item; //TODO : change this
    private static final int VIEW_TYPE_CAMERA = R.layout.row_camera_listview;
    //context
    private final Context mContext;
    //listeners
    private final ItemClickListener mItemClickListener;
    private final SectionStateChangeListener mSectionStateChangeListener;
    public GridLayoutManager gridLayoutManager;
    public boolean isClickable = true;
    public int sectionPosition = 0;
    CameraVO cameraVO = new CameraVO();
    //data array
    private ArrayList<Object> mDataArrayList;
    private ArrayList<CameraCounterModel.Data> mCounterList;
    private CameraClickListener mCameraClickListener;
    private JetsonClickListener jetsonClickListener;
    private OnSmoothScrollList onSmoothScrollList;
    private TempClickListener tempClickListener;

    public SectionedExpandableGridAdapter(Context context, ArrayList<Object> dataArrayList, ArrayList<CameraCounterModel.Data> counterList,
                                          final GridLayoutManager gridLayout, ItemClickListener itemClickListener,
                                          OnSmoothScrollList onSmoothScroll, TempClickListener tempClickListener, SectionStateChangeListener sectionStateChangeListener) {
        mContext = context;
        mItemClickListener = itemClickListener;
        mSectionStateChangeListener = sectionStateChangeListener;
        mDataArrayList = dataArrayList;
        mCounterList = counterList;
        onSmoothScrollList = onSmoothScroll;
        this.tempClickListener = tempClickListener;
        this.gridLayoutManager = gridLayout;

        setGridView();

    }

    public void setCameraClickListener(CameraClickListener mCameraClickListener) {
        this.mCameraClickListener = mCameraClickListener;
    }

    public void setJetsonClickListener(JetsonClickListener jetsonClickListener) {
        this.jetsonClickListener = jetsonClickListener;
    }

    public void setGridView() {
        try {
            /* grid view set as per view */
            this.gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    int ffposition = 1;

                    if (isCamera(position)) {
                        ffposition = isCamera(position) ? gridLayoutManager.getSpanCount() : 1;
                    } else {
                        ffposition = isSection(position) || isPanel(position) ? gridLayoutManager.getSpanCount() : 1;
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

    /**
     * if swipe refresh is enable then disable the click event on recycler item
     *
     * @param isClickable
     */

    public void setClickable(boolean isClickable) {
        this.isClickable = isClickable;
    }

    @SuppressLint("ResourceType")
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        switch (holder.viewType) {
            case VIEW_TYPE_SECTION:
                sectionPosition = position;
                try {

                    if (position == 0) {
                        holder.view_line_top.setVisibility(View.GONE);
                    } else {
                        holder.view_line_top.setVisibility(View.GONE);
                    }

                    final RoomVO section = (RoomVO) mDataArrayList.get(position);

                    String roomName = section.getRoomName();

                    ChatApplication.logDisplay("expanded is or not " + section.isExpanded() + " " + section.getRoomName() + " " + section.getRoomId());
                    if (section.isExpanded()) {
                        holder.mImgIcnLog.setVisibility(View.VISIBLE);
                        holder.sectionTextView.setMaxLines(2);
                        if (section.getRoomName().length() >= 50) {
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
                            //  roomName = section.getRoomName().substring(0, 16);
                        }
                        holder.sectionTextView.setMaxLines(2);
                    }

                    if (section.isExpanded()) {
/*
                        ViewGroup.MarginLayoutParams layoutParams =
                                (ViewGroup.MarginLayoutParanms) holder.card_layout.getLayoutParams();
                        layoutParams.setMargins(2, 10, 2, -5);
                        holder.card_layout.requestLayout();*/
                        //   holder.card_layout.setCardElevation(0);
                        //  holder.ll_root_view_section.setBackground(mContext.getDrawable(R.drawable.background_shadow_bottom_side_mood));
                        //   holder.card_layout.setElevation(0);
                        //  holder.card_layout.setBackgroundResource(R.drawable.background_shadow_bottom_side);
                        //   holder.card_layout.setCardElevation(0);
                        //   holder.card_layout.setElevation(0);
                        holder.card_layout.setBackground(mContext.getDrawable(R.drawable.background_shadow_bottom_side)); // dev arp change drawable class on 23 june 2020
                    } else {
                        holder.card_layout.setBackground(mContext.getDrawable(R.drawable.background_with_shadow_new));
                       /* ViewGroup.MarginLayoutParams layoutParams =
                                (ViewGroup.MarginLayoutParams) holder.card_layout.getLayoutParams();
                        layoutParams.setMargins(2, 10, 2, 15);
                        holder.card_layout.requestLayout();
                        holder.card_layout.setCardElevation(8);
                          holder.card_layout.setElevation(8);*/
                        //  holder.card_layout.setBackgroundResource(R.drawable.background_shadow);
                        //    holder.ll_root_view_section.setBackground(mContext.getDrawable(R.drawable.background_shadow));

                    }


                    String styledText = "<b><font color='#333333'>" + roomName + "</font></b>";
                    holder.sectionTextView.setText(Html.fromHtml(styledText), TextView.BufferType.SPANNABLE);

                    if (section.getRoom_status() == 1) {
                        //  holder.text_section_on_off.setBackground(mContext.getResources().getDrawable(R.drawable.room_on));
                        holder.toggle_section_on_off.setBackground(mContext.getResources().getDrawable(R.drawable.panel_on));
//                        holder.text_section_on_off.setTextColor(mContext.getResources().getColor(R.color.automation_white));
                    } else {
                        //    holder.text_section_on_off.setBackground(mContext.getResources().getDrawable(R.drawable.room_off));
                        holder.toggle_section_on_off.setBackground(mContext.getResources().getDrawable(R.drawable.panel_off));
//                        holder.text_section_on_off.setTextColor(mContext.getResources().getColor(R.color.sky_blue));
                    }

                    // holder.sectionToggleButton.setChecked(section.isExpanded());

                    ChatApplication.logDisplay("is expanded is " + section.getRoomId() + " " + section.isExpanded());


                    if (section.getRoomId().equalsIgnoreCase("camera") || section.getRoomId().startsWith("JETSON-"))
                    {
                        holder.txt_total_devices.setVisibility(View.GONE);
                        holder.textShowCamera.setVisibility(View.VISIBLE);
                        holder.textRefreshCamera.setVisibility(View.VISIBLE);
                        holder.linear_preview.setVisibility(View.GONE);
                        holder.linear_refresh.setVisibility(View.GONE);
                        holder.linear_schedule.setVisibility(View.GONE);
                        holder.text_section_edit.setVisibility(View.GONE);
                        //  holder.text_section_on_off.setVisibility(View.GONE);
                        holder.toggle_section_on_off.setVisibility(View.GONE);
                        holder.img_room_delete.setVisibility(View.GONE);
                        holder.linearPanelList.setVisibility(View.VISIBLE);
                        holder.frame_beacon_alert_bell.setVisibility(View.GONE);
                        //  holder.text_section_on_off.setBackgroundResource(R.drawable.icn_camera_power);
                        //     holder.mImgSch.setImageResource(R.drawable.recoeding_new_camera);
                        holder.mImgSch.setImageResource(R.drawable.record);
                        holder.txt_log_label.setVisibility(View.VISIBLE);
                        holder.txt_notify_label.setVisibility(View.VISIBLE);
                        holder.txt_schedulelabel.setText("Recording");
                        holder.txt_schedulelabel.setVisibility(View.VISIBLE);
                        holder.txt_preview_label.setVisibility(View.VISIBLE);
                        holder.txt_refresh_label.setVisibility(View.VISIBLE);
                        if (section.getRoomId().startsWith("JETSON-")) {
                            holder.img_setting_badge.setVisibility(View.VISIBLE);
                            holder.img_setting_badge_count.setVisibility(View.VISIBLE);
                        } else {
                            holder.img_setting_badge.setVisibility(View.GONE);
                            holder.img_setting_badge_count.setVisibility(View.GONE);
                        }

                        holder.img_setting_badge.setVisibility(View.VISIBLE);
                        /*holder.text_section_on_off.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (!isClickable)
                                    return;
                                mItemClickListener.itemClicked(section, "c");
                            }
                        });*/

                        holder.toggle_section_on_off.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (!isClickable)
                                    return;
                                mItemClickListener.itemClicked(section, "c");
                            }
                        });
                    } else {
                        holder.linear_schedule.setVisibility(View.VISIBLE);
                        holder.textShowCamera.setVisibility(View.GONE);
                        holder.textRefreshCamera.setVisibility(View.GONE);
                        holder.txt_preview_label.setVisibility(View.GONE);
                        holder.txt_refresh_label.setVisibility(View.GONE);
                        //holder.text_section_on_off.setVisibility(View.VISIBLE);
                        holder.toggle_section_on_off.setVisibility(View.VISIBLE);
                        holder.img_room_delete.setVisibility(View.VISIBLE);
                        holder.linearPanelList.setVisibility(View.VISIBLE);
                        holder.txt_log_label.setVisibility(View.VISIBLE);
                        holder.txt_schedulelabel.setVisibility(View.VISIBLE);
                        holder.txt_schedulelabel.setText("Schedule");
                        holder.mImgSch.setImageResource(R.drawable.blueclock);
                        String totalbeacons = section.getTotalbeacons();
                        // holder.frame_beacon_alert_bell.setVisibility(View.VISIBLE);

                        holder.txt_total_devices.setVisibility(View.VISIBLE);

                        if (Integer.parseInt(section.getDevice_count()) == 0) {
                            holder.txt_total_devices.setVisibility(View.GONE);
                        } else {
                            holder.txt_total_devices.setVisibility(View.VISIBLE);
                            if (!TextUtils.isEmpty(section.getDevice_count()) && Integer.parseInt(section.getDevice_count()) > 99) {
                                holder.txt_total_devices.setText("" + "99+ devices");
                            } else {
                                if (Integer.parseInt(section.getDevice_count()) == 1) {
                                    holder.txt_total_devices.setText("" + section.getDevice_count() + "  device");
                                } else {
                                    holder.txt_total_devices.setText("" + section.getDevice_count() + "  devices");
                                }

                            }
                        }

                        if (!TextUtils.isEmpty(totalbeacons) && Integer.parseInt(totalbeacons) > 0) {

                            holder.frame_beacon_alert_bell.setVisibility(View.VISIBLE);
                            holder.img_beacon_badge_count.setVisibility(View.VISIBLE);
                            if (Integer.parseInt(totalbeacons) > 99) {
                                holder.img_beacon_badge_count.setText("99+");
                                holder.img_beacon_badge_count.getLayoutParams().width = Common.dpToPx(mContext, 27);
                                holder.img_beacon_badge_count.getLayoutParams().height = Common.dpToPx(mContext, 27);
                            } else if (Integer.parseInt(totalbeacons) > 0) {
                                holder.img_beacon_badge_count.setText("" + totalbeacons);
                                holder.img_beacon_badge_count.getLayoutParams().width = Common.dpToPx(mContext, 27);
                                holder.img_beacon_badge_count.getLayoutParams().height = Common.dpToPx(mContext, 27);

                            } else {
                                holder.img_beacon_badge_count.setVisibility(View.GONE);
                            }


                        } else {
                            holder.frame_beacon_alert_bell.setVisibility(View.GONE);
                            holder.img_beacon_badge_count.setVisibility(View.GONE);
                        }


                        if (section.isExpanded() && !Common.getPrefValue(mContext, Constants.USER_ADMIN_TYPE).equalsIgnoreCase("0")) {
                            holder.text_section_edit.setVisibility(View.VISIBLE);
                            holder.img_room_delete.setVisibility(View.VISIBLE);
                        } else {
                            holder.text_section_edit.setVisibility(View.GONE);
                            holder.img_room_delete.setVisibility(View.GONE);
                        }

                        if (section.isExpanded()) {
                            mItemClickListener.itemClicked(section, "heavyloadSocketon");
                        } else {
                            mItemClickListener.itemClicked(section, "heavyloadSocketoff");
                        }

                        holder.textShowCamera.setVisibility(View.GONE);
                        holder.textRefreshCamera.setVisibility(View.GONE);
                        holder.txt_preview_label.setVisibility(View.GONE);
                        holder.txt_refresh_label.setVisibility(View.GONE);
                    }

                 /*   holder.text_section_on_off.setId(position);
                    holder.text_section_on_off.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v) {
                            if (!isClickable)
                                return;
                            *//*
                     * if panel list found 1 or more so change room icon immediately
                     * *//*
                            if (section.getPanelList().size() > 0) {
                                section.setOld_room_status(section.getRoom_status());
                                section.setRoom_status(section.getRoom_status() == 0 ? 1 : 0);
                                notifyItemChanged(position);

                            }
                            mItemClickListener.itemClicked(section, "onoffclick");
                        }
                    });*/

                    holder.toggle_section_on_off.setId(position);
                    holder.toggle_section_on_off.setOnClickListener(new View.OnClickListener() {
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
                    //    final CameraVO cameraVO = (CameraVO) mDataArrayList.get(position);
                    //all camera list
                    holder.textShowCamera.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!isClickable)
                                return;
                            if (section.getRoomId().equalsIgnoreCase("camera")) {
                                mItemClickListener.itemClicked(section, "showGridCamera");
                            } else if (section.getRoomId().startsWith("JETSON-")) {
                                // mItemClickListener.itemClicked(section, "showGridJetsonCamera");


                                if (jetsonClickListener != null) {
                                    jetsonClickListener.jetsonClicked(section.getPanelList().get(0), "showGridJetsonCamera", section.getRoomId(), "");

                                    ChatApplication.logDisplay("Clicked jetson id " + section.getRoomId());
                                }
                            }
                        }
                    });

                    holder.textRefreshCamera.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!isClickable)
                                return;
                            if (section.getRoomId().equalsIgnoreCase("camera") || section.getRoomId().startsWith("JETSON-")) {
                                mItemClickListener.itemClicked(section, "refreshCamera");
                            }
                        }
                    });

                    holder.text_section_edit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!isClickable)
                                return;
                            if (section.getRoomId().equalsIgnoreCase("camera") || section.getRoomId().startsWith("JETSON-")) {
                                mItemClickListener.itemClicked(section, "editclick_true");
                            } else {
                                mItemClickListener.itemClicked(section, "editclick_false");
                            }
                        }
                    });
                  /*  holder.sectionTextView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!isClickable)
                                return;

                            if (section.getRoomId().equalsIgnoreCase("camera")|| section.getRoomId().startsWith("JETSON-")) {
                                onSmoothScrollList.onPoisitionClick(sectionPosition);
                            }

                            mItemClickListener.itemClicked(section, "expandclick");

                            mSectionStateChangeListener.onSectionStateChanged(section, !section.isExpanded);

                        }
                    });
*/
                    holder.linearRowRoom.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!isClickable)
                                return;

                            holder.rel_main_view.performClick();

                          /*  if (section.getRoomId().equalsIgnoreCase("camera")|| section.getRoomId().startsWith("JETSON-")) {
                                onSmoothScrollList.onPoisitionClick(sectionPosition);
                            }

                            mItemClickListener.itemClicked(section, "expandclick");

                            mSectionStateChangeListener.onSectionStateChanged(section, !section.isExpanded);*/
                        }
                    });

                    holder.rel_main_view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!isClickable)
                                return;
                            if (section.getRoomId().equalsIgnoreCase("camera")) {
                                onSmoothScrollList.onPoisitionClick(sectionPosition);
                                mItemClickListener.itemClicked(section, "camera_click");
                            } else if (section.getRoomId().startsWith("JETSON-")) {
                                onSmoothScrollList.onPoisitionClick(sectionPosition);
                                mItemClickListener.itemClicked(section, "jetson_click");
                            } else {
                                mItemClickListener.itemClicked(section, "room_click");
                            }
                          //   mSectionStateChangeListener.onSectionStateChanged(section, !section.isExpanded());
                        }
                    });

                    holder.linearClickExpanded.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!isClickable)
                                return;
                            holder.rel_main_view.performClick();
                        }
                    });


                    holder.sectionToggleButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            holder.rel_main_view.performClick();
                        }
                    });


                    holder.sectionToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (!isClickable)
                                return;

                            holder.rel_main_view.performClick();
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

                            } else if (section.getRoomId().startsWith("JETSON-")) {
                                jetsonClickListener.jetsonClicked(section.getPanelList().get(0), "jetsonlog", section.getRoomId(), "");
                            } else {
                                mItemClickListener.itemClicked(section, "icnLog");
                            }
                        }
                    });
                    holder.mImgSch.setId(position);
                    holder.mImgSch.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (section.getRoomId().equalsIgnoreCase("Camera")) {
                                if (!isClickable)
                                    return;
                                mItemClickListener.itemClicked(new RoomVO(), "cameraopen");
                                ChatApplication.logDisplay("camera clicked" + " " + "cameraopen");
                            } else if (section.getRoomId().startsWith("JETSON-")) {
                                jetsonClickListener.jetsonClicked(section.getPanelList().get(0), "jetsoncameraopen", section.getRoomId(), "");
                                ChatApplication.logDisplay("jetson camera clicked" + " " + "jetsoncameraopen");
                            } else {
                                mItemClickListener.itemClicked((RoomVO) mDataArrayList.get(holder.mImgSch.getId()), "icnSch");
                            }
                        }
                    });
                    holder.img_setting_badge.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (section.getRoomId().equalsIgnoreCase("Camera")) {
                                mItemClickListener.itemClicked(section, "cameraNotification");
                            } else if (section.getRoomId().startsWith("JETSON-")) {
                                jetsonClickListener.jetsonClicked(section.getPanelList().get(0), "jetsoncameraNotification", section.getRoomId(), "");
                            } else {
                                mItemClickListener.itemClicked(section, "icnSensorLog");
                            }
                        }
                    });

                    holder.frame_beacon_alert_bell.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mItemClickListener.itemClicked(section, "icnBeacon");
                        }
                    });

                    boolean flag = false;
                    for (int i = 0; i < section.getPanelList().size(); i++) {
                        for (int j = 0; j < section.getPanelList().get(i).getDeviceList().size(); j++) {
                            if (!TextUtils.isEmpty(section.getPanelList().get(i).getDeviceList().get(j).getDeviceType())) {
                                if (section.getPanelList().get(i).getDeviceList().get(j).getDeviceType().equalsIgnoreCase("temp_sensor") ||
                                        section.getPanelList().get(i).getDeviceList().get(j).getDeviceType().equalsIgnoreCase("remote") ||
                                        section.getPanelList().get(i).getDeviceList().get(j).getDeviceType().equalsIgnoreCase("gas_sensor") ||
                                        section.getPanelList().get(i).getDeviceList().get(j).getDeviceType().equalsIgnoreCase("door_sensor") ||
                                        section.getPanelList().get(i).getDeviceList().get(j).getDeviceType().equalsIgnoreCase("water_detector")) {

//                                    flag = true;
                                    flag = false; // dev arpan set condition for notiication con only shows on camera and jetson only other notifition merge with toolbar notifiation
                                }
                            }
                        }
                    }

                    String isunRead = section.getIs_unread();
                    /* notification show only camera & sensor panel */
                   /* if (flag || section.getRoomId().equalsIgnoreCase("Camera"))
                    {
                        ChatApplication.logDisplay("total notification is " + isunRead);
                        holder.img_setting_badge.setVisibility(View.GONE);
                        holder.img_setting_badge_count.setVisibility(View.GONE);
                        if (TextUtils.isEmpty(section.getIs_unread())) {
                            holder.img_setting_badge_count.setVisibility(View.GONE);
                        } else if(section.getIs_unread().equalsIgnoreCase(null)){
                            holder.img_setting_badge_count.setVisibility(View.GONE);
                        } else if (section.getIs_unread().equalsIgnoreCase("0")) {
                            holder.img_setting_badge_count.setVisibility(View.GONE);
                        } else {
                            if (!TextUtils.isEmpty(isunRead) && Integer.parseInt(isunRead) > 99) {
                                holder.img_setting_badge_count.setText("99+");
                            } else {
                                holder.img_setting_badge_count.setText("" + isunRead);
                            }
                        }

                    }*/
                    if (flag) {
                        holder.txt_notify_label.setVisibility(View.VISIBLE);
                    } else {
                        holder.txt_notify_label.setVisibility(View.VISIBLE);
                    }


                    if (flag || section.getRoomId().startsWith("JETSON-") /*|| section.getRoomId().equalsIgnoreCase("Camera")*/) {
                        ChatApplication.logDisplay("total notification is " + isunRead);
                        holder.frame_camera_alert_bell.setVisibility(View.VISIBLE);
                        //  holder.linear_camera_bell.setVisibility(View.VISIBLE);

                        if (TextUtils.isEmpty(section.getIs_unread())) {
                            holder.img_setting_badge_count.setVisibility(View.GONE);
                        } else if (section.getIs_unread().equalsIgnoreCase(null)) {
                            holder.img_setting_badge_count.setVisibility(View.GONE);
                        } else if (section.getIs_unread().equalsIgnoreCase("0")) {
                            holder.img_setting_badge_count.setVisibility(View.GONE);
                        } else {
                            holder.img_setting_badge_count.setVisibility(View.VISIBLE);
                            if (!TextUtils.isEmpty(isunRead) && Integer.parseInt(isunRead) > 99) {
                                holder.img_setting_badge_count.setText("99+");
                            } else {
                                holder.img_setting_badge_count.setText("" + isunRead);
                            }
                        }
                    } else {
                        // holder.linear_camera_bell.setVisibility(View.GONE);
                        holder.frame_camera_alert_bell.setVisibility(View.GONE);
                        holder.txt_notify_label.setVisibility(View.VISIBLE);
                        //  holder.img_setting_badge.setVisibility(View.GONE);
                    }


                    if (!TextUtils.isEmpty(isunRead) && Integer.parseInt(isunRead) > 0) {
                        holder.img_setting_badge_count.setVisibility(View.VISIBLE);
                        if (Integer.parseInt(isunRead) > 99) {
                            holder.img_setting_badge_count.setText("99+");
                            holder.img_setting_badge_count.getLayoutParams().width = Common.dpToPx(mContext, 27);
                            holder.img_setting_badge_count.getLayoutParams().height = Common.dpToPx(mContext, 27);
                        } else if (Integer.parseInt(isunRead) > 0) {
                            holder.img_setting_badge_count.setText("" + isunRead);
                            holder.img_setting_badge_count.getLayoutParams().width = Common.dpToPx(mContext, 27);
                            holder.img_setting_badge_count.getLayoutParams().height = Common.dpToPx(mContext, 27);

                        } else {
                            holder.img_setting_badge_count.setVisibility(View.GONE);
                        }


                    } else {
                        holder.img_setting_badge_count.setVisibility(View.GONE);
                    }


                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                break;
            case VIEW_TYPE_PANEL:

                ChatApplication.logDisplay("status update panel adapter");
                if (position == 0) {
                    holder.view_line_top.setVisibility(View.GONE);
                    holder.view_line_top.setBackgroundResource(R.color.automation_white);
                } else {
                    holder.view_line_top.setVisibility(View.VISIBLE);
                }

                final PanelVO panel1 = (PanelVO) mDataArrayList.get(position);

                if (panel1.getDeviceList().size() == 0) {
                    holder.ll_background.setVisibility(View.GONE);
                } else {
                    holder.ll_background.setVisibility(View.VISIBLE);
                    holder.sectionTextView.setText(panel1.getPanelName());
                }


                if (panel1.getPanel_status() == 1) {
                    holder.iv_room_panel_onoff.setImageResource(R.drawable.panel_on);
                } else {
                    holder.iv_room_panel_onoff.setImageResource(R.drawable.panel_off);
                }

                for (int i = 0; i < panel1.getDeviceList().size(); i++) {
                    holder.iv_room_panel_onoff.setTag(panel1.getDeviceList().get(i).getIsActive());
                    if (panel1.getDeviceList().get(i).getIsActive() == -1) {
                        holder.iv_room_panel_onoff.setImageResource(R.drawable.panel_off);
                        holder.iv_room_panel_onoff.setActivated(false);
                        holder.iv_room_panel_onoff.setClickable(false);
                    }/* else {
                        holder.iv_room_panel_onoff.setImageResource(R.drawable.room_on);
                    //    Toast.makeText(mContext, "No active devices found in" + " " + panel1.getPanelName(), Toast.LENGTH_SHORT).show();
                        holder.iv_room_panel_onoff.setActivated(true);
                        holder.iv_room_panel_onoff.setClickable(true);

                    }*/
                }
                Drawable myDrawable = holder.iv_room_panel_onoff.getDrawable();

                holder.iv_room_panel_onoff.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!isClickable)
                            return;
                        int position = (int) holder.iv_room_panel_onoff.getTag();

                        ChatApplication.logDisplay("position" + position);
                     /*   if(position == -1){
                            holder.iv_room_panel_onoff.setImageResource(R.drawable.room_off);
                            Toast.makeText(mContext, "No active devices found in" + " " + panel1.getPanelName(), Toast.LENGTH_SHORT).show();
                        }*//* else{

                            if(myDrawable== mContext.getResources().getDrawable(R.drawable.room_on))
                            {
                                holder.iv_room_panel_onoff.setImageResource(R.drawable.room_off);
                            }
                            else{
                                holder.iv_room_panel_onoff.setImageResource(R.drawable.room_off);
                            }

                        }*/


                     /*   for (int i = 0; i < panel1.getDeviceList().size(); i++) {

                            if (panel1.getDeviceList().get(i).getIsActive() == 1) {
                                holder.iv_room_panel_onoff.setImageResource(R.drawable.room_on);
                            } else {
                                holder.iv_room_panel_onoff.setImageResource(R.drawable.room_off);
                                Toast.makeText(mContext, "No active devices found in" + " " + panel1.getPanelName(), Toast.LENGTH_SHORT).show();
                            }
                        }*/
                        if (panel1.isActivePanel()) {
                            if (position == -1) {
                                holder.iv_room_panel_onoff.setImageResource(R.drawable.panel_off);
                                Toast.makeText(mContext, "No active devices found in" + " " + panel1.getPanelName(), Toast.LENGTH_SHORT).show();
                            } else {
                                panel1.setOldStatus(panel1.getPanel_status());
                                panel1.setPanel_status(panel1.getPanel_status() == 0 ? 1 : 0);
                                notifyItemChanged(position);
                                mItemClickListener.itemClicked(panel1, "onOffclick");
                            }

                        }
                    }
                });

                //5 for curtain

                /*|| panel1.getPanel_type() == 5*/

                if (panel1.getType().equalsIgnoreCase("camera") || panel1.getType().equalsIgnoreCase("JETSON-") || panel1.getPanel_type() == 5) {
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

                } else if (panel1.getPanelName().toLowerCase().contains("motion")) { /*dev arpan add condition for remove toggle switch from PIR panel*/

                    holder.iv_room_panel_onoff.setVisibility(View.GONE);

                } else {
                    holder.sectionTextView.setVisibility(View.VISIBLE);
                    holder.txt_recording.setVisibility(View.GONE);
                    holder.iv_room_panel_onoff.setVisibility(View.VISIBLE);

                    if (panel1.isSensorPanel()) {
                        if (panel1.isRemoteAvabile()) {
                            holder.iv_room_panel_onoff.setVisibility(View.VISIBLE);
                        } else {
                            holder.iv_room_panel_onoff.setVisibility(View.GONE);
                        }
                    }
                }
                break;
            case VIEW_TYPE_ITEM:
                final DeviceVO item = (DeviceVO) mDataArrayList.get(position);

                String itemDeviceName = "";
                int itemIcon = 0;
                /* for device */
                if (!item.isSensor()) {
//                    ChatApplication.logDisplay("status update device adapter "+item.getDeviceStatus()+" "+item.getDeviceId());
                    holder.txt_temp_in_cf.setVisibility(View.GONE);
                    holder.iv_icon_badge.setVisibility(View.GONE);
                    itemDeviceName = item.getDeviceName();

                    if (item.getDevice_icon().equalsIgnoreCase("heavyload")) {
                        if (item.getIsActive() == 1) {
                            itemIcon = item.getDeviceStatus() == 1 ? R.drawable.high_wolt_on : R.drawable.high_wolt_off;
                        } else {
                            itemIcon = R.drawable.headload_inactive;
                        }
                    } else {
                        itemIcon = Common.getIcon(item.getDeviceStatus(), item.getDevice_icon());
                    }

                    if (item.getIsActive() == -1) {
                        holder.itemTextView.setAlpha(0.50f);
                        holder.txt_temp_in_cf.setAlpha(0.50f);
                    } else {
                        holder.itemTextView.setAlpha(1);
                        holder.txt_temp_in_cf.setAlpha(1);
                    }

                    if (item.getDevice_icon().equalsIgnoreCase("bulb")) {
                        if (item.getIsActive() == 1) {
                            itemIcon = item.getDeviceStatus() == 1 ? R.drawable.oncfl : R.drawable.offcfl;
                        } else {
                            itemIcon = R.drawable.cfl_off_inactive;
                        }
                    }

                    if (item.getDevice_icon().equalsIgnoreCase("fan")) {
                        if (item.getIsActive() == 1) {
                            itemIcon = item.getDeviceStatus() == 1 ? R.drawable.onfan : R.drawable.offfan;
                        } else {
                            itemIcon = R.drawable.fan_off_inactive;
                        }
                    }

                    if (item.getDevice_icon().equalsIgnoreCase("generic")) {
                        if (item.getIsActive() == 1) {
                            itemIcon = item.getDeviceStatus() == 1 ? R.drawable.genericelectricdevice_on : R.drawable.genericelectricdevice_off;
                        } else {
                            itemIcon = R.drawable.genericelectricdevice_off_inactive;
                        }
                    }

                    if (item.getDevice_icon().equalsIgnoreCase("ac")) {
                        if (item.getIsActive() == 1) {
                            itemIcon = item.getDeviceStatus() == 1 ? R.drawable.ac : R.drawable.ac_off;
                        } else {
                            itemIcon = R.drawable.ac_off_inactive;
                        }
                    }

                    if (item.getDevice_icon().equalsIgnoreCase("pir")) { // dev arpan added 20 july 2020
                        if (item.getIsActive() == 1) {
                            itemIcon = item.getDeviceStatus() == 1 ? R.drawable.pir_detector_on : R.drawable.pir_detector_off;
                        } else {
                            itemIcon = R.drawable.pir_detector_inactive;
                        }
                    }


                } else {
                    /*--Sensor type start--*/

                    //onlydoor, subtype=1
                    //only lock, subtype=2
                    //door+lock, subtype=3

                    if (item.getIsActive() == -1) {
                        holder.itemTextView.setAlpha(0.50f);
                    } else {
                        holder.itemTextView.setAlpha(1);
                    }

                    if (item.getDeviceType().equalsIgnoreCase("remote")) {
                        holder.txt_temp_in_cf.setVisibility(View.INVISIBLE);
                        holder.iv_icon.setVisibility(View.VISIBLE);

                        if (item.getIsActive() == -1) {
                            itemIcon = Common.getIconInActive(item.getDeviceStatus(), item.getDevice_icon());
                        } else {
                            itemIcon = Common.getIcon(item.getDeviceStatus(), item.getSensor_icon()); //AC off icon  itemIcon = Common.getDoorIcon(item.getDeviceStatus());
                        }


                        holder.iv_icon_badge.setVisibility(View.GONE);
                        holder.itemTextView.setVisibility(View.VISIBLE);
                        holder.itemTextView.setText(item.getSensor_name());

                        if (item.getTemprature().equals("0")) {
                            holder.txt_temp_in_cf.setVisibility(View.INVISIBLE);
                        } else {
                            holder.txt_temp_in_cf.setVisibility(View.VISIBLE);
                            String[] temp = item.getTemprature().split("\\.");
                            String replacetemp = temp[0];
                            holder.txt_temp_in_cf.setText(replacetemp + " " + Common.getC());
                        }

                        if (item.getIsActive() == -1) {
                            holder.itemTextView.setAlpha(0.50f);
                        } else {
                            holder.itemTextView.setAlpha(1);
                        }

                        itemDeviceName = item.getSensor_name();

                    } else if (item.getSensor_type().equalsIgnoreCase(mContext.getResources().getString(R.string.door_sensor))) {

                        if (item.getIsActive() == -1) {
                            holder.itemTextView.setAlpha(0.50f);
                        } else {
                            holder.itemTextView.setAlpha(1);
                        }

                        //onlydoor, subtype=1
                        //only lock, subtype=2
                        //door+lock, subtype=3

                        /*only for door 1=close , 0=open*/
                        if (item.getIsActive() == -1) {
                            itemIcon = Common.getIconInActive(item.getDeviceStatus(), item.getDevice_icon());
                        } else {
                            itemIcon = Common.getDoorIcon(item.getDeviceStatus());
                        }

//                        if(item.getDeviceType().equals("1")){
//                            if(TextUtils.isEmpty(item.getDoor_sensor_status())){
//                                item.setDoor_lock_status(1);
//                            }
//                            itemIcon = Common.getIcon(item.getDeviceStatus()==1 ? 1 : 0, item.getSensor_icon()); //AC off icon
//
//                            if(item.getIsActive()==-1){
//                                itemIcon = R.drawable.door_off_inactive;
//                            }
//
//                        }else if(item.getDoor_subtype()==2){
//                            if(TextUtils.isEmpty(item.getDoor_lock_status()+"")){
//                                item.setDoor_lock_status(0);
//                            }
//                            itemIcon = Common.getIcon(item.getDoor_lock_status()==1 ? 1 : 0, "lockOnly"); //AC off icon
//
//                            if(item.getIs_lock_active()== -1){
//                                itemIcon = R.drawable.gray_lock_disabled;
//                            }
//                        }else {
//                            if(TextUtils.isEmpty(item.getDoor_sensor_status()+"")){
//                                item.setDoor_lock_status(1);
//                            }
//                            itemIcon = Common.getIcon(item.getDoor_sensor_status().equals("1") ? 1 : 0, "lockWithDoor");
//
//                            if(item.getIs_lock_active() == -1 && item.getIs_door_active() == -1){
//                                itemIcon=R.drawable.green_lock_gray_door_both_disabled;
//                            }else if(item.getIs_lock_active() == -1 && item.getIs_door_active() ==1){
//                                if(item.getDoor_sensor_status().equals("1")){
//                                    itemIcon=R.drawable.yellow_door_gray_lock_disabled;
//                                }else {
//                                    itemIcon=R.drawable.gray_door_gray_lock_disabled;
//                                }
//                                ChatApplication.logDisplay("door status is "+item.getDoor_sensor_status());
//                            }else if(item.getIs_lock_active() == 1 && item.getIs_door_active() == -1){
//                                if(item.getDoor_lock_status()==1){
//                                    itemIcon=R.drawable.red_lock_gray_door_disabled;
//                                }else {
//                                    itemIcon=R.drawable.green_lock_gray_door_disabled;
//                                }
//                            }else if(item.getIs_lock_active() == 1 && item.getIs_door_active() == 1){
//
//                                if(item.getDoor_sensor_status().equals("1") && item.getDoor_lock_status()==1){
//                                    itemIcon=R.drawable.door_unlocked;
//                                }else if(item.getDoor_sensor_status().equals("1") && item.getDoor_lock_status()==0){
//                                    itemIcon=R.drawable.green_lock;
//                                }else if(item.getDoor_sensor_status().equals("0") && item.getDoor_lock_status()==1){
//                                    itemIcon=R.drawable.red_lock;
//                                }else if(item.getDoor_sensor_status().equals("0") && item.getDoor_lock_status()==0){
//                                    itemIcon=R.drawable.door_locked;
//
//                                }
//
//                            }
//                        }

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

                    } else if (item.getSensor_type().equals("gas_sensor")) {
                        holder.txt_temp_in_cf.setVisibility(View.INVISIBLE);
                        holder.iv_icon.setVisibility(View.VISIBLE);

                        if (item.getIsActive() == -1) {
                            itemIcon = Common.getIconInActive(item.getDeviceStatus(), item.getDevice_icon());
                        } else {
                            itemIcon = Common.getIcon(1, item.getDevice_icon());
                        }


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

                    } else if (item.getSensor_type().equals("water_detector")) {
                        holder.txt_temp_in_cf.setVisibility(View.INVISIBLE);
                        holder.iv_icon.setVisibility(View.VISIBLE);

                        if (item.getIsActive() == -1) {
                            itemIcon = Common.getIconInActive(item.getDeviceStatus(), item.getDevice_icon());
                        } else {
                            itemIcon = Common.getIcon(1, item.getDevice_icon());
                        }


                        holder.itemTextView.setVisibility(View.VISIBLE);
                        holder.itemTextView.setText(item.getSensor_name().trim());

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

                    } else if (item.getSensor_type().equals("lock")) {
                        holder.txt_temp_in_cf.setVisibility(View.INVISIBLE);
                        holder.iv_icon.setVisibility(View.VISIBLE);

                        if (item.getDevice_icon().equalsIgnoreCase("lock")) {
                            if (item.getIsActive() == 1) {
                                itemIcon = item.getDeviceStatus() == 1 ? R.drawable.lock_only : R.drawable.unlock_only;
                            } else {
                                itemIcon = R.drawable.gray_lock_disabled;
                            }
                        } /*else {
                            if (item.getDeviceStatus() == 0) {
                                itemIcon = Common.getIcon(1, item.getDevice_icon());
                            }  else{
                                itemIcon = Common.getIcon(0, item.getDevice_icon());
                            }

                        }*/


                        holder.itemTextView.setVisibility(View.VISIBLE);
                        holder.itemTextView.setText(item.getSensor_name().trim());

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

                        if (item.getTemp_in_c() != null) {
                            if (item.getTemp_in_c().equalsIgnoreCase("C")) {
                                if (TextUtils.isEmpty("" + item.getDeviceStatus())) {
                                    tempInCF = "-- ";
                                } else {
                                    tempInCF = String.valueOf(item.getDeviceStatus());
                                }
                                cf = Common.getC();

                            } else {
                                if (TextUtils.isEmpty(item.getTemp_in_c())) {
                                    tempInCF = "-- ";
                                } else {
                                    tempInCF = "" + Constants.getFTemp("" + item.getDeviceStatus());

                                }
                                cf = Common.getF();
                            }
                        }

                        ChatApplication.logDisplay("tem pis " + tempInCF);

                        String humility = "";

                        if (!TextUtils.isEmpty(item.getDevice_sub_status()) && item.getDevice_sub_status() != null) {
                            humility = item.getDevice_sub_status();
                        }
                        if (TextUtils.isEmpty(humility)) {
                            humility = "";
                        }
                        if (humility.equalsIgnoreCase("null")) {
                            tempInCF = tempInCF + " " + cf;
                        } else {
                            tempInCF = tempInCF + " " + cf + " / " + item.getDevice_sub_status() + "%";
                        }


                        if (item.getIsActive() != -1) {
                            holder.txt_temp_in_cf.setAlpha(1);
                            holder.txt_temp_in_cf.setText(Html.fromHtml("<b>" + tempInCF + "</b>"));
                        } else {
                            holder.txt_temp_in_cf.setText(Html.fromHtml("<b>" + "-- " + cf + "</b>"));
                            holder.txt_temp_in_cf.setAlpha(0.50f);
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
                        ChatApplication.logDisplay("temp_sensor is " + item.getIsActive() + " " + item.getSensor_icon());
                        if (item.getIsActive() == -1) {
                            if (item.getDevice_icon().equalsIgnoreCase(mContext.getResources().getString(R.string.temp_sensor))) {
                                itemIcon = Common.getIconInActive(0, item.getDevice_icon());
                            } else {
                                itemIcon = Common.getIconInActive(0, item.getDevice_icon()); //unavailable means temp or dead sensor is on dead mode
                            }
                        } else {
                            itemIcon = Common.getIcon(1, item.getDevice_icon());

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

                if (item.getDeviceType().equals(mContext.getResources().getString(R.string.curtain))) {
                    if (item.getDeviceStatus() == 1) {
                        itemIcon = Common.getIcon(1, item.getDevice_icon());
                    } else if (item.getDeviceStatus() == 2) {
                        itemIcon = Common.getIcon(1, item.getDevice_icon());
                    } else {
                        itemIcon = Common.getIcon(0, item.getDevice_icon());
                    }

                    if (item.getDevice_icon().equalsIgnoreCase("curtain")) {
                        if (item.getIsActive() == 1) {
                            if (item.getDeviceStatus() == 2) {
                                itemIcon = R.drawable.curtains_on;
                            } else {
                                itemIcon = item.getDeviceStatus() == 1 ? R.drawable.curtains_on : R.drawable.curtains_off;
                            }
                        } else {
                            itemIcon = R.drawable.curtain_closed_inactive;
                        }

                       /* if (item.getDeviceStatus() == 2) {
                            itemIcon = R.drawable.curtains_on;
                        }*/
                    }
                }

                holder.iv_icon.setImageResource(itemIcon);
                holder.view.setId(position);
                holder.iv_icon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!isClickable)
                            return;
                        ChatApplication.logDisplay("postion click is " + item.getDeviceType());
                        ChatApplication.logDisplay("device status is " + item.getDeviceStatus());
                        if (item.getIsActive() == 1) {
                            holder.iv_icon.setClickable(true);
                        } else {
                            holder.iv_icon.setClickable(false);
                        }

                        if (item.getIsActive() == -1) {

                            //  itemIcon =  Common.getIcon(item.getDeviceStatus(), item.getDevice_icon());
                            //  Toast.makeText(mContext, item.getDeviceName() + " " + "is inactive", Toast.LENGTH_SHORT).show();
                        } else {
                            // itemIcon = Common.getIcon(item.getDeviceStatus(), item.getDevice_icon()); //AC off icon  itemIcon = Common.getDoorIcon(item.getDeviceStatus());
                        }


                        if (item.getDeviceType().equals(mContext.getResources().getString(R.string.curtain))) {

                        } else if (item.getDeviceType().equalsIgnoreCase("door_sensor") || item.getDeviceType().equalsIgnoreCase("temp_sensor")) {

                        } else {
                            item.setOldStatus(item.getDeviceStatus());

                            if (!item.getDeviceType().equalsIgnoreCase("pir_device")) {
                                item.setDeviceStatus(item.getDeviceStatus() == 0 ? 1 : 0);
                                notifyItemChanged(position, item);
                            }
                        }

                        if (!item.isSensor()) {
                            if (item.getDeviceType().equalsIgnoreCase("pir_device")) {
                                if (item.getIsActive() != -1)
                                    if (item.getDevice_sub_type().equalsIgnoreCase("normal")) {
                                        tempClickListener.itemClicked(item, "pir", true, position);
                                    }
                            } else if (item.getDeviceType().equalsIgnoreCase("3")) {
                                mItemClickListener.itemClicked(item, "philipsClick", position);
                            } else if (item.getDevice_icon().equalsIgnoreCase("curtain")) {
                                mItemClickListener.itemClicked(item, "curtain", position);
                            } else {
                                mItemClickListener.itemClicked(item, "itemclick", position);
                            }
                        } else {
                            if (item.getDeviceType().equalsIgnoreCase("remote")) {
                                tempClickListener.itemClicked(item, "isIRSensorClick", true, position);
                            } else {
                                // if (item.getIsActive() != -1) {
                                tempClickListener.itemClicked(item, "isSensorClick", true, position);
                                //   }
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

                      /*  if (item.getIsActive() != 1) {
                            return false;
                        }*/
                        item.setOldStatus(item.getDeviceStatus());
                        if (item.getIs_locked() == 1) {

                            if (!item.getDeviceType().equalsIgnoreCase("pir_device")) {
                                item.setDeviceStatus(item.getDeviceStatus() == 0 ? 1 : 0);
                                notifyItemChanged(position, item);
                            }
                        }


                        if (item.getDeviceType().equalsIgnoreCase("heavyload")) {
                            tempClickListener.itemClicked(item, "heavyloadlongClick", true, position);
                        } else if (item.getDeviceType().equalsIgnoreCase("3")) {
                            tempClickListener.itemClicked(item, "philipslongClick", true, position);
                        } else if (item.getDeviceType().equalsIgnoreCase("remote")) {
                            tempClickListener.itemClicked(item, "isIRSensorLongClick", true, position);
                        } else if (item.getDeviceType().equalsIgnoreCase("lock")) {
                            tempClickListener.itemClicked(item, "isLockLongClick", true, position);
                        } else if (item.getDeviceType().equalsIgnoreCase("pir_device")) {
                            tempClickListener.itemClicked(item, "isPIRLongClick", true, position);
                        }
                        return false;
                    }
                });


                if (item.getDeviceType().equals("remote") || item.getDeviceType().equalsIgnoreCase("heavyload") || item.getDeviceType().equalsIgnoreCase("fan") ||
                        item.getDeviceType().equalsIgnoreCase("2") || item.getDeviceType().equalsIgnoreCase("3") || item.getDeviceType().equalsIgnoreCase("lock")
                        || item.getDeviceType().equalsIgnoreCase("pir_device")) {
                    holder.imgLongClick.setVisibility(View.VISIBLE);
                } else {
                    if (!item.getDeviceType().equalsIgnoreCase("1")) {
                        holder.imgLongClick.setVisibility(View.GONE);
                    } else {
                        holder.imgLongClick.setVisibility(View.GONE);
                    }
                }


//                if (!TextUtils.isEmpty(item.getDeviceId()) && Integer.parseInt(item.getDeviceId()) == 1 && Integer.parseInt(item.getDeviceType()) == 1) {

//                ChatApplication.logDisplay("device type is "+item.getDeviceName()+" "+item.getDeviceType());
                try {
                    if (item.getDevice_sub_type() != null) {
                        if ((!TextUtils.isEmpty(item.getDeviceType()) && item.getDeviceType().equals(mContext.getResources().getString(R.string.fan)) && item.getDevice_sub_type().equals("normal"))) {
                            holder.imgLongClick.setVisibility(View.GONE);
                            holder.view.setOnLongClickListener(null);
                        } else if ((!TextUtils.isEmpty(item.getDeviceType()) && item.getDeviceType().equals(mContext.getResources().getString(R.string.fan)))) {
                            holder.iv_icon.setOnLongClickListener(new View.OnLongClickListener() {
                                @Override
                                public boolean onLongClick(View view) {
//                            if(item.getIs_locked()==1){
//                                Toast.makeText(mContext,mContext.getResources().getString(R.string.fan_error),Toast.LENGTH_LONG).show();
//                            }else {
                                    if (item.getDeviceStatus() == 1) {
                                        mItemClickListener.itemClicked(item, "longclick", position);
                                    }
//                            }
                                    return true;
                                }
                            });
                        } else {
                            holder.view.setOnLongClickListener(null);
                        }
                    }

                    holder.iv_icon_text.setVisibility(View.GONE);
                    holder.ll_room_item.setOnClickListener(null);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
            case VIEW_TYPE_CAMERA:
                final CameraVO cameraVO = (CameraVO) mDataArrayList.get(position);
                holder.itemTextView.setText(cameraVO.getCamera_name());

                holder.ll_room_item.setOnClickListener(null);

                if (cameraVO.getJetson_device_id().startsWith("JETSON-")) {
                    holder.frame_camera_alert.setVisibility(View.VISIBLE);
                } else {
                    try {
                        holder.frame_camera_alert.setVisibility(View.INVISIBLE);
                        holder.txt_notify_label.setVisibility(View.INVISIBLE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                if (cameraVO.getIsActive() == 0) {
                    holder.iv_icon.setImageResource(Common.getIconInActive(1, cameraVO.getCamera_icon()));
                } else {
                    holder.iv_icon.setImageResource(Common.getIcon(1, cameraVO.getCamera_icon()));
                }

                if (!TextUtils.isEmpty(cameraVO.getIs_unread()) && !cameraVO.getIs_unread().equalsIgnoreCase("0")) {
                    holder.txtCameraCount.setVisibility(View.VISIBLE);
                    if (!TextUtils.isEmpty(cameraVO.getIs_unread()) && Integer.parseInt(cameraVO.getIs_unread()) > 99) {
                        holder.txtCameraCount.setText("99+");
                    } else {
                        holder.txtCameraCount.setVisibility(View.VISIBLE);
                        holder.txtCameraCount.setText("" + cameraVO.getIs_unread());
                    }

                } else {
                    holder.txtCameraCount.setVisibility(View.INVISIBLE);
                }

                ChatApplication.logDisplay("SectionExpandableGridAdapter count " + cameraVO.getIs_unread());

                holder.iv_icon_text.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mCameraClickListener.itemClicked(cameraVO, "editcamera");
                    }
                });

                holder.iv_icon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mCameraClickListener != null && cameraVO.getIsActive() == 1) {
                            mCameraClickListener.itemClicked(cameraVO, "editclick_true");
                        } else {
                            Common.showToast("Camera not active");
                        }
                    }
                });
                holder.itemTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mCameraClickListener != null && cameraVO.getIsActive() == 1) {
                            mCameraClickListener.itemClicked(cameraVO, "editclick_true");
                        } else {
                            Common.showToast("Camera not active");
                        }
                    }
                });
                holder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mCameraClickListener != null && cameraVO.getIsActive() == 1) {
                            mCameraClickListener.itemClicked(cameraVO, "editclick_true");
                        } else {
                            Common.showToast("Camera not active");
                        }
                    }
                });


                holder.imgLogCamera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (cameraVO.getJetson_device_id().startsWith("JETSON-")) {
                            jetsonClickListener.jetsonClicked(null, "showjetsoncameraLog", cameraVO.getJetson_device_id(), cameraVO.getCamera_id());
                        } else {
                            mItemClickListener.itemClicked(cameraVO, "cameraLog");
                        }

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

    public interface CameraClickListener {
        void itemClicked(CameraVO item, String action);
    }

    public interface JetsonClickListener {
        void jetsonClicked(PanelVO panelVO, String action, String jetsonid, String camera_id);
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        View view;
        int viewType;
        ImageView iv_room_panel_onoff, view_line_top, mImgSch, img_setting_badge, mImgIcnLog, img_room_delete, iv_icon, imgLongClick, iv_icon_text, mImgCameraActive, imgLogCamera;
        ToggleButton sectionToggleButton, toggle_section_on_off;
        TextView sectionTextView, /*text_section_on_off,*/
                text_section_edit, iv_icon_badge_room, img_setting_badge_count, img_beacon_badge_count,
                itemTextView, iv_icon_badge, txt_temp_in_cf, txtTotalDevices, txtCameraCount,
                txt_recording, textRefreshCamera, textShowCamera,
                txt_schedulelabel, txt_log_label, txt_notify_label, txt_preview_label, txt_refresh_label, txt_total_devices;
        RelativeLayout rel_main_view;
        LinearLayout ll_background, ll_room_item, linearRowRoom, linearPanelList, ll_root_view_section,
                linearClickExpanded;  /*linear_camera_bell*/
        FrameLayout frame_camera_alert, frame_camera_alert_bell, frame_beacon_alert_bell;
        LinearLayout card_layout,linear_preview,linear_refresh,linear_schedule;

        public ViewHolder(View view, int viewType) {
            super(view);
            this.viewType = viewType;
            this.view = view;
            if (viewType == VIEW_TYPE_ITEM) {
                itemTextView = view.findViewById(R.id.text_item);
                iv_icon = view.findViewById(R.id.iv_icon);
                iv_icon_text = view.findViewById(R.id.iv_icon_text);
                ll_room_item = view.findViewById(R.id.ll_room_item);
                iv_icon_badge = view.findViewById(R.id.iv_icon_badge);
                txt_temp_in_cf = view.findViewById(R.id.txt_temp_in_cf);
                imgLongClick = view.findViewById(R.id.imgLongClick);
                mImgCameraActive = view.findViewById(R.id.iv_icon_active_camera);

            } else if (viewType == VIEW_TYPE_PANEL) {

                view_line_top = view.findViewById(R.id.view_line_top);

                itemTextView = view.findViewById(R.id.heading);
                sectionTextView = itemTextView;
                iv_room_panel_onoff = view.findViewById(R.id.iv_room_panel_onoff);
                ll_background = view.findViewById(R.id.ll_background);
                txt_recording = view.findViewById(R.id.txt_recording);
            } else if (viewType == VIEW_TYPE_CAMERA) {
                itemTextView = view.findViewById(R.id.text_item);
                iv_icon = view.findViewById(R.id.iv_icon);
                iv_icon_text = view.findViewById(R.id.iv_icon_text);
                ll_room_item = view.findViewById(R.id.ll_room_item);
                imgLogCamera = view.findViewById(R.id.imgLogCamera);
                txtCameraCount = view.findViewById(R.id.txtCameraCount);
                frame_camera_alert = view.findViewById(R.id.frame_camera_alert);

            } else {
                card_layout = view.findViewById(R.id.card_layout);
                txt_log_label = view.findViewById(R.id.txt_log_label);
                txt_notify_label = view.findViewById(R.id.txt_notify_label);
                txt_schedulelabel = view.findViewById(R.id.txt_schedulelabel);
                textShowCamera = view.findViewById(R.id.textShowCamera);
                textRefreshCamera = view.findViewById(R.id.textRefreshCamera);
                txt_preview_label = view.findViewById(R.id.txt_preview_label);
                txt_refresh_label = view.findViewById(R.id.txt_refresh_label);
                rel_main_view = view.findViewById(R.id.rel_main_view);
                linearRowRoom = view.findViewById(R.id.linearRowRoom);
                linearPanelList = view.findViewById(R.id.linearPanelList);
                frame_camera_alert_bell = view.findViewById(R.id.frame_camera_alert_bell);
                frame_beacon_alert_bell = view.findViewById(R.id.frame_beacon_alert_bell);
                // linear_camera_bell = view.findViewById(R.id.linear_camera_bell);
                view_line_top = view.findViewById(R.id.view_line_top);
                sectionTextView = view.findViewById(R.id.text_section);

                // text_section_on_off = view.findViewById(R.id.text_section_on_off);
                toggle_section_on_off = view.findViewById(R.id.toggle_section_on_off);
                text_section_edit = view.findViewById(R.id.text_section_edit);

                img_room_delete = view.findViewById(R.id.iv_room_delete);

                sectionToggleButton = view.findViewById(R.id.toggle_button_section);//toggle_button_section
                ll_root_view_section = view.findViewById(R.id.ll_root_view_section);
                iv_icon_badge_room = view.findViewById(R.id.iv_icon_badge_room);

                mImgIcnLog = view.findViewById(R.id.img_icn_log);
                mImgSch = view.findViewById(R.id.icn_schedule_v2);
                img_setting_badge = view.findViewById(R.id.img_setting_badge);
                img_setting_badge_count = view.findViewById(R.id.img_setting_badge_count);
                img_beacon_badge_count = view.findViewById(R.id.img_beacon_badge_count);
                txtTotalDevices = view.findViewById(R.id.txtTotalDevices);
                linearClickExpanded = view.findViewById(R.id.linearClickExpanded);
                txt_total_devices = view.findViewById(R.id.txt_total_devices);
                linear_preview= view.findViewById(R.id.linear_preview);
                linear_refresh= view.findViewById(R.id.linear_refresh);
                linear_schedule= view.findViewById(R.id.linear_schedule);
            }
        }
    }
}
