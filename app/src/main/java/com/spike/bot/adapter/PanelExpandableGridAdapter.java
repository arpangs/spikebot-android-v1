package com.spike.bot.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.spike.bot.ChatApplication;
import com.spike.bot.R;
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


public class PanelExpandableGridAdapter extends RecyclerView.Adapter<PanelExpandableGridAdapter.ViewHolder> {

    //view type
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

    public PanelExpandableGridAdapter(Context context, ArrayList<Object> dataArrayList, ArrayList<CameraCounterModel.Data> counterList,
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
                        ffposition = isPanel(position) ? gridLayoutManager.getSpanCount() : 1;
                    }

                    return ffposition;
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
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
                    }
                }

                holder.iv_room_panel_onoff.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!isClickable)
                            return;
                        int position = (int) holder.iv_room_panel_onoff.getTag();

                        ChatApplication.logDisplay("position" + position);
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

                if (panel1.getType().equalsIgnoreCase("camera") || panel1.getType().equalsIgnoreCase("JETSON-")) {
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

                } else if (panel1.getPaneltype().equalsIgnoreCase("pir") || panel1.getPaneltype().equalsIgnoreCase("sensor")) { /*dev arpan add condition for remove toggle switch from PIR panel*/

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

                    } else if (item.getDeviceType().equalsIgnoreCase(mContext.getResources().getString(R.string.door_sensor))) {

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

                    } else if (item.getDeviceType().equals("gas_sensor")) {
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

                    } else if (item.getDeviceType().equals("water_detector")) {
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

                    } else if (item.getDeviceType().equals("lock")) {
                        holder.txt_temp_in_cf.setVisibility(View.INVISIBLE);
                        holder.iv_icon.setVisibility(View.VISIBLE);

                        if (item.getDevice_icon().equalsIgnoreCase("lock")) {
                            if (item.getIsActive() == 1) {
                                itemIcon = item.getDeviceStatus() == 1 ? R.drawable.lock_only : R.drawable.unlock_only;
                            } else {
                                itemIcon = R.drawable.gray_lock_disabled;
                            }
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

                try {
                    if (item.getDevice_sub_type() != null) {
                        if ((!TextUtils.isEmpty(item.getDeviceType()) && item.getDeviceType().equals(mContext.getResources().getString(R.string.fan)) && item.getDevice_sub_type().equals("normal"))) {
                            holder.imgLongClick.setVisibility(View.GONE);
                            holder.view.setOnLongClickListener(null);
                        } else if ((!TextUtils.isEmpty(item.getDeviceType()) && item.getDeviceType().equals(mContext.getResources().getString(R.string.fan)))) {
                            holder.iv_icon.setOnLongClickListener(new View.OnLongClickListener() {
                                @Override
                                public boolean onLongClick(View view) {

                                    if (item.getDeviceStatus() == 1) {
                                        mItemClickListener.itemClicked(item, "longclick", position);
                                    }
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
        if (isPanel(position)) {
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
        ImageView iv_room_panel_onoff, view_line_top, iv_icon, imgLongClick, iv_icon_text, mImgCameraActive, imgLogCamera;
        TextView sectionTextView, itemTextView, iv_icon_badge, txt_temp_in_cf, txtCameraCount, txt_recording, txt_notify_label;
        RelativeLayout rel_main_view;
        LinearLayout ll_background, ll_room_item;
        FrameLayout frame_camera_alert;

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

            }
        }
    }
}

