package com.spike.bot.adapter;

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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.spike.bot.R;
import com.spike.bot.core.Common;
import com.spike.bot.customview.recycle.ItemClickRoomEditListener;
import com.spike.bot.model.DeviceVO;
import com.spike.bot.model.PanelVO;
import com.spike.bot.model.RoomVO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by lenovo on 2/23/2016.
 */
public class RoomEditGridAdapter extends RecyclerView.Adapter<RoomEditGridAdapter.ViewHolder> {

    private HashMap<String, String> txtValueMap = new HashMap<String, String>();

    //data array
    private ArrayList<Object> mDataArrayList;

    //context
    private final Context mContext;

    //listeners
    private final ItemClickRoomEditListener mItemClickListener;
    //view type
    private static final int VIEW_TYPE_SECTION = R.layout.row_room_home;
    private static final int VIEW_TYPE_PANEL = R.layout.row_room_edit_panel;
    private static final int VIEW_TYPE_ITEM = R.layout.row_room_switch_item;

    public RoomEditGridAdapter(Context context , final GridLayoutManager gridLayoutManager, ItemClickRoomEditListener itemClickListener ) {
        mContext = context;
        mItemClickListener = itemClickListener;
        mDataArrayList = new ArrayList<>();

        etValArr = new String[mDataArrayList.size()];

        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                //return isSection(position)?gridLayoutManager.getSpanCount():1;
//                Log.d("","getSpanSize position     " + position);
//                Log.d("","getSpanSize getSpanCount " + gridLayoutManager.getSpanCount());
                return isSection(position) || isPanel(position) ? gridLayoutManager.getSpanCount():1 ;
                //return isSection(position)?gridLayoutManager.getSpanCount():1;
            }
        });
    }
    public void generateDataList (RoomVO roomVO) {
        mDataArrayList.clear();

        for(PanelVO panel:roomVO.getPanelList()){
            mDataArrayList.add(panel);
            mDataArrayList.addAll(panel.getDeviceList());
        }
    }
    public ArrayList<PanelVO> getPanelArray() {
        ArrayList<PanelVO> panelVOs = new ArrayList<>();
        for (Object obj : mDataArrayList) {
            if (obj instanceof PanelVO) {
                panelVOs.add((PanelVO) obj);
            }
        }

        return panelVOs;
    }

    private boolean isSection(int position) {
        return mDataArrayList.get(position) instanceof RoomVO;
    }
    private boolean isPanel(int position) {
        return mDataArrayList.get(position) instanceof PanelVO;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //return new ViewHolder(LayoutInflater.from(mContext).inflate(viewType, parent, false),viewType,new GenericTextWatcher());
       // ViewHolder viewHolder = new ViewHolder(LayoutInflater.from(mContext).inflate(viewType, parent, false),viewType,new GenericTextWatcher());

        return new ViewHolder(LayoutInflater.from(mContext).inflate(viewType, parent, false),viewType/*,new GenericTextWatcher()*/);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.setIsRecyclable(false);
        switch (holder.viewType) {
            case VIEW_TYPE_SECTION :
                final RoomVO section = (RoomVO) mDataArrayList.get(position);
                holder.sectionTextView.setText(section.getRoomName());
                holder.img_room_delete.setVisibility(View.GONE);
                break;
            case VIEW_TYPE_PANEL :
                if(position==0){
                    holder.view_panel_line.setVisibility(View.GONE);
                }else{
                    holder.view_panel_line.setVisibility(View.VISIBLE);
                }
                final PanelVO item1 = (PanelVO) mDataArrayList.get(position);

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

               // holder.et_panel.addTextChangedListener(myWatcher);
               // holder.myCustomEditTextListener.updatePosition(position);

                holder.et_panel.setTag(item1.getPanelId());

                holder.et_panel.setText( item1.getPanelName() );
                holder.sectionTextView.setText(item1.getPanelName());

                txtValueMap.put(item1.getPanelId(),holder.et_panel.getText().toString());

                holder.et_panel.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                        if(mDataArrayList.get(position) instanceof PanelVO){

                            txtValueMap.put(item1.getPanelId(),holder.et_panel.getText().toString());
                            final PanelVO item1 = (PanelVO) mDataArrayList.get(position);

                            Iterator myVeryOwnIterator = txtValueMap.keySet().iterator();
                            while(myVeryOwnIterator.hasNext()) {

                                String key = (String)myVeryOwnIterator.next();
                                String value = (String) txtValueMap.get(key);

                                if(key.equalsIgnoreCase(item1.getPanelId())){

                                    item1.setPanelName(holder.et_panel.getText().toString());
                                }
                            }

                        }
                    }
                });


                break;
            case VIEW_TYPE_ITEM :
                    final DeviceVO item = (DeviceVO) mDataArrayList.get(position);

                     if(!item.isSensor()){

                         holder.itemTextView.setText(item.getDeviceName());

                         holder.iv_icon.setImageResource(Common.getIcon(0,item.getDevice_icon())); //all icon grey
                         //holder.iv_icon.setVisibility(View.GONE);
                         holder.view.setOnClickListener(new View.OnClickListener() {
                             @Override
                             public void onClick(View view) {
                                 mItemClickListener.itemClicked(item,"1",view);
                             }
                         });

                         holder.iv_icon_text.setVisibility(View.GONE);

                         holder.iv_icon.setOnClickListener(new View.OnClickListener() {
                             @Override
                             public void onClick(View view) {
                                 mItemClickListener.itemClicked(item,"1",view);
                             }
                         });

                         holder.ll_room_item.setOnClickListener(new View.OnClickListener() {
                             @Override
                             public void onClick(View view) {
                                 mItemClickListener.itemClicked(item,"1",view);
                             }
                         });
                    }else{
                         //Door or Temperature sensor device list

                     }

                break;
        }
    }
    String[] etValArr;

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
    private class MyCustomEditTextListener implements TextWatcher {
        private int position;

        public void updatePosition(int position) {
            this.position = position;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            // no op
           // Log.d("roomEditGridAdapter",position +"afterTextChanged beforeTextChanged " +charSequence.toString());

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            //mDataset[position] = charSequence.toString();

        }

        @Override
        public void afterTextChanged(Editable editable) {
            // no op
            final PanelVO item1 = (PanelVO) mDataArrayList.get(position);
            item1.setPanelName(editable.toString());
           // getPanelEditValue();

        }
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {

        //common
        View view;
        int viewType;
        //for section
        TextView sectionTextView;
        ToggleButton sectionToggleButton;
        TextView text_section_on_off;
        TextView text_section_edit;
        MyCustomEditTextListener myCustomEditTextListener;
        // for panel
        ImageView iv_room_panel_add,iv_room_panel_delete,iv_room_panel_info ;
        EditText et_panel;
        View view_panel_line;
        ImageView img_room_delete;
        //for item
        TextView itemTextView;
        ImageView iv_icon;
        LinearLayout ll_room_item;
        ImageView iv_icon_text;
        GenericTextWatcher genericTextWatcher;
        public ViewHolder(View view, int viewType/*,GenericTextWatcher genric*/) {
            super(view);
            this.viewType = viewType;
            this.view = view;
            this.myCustomEditTextListener = myCustomEditTextListener;

            if (viewType == VIEW_TYPE_ITEM) {
                itemTextView = (TextView) view.findViewById(R.id.text_item);
                iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
                iv_icon_text = (ImageView) view.findViewById(R.id.iv_icon_text );
                ll_room_item = (LinearLayout) view.findViewById(R.id.ll_room_item );

            } else if (viewType == VIEW_TYPE_PANEL) {
                view_panel_line = (View) view.findViewById(R.id.view_panel_line );
                itemTextView = (TextView) view.findViewById(R.id.heading);
                sectionTextView = itemTextView;

                iv_room_panel_add = (ImageView) view.findViewById(R.id.iv_room_panel_add);
                iv_room_panel_delete = (ImageView) view.findViewById(R.id.iv_room_panel_delete );
                iv_room_panel_info = (ImageView) view.findViewById(R.id.iv_room_panel_info  );
                et_panel = (EditText) view.findViewById(R.id.et_panel );

                et_panel.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);

              //  genericTextWatcher = genric;
             //   et_panel.addTextChangedListener(genericTextWatcher);
             //   genericTextWatcher.updatePosition(-1);

                // et_panel.addTextChangedListener(this.myCustomEditTextListener);
               // et_panel.addTextChangedListener(myWatcher);
              //  iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
            }
            else {
                sectionTextView = (TextView) view.findViewById(R.id.text_section);

                text_section_on_off = (TextView) view.findViewById(R.id.text_section_on_off);
                text_section_edit = (TextView) view.findViewById(R.id.text_section_edit);
                img_room_delete = (ImageView) view.findViewById(R.id.iv_room_delete);
                sectionToggleButton = (ToggleButton) view.findViewById(R.id.toggle_button_section);
            }
        }
    }
    private class GenericTextWatcher implements TextWatcher{

        private View view;
        private int pos = -1;
        private String panelId;

        public GenericTextWatcher(){

        }

        private GenericTextWatcher(View view, int position, String panelId) {
            this.view = view;
            this.pos = position;
            this.panelId = panelId;
        }
        public void updatePosition(int position , ViewHolder vw) {
            this.pos = position;

        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            if(pos!=-1){

                if(mDataArrayList.get(pos) instanceof PanelVO){

                    String text = charSequence.toString();
                    //save the value for the given tag :
                    txtValueMap.put(""+panelId, text);

                    final PanelVO item1 = (PanelVO) mDataArrayList.get(pos);
                    item1.setPanelName(text);
                }
            }

        }

        public void afterTextChanged(Editable editable) {
            // notifyItemChanged(pos,item1);
        }
    }


    public void getPanelEditValue(){

        if(txtValueMap !=null){
            Iterator myVeryOwnIterator = txtValueMap.keySet().iterator();
            while(myVeryOwnIterator.hasNext()) {
                String key=(String)myVeryOwnIterator.next();
                String value=(String) txtValueMap.get(key);
                for(int i=0;i<mDataArrayList.size();i++){
                    if(mDataArrayList.get(i) instanceof PanelVO){
                        if(((PanelVO) mDataArrayList.get(i)).getPanelId().equalsIgnoreCase(key)){
                            ((PanelVO) mDataArrayList.get(i)).setPanelName(value);
                            notifyItemChanged(i,mDataArrayList.get(i));
                        }
                    }
                }
            }
        }

    }
    public HashMap<String,String> getTextValues(){
        return txtValueMap;
    }
    public ArrayList<Object> getDataArrayList(){
        return mDataArrayList;
    }

    public void getValueFromView(RecyclerView recyclerView){
        for(int i=0;i<mDataArrayList.size();i++){
            //View v = recyclerView.getLayoutManager().findViewByPosition(i);
            //EditText et_panel = (EditText) v.findViewById(R.id.et_panel);
            //view.et_panel
            if(mDataArrayList.get(i) instanceof PanelVO){
                PanelVO panelvo = (PanelVO) mDataArrayList.get(i);


                ViewHolder view = (ViewHolder)recyclerView.findViewHolderForAdapterPosition(i);
                if(view==null){
                    return;
                }
                String text = view.et_panel.getText().toString();
                panelvo.setPanelName(text);
                //notifyItemChanged(0);
            }


        }
        notifyDataSetChanged();
    }

}

