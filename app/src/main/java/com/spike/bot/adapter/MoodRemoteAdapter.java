package com.spike.bot.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.spike.bot.R;
import com.spike.bot.listener.MoodEvent;
import com.spike.bot.model.RemoteMoodModel;

import java.util.List;

/**
 * Created by Sagar on 16/11/18.
 * Gmail : jethvasagar2@gmail.com
 */
public class MoodRemoteAdapter extends RecyclerView.Adapter<MoodRemoteAdapter.UnassignedHolder>{

    private List<RemoteMoodModel> roomdeviceList;
    private MoodEvent moodEvent;
    public Context context;

    public MoodRemoteAdapter(Context irBlasterRemote, List<RemoteMoodModel> roomdeviceList, MoodEvent moodEvent){
        this.roomdeviceList = roomdeviceList;
        this.moodEvent = moodEvent;
        this.context = irBlasterRemote;
    }

    @Override
    public UnassignedHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_mood_remote,parent,false);
        return new UnassignedHolder(view);
    }

    @Override
    public void onBindViewHolder(UnassignedHolder holder, final int position) {

        holder.txtMoodName.setText(roomdeviceList.get(position).getMoodName());

        if(roomdeviceList.get(position).isSelect()){
            holder.txtMoodName.setTextColor(context.getResources().getColor(R.color.automation_white));
            holder.linearMood.setBackground(context.getResources().getDrawable(R.drawable.drawable_mood_unselect));
        }else {
            holder.txtMoodName.setTextColor(context.getResources().getColor(R.color.signupblack));
            holder.linearMood.setBackground(context.getResources().getDrawable(R.drawable.drawable_black_boader));
        }

        holder.linearMood.setId(position);
        holder.linearMood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for(int i=0; i<roomdeviceList.size(); i++){
                    if(i==position){
                        roomdeviceList.get(i).setSelect(true);
                    }else {
                        roomdeviceList.get(i).setSelect(false);
                    }
                }

                moodEvent.selectMood(roomdeviceList.get(v.getId()));
            }
        });

    }

    @Override
    public int getItemCount() {
        return roomdeviceList.size();
    }

    class UnassignedHolder extends RecyclerView.ViewHolder{

        private TextView txtMoodName;
        private LinearLayout linearMood;

        UnassignedHolder(View itemView) {
            super(itemView);
            txtMoodName = (TextView) itemView.findViewById(R.id.txtMoodName);
            linearMood = (LinearLayout) itemView.findViewById(R.id.linearMood);
        }
    }
    public interface UnassignedClickEvent{
        void onClick(int position, RemoteMoodModel roomdeviceList);
    }
}
