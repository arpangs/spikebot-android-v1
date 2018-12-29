package com.spike.bot.adapter.room;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.spike.bot.R;
import com.spike.bot.core.Constants;
import com.spike.bot.customview.recycle.ItemClickRoomEditListener;
import com.spike.bot.model.DeviceVO;
import com.spike.bot.model.PanelVO;
import com.spike.bot.model.RoomVO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by Sagar on 13/4/18.
 * Gmail : jethvasagar2@gmail.com
 */

public class RoomEditAdapterV2 extends RecyclerView.Adapter<RoomEditAdapterV2.EditViewHolder> implements ItemClickRoomEditListener{

    private ArrayList<PanelVO> panelVOs;
    private ItemClickRoomEditListener mItemClickListener;
    private HashMap<String, String> txtValueMap = new HashMap<String, String>();

    private Context context;
    private Activity activity;
    public RoomEditAdapterV2(ArrayList<PanelVO> panelVOs, ItemClickRoomEditListener itemClickRoomEditListener,Activity activity){
        this.panelVOs = panelVOs;
        this.mItemClickListener = itemClickRoomEditListener;
        this.activity = activity;
    }

    @Override
    public EditViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_room_edit_panel_v2,parent,false);
        this.context = view.getContext();
        return new EditViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final EditViewHolder holder, final int position) {
        holder.setIsRecyclable(false);
        final PanelVO item1 = (PanelVO) panelVOs.get(position);

        holder.iv_room_panel_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mItemClickListener.itemClicked(item1,"edit",view);
            }
        });
        holder.iv_room_panel_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mItemClickListener.itemClicked(item1,"delete",view);
            }
        });
        holder.iv_room_panel_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mItemClickListener.itemClicked(item1,"info",view);
            }
        });

        //Hide sensor edit option if found panel is sensor
        if(item1.isSensorPanel()){
            holder.iv_room_panel_add.setVisibility(View.INVISIBLE);
        }else {
            holder.iv_room_panel_add.setVisibility(View.VISIBLE);
        }

        holder.et_panel.setTag(item1.getPanelId());

        holder.et_panel.setText( item1.getPanelName() );
       // holder.sectionTextView.setText(item1.getPanelName()); //todo comment here

        txtValueMap.put(item1.getPanelId(),holder.et_panel.getText().toString());

        holder.et_panel.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN|WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if(panelVOs.get(position) != null){

                    txtValueMap.put(item1.getPanelId(),holder.et_panel.getText().toString());
                    final PanelVO item1 = (PanelVO) panelVOs.get(position);

                    Iterator myVeryOwnIterator = txtValueMap.keySet().iterator();
                    while(myVeryOwnIterator.hasNext()) {

                        String key = (String)myVeryOwnIterator.next();
                        String value = (String) txtValueMap.get(key);

                        if(key.equalsIgnoreCase(item1.getPanelId())){

                            item1.setPanelName(holder.et_panel.getText().toString());
                        }
                    }

                }

                if(holder.et_panel.getText().length() == 0){
                    holder.et_panel.setError("Enter Panel Name");
                    holder.et_panel.requestFocus();
                    return;
                }
            }
        });

        holder.roomEditAdapterDeviceV2 = new RoomEditAdapterDeviceV2(panelVOs.get(position).getDeviceList(),this);
        holder.list_edit_device.setAdapter(holder.roomEditAdapterDeviceV2);

    }
    private int isChanges = 0;

    @Override
    public int getItemCount() {
        return panelVOs.size();
    }

    @Override
    public void itemClicked(RoomVO item, String action, View view) {
        mItemClickListener.itemClicked(item,action,view);
    }

    @Override
    public void itemClicked(PanelVO panelVO, String action, View view) {
        mItemClickListener.itemClicked(panelVO,action,view);
    }

    @Override
    public void itemClicked(DeviceVO section, String action, View view) {
        mItemClickListener.itemClicked(section,action,view);
    }

    public class EditViewHolder extends RecyclerView.ViewHolder{

        ImageView iv_room_panel_add,iv_room_panel_delete,iv_room_panel_info ;
        EditText et_panel;
        View view_panel_line;
        ImageView img_room_delete;
        RecyclerView list_edit_device;
        RoomEditAdapterDeviceV2 roomEditAdapterDeviceV2;

        public EditViewHolder(View view) {
            super(view);

            view_panel_line = (View) view.findViewById(R.id.view_panel_line );
          //  sectionTextView = (TextView) view.findViewById(R.id.text_section);
            iv_room_panel_add = (ImageView) view.findViewById(R.id.iv_room_panel_add);
            iv_room_panel_delete = (ImageView) view.findViewById(R.id.iv_room_panel_delete );
            iv_room_panel_info = (ImageView) view.findViewById(R.id.iv_room_panel_info  );
            et_panel = (EditText) view.findViewById(R.id.et_panel);
            et_panel.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            list_edit_device = (RecyclerView) view.findViewById(R.id.list_edit_device);
            list_edit_device.setLayoutManager(new GridLayoutManager(context,Constants.SWITCH_NUMBER));
        }
    }

    public void getPanelEditValue(){

        if(txtValueMap !=null){
            Iterator myVeryOwnIterator = txtValueMap.keySet().iterator();
            while(myVeryOwnIterator.hasNext()) {
                String key=(String)myVeryOwnIterator.next();
                String value=(String) txtValueMap.get(key);
                for(int i=0;i<panelVOs.size();i++){
                    if(panelVOs.get(i) != null){
                        if(((PanelVO) panelVOs.get(i)).getPanelId().equalsIgnoreCase(key)){
                            ((PanelVO) panelVOs.get(i)).setPanelName(value);
                            notifyItemChanged(i,panelVOs.get(i));
                        }
                    }
                }
            }
        }

    }
    public HashMap<String,String> getTextValues(){
        return txtValueMap;
    }
    public ArrayList<PanelVO> getDataArrayList(){
        return panelVOs;
    }
}
