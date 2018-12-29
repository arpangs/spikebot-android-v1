package com.spike.bot.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.spike.bot.R;
import com.spike.bot.model.RoomVO;

import java.util.List;

public class RoomListArrayAdapter extends ArrayAdapter<RoomVO> {

    int groupid;
    Activity context;
    List<RoomVO> list;
    LayoutInflater inflater;
    String mRoomName;

    public RoomListArrayAdapter(Activity context, int groupid, int id, List<RoomVO> list,String mRoomName){
        super(context,id,list);
        this.list=list;
        inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.groupid=groupid;
        this.mRoomName = mRoomName;
        roomName = mRoomName;
    }

    public View getView(int position, View convertView, ViewGroup parent ){

        View itemView = inflater.inflate(groupid,parent,false);

        RoomVO roomVO = list.get(position);

        ImageView imageView = (ImageView)itemView.findViewById(R.id.txt_spinner_image);
        if(getRoomName().equalsIgnoreCase(roomVO.getRoomName())){
            imageView.setVisibility(View.GONE);
        }else{
            imageView.setVisibility(View.GONE);
        }

        TextView textView=(TextView)itemView.findViewById(R.id.txt_spinner_title);
        textView.setText(list.get(position).getRoomName());


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