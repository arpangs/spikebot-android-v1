package com.spike.bot.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.spike.bot.R;
import com.spike.bot.model.RoomVO;

import java.util.List;

public class MoodListArrayAdapter extends ArrayAdapter<RoomVO> {

    int groupid;
    Activity context;
    List<RoomVO> list;
    LayoutInflater inflater;
    String mRoomName;

    public MoodListArrayAdapter(Activity context, int groupid, int id, List<RoomVO> list, String mRoomName){
        super(context,id,list);
        this.list = list;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.groupid = groupid;
        this.mRoomName = mRoomName;
        roomName = mRoomName;
    }

    public View getView(int position, View convertView, ViewGroup parent ){

        View itemView = inflater.inflate(groupid,parent,false);

        RoomVO roomVO = list.get(position);

        LinearLayout spinner_view_top = (LinearLayout)itemView.findViewById(R.id.spinner_view_top);
        TextView spinner_txt_top = (TextView) itemView.findViewById(R.id.spinner_txt_top);

        ImageView imageView = (ImageView)itemView.findViewById(R.id.txt_spinner_image);
        TextView textView=(TextView)itemView.findViewById(R.id.txt_spinner_title);

        if(getRoomName().equalsIgnoreCase(roomVO.getRoomName())){
            imageView.setVisibility(View.GONE);
        }else{
            imageView.setVisibility(View.GONE);
        }
        if(position == 0){
            spinner_txt_top.setText(list.get(position).getRoomName());
            spinner_view_top.setVisibility(View.VISIBLE);
            textView.setVisibility(View.INVISIBLE);
        }else{
            spinner_view_top.setVisibility(View.GONE);
            textView.setText(list.get(position).getRoomName());
            textView.setVisibility(View.VISIBLE);
        }



        return itemView;
    }
    public String roomName = "";

    public String getRoomName() {
        return mRoomName;
    }

    public void setRoomName(String mRoomName) {
        this.mRoomName = mRoomName;
    }

    public View getDropDownView(int position, View convertView, ViewGroup parent){
        /*TextView txt = new TextView(parent.getContext());
        txt.setGravity(Gravity.CENTER);
        txt.setPadding(16, 16, 16, 16);
        txt.setTextSize(16);
        txt.setText("Title");
        txt.setTextColor(Color.parseColor("#000000"));
        return  txt;*/
        return getView(position,convertView,parent);

    }

}