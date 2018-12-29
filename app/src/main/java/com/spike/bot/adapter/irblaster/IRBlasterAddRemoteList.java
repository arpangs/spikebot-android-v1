package com.spike.bot.adapter.irblaster;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.spike.bot.R;
import com.spike.bot.activity.ir.blaster.IRBlasterRemote;
import com.spike.bot.model.IRBlasterAddRes;

import java.util.List;

/**
 * Created by Sagar on 21/8/18.
 * Gmail : jethvasagar2@gmail.com
 */
public class IRBlasterAddRemoteList extends RecyclerView.Adapter<IRBlasterAddRemoteList.IRBlasterRemoteViewHolder>{

    private Context mContext;
    private List<IRBlasterAddRes.Data.IrList.RemoteList> remoteList;

    public IRBlasterAddRemoteList(List<IRBlasterAddRes.Data.IrList.RemoteList> lists){
        this.remoteList = lists;
    }

    @Override
    public IRBlasterRemoteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_room_edit_device_v2,parent,false);
        mContext = view.getContext();
        return new IRBlasterRemoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(IRBlasterRemoteViewHolder holder, int position) {

        final IRBlasterAddRes.Data.IrList.RemoteList remote = remoteList.get(position);
        holder.mRemoteName.setText(remote.getRemoteName());
        //TODO code here for change remote icon
        holder.mRemoteIcon.setImageResource(R.drawable.remote_ac_off);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(mContext,IRBlasterRemote.class);
                intent.putExtra("REMOTE_ID",remote.getRemoteId());
                intent.putExtra("IR_BLASTER_ID",remote.getIrBlasterId());
              //  mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return remoteList.size();
    }

    class IRBlasterRemoteViewHolder extends RecyclerView.ViewHolder{

        private TextView mRemoteName;
        private ImageView mRemoteIcon;

        IRBlasterRemoteViewHolder(View itemView) {
            super(itemView);
            mRemoteName = (TextView) itemView.findViewById(R.id.text_item);
            mRemoteIcon = (ImageView) itemView.findViewById(R.id.iv_icon);
        }
    }
}
