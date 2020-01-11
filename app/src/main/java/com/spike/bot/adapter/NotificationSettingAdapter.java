package com.spike.bot.adapter;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.spike.bot.R;
import com.spike.bot.model.NotificationListRes;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sagar on 24/7/18.
 * Gmail : jethvasagar2@gmail.com
 */
public class NotificationSettingAdapter extends RecyclerView.Adapter<NotificationSettingAdapter.NotificationHolder>{

    private List<NotificationListRes.Data> notificationList=new ArrayList<>();
    private SwitchChanges switchChanges;

    public NotificationSettingAdapter(List<NotificationListRes.Data> notificationList,SwitchChanges switchChanges){
        this.notificationList = notificationList;
        this.switchChanges = switchChanges;
    }

    @Override
    public NotificationHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_notification_settings,parent,false);
        return new NotificationHolder(view);
    }

    @Override
    public void onBindViewHolder(NotificationHolder holder, final int position) {

        holder.notification_title.setText(notificationList.get(position).getTitle());

        holder.switch_temp.setChecked((notificationList.get(position).getValue() == 1));

        holder.switch_temp.setId(position);
        holder.switch_temp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(buttonView.isPressed()){
                    notificationList.get(holder.switch_temp.getId()).setValue(isChecked ? 1 : 0);
                    notifyItemChanged(holder.switch_temp.getId(),notificationList.get(holder.switch_temp.getId()));
                    switchChanges.onCheckedChanged(notificationList.get(holder.switch_temp.getId()),holder.switch_temp.getId());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    static class NotificationHolder extends RecyclerView.ViewHolder{

        private TextView notification_title;
        private SwitchCompat switch_temp;

        public NotificationHolder(View itemView) {
            super(itemView);

            notification_title = (TextView) itemView.findViewById(R.id.notification_title);
            switch_temp = (SwitchCompat) itemView.findViewById(R.id.switch_temp);
        }
    }

    public interface SwitchChanges{
        void onCheckedChanged(NotificationListRes.Data data, int position);
    }
}
