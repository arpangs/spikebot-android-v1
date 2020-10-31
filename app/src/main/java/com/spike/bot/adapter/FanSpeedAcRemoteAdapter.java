package com.spike.bot.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.spike.bot.R;
import com.spike.bot.listener.ACFanSpeedEvent;
import com.spike.bot.model.RemoteMoodModel;

import java.util.List;

/**
 * Created by Sagar on 16/11/18.
 * Gmail : jethvasagar2@gmail.com
 */
public class FanSpeedAcRemoteAdapter extends RecyclerView.Adapter<FanSpeedAcRemoteAdapter.UnassignedHolder> {

    public Context context;
    private List<RemoteMoodModel> roomdeviceList;
    private ACFanSpeedEvent acFanSpeedEvent;
    private boolean isActive;

    public FanSpeedAcRemoteAdapter(Context irBlasterRemote, List<RemoteMoodModel> roomdeviceList, ACFanSpeedEvent acFanSpeedEvent, boolean isActive) {
        this.roomdeviceList = roomdeviceList;
        this.acFanSpeedEvent = acFanSpeedEvent;
        this.context = irBlasterRemote;
        this.isActive = isActive;
    }

    public void setIsActive(boolean isActive){
        this.isActive = isActive;
    }

    @Override
    public UnassignedHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_mood_remote, parent, false);
        return new UnassignedHolder(view);
    }

    @Override
    public void onBindViewHolder(UnassignedHolder holder, final int position) {

        holder.txtMoodName.setText(roomdeviceList.get(position).getMoodName());

        if (roomdeviceList.get(position).isSelect()) {
            holder.txtMoodName.setTextColor(context.getResources().getColor(R.color.automation_white));
            holder.linearMood.setBackground(context.getResources().getDrawable(R.drawable.drawable_mood_unselect));
        } else {
            holder.txtMoodName.setTextColor(context.getResources().getColor(R.color.signupblack));
            holder.linearMood.setBackground(context.getResources().getDrawable(R.drawable.drawable_black_boader));
        }

        holder.linearMood.setId(position);
        holder.linearMood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isActive) {

                    for (int i = 0; i < roomdeviceList.size(); i++) {
                        if (i == position) {
                            roomdeviceList.get(i).setSelect(true);
                        } else {
                            roomdeviceList.get(i).setSelect(false);
                        }
                        notifyDataSetChanged();
                    }

                    acFanSpeedEvent.selectAcFanSpeed(roomdeviceList.get(v.getId()));
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return roomdeviceList.size();
    }

    public interface UnassignedClickEvent {
        void onClick(int position, RemoteMoodModel roomdeviceList);
    }

    class UnassignedHolder extends RecyclerView.ViewHolder {

        private TextView txtMoodName;
        private LinearLayout linearMood;

        UnassignedHolder(View itemView) {
            super(itemView);
            txtMoodName = (TextView) itemView.findViewById(R.id.txtMoodName);
            linearMood = (LinearLayout) itemView.findViewById(R.id.linearMood);
        }
    }
}
