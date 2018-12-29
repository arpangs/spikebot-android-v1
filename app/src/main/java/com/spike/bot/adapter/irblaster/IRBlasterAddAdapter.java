package com.spike.bot.adapter.irblaster;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.spike.bot.R;
import com.spike.bot.model.IRBlasterAddRes;

import java.util.List;

/**
 * Created by Sagar on 21/8/18.
 * Gmail : jethvasagar2@gmail.com
 */
public class IRBlasterAddAdapter extends RecyclerView.Adapter<IRBlasterAddAdapter.IRBlasterViewHolder>{

    List<IRBlasterAddRes.Data.IrList> irList;
    private Context mContext;
    private BlasterAction blasterAction;

    public IRBlasterAddAdapter(List<IRBlasterAddRes.Data.IrList> irList,BlasterAction blasterAction){
        this.irList= irList;
        this.blasterAction = blasterAction;
    }

    @Override
    public IRBlasterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_ir_blaster_list,parent,false);
        mContext = view.getContext();
        return new IRBlasterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(IRBlasterViewHolder holder, final int position) {

        final IRBlasterAddRes.Data.IrList ir = irList.get(position);
        holder.mIBName.setText(ir.getIrBlasterName());
        holder.mRoomName.setText("[ "+ir.getRoomName()+" ]");

        holder.mIrRemoteListAdapter = new IRBlasterAddRemoteList(ir.getRemoteList());
        holder.mIRRemoteList.setAdapter(holder.mIrRemoteListAdapter);

        holder.mIrEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                blasterAction.onEdit(position,ir);
            }
        });

        holder.mIrDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                blasterAction.onDelete(position,ir);
            }
        });

    }

    @Override
    public int getItemCount() {
        return irList.size();
    }

    class IRBlasterViewHolder extends RecyclerView.ViewHolder{

        private TextView mIBName;
        private TextView mRoomName;
        private ImageView mIrEdit,mIrDelete;

        private RecyclerView mIRRemoteList;
        private IRBlasterAddRemoteList mIrRemoteListAdapter;

        IRBlasterViewHolder(View itemView) {
            super(itemView);
            mIBName = (TextView) itemView.findViewById(R.id.et_panel);
            mRoomName  = (TextView) itemView.findViewById(R.id.room_name);
            mIrEdit = (ImageView) itemView.findViewById(R.id.iv_room_panel_add);
            mIrDelete = (ImageView) itemView.findViewById(R.id.iv_room_panel_delete);

            mIRRemoteList = (RecyclerView) itemView.findViewById(R.id.list_edit_device);
            mIRRemoteList.setLayoutManager(new GridLayoutManager(mContext,4));
        }
    }


    public interface BlasterAction{
        void onEdit(int position, IRBlasterAddRes.Data.IrList ir);
        void onDelete(int position, IRBlasterAddRes.Data.IrList ir);
    }

}
