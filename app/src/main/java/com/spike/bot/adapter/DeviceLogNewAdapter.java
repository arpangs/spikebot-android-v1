package com.spike.bot.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.spike.bot.R;
import com.spike.bot.core.Constants;
import com.spike.bot.model.DeviceLog;

import java.util.List;

public class DeviceLogNewAdapter extends RecyclerView.Adapter<DeviceLogNewAdapter.ViewHolder> {

    Activity mContext;
    List<DeviceLog> deviceLogs;
    public boolean isEOR = false;
    String dateTime="",strDateOfTime;
    String[] strDateOfTimeTemp,actionList;
    DeviceLog deviceLog;

    public void setEOR(boolean isEOR){
        this.isEOR = isEOR;
    }


    public DeviceLogNewAdapter(Activity context, List<DeviceLog> deviceLogList) {

        this.mContext   = context;
        this.deviceLogs = deviceLogList;
    }
    @Override
    public int getItemViewType(int position) {
        return R.layout.row_device_log_new;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new ViewHolder(view , viewType);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int listPosition) {

        deviceLog = deviceLogs.get(listPosition);

        try{


            if(!TextUtils.isEmpty(deviceLog.getActivity_time())){

                String dateString = Constants.logConverterDate(Long.parseLong(deviceLog.getActivity_time()));

                strDateOfTimeTemp  =dateString.split(" ");

                if(strDateOfTimeTemp[0].equalsIgnoreCase(Constants.getCurrentDate())){
                    dateTime = strDateOfTimeTemp[1]+" "+strDateOfTimeTemp[2];
                }else {
                    dateTime = strDateOfTimeTemp[1]+ strDateOfTimeTemp[2]+ System.getProperty("line.separator") + strDateOfTimeTemp[0];
                }

                holder.tv_device_log_date.setText(dateTime);
                holder.tv_device_log_time.setText(dateTime);
            }

            if(deviceLog.getIs_unread().equalsIgnoreCase("1")){
                holder.tv_device_log_date.setTextColor(mContext.getResources().getColor(R.color.automation_red));
                holder.tv_device_log_time.setTextColor(mContext.getResources().getColor(R.color.automation_red));
                holder.tv_device_name.setTextColor(mContext.getResources().getColor(R.color.automation_red));
                holder.tv_device_description.setTextColor(mContext.getResources().getColor(R.color.automation_red));
                holder.tv_room_name.setTextColor(mContext.getResources().getColor(R.color.automation_red));
                holder.tv_panel_name.setTextColor(mContext.getResources().getColor(R.color.automation_red));
                holder.tv_separator.setTextColor(mContext.getResources().getColor(R.color.automation_red));
                holder.tv_separator1.setTextColor(mContext.getResources().getColor(R.color.automation_red));
            }else {
                holder.tv_device_log_date.setTextColor(mContext.getResources().getColor(R.color.automation_black));
                holder.tv_device_log_time.setTextColor(mContext.getResources().getColor(R.color.automation_black));
                holder.tv_device_name.setTextColor(mContext.getResources().getColor(R.color.automation_black));
                holder.tv_device_description.setTextColor(mContext.getResources().getColor(R.color.automation_black));
                holder.tv_room_name.setTextColor(mContext.getResources().getColor(R.color.automation_black));
                holder.tv_panel_name.setTextColor(mContext.getResources().getColor(R.color.automation_black));
                holder.tv_separator.setTextColor(mContext.getResources().getColor(R.color.automation_black));
                holder.tv_separator1.setTextColor(mContext.getResources().getColor(R.color.automation_black));
            }

         //   holder.tv_device_log_type.setText(deviceLog.getActivity_action() + " - " + deviceLog.getActivity_type());
            holder.tv_device_description.setText(deviceLog.getMessage() + " " + "at");
            actionList = deviceLog.getActivity_description().split("\\|");

            if(actionList[0]!=null){
                holder.tv_device_name.setVisibility(View.VISIBLE);
                holder.tv_separator.setVisibility(View.VISIBLE);
                holder.tv_device_name.setText(actionList[0].trim());
            }else{
                holder.tv_device_name.setVisibility(View.GONE);
                holder.tv_separator.setVisibility(View.GONE);
            }


            if(actionList.length > 1 && actionList[1]!=null){
                holder.tv_panel_name.setVisibility(View.VISIBLE);
                holder.tv_separator1.setVisibility(View.VISIBLE);
                holder.tv_panel_name.setText(actionList[1].trim());
            }else{
                holder.tv_panel_name.setVisibility(View.GONE);
                holder.tv_separator1.setVisibility(View.GONE);
            }

            if(actionList.length > 2 && actionList[2]!=null){
                holder.tv_room_name.setVisibility(View.VISIBLE);
                holder.tv_separator1.setVisibility(View.VISIBLE);
                holder.tv_room_name.setText(actionList[2]);
            }else{
                holder.tv_room_name.setVisibility(View.GONE);
                holder.tv_separator1.setVisibility(View.GONE);
            }

        }catch (Exception e){
            e.printStackTrace();
        }

       /* if(deviceLog.getActivity_action().equalsIgnoreCase("End of Record")){
            holder.txt_empty_view.setVisibility(View.GONE);
            holder.txt_empty_view.setText(deviceLog.getActivity_type());
            holder.tv_device_log_date.setVisibility(View.GONE);
            holder.tv_device_log_type.setVisibility(View.GONE);
            holder.tv_device_description.setVisibility(View.GONE);
        }else{
            holder.txt_empty_view.setVisibility(View.GONE);
            holder.tv_device_log_date.setVisibility(View.VISIBLE);
            holder.tv_device_log_type.setVisibility(View.VISIBLE);
            holder.tv_device_description.setVisibility(View.VISIBLE);
        }*/

       /* deviceLog = deviceLogs.get(listPosition);
        try {

            if(listPosition==0){
                holder.view_header.setVisibility(View.GONE);
            }else {
                holder.view_header.setVisibility(View.VISIBLE);
            }

            if(deviceLog.getUser_name()!=null && !deviceLog.getUser_name().equals("null")){
                holder.tvUserName.setVisibility(View.VISIBLE);
                holder.tvUserName.setText(deviceLog.getUser_name());
            }else {
                holder.tvUserName.setVisibility(View.GONE);
            }

            if(deviceLog.getActivity_action().equalsIgnoreCase("End of Record")){
                holder.txt_empty_view.setVisibility(View.GONE);
                holder.txt_empty_view.setText(deviceLog.getActivity_type());
                holder.tv_device_log_date.setVisibility(View.GONE);
                holder.tv_device_log_type.setVisibility(View.GONE);
                holder.tv_device_description.setVisibility(View.GONE);
            }else{
                holder.txt_empty_view.setVisibility(View.GONE);
                holder.tv_device_log_date.setVisibility(View.VISIBLE);
                holder.tv_device_log_type.setVisibility(View.VISIBLE);
                holder.tv_device_description.setVisibility(View.VISIBLE);
            }

            if(!TextUtils.isEmpty(deviceLog.getActivity_time())){

                String dateString = Constants.logConverterDate(Long.parseLong(deviceLog.getActivity_time()));

                strDateOfTimeTemp  =dateString.split(" ");

                if(strDateOfTimeTemp[0].equalsIgnoreCase(Constants.getCurrentDate())){
                    dateTime = strDateOfTimeTemp[1]+" "+strDateOfTimeTemp[2];
                }else {
                    dateTime = strDateOfTimeTemp[1]+ strDateOfTimeTemp[2]+ System.getProperty("line.separator") + strDateOfTimeTemp[0] ;
                }

                holder.tv_device_log_date.setText(dateTime);
            }


            if(deviceLog.getIs_unread().equalsIgnoreCase("1")){
                holder.tv_device_log_date.setTextColor(mContext.getResources().getColor(R.color.automation_red));
                holder.tv_device_log_type.setTextColor(mContext.getResources().getColor(R.color.automation_red));
                holder.tvUserName.setTextColor(mContext.getResources().getColor(R.color.automation_red));
                holder.tv_device_description.setTextColor(mContext.getResources().getColor(R.color.automation_red));
                holder.tv_device_description2.setTextColor(mContext.getResources().getColor(R.color.automation_red));
                holder.tv_device_description3.setTextColor(mContext.getResources().getColor(R.color.automation_red));
            }else {
                holder.tv_device_log_date.setTextColor(mContext.getResources().getColor(R.color.txtPanal));
                holder.tv_device_log_type.setTextColor(mContext.getResources().getColor(R.color.txtPanal));
                holder.tvUserName.setTextColor(mContext.getResources().getColor(R.color.txtPanal));
                holder.tv_device_description.setTextColor(mContext.getResources().getColor(R.color.txtPanal));
                holder.tv_device_description2.setTextColor(mContext.getResources().getColor(R.color.txtPanal));
                holder.tv_device_description3.setTextColor(mContext.getResources().getColor(R.color.txtPanal));
            }
            holder.tv_device_log_type.setText(deviceLog.getActivity_action() + " - " + deviceLog.getActivity_type());

            actionList = deviceLog.getActivity_description().split("\\|");

            if(actionList[0]!=null){
                holder.tv_device_description.setVisibility(View.VISIBLE);
                holder.tv_device_description.setText(actionList[0].trim());
            }else{
                holder.tv_device_description.setVisibility(View.GONE);
            }
            if(actionList.length > 1 && actionList[1]!=null){
                holder.tv_device_description2.setVisibility(View.VISIBLE);
                holder.tv_device_description2.setText(actionList[1].trim());
            }else{
                holder.tv_device_description2.setVisibility(View.GONE);
            }

            if(actionList.length > 2 && actionList[2]!=null){
                holder.tv_device_description3.setVisibility(View.VISIBLE);
                holder.tv_device_description3.setText(actionList[2]);
            }else{
                holder.tv_device_description3.setVisibility(View.GONE);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    @Override
    public void onViewDetachedFromWindow(ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        ((ViewHolder)holder).clearAnimation();
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
    }


    @Override
    public int getItemCount() {
        return deviceLogs.size();
    }
    protected static class ViewHolder extends RecyclerView.ViewHolder {
        //common
        View view,view_header;
        int viewType;
        TextView tv_device_log_date,tv_device_description,tv_device_log_time,tv_device_name,tv_panel_name,tv_room_name,txt_empty_view,tv_separator,tv_separator1;

        public ViewHolder(View view, int viewType) {
            super(view);
           /* this.viewType = viewType;
            this.view = view;
            view_header = (View) view.findViewById(R.id.view_header );
            tv_device_log_type = (TextView) view.findViewById(R.id.tv_device_log_type );
            tv_device_log_date = (TextView) view.findViewById(R.id.tv_device_log_date );
            tv_device_description = (TextView) view.findViewById(R.id.tv_device_description);
            tv_device_description2 = (TextView) view.findViewById(R.id.tv_device_description2);
            tv_device_description3 = (TextView) view.findViewById(R.id.tv_device_description3);
            txt_empty_view = (TextView) view.findViewById(R.id.txt_empty_view);
            tvUserName = (TextView) view.findViewById(R.id.tvUserName);*/
            this.viewType = viewType;
            this.view = view;
            tv_device_log_date = view.findViewById(R.id.txt_log_date);
            tv_device_description = view.findViewById(R.id.txt_log_description);
            tv_device_log_time = view.findViewById(R.id.txt_log_time);
            tv_device_name =  view.findViewById(R.id.txt_device_name);
            tv_panel_name =  view.findViewById(R.id.txt_panel_name);
            tv_room_name =  view.findViewById(R.id.txt_room_name);
            txt_empty_view = view.findViewById(R.id.txt_empty_view);
            tv_separator = view.findViewById(R.id.txt_separator);
            tv_separator1 = view.findViewById(R.id.txt_separator1);
        }
        public void clearAnimation() {
            view.clearAnimation();
        }
    }
}