package com.spike.bot.adapter.mood;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.spike.bot.R;
import com.spike.bot.core.Common;
import com.spike.bot.customview.recycle.ItemClickListener;
import com.spike.bot.customview.recycle.SectionStateChangeListener;
import com.spike.bot.model.CameraVO;
import com.spike.bot.model.DeviceVO;
import com.spike.bot.model.PanelVO;
import com.spike.bot.model.RoomVO;

import java.util.ArrayList;

/**
 * Created by lenovo on 2/23/2016.
 */
public class MoodSectionedExpandableGridAdapter extends RecyclerView.Adapter<MoodSectionedExpandableGridAdapter.ViewHolder> {

    //data array
    private ArrayList<Object> mDataArrayList;

    //context
    private final Context mContext;

    //listeners
    private final ItemClickListener mItemClickListener;
    private CameraClickListener mCameraClickListener;
    private final SectionStateChangeListener mSectionStateChangeListener;

    //view type
    private static final int VIEW_TYPE_SECTION = R.layout.row_room_mood_home;
    private static final int VIEW_TYPE_PANEL = R.layout.row_room_mood_panel;
    private static final int VIEW_TYPE_ITEM = R.layout.row_room_mood_switch_item; //TODO : change this
    private static final int VIEW_TYPE_CAMERA = R.layout.row_room_mood_camera_item;

    public void setCameraClickListener(CameraClickListener mCameraClickListener) {
        this.mCameraClickListener = mCameraClickListener;
    }

    public MoodSectionedExpandableGridAdapter(Context context, ArrayList<Object> dataArrayList,
                                              final GridLayoutManager gridLayoutManager, ItemClickListener itemClickListener,
                                              SectionStateChangeListener sectionStateChangeListener) {
        mContext = context;
        mItemClickListener = itemClickListener;
        mSectionStateChangeListener = sectionStateChangeListener;
        mDataArrayList = dataArrayList;

        try{
            /*set view grid count 1 or 4 */
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {

                    //return isSection(position) ? gridLayoutManager.getSpanCount():1;
                    return isSection(position) || isPanel(position) ? gridLayoutManager.getSpanCount():1 ;
                    //return isSection(position) ? gridLayoutManager.getSpanCount():1;
                }
            });
        }catch (Exception ex){ex.printStackTrace(); }

    }
    private boolean isSection(int position) {
        try{
            return mDataArrayList.get(position) instanceof RoomVO;
        }catch (Exception ex){
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
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new ViewHolder(mView,viewType);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        switch (holder.viewType) {
            case VIEW_TYPE_SECTION :
                if(position == 0){
                    holder.view_line_top.setVisibility(View.GONE);
                }else{
                    holder.view_line_top.setVisibility(View.VISIBLE);
                }
                final RoomVO section = (RoomVO) mDataArrayList.get(position);
                holder.sectionTextView.setText(section.getRoomName());
                if(section.getRoom_status()==1 ){ //|| position%2 ==1
                    holder.text_section_on_off.setBackground(mContext.getResources().getDrawable(R.drawable.room_on));
                    holder.text_section_on_off.setTextColor(mContext.getResources().getColor(R.color.automation_white));
                }
                else{
                    holder.text_section_on_off.setBackground(mContext.getResources().getDrawable(R.drawable.room_off));
                    holder.text_section_on_off.setTextColor(mContext.getResources().getColor(R.color.sky_blue));
                }

                holder.text_section_on_off.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mItemClickListener.itemClicked(section,"onoffclick");
                    }
                });
                holder.text_section_edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mItemClickListener.itemClicked(section,"editclick");
                        //mSectionStateChangeListener.onSectionStateChanged(section, !section.isExpanded);
                    }
                });
                holder.sectionTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mItemClickListener.itemClicked(section,"expandclick");

                        mSectionStateChangeListener.onSectionStateChanged(section, !section.isExpanded);
                    }
                });

                holder.sectionToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        mSectionStateChangeListener.onSectionStateChanged(section, isChecked);
                    }
                });
                holder.sectionToggleButton.setChecked(section.isExpanded);

                if(section.getRoomId().equalsIgnoreCase("camera")){
                    holder.text_section_edit.setVisibility(View.GONE);
                    holder.text_section_on_off.setVisibility(View.GONE);
                }
                else{
                    holder.text_section_on_off.setVisibility(View.VISIBLE);

                    if(section.isExpanded){
                        holder.text_section_edit.setVisibility(View.VISIBLE);
                    }
                    else{
                        holder.text_section_edit.setVisibility(View.GONE);
                    }
                }



                break;
            case VIEW_TYPE_PANEL :
                if(position == 0){
                    holder.view_line_top.setVisibility(View.GONE);
                }else{
                    holder.view_line_top.setVisibility(View.VISIBLE);
                }

                final PanelVO panel1 = (PanelVO) mDataArrayList.get(position);
                holder.sectionTextView.setText(panel1.getPanelName());

                holder.iv_room_panel_onoff.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mItemClickListener.itemClicked(panel1,"onOffclick");
                    }
                });
                if(panel1.getPanel_status()==1 ){ //|| position%2 ==1
                    holder.iv_room_panel_onoff.setImageResource(R.drawable.room_on);
                }
                else{
                    holder.iv_room_panel_onoff.setImageResource(R.drawable.room_off);
                }

                if(panel1.getType().equalsIgnoreCase("camera")){
                    holder.iv_room_panel_onoff.setVisibility(View.INVISIBLE);
                }
                else{
                    holder.iv_room_panel_onoff.setVisibility(View.VISIBLE);
                }

                break;
            case VIEW_TYPE_ITEM :
                final DeviceVO item = (DeviceVO) mDataArrayList.get(position);
                holder.itemTextView.setText(item.getDeviceName());

                holder.iv_icon.setImageResource(Common.getIcon(item.getDeviceStatus(),item.getDevice_icon()));
                //holder.iv_icon.setVisibility(View.GONE);
                holder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                     //   DeviceVO itemCopy = (DeviceVO) item.clone();
                        mItemClickListener.itemClicked(item,"itemclick",position);

                        //change immediate.
                        //item.setDeviceStatus(item.getDeviceStatus()==0?1:0);
                        //notifyItemChanged(position);
                    }
                });
                if(Integer.parseInt(item.getDeviceId())==1 && Integer.parseInt(item.getDeviceType())==1){
                    holder.view.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            mItemClickListener.itemClicked(item,"longclick",position);
                            return true;
                        }
                    });
                }
                else{
                    holder.view.setOnLongClickListener(null);
                }
                    holder.iv_icon_text.setVisibility(View.GONE);
                    holder.ll_room_item.setOnClickListener(null);
                break;
            case VIEW_TYPE_CAMERA:
                final CameraVO cameraVO = (CameraVO) mDataArrayList.get(position);
                holder.itemTextView.setText(cameraVO.getCamera_name());

                holder.iv_icon_text.setVisibility(View.GONE);
                holder.ll_room_item.setOnClickListener(null);
                holder.iv_icon.setImageResource(Common.getIcon(1,cameraVO.getCamera_icon()));

                holder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //   DeviceVO itemCopy = (DeviceVO) item.clone();
                        if(mCameraClickListener!=null){
                            mCameraClickListener.itemClicked(cameraVO,"cameraClick");
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
        if (isSection(position)){
            return VIEW_TYPE_SECTION;
        }
        else if(isPanel(position)){
            return VIEW_TYPE_PANEL;
        }
        else if(isCamera(position)){
            return VIEW_TYPE_CAMERA;
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
        LinearLayout ll_background;
                //for item
        TextView itemTextView;
        ImageView iv_icon;
        LinearLayout ll_room_item;
        ImageView iv_icon_text;
        LinearLayout ll_root_view_section;

        public ViewHolder(View view, int viewType) {
            super(view);
            this.viewType = viewType;
            this.view = view;
            if (viewType == VIEW_TYPE_ITEM || viewType == VIEW_TYPE_CAMERA) {
                itemTextView =  view.findViewById(R.id.text_item);
                iv_icon =  view.findViewById(R.id.iv_icon);
                iv_icon_text =  view.findViewById(R.id.iv_icon_text );
                ll_room_item =  view.findViewById(R.id.ll_room_item );

            } else if (viewType == VIEW_TYPE_PANEL) {

                view_line_top =  view.findViewById(R.id.view_line_top);

                itemTextView =  view.findViewById(R.id.heading);
                sectionTextView = itemTextView;
                iv_room_panel_onoff =  view.findViewById(R.id.iv_room_panel_onoff);
                ll_background =  view.findViewById(R.id.ll_background );
            }
            else {
                view_line_top =  view.findViewById(R.id.view_line_top);
                sectionTextView =  view.findViewById(R.id.text_section);

                text_section_on_off =  view.findViewById(R.id.text_section_on_off);
                text_section_edit =  view.findViewById(R.id.text_section_edit);

                sectionToggleButton =  view.findViewById(R.id.toggle_button_section);//toggle_button_section
                ll_root_view_section =  view.findViewById(R.id.ll_root_view_section);
            }
           // this.setIsRecyclable(false);
        }
    }
    public interface CameraClickListener {
        void itemClicked(CameraVO item, String action);
    }
}
