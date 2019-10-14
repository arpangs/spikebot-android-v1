package com.spike.bot.adapter.irblaster;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.spike.bot.R;
import com.spike.bot.model.IRDeviceDetailsRes;

import java.util.List;

/**
 * Created by Sagar on 1/8/18.
 * Gmail : jethvasagar2@gmail.com
 */
public class IRBlasterAddListAdapter extends RecyclerView.Adapter<IRBlasterAddListAdapter.IRBlasterHolder> {


    List<IRDeviceDetailsRes.Data.Devicelist> mIRDeviceList;
    IRDeviceDetailsRes.Data.Devicelist devicelist;

    private IRDeviceClikListener irDeviceClikListener;

    public IRBlasterAddListAdapter(List<IRDeviceDetailsRes.Data.Devicelist> mIRDeviceList, IRDeviceClikListener irDeviceClikListener) {
        this.mIRDeviceList = mIRDeviceList;
        this.irDeviceClikListener = irDeviceClikListener;
    }

    @Override
    public IRBlasterHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_ir_add_list, parent, false);
        return new IRBlasterHolder(view);
    }

    @Override
    public void onBindViewHolder(IRBlasterHolder holder, int position) {
        devicelist = mIRDeviceList.get(position);
        holder.ir_add_remote_name.setText(devicelist.getDeviceType());

        holder.ir_rrot_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                irDeviceClikListener.onIRDeviceClick(devicelist);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mIRDeviceList.size();
    }

    class IRBlasterHolder extends RecyclerView.ViewHolder {

        ImageView ir_add_remote_img;
        TextView ir_add_remote_name;
        RelativeLayout ir_rrot_click;

        IRBlasterHolder(View itemView) {
            super(itemView);

            ir_rrot_click = (RelativeLayout) itemView.findViewById(R.id.ir_rrot_click);

            ir_add_remote_img = (ImageView) itemView.findViewById(R.id.ir_add_remote_img);
            ir_add_remote_name = (TextView) itemView.findViewById(R.id.ir_add_remote_name);
        }
    }

    public interface IRDeviceClikListener {
        void onIRDeviceClick(IRDeviceDetailsRes.Data.Devicelist devicelist);
    }
}
