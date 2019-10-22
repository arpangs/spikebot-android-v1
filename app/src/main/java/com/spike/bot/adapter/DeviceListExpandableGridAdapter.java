package com.spike.bot.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.spike.bot.R;
import com.spike.bot.core.Common;
import com.spike.bot.customview.recycle.ItemClickListener;
import com.spike.bot.customview.recycle.SectionStateChangeListener;
import com.spike.bot.listener.SelectDevicesListener;
import com.spike.bot.model.DeviceVO;
import com.spike.bot.model.PanelVO;
import com.spike.bot.model.RoomVO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by lenovo on 2/23/2016.
 */
public class DeviceListExpandableGridAdapter extends RecyclerView.Adapter<DeviceListExpandableGridAdapter.ViewHolder> {

    //data array
    private ArrayList<Object> mDataArrayList;

    //context
    private Context mContext = null;

    //listeners
    private final ItemClickListener mItemClickListener;
    private final SectionStateChangeListener mSectionStateChangeListener;

    //view type
    private static final int VIEW_TYPE_SECTION = R.layout.row_room_home;
    private static final int VIEW_TYPE_ITEM = R.layout.row_room_switch_item; //TODO : change this
    private static final int VIEW_TYPE_PANEL = R.layout.row_room_panel;

    public boolean isMoodAdapter;
    SelectDevicesListener selectDevicesListener;

    public String[] strArray;
    public List<String> listDeviceIds;

    public DeviceListExpandableGridAdapter(Context context, ArrayList<Object> dataArrayList,
                                           final GridLayoutManager gridLayoutManager, ItemClickListener itemClickListener,
                                           SectionStateChangeListener sectionStateChangeListener, boolean isMoodAdapter, SelectDevicesListener selectDevicesListener) {
        mContext = context;
        mItemClickListener = itemClickListener;
        mSectionStateChangeListener = sectionStateChangeListener;
        mDataArrayList = dataArrayList;
        this.isMoodAdapter = isMoodAdapter;
        this.selectDevicesListener = selectDevicesListener;

        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return isSection(position) || isPanel(position) ? gridLayoutManager.getSpanCount() : 1;
            }
        });
    }

    public void setSelection(String room_device_id) {
        strArray = room_device_id.split(",");
        listDeviceIds = Arrays.asList(strArray);

        for (int i = 0; i < mDataArrayList.size(); i++) {
            if (mDataArrayList.get(i) instanceof DeviceVO) {
                final DeviceVO item = (DeviceVO) mDataArrayList.get(i);
//                if (listDeviceIds.contains(item.getRoomDeviceId())) {
                if (listDeviceIds.contains(item.getDeviceId())) {
                    item.setSelected(true);
                }
            }
        }

    }

    public void setSelectionAllDevices() {

        for (int i = 0; i < mDataArrayList.size(); i++) {
            if (mDataArrayList.get(i) instanceof DeviceVO) {
                final DeviceVO item = (DeviceVO) mDataArrayList.get(i);
                if (item.getDeviceType().equalsIgnoreCase("2")) {
                    if (item.getIsActive() == 1) {
                        item.setSelected(false);
                    }
                } else {
                    item.setSelected(false);
                }
            }
        }
    }

    private boolean isSection(int position) {
        return mDataArrayList.get(position) instanceof RoomVO;
    }

    private boolean isPanel(int position) {
        return mDataArrayList.get(position) instanceof PanelVO;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false), viewType);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        switch (holder.viewType) {
            case VIEW_TYPE_SECTION:

                holder.img_room_delete.setVisibility(View.GONE);

                if (position == 0) {
                    holder.view_line_top.setVisibility(View.GONE);
                } else {
                    holder.view_line_top.setVisibility(View.VISIBLE);
                }

                final RoomVO section = (RoomVO) mDataArrayList.get(position);

                holder.sectionTextView.setText(section.getRoomName());
                holder.text_section_on_off.setVisibility(View.GONE);
                holder.text_section_edit.setVisibility(View.GONE);
                holder.sectionToggleButton.setVisibility(View.VISIBLE);

                holder.rel_main_view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mSectionStateChangeListener.onSectionStateChanged(section, !section.isExpanded);
                    }
                });

                holder.sectionToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (mContext != null) {
                            Common.hideSoftKeyboard((Activity) mContext);
                        }
                        mSectionStateChangeListener.onSectionStateChanged(section, isChecked);
                    }
                });
                holder.sectionToggleButton.setChecked(section.isExpanded);

                holder.txtSelectDevice.setVisibility(View.VISIBLE);
                if (!section.getSelectDevice().equalsIgnoreCase("0")) {
                    holder.txtSelectDevice.setText(section.getSelectDevice() + " devices");
                } else {
                    holder.txtSelectDevice.setText("0 devices");
                }

                break;
            case VIEW_TYPE_PANEL:
                final PanelVO panel1 = (PanelVO) mDataArrayList.get(position);

                holder.sectionTextView.setText(panel1.getPanelName());
                holder.iv_room_panel_onoff.setVisibility(View.GONE);

                if (panel1.isActivePanel()) {
                    holder.ll_background.setVisibility(View.VISIBLE);
                } else {
                    holder.ll_background.setVisibility(View.GONE);
                }

                break;
            case VIEW_TYPE_ITEM:
                final DeviceVO item = (DeviceVO) mDataArrayList.get(position);
                if (item.isSensor() || item.getDeviceType().equalsIgnoreCase("2")) {

                    if (TextUtils.isEmpty(item.getSensor_icon())) {

                        holder.itemTextView.setText(item.getDeviceName());

                        if (item.getIsActive() == 0) {
                                holder.iv_icon.setImageResource(Common.getIconInActive(1, item.getDevice_icon()));//item.getDeviceStatus()
                        } else {
                                holder.iv_icon.setImageResource(Common.getIcon(0, item.getDevice_icon()));//item.getDeviceStatus()
                        }
                    } else {

                        holder.itemTextView.setText(item.getSensor_name());

                        if (item.getDevice_icon().equalsIgnoreCase("2") && item.getIsActive() == 0) {
                            if (item.getSensor_icon() != null) {
                                holder.iv_icon.setImageResource(Common.getIconInActive(1, item.getSensor_icon()));//item.getDeviceStatus()
                            }
                        } else {
                            if (item.getSensor_icon() != null) {
                                holder.iv_icon.setImageResource(Common.getIcon(0, item.getSensor_icon()));//item.getDeviceStatus()
                            }
                        }
                    }
                } else {
                    holder.itemTextView.setText(item.getDeviceName());
                    if (item.getDevice_icon() != null) {
                        holder.iv_icon.setImageResource(Common.getIcon(0, item.getDevice_icon()));//item.getDeviceStatus()
                    }
                }
                if (item.isSelected()) {
                    String type = "";
                    if (!TextUtils.isEmpty(item.getSensor_type())) {
                        type = item.getSensor_type();
                    }
                    if (type.equalsIgnoreCase("temp_sensor") || type.equalsIgnoreCase("door_sensor")|| type.equalsIgnoreCase("gas_sensor")) {
                        holder.iv_icon_select.setVisibility(View.GONE);
                    } else {
                        holder.iv_icon_select.setVisibility(View.VISIBLE);
                    }

                } else {
                    holder.iv_icon_select.setVisibility(View.GONE);
                }

                holder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String isUse = "";
                        if (!TextUtils.isEmpty(item.getTo_use())) {
                            isUse = item.getTo_use();
                        }

                        if (isUse.equalsIgnoreCase("0")) {
                            alertShow();
                        } else {
                            if (item.isSelected()) {
                                holder.iv_icon_select.setVisibility(View.GONE);
                                item.setSelected(!item.isSelected());

                            } else {

                                if (!isMoodAdapter) {

                                    if (item.getSensor_icon() != null && item.getSensor_icon().equalsIgnoreCase("Remote_AC")) {

                                        if (item.getIsActive() == 0) {

                                        } else {

                                            item.setSelected(!item.isSelected());
                                            notifyItemChanged(position, item);
                                        }

                                    } else {
                                        if (item.getIs_original() == 0) {

                                            mItemClickListener.itemClicked(item, "disable_device", position);
                                        } else {
                                            holder.iv_icon_select.setVisibility(View.VISIBLE);
                                            item.setSelected(!item.isSelected());
                                        }
                                    }

                                } else {

                                    if (item.getDeviceType().equalsIgnoreCase("2")) {
                                        if (item.getIsActive() == 0) {

                                        } else {
                                            holder.iv_icon_select.setVisibility(View.VISIBLE);
                                            item.setSelected(!item.isSelected());
                                        }

                                    } else {

                                        holder.iv_icon_select.setVisibility(View.VISIBLE);
                                        item.setSelected(!item.isSelected());
                                    }
                                }

                            }
                        }
                        notifyItemChanged(position, item);
                        selectDevicesListener.selectdeviceList();
                    }
                });


                //If found duplicate device then set alpha in devices
//                if (item.getIs_original() == 0 && !item.getDeviceType().equalsIgnoreCase("2")) {
//                    holder.view.setAlpha(0.50f);
//                } else {
                    holder.view.setAlpha(1);
//                }

                holder.iv_icon_text.setVisibility(View.GONE);
                holder.ll_room_item.setOnClickListener(null);

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
        } else {
            return VIEW_TYPE_ITEM;
        }
    }

    private void alertShow() {
        final AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Alert")
                .setMessage("This sensor is not allowed to be added in mood.")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {

        //common
        View view;
        int viewType;
        ImageView iv_room_panel_onoff, img_room_delete, iv_icon, iv_icon_select, iv_icon_text;
        View view_line_top;
        //for section
        TextView sectionTextView, text_section_on_off, txtSelectDevice, text_section_edit, itemTextView;
        ToggleButton sectionToggleButton;

        //for item
        LinearLayout ll_room_item, ll_background;
        RelativeLayout rel_main_view;

        public ViewHolder(View view, int viewType) {
            super(view);
            this.viewType = viewType;
            this.view = view;

            if (viewType == VIEW_TYPE_ITEM) {
                itemTextView = (TextView) view.findViewById(R.id.text_item);
                iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
                iv_icon_text = (ImageView) view.findViewById(R.id.iv_icon_text);
                iv_icon_select = (ImageView) view.findViewById(R.id.iv_icon_select);
                ll_room_item = (LinearLayout) view.findViewById(R.id.ll_room_item);

            } else if (viewType == VIEW_TYPE_PANEL) {
                view_line_top = (View) view.findViewById(R.id.view_line_top);

                itemTextView = (TextView) view.findViewById(R.id.heading);
                sectionTextView = itemTextView;
                iv_room_panel_onoff = (ImageView) view.findViewById(R.id.iv_room_panel_onoff);
                ll_background = view.findViewById(R.id.ll_background);

            } else {
                view_line_top = (View) view.findViewById(R.id.view_line_top);
                sectionTextView = (TextView) view.findViewById(R.id.text_section);

                txtSelectDevice = (TextView) view.findViewById(R.id.txtSelectDevice);
                text_section_on_off = (TextView) view.findViewById(R.id.text_section_on_off);
                text_section_edit = (TextView) view.findViewById(R.id.text_section_edit);
                img_room_delete = (ImageView) view.findViewById(R.id.iv_room_delete);
                sectionToggleButton = (ToggleButton) view.findViewById(R.id.toggle_button_section);
                rel_main_view = (RelativeLayout) view.findViewById(R.id.rel_main_view);
            }
        }
    }

    public String getSelectedItemIds() {

        String roomIds = "";

        for (Object obj : mDataArrayList) {
            if (obj instanceof DeviceVO) {
                DeviceVO device = (DeviceVO) obj;
                if (device.isSelected()) {
                    if (!isMoodAdapter) {
                        roomIds += "," + device.getOriginal_room_device_id();
                    } else {
                        roomIds += "," + device.getRoomDeviceId();

                    }
                }
            } else if (obj instanceof RoomVO) {
                RoomVO roomVO = (RoomVO) obj;
                for (int i = 0; i < roomVO.getPanelList().size(); i++) {
                    for (int j = 0; j < roomVO.getPanelList().get(i).getDeviceList().size(); j++) {
                        if (roomVO.getPanelList().get(i).getDeviceList().get(j).isSelected()) {
                            if (!isMoodAdapter) {
                                roomIds += "," + roomVO.getPanelList().get(i).getDeviceList().get(j).getOriginal_room_device_id();
                            } else {
                                roomIds += "," + roomVO.getPanelList().get(i).getDeviceList().get(j).getRoomDeviceId();

                            }
                        }
                    }

                }

            }
        }

        return roomIds;
    }

    /*
     * get SelectedItemList Devices in ArrayList
     * */
    public ArrayList<DeviceVO> getSelectedItemList() {

        ArrayList<DeviceVO> arrayList = new ArrayList<>();

        for (Object obj : mDataArrayList) {
            if (obj instanceof DeviceVO) {
            } else if (obj instanceof RoomVO) {
                RoomVO roomVO = (RoomVO) obj;
                for (int i = 0; i < roomVO.getPanelList().size(); i++) {
                    for (int j = 0; j < roomVO.getPanelList().get(i).getDeviceList().size(); j++) {
                        String type = "";
                        if (!TextUtils.isEmpty(roomVO.getPanelList().get(i).getDeviceList().get(j).getSensor_type())) {
                            type = roomVO.getPanelList().get(i).getDeviceList().get(j).getSensor_type();
                        }
                        if (type.equalsIgnoreCase("temp") ||
                                type.equalsIgnoreCase("door")) {
                        } else {
                            if (roomVO.getPanelList().get(i).getDeviceList().get(j).isSelected()) {
                                arrayList.add(roomVO.getPanelList().get(i).getDeviceList().get(j));
                                //  counter++;
                            }
                        }
                    }

                }

            }
        }

        return arrayList;
    }
}
