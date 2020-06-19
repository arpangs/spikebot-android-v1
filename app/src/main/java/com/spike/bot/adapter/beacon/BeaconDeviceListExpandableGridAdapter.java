package com.spike.bot.adapter.beacon;

import android.app.Activity;
import android.content.Context;
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

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.core.Common;
import com.spike.bot.customview.recycle.SectionStateChangeListener;
import com.spike.bot.model.DeviceVO;
import com.spike.bot.model.RoomVO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BeaconDeviceListExpandableGridAdapter extends RecyclerView.Adapter<BeaconDeviceListExpandableGridAdapter.ViewHolder> {


    //data array
    private ArrayList<Object> mDataArrayList;

    //context
    private Context mContext = null;

    private final SectionStateChangeListener mSectionStateChangeListener;

    //view type
    private static final int VIEW_TYPE_SECTION = R.layout.row_beacon_list;
    private static final int VIEW_TYPE_ITEM = R.layout.row_room_switch_item; //TODO : change this

    public boolean isMoodAdapter;


    /**
     * Beacon Device List Expandable Grid Adapter
     *
     * @param context
     * @param dataArrayList
     * @param gridLayoutManager
     * @param sectionStateChangeListener
     * @param isBeaconAdapter
     */
    public BeaconDeviceListExpandableGridAdapter(Context context, ArrayList<Object> dataArrayList,
                                                 final GridLayoutManager gridLayoutManager,
                                                 SectionStateChangeListener sectionStateChangeListener, boolean isBeaconAdapter) {
        mContext = context;
        mSectionStateChangeListener = sectionStateChangeListener;
        mDataArrayList = dataArrayList;
        this.isMoodAdapter = isMoodAdapter;

        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return isSection(position) ? gridLayoutManager.getSpanCount() : 1;
            }
        });
    }

    /**
     * set selection room device id
     *
     * @param room_device_id
     */
    public void setSelection(String room_device_id) {
        String[] strArray = room_device_id.split(",");
        List<String> listDeviceIds = Arrays.asList(strArray);
        for (int i = 0; i < mDataArrayList.size(); i++) {
            if (mDataArrayList.get(i) instanceof DeviceVO) {
                final DeviceVO item = (DeviceVO) mDataArrayList.get(i);
                if (listDeviceIds.contains(item.getDeviceId())) {
                    item.setSelected(true);
                }
            }
        }

    }

    /**
     * check if given number positon is room section or not
     *
     * @param position
     * @return
     */
    private boolean isSection(int position) {
        return mDataArrayList.get(position) instanceof RoomVO;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false), viewType);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        switch (holder.viewType) {
            case VIEW_TYPE_SECTION:
                holder.img_room_delete.setVisibility(View.GONE);
                /*if (position == 0) {
                    holder.view_line_top.setVisibility(View.GONE);
                } else {
                    holder.view_line_top.setVisibility(View.VISIBLE);
                }*/

                final RoomVO section = (RoomVO) mDataArrayList.get(position);
                holder.sectionTextView.setText(section.getRoomName());
                holder.text_section_on_off.setVisibility(View.GONE);
                holder.text_section_edit.setVisibility(View.GONE);
                holder.sectionToggleButton.setVisibility(View.VISIBLE);

                holder.rel_main_view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mSectionStateChangeListener.onSectionStateChanged(section, !section.isExpanded());
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
                holder.sectionToggleButton.setChecked(section.isExpanded());

                if (section.isExpanded()) {
                    holder.view_line_top.setVisibility(View.VISIBLE);
                    holder.card_layout.setBackground(mContext.getDrawable(R.drawable.background_shadow_bottom_side_mood));
                    // holder.ll_root_view_section.setBackground(mContext.getDrawable(R.drawable.background_shadow_bottom_side));
                } else {
                    holder.view_line_top.setVisibility(View.GONE);
                    holder.card_layout.setBackground(mContext.getDrawable(R.drawable.background_with_shadow_new));

                }


                break;
            case VIEW_TYPE_ITEM:
                final DeviceVO item = (DeviceVO) mDataArrayList.get(position);
                holder.iv_icon.setImageResource(Common.getIcon(0, item.getDevice_icon()));
                if (item.isSensor()) {
                    holder.itemTextView.setText(item.getSensor_name());

                } else {
                    holder.itemTextView.setText(item.getDeviceName());
                }

                /*check is device active or not*/
                if (item.getIsActive() == -1) {
                    holder.view.setAlpha(0.50f);
                } else {
                    holder.view.setAlpha(1);
                }

                if (item.isSelected()) {
                    holder.iv_icon_select.setVisibility(View.VISIBLE);
                } else {
                    holder.iv_icon_select.setVisibility(View.GONE);
                }

                String isUse = "";
                if (!TextUtils.isEmpty(item.getTo_use())) {
                    isUse = item.getTo_use();
                }

               /* holder.view.setId(position);
                holder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (item.getIsActive() != -1) {
                            ChatApplication.logDisplay("item is " + item.isSelected());
                            item.setSelected(!item.isSelected());
                            notifyItemChanged(v.getId(), item);
                            ((DeviceVO) mDataArrayList.get(v.getId())).setSelected(item.isSelected());
                        }

                    }
                });*/

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
        } else {
            return VIEW_TYPE_ITEM;
        }
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {

        //common
        View view, view_line_top;
        int viewType;
        ImageView iv_room_panel_onoff, img_room_delete, iv_icon, iv_icon_text, iv_icon_select;
        //for section
        ToggleButton sectionToggleButton;
        TextView sectionTextView, text_section_on_off, text_section_edit, itemTextView;
        LinearLayout ll_room_item, ll_root_view_section;
        RelativeLayout rel_main_view;
        LinearLayout card_layout;

        public ViewHolder(View view, int viewType) {
            super(view);
            this.viewType = viewType;
            this.view = view;

            if (viewType == VIEW_TYPE_ITEM) {
                itemTextView = view.findViewById(R.id.text_item);
                iv_icon = view.findViewById(R.id.iv_icon);
                iv_icon_text = view.findViewById(R.id.iv_icon_text);
                iv_icon_select = view.findViewById(R.id.iv_icon_select);
                ll_room_item = view.findViewById(R.id.ll_room_item);

            } else {
                card_layout = view.findViewById(R.id.card_layout);
                view_line_top = view.findViewById(R.id.view_line_top);
                sectionTextView = view.findViewById(R.id.text_section);
                ll_root_view_section = view.findViewById(R.id.ll_root_view_section);
                text_section_on_off = view.findViewById(R.id.text_section_on_off);
                text_section_edit = view.findViewById(R.id.text_section_edit);
                img_room_delete = view.findViewById(R.id.iv_room_delete);

                sectionToggleButton = view.findViewById(R.id.toggle_button);
                rel_main_view = view.findViewById(R.id.rel_view);
            }
        }
    }

    //
    public String getSelectedItemIds() {
        String roomIds = "";

        for (Object obj : mDataArrayList) {
            if (obj instanceof DeviceVO) {
                //3,4,5
                DeviceVO device = (DeviceVO) obj;
                if (device.isSelected()) {
                    roomIds += "," + device.getRoomDeviceId();
                }
            }
        }

        if (roomIds.startsWith(",")) {
            roomIds = roomIds.replaceFirst(",", "");
        }
        return roomIds;
    }


    /*
     * get SelectedItemList Devices in ArrayList
     * */
    public ArrayList<DeviceVO> getSelectedItemList() {

        ArrayList<DeviceVO> arrayList = new ArrayList<>();

        for (Object obj : mDataArrayList) {
            if (obj instanceof RoomVO) {
                RoomVO roomVO = (RoomVO) obj;
                for (int i = 0; i < roomVO.getPanelList().size(); i++) {
                    for (int j = 0; j < roomVO.getPanelList().get(i).getDeviceList().size(); j++) {
                        String sensor_type = "";
                        if (TextUtils.isEmpty(roomVO.getPanelList().get(i).getDeviceList().get(j).getSensor_type())) {
                            sensor_type = "";
                        } else {
                            sensor_type = roomVO.getPanelList().get(i).getDeviceList().get(j).getSensor_type();
                        }

                        if (sensor_type.equalsIgnoreCase("temp") || sensor_type.equalsIgnoreCase("door_sensor")) {
                        } else if (roomVO.getPanelList().get(i).getDeviceList().get(j).isSelected()) {
                            arrayList.add(roomVO.getPanelList().get(i).getDeviceList().get(j));
                        }
                        ChatApplication.logDisplay("item is selected " + roomVO.getPanelList().get(i).getDeviceList().get(j).isSelected());
                    }

                }

            }
        }

        return arrayList;
    }

}
