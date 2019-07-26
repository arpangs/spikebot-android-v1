package com.spike.bot.adapter.mood;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
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

import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.core.Common;
import com.spike.bot.customview.recycle.ItemClickListener;
import com.spike.bot.customview.recycle.SectionStateChangeListener;
import com.spike.bot.model.DeviceVO;
import com.spike.bot.model.PanelVO;
import com.spike.bot.model.RoomVO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by lenovo on 2/23/2016.
 */
public class MoodDeviceListExpandableGridAdapter extends RecyclerView.Adapter<MoodDeviceListExpandableGridAdapter.ViewHolder> {

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

    /**
     * Mood Device List Expandable Grid Adapter
     * @param context
     * @param dataArrayList
     * @param gridLayoutManager
     * @param itemClickListener
     * @param sectionStateChangeListener
     * @param isMoodAdapter
     */
    public MoodDeviceListExpandableGridAdapter(Context context, ArrayList<Object> dataArrayList,
                                               final GridLayoutManager gridLayoutManager, ItemClickListener itemClickListener,
                                               SectionStateChangeListener sectionStateChangeListener, boolean isMoodAdapter) {
        mContext = context;
        mItemClickListener = itemClickListener;
        mSectionStateChangeListener = sectionStateChangeListener;
        mDataArrayList = dataArrayList;
        this.isMoodAdapter = isMoodAdapter;

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

    /**
     * set selection room device id
     * @param room_device_id
     */
    public void setSelection(String room_device_id){
       // Log.d("setSelectionEditMode","getSpanSize setSelection room_device_id " + room_device_id );
        String[] strArray = room_device_id.split(",");
        List<String> listDeviceIds = Arrays.asList(strArray);
       // Log.d("setSelectionEditMode", listDeviceIds.size() +  " setSelection item.getDeviceId()  "   );

        for(int i=0;i<mDataArrayList.size();i++){

            if(mDataArrayList.get(i) instanceof DeviceVO){
                final DeviceVO item = (DeviceVO) mDataArrayList.get(i);
                if(listDeviceIds.contains(item.getRoomDeviceId())){
                    item.setSelected(true);
                }
            }
        }

    }

    /**
     * set selection of all devices list
     */
    public void setSelectionAllDevices(){

        for(int i=0;i<mDataArrayList.size();i++){
            if(mDataArrayList.get(i) instanceof DeviceVO){
                final DeviceVO item = (DeviceVO) mDataArrayList.get(i);
                item.setSelected(true);
            }
        }
    }

    /**
     * check if given number positon is room section or not
     * @param position
     * @return
     */
    private boolean isSection(int position) {
        return mDataArrayList.get(position) instanceof RoomVO;
    }

    /**
     * check if given number of positon is panel or not
     * @param position
     * @return
     */
    private boolean isPanel(int position) {
        return mDataArrayList.get(position) instanceof PanelVO;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false), viewType);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        switch (holder.viewType) {
            case VIEW_TYPE_SECTION :

                holder.img_room_delete.setVisibility(View.GONE);

                if(position == 0){
                    holder.view_line_top.setVisibility(View.GONE);
                }else{
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
                     //   mItemClickListener.itemClicked(section,"expandclick");
                        mSectionStateChangeListener.onSectionStateChanged(section, !section.isExpanded);
                    }
                });

                holder.sectionToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(mContext!=null){
                            Common.hideSoftKeyboard((Activity)mContext);
                        }
                        mSectionStateChangeListener.onSectionStateChanged(section, isChecked);
                    }
                });
                holder.sectionToggleButton.setChecked(section.isExpanded);

                break;
            case VIEW_TYPE_PANEL :
                /*if(position==0){
                    holder.view_line_top.setVisibility(View.GONE);
                }
                else{
                    holder.view_line_top.setVisibility(View.VISIBLE);
                }*/

                final PanelVO panel1 = (PanelVO) mDataArrayList.get(position);

                holder.sectionTextView.setText(panel1.getPanelName());
                holder.iv_room_panel_onoff.setVisibility(View.GONE);

                if(TextUtils.isEmpty(panel1.getPanelName())){
                    holder.sectionTextView.setVisibility(View.GONE);
                }else {
                    holder.sectionTextView.setVisibility(View.VISIBLE);
                }
                break;
            case VIEW_TYPE_ITEM :
                final DeviceVO item = (DeviceVO) mDataArrayList.get(position);


                if(item.isSensor() || item.getDeviceType().equalsIgnoreCase("2")){
//                    holder.itemTextView.setText(item.getSensor_name());
//                    if(item.getSensor_icon()!=null){
//                        holder.iv_icon.setImageResource(Common.getIcon(1,item.getSensor_icon()));//item.getDeviceStatus()
//                    }

                    if(TextUtils.isEmpty(item.getSensor_icon())){

                        holder.itemTextView.setText(item.getDeviceName());

                        if(item.getIsActive() == 0){
                            if(item.getDevice_icon()!=null){
                                holder.iv_icon.setImageResource(Common.getIconInActive(1,item.getDevice_icon()));//item.getDeviceStatus()
                            }
                        }else{
                            if(item.getDevice_icon()!=null){
//                                holder.iv_icon.setImageResource(Common.getIcon(1,item.getDevice_icon()));//item.getDeviceStatus()
                                holder.iv_icon.setImageResource(Common.getIcon(0,item.getDevice_icon()));//item.getDeviceStatus()
                            }
                        }
                    }else{

                        holder.itemTextView.setText(item.getSensor_name());

                        if(item.getDeviceType().equalsIgnoreCase("2") && item.getIsActive() == 0){
                            if(item.getSensor_icon()!=null){
                                holder.iv_icon.setImageResource(Common.getIconInActive(1,item.getSensor_icon()));//item.getDeviceStatus()
                            }
                        }else{
                            if(item.getSensor_icon()!=null){
                                holder.iv_icon.setImageResource(Common.getIcon(0,item.getSensor_icon()));//item.getDeviceStatus()
                            }
                        }
                    }

                }else{
                    holder.itemTextView.setText(item.getDeviceName());
                    if(item.getDevice_icon()!=null){

//                        if(item.getDeviceType().equalsIgnoreCase("-1")){
//                            holder.iv_icon.setImageResource(R.drawable.off);//item.getDeviceStatus()
//                        }else {
                            holder.iv_icon.setImageResource(Common.getIcon(0,item.getDevice_icon()));//item.getDeviceStatus()
//                        }


                    }
                }
                //holder.iv_icon.setVisibility(View.GONE);
              if(item.isSelected()){
                    if(item.isSensor()){
                        if(item.getDeviceType().equalsIgnoreCase("2")){
                            holder.iv_icon_select.setVisibility(View.VISIBLE);
                        }else {
                            holder.iv_icon_select.setVisibility(View.GONE);
                        }

                       /* if(item.getTo_use().equalsIgnoreCase("1")){
                            holder.iv_icon_select.setVisibility(View.VISIBLE);
                        }else {
                            holder.iv_icon_select.setVisibility(View.GONE);
                        }*/
                    }else {
                        holder.iv_icon_select.setVisibility(View.VISIBLE);
                    }
              }else{
                  holder.iv_icon_select.setVisibility(View.GONE);
              }

                if(item.getSensor_icon()!=null && item.getSensor_icon().equalsIgnoreCase("Remote_AC") &&
                         item.getIsActive() == 0){
                   // holder.view.setClickable(false);
                    holder.iv_icon.setImageResource(Common.getIconInActive(0,item.getSensor_type()));//item.getDeviceStatus()

                }else if(item.getSensor_icon()!=null && item.getSensor_icon().equalsIgnoreCase("Remote_AC") &&
                        item.getIsActive() == 1){
                    holder.view.setClickable(true);
                    holder.iv_icon.setImageResource(Common.getIcon(0,item.getSensor_icon()));
                }

                DeviceVO itemCopy = (DeviceVO) item.clone();

                String isUse="";
                if(!TextUtils.isEmpty(item.getTo_use())){
                    isUse=item.getTo_use();
                }

                if(isUse.equalsIgnoreCase("0")){
                    holder.iv_icon.setImageResource(Common.getIcon(0,item.getSensor_icon()));
                }

                holder.view.setId(position);
                holder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DeviceVO itemCopy = (DeviceVO) item.clone();

                        String isUse="";
                        if(!TextUtils.isEmpty(item.getTo_use())){
                            isUse=item.getTo_use();
                        }

                        if(isUse.equalsIgnoreCase("0")){
                            alertShow();
                        }else if (item.getIs_original() == 0) { //id Device room id is duplicate that's means display device get info dialog in mood
                            mItemClickListener.itemClicked(item, "disable_device", position);

                        }else {

                            if (item.getSensor_icon() != null && item.getSensor_icon().equalsIgnoreCase("Remote_AC")) {
                                if (item.getIsActive() == 0) {
                                } else {
                                    item.setSelected(!item.isSelected());
                                    notifyItemChanged(v.getId(), item);
                                }

                            }else {
                                ChatApplication.logDisplay("item is "+item.isSelected());
                                item.setSelected(!item.isSelected());
                                notifyItemChanged(v.getId(), item);
                                ((DeviceVO) mDataArrayList.get(v.getId())).setSelected(item.isSelected());
                                final DeviceVO item1 = (DeviceVO) mDataArrayList.get(v.getId());
                                ChatApplication.logDisplay("item is111 "+item.isSelected());
                                ChatApplication.logDisplay("item is11111 "+item1.isSelected());
                            }
                           /* if (item.isSelected() && item.getSensor_icon() != null && item.getSensor_icon().equalsIgnoreCase("Remote_AC")
                                    && item.getIsActive() == 0) {
                                item.setSelected(!item.isSelected());
                                notifyItemChanged(position, item);

                            } else if (item.getSensor_icon() != null && item.getSensor_icon().equalsIgnoreCase("Remote_AC")
                                    && item.getIsActive() == 1) {

                                item.setSelected(!item.isSelected());
                                notifyItemChanged(position, item);

                            } else {
                                    item.setSelected(!item.isSelected());
                                    notifyItemChanged(position, item);
                            }*/
                        }
                    }
                });

                //disable device if is_Original is 0
                if(item.getIs_original() == 0 && !item.getDeviceType().equalsIgnoreCase("2")){
                  //  holder.view.setEnabled(true);
                    holder.view.setAlpha(0.50f);
                }else{
                 //   holder.view.setEnabled(true);
                    holder.view.setAlpha(1);
                }

                    holder.iv_icon_text.setVisibility(View.GONE);
                    holder.ll_room_item.setOnClickListener(null);
               // }



                break;
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

    @Override
    public int getItemCount() {
        return mDataArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (isSection(position)){
            return VIEW_TYPE_SECTION;
        }
        else if(isPanel(position)){
            return VIEW_TYPE_PANEL;
        }
        else {
            return VIEW_TYPE_ITEM;
        }
    }
    protected static class ViewHolder extends RecyclerView.ViewHolder {

        //common
        View view;
        int viewType;
        ImageView iv_room_panel_onoff;
        View view_line_top;
        //for section
        TextView sectionTextView;
        ToggleButton sectionToggleButton;
        TextView text_section_on_off;
        TextView text_section_edit;

        ImageView img_room_delete;
                //for item
        TextView itemTextView;
        ImageView iv_icon;
        LinearLayout ll_room_item;
        ImageView iv_icon_text;
        ImageView iv_icon_select;
        RelativeLayout rel_main_view;

        public ViewHolder(View view, int viewType) {
            super(view);
            this.viewType = viewType;
            this.view = view;

            if (viewType == VIEW_TYPE_ITEM) {
                itemTextView = (TextView) view.findViewById(R.id.text_item);
                iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
                iv_icon_text = (ImageView) view.findViewById(R.id.iv_icon_text );
                iv_icon_select = (ImageView) view.findViewById(R.id.iv_icon_select );
                ll_room_item = (LinearLayout) view.findViewById(R.id.ll_room_item );

            } else if (viewType == VIEW_TYPE_PANEL) {
                view_line_top = (View) view.findViewById(R.id.view_line_top);

                itemTextView = (TextView) view.findViewById(R.id.heading);
                sectionTextView = itemTextView;
                iv_room_panel_onoff = (ImageView) view.findViewById(R.id.iv_room_panel_onoff);

            } else {
                view_line_top = (View) view.findViewById(R.id.view_line_top);
                sectionTextView = (TextView) view.findViewById(R.id.text_section);

                text_section_on_off = (TextView) view.findViewById(R.id.text_section_on_off);
                text_section_edit = (TextView) view.findViewById(R.id.text_section_edit);
                img_room_delete = (ImageView) view.findViewById(R.id.iv_room_delete);

                sectionToggleButton = (ToggleButton) view.findViewById(R.id.toggle_button_section);
                rel_main_view = (RelativeLayout) view.findViewById(R.id.rel_main_view);
            }
        }
    }
    //
    public String getSelectedItemIds(){
        String roomIds="";

        for(Object obj :mDataArrayList){
            if(obj instanceof DeviceVO){
                //3,4,5
                DeviceVO device = (DeviceVO) obj ;
                if(device.isSelected()){
                    roomIds += "," + device.getRoomDeviceId();
                }
            }
        }

        if(roomIds.startsWith(",")) {
            roomIds = roomIds.replaceFirst(",","");
        }
        return roomIds;
    }


    /*
    * get SelectedItemList Devices in ArrayList
    * */
    public ArrayList<DeviceVO> getSelectedItemList(){

//        ArrayList<DeviceVO> arrayList = new ArrayList<>();
//
//        for(Object obj :mDataArrayList){
//            if(obj instanceof DeviceVO){
////                //3,4,5
////                DeviceVO device = (DeviceVO) obj ;
////
////                String sensor_type="";
////                if(TextUtils.isEmpty(device.getSensor_type())){
////                    sensor_type="";
////                }else {
////                    sensor_type=device.getSensor_type();
////                }
////                if(sensor_type.equalsIgnoreCase("temp")){
////                }else if(sensor_type.equalsIgnoreCase("door")){
////                }else if(device.isSelected()){
////
////                    arrayList.add(device);
////                }
//            }else if(obj instanceof RoomVO){
//                RoomVO roomVO =(RoomVO)obj;
//                for(int i=0; i<roomVO.getPanelList().size(); i++){
//                    for(int j=0; j<roomVO.getPanelList().get(i).getDeviceList().size(); j++){
//                        String sensor_type="";
//                        if(TextUtils.isEmpty(roomVO.getPanelList().get(i).getDeviceList().get(j).getSensor_type())){
//                            sensor_type="";
//                        }else {
//                            sensor_type=roomVO.getPanelList().get(i).getDeviceList().get(j).getSensor_type();
//                        }
//
//                        if(sensor_type.equalsIgnoreCase("temp")){
//                        }else if(sensor_type.equalsIgnoreCase("door")){
//                        }else if(roomVO.getPanelList().get(i).getDeviceList().get(j).isSelected()){
//                            arrayList.add(roomVO.getPanelList().get(i).getDeviceList().get(j));
//                        }
//                        ChatApplication.logDisplay("item is selected "+roomVO.getPanelList().get(i).getDeviceList().get(j).isSelected());
//                    }
//
//                }
//
//            }
//        }


        ArrayList<DeviceVO> arrayList = new ArrayList<>();

        for(Object obj :mDataArrayList){
             if(obj instanceof RoomVO){
                RoomVO roomVO =(RoomVO)obj;
                for(int i=0; i<roomVO.getPanelList().size(); i++){
                    for(int j=0; j<roomVO.getPanelList().get(i).getDeviceList().size(); j++){
                        String sensor_type="";
                        if(TextUtils.isEmpty(roomVO.getPanelList().get(i).getDeviceList().get(j).getSensor_type())){
                            sensor_type="";
                        }else {
                            sensor_type=roomVO.getPanelList().get(i).getDeviceList().get(j).getSensor_type();
                        }

                        if(sensor_type.equalsIgnoreCase("temp")){
                        }else if(sensor_type.equalsIgnoreCase("door")){
                        }else if(roomVO.getPanelList().get(i).getDeviceList().get(j).isSelected()){
                            arrayList.add(roomVO.getPanelList().get(i).getDeviceList().get(j));
                        }
                        ChatApplication.logDisplay("item is selected "+roomVO.getPanelList().get(i).getDeviceList().get(j).isSelected());
                    }

                }

            }
        }

        return arrayList;
    }
}
