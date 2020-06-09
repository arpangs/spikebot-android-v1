package com.spike.bot.adapter.irblaster;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.spike.bot.R;
import com.spike.bot.activity.ir.blaster.IRBlasterRemote;
import com.spike.bot.model.IRBlasterAddRes;

import java.util.List;

/**
 * Created by Sagar on 21/8/18.
 * Gmail : jethvasagar2@gmail.com
 */
public class IRBlasterAddRemoteList extends RecyclerView.Adapter<IRBlasterAddRemoteList.IRBlasterRemoteViewHolder> {

    private Context mContext;
    private List<IRBlasterAddRes.RemoteList> remoteList;
    IRBlasterAddRes.RemoteList remote;

    public IRBlasterAddRemoteList(List<IRBlasterAddRes.RemoteList> remoteList) {
        this.remoteList = remoteList;
    }

    @Override
    public IRBlasterRemoteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_room_edit_device_v2, parent, false);
        mContext = view.getContext();
        return new IRBlasterRemoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(IRBlasterRemoteViewHolder holder, int position) {
        remote = remoteList.get(position);
        holder.mRemoteName.setText(remote.getDeviceName());
        //TODO code here for change remote icon
        holder.mRemoteIcon.setImageResource(R.drawable.remote_ac_off);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(mContext, IRBlasterRemote.class);
                intent.putExtra("REMOTE_ID", remote.getDeviceId());
                intent.putExtra("IR_BLASTER_ID", remote.getDeviceId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return remoteList.size();
    }

    class IRBlasterRemoteViewHolder extends RecyclerView.ViewHolder {

        private TextView mRemoteName;
        private ImageView mRemoteIcon;

        IRBlasterRemoteViewHolder(View itemView) {
            super(itemView);
            mRemoteName =  itemView.findViewById(R.id.text_item);
            mRemoteIcon =  itemView.findViewById(R.id.iv_icon);
        }
    }
}
