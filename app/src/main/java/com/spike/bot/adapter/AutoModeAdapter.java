package com.spike.bot.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.spike.bot.R;
import com.spike.bot.core.Common;
import com.spike.bot.listener.AutoModeClickListener;
import com.spike.bot.model.AutoModeVO;

import java.util.ArrayList;

/**
 * Created by kaushal on 27/12/17.
 */

public class AutoModeAdapter extends RecyclerView.Adapter<AutoModeAdapter.ViewHolder> {

    Activity mContext;
    ArrayList<AutoModeVO> autoModArrayList ;
    AutoModeClickListener autoModeClickListener;

    public AutoModeAdapter(Activity context, ArrayList<AutoModeVO> autoModArrayList ,AutoModeClickListener autoModeClickListener) {
        this.autoModArrayList = autoModArrayList;
        this.mContext =context;
        this.autoModeClickListener = autoModeClickListener;
        Log.d("", "AutoModeAdapter AutoModeAdapter "  );
    }
    @Override
    public int getItemViewType(int position) {

        return R.layout.row_room_schedule_auto_mode;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        /*View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_room_home, parent, false);
        //view.setOnClickListener(MainActivity.myOnClickListener);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;*/
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        /*int width = parent.getMeasuredWidth() / 5;
        view.setMinimumHeight(width);*/

        /*int width =  parent.getMeasuredWidth();
        //float height = (float) width / Config.ASPECT_RATIO;//(Width/Height)
        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) view.getLayoutParams();
        params.width = (int) (width / Constants.CAMERA_NUMBER);
        Log.d("","width = " + width );
        view.setLayoutParams(params);*/
        //return new MyViewHolder(itemView);
        return new ViewHolder(view , viewType);
    }
    String url;
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int listPosition) {
        Log.d("", "AutoModeAdapter onBindViewHolder " +listPosition  );
      /*  TextView textViewName = holder.textViewName;
        // TextView textViewVersion = holder.textViewVersion;
        ImageView imageView = holder.imageViewIcon;

        textViewName.setText(cameraArray.get(listPosition).getName());
        //textViewVersion.setText(dataSet.get(listPosition).getVersion());
        imageView.setImageResource(cameraArray.get(listPosition).getImage());*/



        final AutoModeVO autoModeVO = autoModArrayList.get(listPosition);

        if(listPosition==0){
            holder.view_header.setVisibility(View.GONE);
        }

        Log.d(""," setOnClickListener autoModeVO.getAuto_on_off_timer = " + autoModeVO.getAuto_on_off_timer() );
        //   int status = obj.getInt("auto_off_status");
        String array[] = autoModeVO.getAuto_on_off_timer().split(":");
        boolean isHour = true;
        String value = "";
        if(Integer.parseInt(array[0])==0){
            isHour = false;
            value = array[1];
        }
        else{
            value = array[0];
            isHour = true;
        }
        holder.tv_auto_mode_on_off.setText(value + (isHour ?" Hrs" :" Min"));
        holder.tv_auto_mode_name.setText((autoModeVO.getAuto_on_off_status()==1 ? "Turn on ":"Turn off " )+"after "+  (isHour ?" Hrs" :" Min") + " ");
        Common.setBackground(mContext,holder.tv_auto_mode_on_off,autoModeVO.getIs_active()==1?true:false);
            /*"auto_off_id": "1514293613379_SyruKT1Xf",
                    "auto_off_timer": "5:00",
                    "auto_off_status": 0,
                    "is_active": 1*/
            //holder.itemTextView.setBackgroundColor(mContext.getColor(android.R.color.holo_green_light));


        holder.iv_auto_mode_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // mItemClickListener.itemClicked(item);
                Log.d(""," setOnClickListener iv_auto_mode_delete = " );
                autoModeClickListener.itemClicked(autoModeVO,"delete");
            }
        });
        holder.tv_auto_mode_on_off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // mItemClickListener.itemClicked(item);
                autoModeVO.setIs_active(autoModeVO.getIs_active()==1?0:1);
                Log.d(""," setOnClickListener tv_auto_mode_on_off = " );
                autoModeClickListener.itemClicked(autoModeVO,"active");
            }
        });

    }

    @Override
    public int getItemCount() {
        return autoModArrayList.size();
    }
    protected static class ViewHolder extends RecyclerView.ViewHolder {
        //common
        View view;
        int viewType;
        View view_header;
        TextView tv_auto_mode_on_off,tv_auto_mode_name;
        ImageView iv_auto_mode_delete;
        public ViewHolder(View view, int viewType) {
            super(view);
            this.viewType = viewType;
            this.view = view;
            view_header = (View) view.findViewById(R.id.view_header );
            tv_auto_mode_on_off = (TextView) view.findViewById(R.id.tv_auto_mode_on_off );
            tv_auto_mode_name = (TextView) view.findViewById(R.id.tv_auto_mode_name );
            iv_auto_mode_delete = (ImageView) view.findViewById(R.id.iv_auto_mode_delete);
          //  itemTextView.setLayoutParams(new LinearLayout.LayoutParams(mContext));
        }
    }
}