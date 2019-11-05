package com.spike.bot.adapter;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.kp.core.ActivityHelper;
import com.kp.core.GetJsonTask;
import com.kp.core.ICallBack;
import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.core.APIConst;
import com.spike.bot.core.Common;
import com.spike.bot.core.Constants;
import com.spike.bot.customview.recycle.ItemClickMoodListener;
import com.spike.bot.customview.recycle.MoodStateChangeListener;
import com.spike.bot.listener.NotifityData;
import com.spike.bot.model.DeviceVO;
import com.spike.bot.model.PanelVO;
import com.spike.bot.model.RoomVO;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.view.View.GONE;

/**
 * Created by lenovo on 2/23/2016.
 */
public class MoodExpandableGridAdapter extends RecyclerView.Adapter<MoodExpandableGridAdapter.ViewHolder> {

    //data array
    private ArrayList<Object> mDataArrayList;

    //context
    private final Context mContext;

    //listeners
    private final ItemClickMoodListener mItemClickListener;
    private final MoodStateChangeListener mSectionStateChangeListener;
    public NotifityData notifityData;

    //view type
    private static final int VIEW_TYPE_SECTION = R.layout.row_mood_home2;
    private static final int VIEW_TYPE_PANEL = R.layout.row_mood_panel;
    private static final int VIEW_TYPE_ITEM = R.layout.row_room_switch_item_mood; //TODO : change this

    public MoodExpandableGridAdapter(Context context, ArrayList<Object> dataArrayList,
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
                return isSection(position) || isPanel(position) ? gridLayoutManager.getSpanCount():1 ;
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
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        switch (holder.viewType) {
            case VIEW_TYPE_SECTION :
                final RoomVO section = (RoomVO) mDataArrayList.get(position);
                String styledText = "<u><font color='#0098C0'>"+section.getRoomName()+"</font></u>";
                holder.sectionTextView.setText(Html.fromHtml(styledText), TextView.BufferType.SPANNABLE);

                if(section.isExpanded()){
                    holder.sectionTextView.setSingleLine(false);
                }else{
                    holder.sectionTextView.setSingleLine(true);
                }

                ChatApplication.logDisplay("status update mood room update " + section.getRoom_status());
                if (section.getRoom_status() == 1) {
                    holder.iv_icon.setImageResource(R.drawable.room_on);
                } else {
                    holder.iv_icon.setImageResource(R.drawable.room_off);
                }



                holder.sectionToggleButton.setChecked(section.isExpanded);

                if(section.isExpanded){
                    holder.iv_mood_delete.setVisibility(View.VISIBLE);
                    holder.iv_mood_edit.setVisibility(View.VISIBLE);
                }
                else{
                    holder.iv_mood_delete.setVisibility(View.GONE);
                    holder.iv_mood_edit.setVisibility(View.GONE);
                }

                holder.iv_icon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!isClickable)
                            return;
                        section.setOld_room_status(section.getRoom_status());
                        section.setRoom_status(section.getRoom_status()==0?1:0);
                        notifyItemChanged(position);
                        mItemClickListener.itemClicked(section,"onoffclick");
                    }
                });

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

                if (!Common.getPrefValue(mContext, Constants.USER_ADMIN_TYPE).equalsIgnoreCase("0")) {
                    if(section.getSmart_remote_number().length()==0){
                        holder.txtRemote.setVisibility(GONE);
                        holder.imgRemote.setVisibility(View.VISIBLE);
                    }else {
                        holder.txtRemote.setVisibility(View.VISIBLE);
                        holder.imgRemote.setVisibility(View.VISIBLE);
                        holder.txtRemote.setText(section.getSmart_remote_number());
                    }

                    holder.imgRemote.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(section.getSmart_remote_number()!=null && section.getSmart_remote_number().length()>0){
                                dialogRemoteshow(true,section);
                            }else {
                                dialogRemoteshow(false,section);
                            }
                        }
                    });

                    holder.frameRemote.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(section.getSmart_remote_number()!=null && section.getSmart_remote_number().length()>0){
                                dialogRemoteshow(true,section);
                            }else {
                                dialogRemoteshow(false,section);
                            }
                        }
                    });

                }else {
                    holder.txtRemote.setVisibility(GONE);
                    holder.imgRemote.setVisibility(GONE);
                }


                break;
            case VIEW_TYPE_PANEL :

                final PanelVO panel1 = (PanelVO) mDataArrayList.get(position);

                ChatApplication.logDisplay("status update room mood adapter panel call");
                if(position==0){
                    holder.txtLine.setVisibility(GONE);
                }else{
                    holder.txtLine.setVisibility(View.VISIBLE);
                }
                holder.itemTextView.setText(panel1.getPanelName());
                holder.iv_mood_panel_schedule_click.setVisibility(GONE);
                break;
            case VIEW_TYPE_ITEM :
                final DeviceVO item = (DeviceVO) mDataArrayList.get(position);

                holder.itemTextView.setText(item.getDeviceName());
                ChatApplication.logDisplay("status update room mood adapter device call "+item.getDeviceName());
                if(item.getDevice_icon().equalsIgnoreCase("2")){
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
                            mItemClickListener.itemClicked(item,"textclick");
                        }
                    });

                holder.iv_icon.setId(position);
                holder.iv_icon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!isClickable)
                            return;
                        //If Device icon Equal to Remote AC then open remote instead of call onOff Device item
                        if(item.getDevice_icon().equalsIgnoreCase("Remote_AC")){ //click on remote device id
                            mItemClickListener.itemClicked(item,"isIRSensorOnClick");
                        }else{
                            item.setOldStatus(item.getDeviceStatus());
                            item.setDeviceStatus(item.getDeviceStatus() == 0 ? 1:0 );
                            mItemClickListener.itemClicked(item,"itemOnOffclick");
                            notifityData.notifyData();
                        }
                    }
                });

                holder.iv_icon.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if(item.getDevice_icon().equalsIgnoreCase("Remote_AC")){ //click on remote device id
                            mItemClickListener.itemClicked(item,"isIRSensorClick");
                        }else if(item.getDevice_icon().equalsIgnoreCase("-1")){
                            mItemClickListener.itemClicked(item, "heavyloadlongClick");
                        }else if(item.getDeviceType().equalsIgnoreCase("3")){
                            mItemClickListener.itemClicked(item, "philipslongClick");
                        }
                        return false;
                    }
                });

                if(item.getDeviceType().equalsIgnoreCase("fan")){
//                    if(Integer.parseInt(item.getDeviceId()) == 1 && Integer.parseInt(item.getDeviceType()) == 1){
                        holder.iv_icon.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View view) {
                                if(item.getIs_locked()==1){
                                    ChatApplication.showToast(mContext,mContext.getResources().getString(R.string.fan_error));
                                }else {
                                    mItemClickListener.itemClicked(item, "longclick");
                                }
                                return true;
                            }
                        });
//                    }else{
//                        holder.iv_icon.setOnLongClickListener(null);
//                    }
                }else {
                    holder.iv_icon.setOnLongClickListener(null);
                }

                if(item.getDevice_icon().equalsIgnoreCase("Remote_AC")){
                    holder.imgLongClick.setVisibility(View.VISIBLE);
                }else if(item.getDeviceType().equalsIgnoreCase("fan") ||item.getDeviceType().equalsIgnoreCase("-1") || item.getDeviceType().equalsIgnoreCase("3")){
                    holder.imgLongClick.setVisibility(View.VISIBLE);
                }else {
                    if(item.getDeviceType().equalsIgnoreCase("1")){
                        if(Integer.parseInt(item.getDeviceId()) == 1 && Integer.parseInt(item.getDeviceType()) == 1){
                            holder.imgLongClick.setVisibility(View.VISIBLE);
                        }else{
                            holder.imgLongClick.setVisibility(GONE);
                        }
                    }else {
                        holder.imgLongClick.setVisibility(View.GONE);
                    }
                }

                if(item.getDevice_icon().equalsIgnoreCase("Remote_AC")){
                    holder.iv_icon_text.setVisibility(View.GONE);
                } else {
                    holder.iv_icon_text.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    private void dialogRemoteshow(final boolean isFlag, final RoomVO section) {
        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.dialog_remote_key);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);

        final AppCompatEditText editKeyValue =  dialog.findViewById(R.id.editKeyValue);
        Button btnSubmit =  dialog.findViewById(R.id.btnSubmit);
        Button btnCancel =  dialog.findViewById(R.id.btnCancel);
        ImageView iv_close =  dialog.findViewById(R.id.iv_close);
        TextView txtTitalMood =  dialog.findViewById(R.id.txtTitalMood);

        txtTitalMood.setText("Assign Number");
        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                ChatApplication.keyBoardHideForce(mContext);
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        if(section.getSmart_remote_number()!=null){
            editKeyValue.setText(section.getSmart_remote_number());
            editKeyValue.setSelection(editKeyValue.getText().length());
        }else {
            editKeyValue.setText("");
        }

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                ChatApplication.keyBoardHideForce(mContext);
                if(isFlag){
                    callSmartRemote(true,editKeyValue.getText().toString(),section.getRoomId(),section);
                }else {
                    if(editKeyValue.getText().toString().length()==0){
                        ChatApplication.showToast(mContext,"Please enter value");
                    }else {
                        callSmartRemote(false,editKeyValue.getText().toString(),section.getRoomId(),section);
                    }
                }

            }
        });


        if (!dialog.isShowing()) {
            dialog.show();
        }

    }

    private void callSmartRemote(boolean b, final String value, String module_id, final RoomVO section) {
        if(!ActivityHelper.isConnectingToInternet(mContext)){
            Toast.makeText(mContext, R.string.disconnect , Toast.LENGTH_SHORT).show();
            return;
        }
        ActivityHelper.showProgressDialog(mContext,"Please wait...",false);

        JSONObject obj = new JSONObject();
        try {

            obj.put("room_id", module_id);
            obj.put("user_id", Common.getPrefValue(mContext, Constants.USER_ID) );
            obj.put("smart_remote_no",""+value);
            obj.put(APIConst.PHONE_ID_KEY, APIConst.PHONE_ID_VALUE);
            obj.put(APIConst.PHONE_TYPE_KEY,APIConst.PHONE_TYPE_VALUE);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url =  ChatApplication.url + Constants.moodsmartremote;
        new GetJsonTask(mContext,url ,"POST",obj.toString(), new ICallBack() { //Constants.CHAT_SERVER_URL
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    //{"code":200,"message":"success"}
                    int code = result.getInt("code");
                    String message = result.getString("message");

                    if(code==200){
                        section.setSmart_remote_number(value);
                        if(!TextUtils.isEmpty(message)){
                            ChatApplication.showToast(mContext,message);
                        }
                        notifyDataSetChanged();

                        ActivityHelper.dismissProgressDialog();
                    }
                    else{
                        ChatApplication.showToast(mContext,message);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                finally {
                    ActivityHelper.dismissProgressDialog();

                }

            }
            @Override
            public void onFailure(Throwable throwable, String error) {
                ActivityHelper.dismissProgressDialog();
            }
        }).execute();
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
        View view,ll_top_section,vi_test;
        int viewType;
        ImageView iv_mood_panel_schedule_click,view_line_top,imgRemote,imgLog,icnSchedule,iv_mood_delete,iv_mood_edit,iv_icon_text,imgLongClick,iv_icon;

        //for section
        TextView sectionTextView,txtLine,text_section_on_off,txtRemote,text_section_edit,txtTotalDevices,itemTextView;
        ToggleButton sectionToggleButton;
        LinearLayout ll_room_item;
        RelativeLayout view_rel;
        FrameLayout frameRemote;

        public ViewHolder(View view, int viewType) {
            super(view);
            this.viewType = viewType;
            this.view = view;
            if (viewType == VIEW_TYPE_ITEM) {
                itemTextView =  view.findViewById(R.id.text_item);
                iv_icon =  view.findViewById(R.id.iv_icon);
                iv_icon_text =  view.findViewById(R.id.iv_icon_text );
                ll_room_item =  view.findViewById(R.id.ll_room_item );
                view_rel =  view.findViewById(R.id.view_rel);
                vi_test = view.findViewById(R.id.vi_test);
                imgLongClick = view.findViewById(R.id.imgLongClick);

            } else if (viewType == VIEW_TYPE_PANEL) {
                itemTextView =  view.findViewById(R.id.heading);
                txtLine =  view.findViewById(R.id.txtLine);
                sectionTextView = itemTextView;
                iv_mood_panel_schedule_click =  view.findViewById(R.id.iv_mood_panel_schedule_click);
            } else {
                view_line_top =  view.findViewById(R.id.view_line_top);
                sectionTextView =  view.findViewById(R.id.text_section);
                iv_icon =  view.findViewById(R.id.iv_icon);

                text_section_on_off =  view.findViewById(R.id.text_section_on_off);
                text_section_edit =  view.findViewById(R.id.text_section_edit);
                iv_mood_edit =  view.findViewById(R.id.iv_mood_edit);
                iv_mood_delete =  view.findViewById(R.id.iv_mood_delete);
                sectionToggleButton =  view.findViewById(R.id.toggle_button_section);
                ll_top_section = view.findViewById(R.id.ll_top_section);

                imgLog =  view.findViewById(R.id.img_icn_log);
                icnSchedule =  view.findViewById(R.id.icn_schedule_v2);
                txtTotalDevices =  view.findViewById(R.id.txt_total_devices);
                txtRemote = view.findViewById(R.id.txtRemote);
                imgRemote = view.findViewById(R.id.imgRemote);
                frameRemote = view.findViewById(R.id.frameRemote);
            }
        }
    }
}
