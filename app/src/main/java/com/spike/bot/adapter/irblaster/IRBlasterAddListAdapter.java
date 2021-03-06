package com.spike.bot.adapter.irblaster;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.spike.bot.R;
import com.spike.bot.model.IRDeviceDetailsRes;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sagar on 1/8/18.
 * Gmail : jethvasagar2@gmail.com
 */
public class IRBlasterAddListAdapter extends RecyclerView.Adapter<IRBlasterAddListAdapter.IRBlasterHolder> {


    List<IRDeviceDetailsRes.Data> mIRDeviceList;
    IRDeviceDetailsRes.Data mIRBlaster;
    ArrayList<String> mRemoteTypeList;

    private IRDeviceClikListener irDeviceClikListener;

    public IRBlasterAddListAdapter(IRDeviceDetailsRes.Data mIRDevice, ArrayList<String> mRemoteTypeList, IRDeviceClikListener irDeviceClikListener) {
        this.mIRBlaster = mIRDevice;
        this.irDeviceClikListener = irDeviceClikListener;
        this.mRemoteTypeList = mRemoteTypeList;
    }

    @Override
    public IRBlasterHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_ir_add_list, parent, false);
        return new IRBlasterHolder(view);
    }

    @Override
    public void onBindViewHolder(IRBlasterHolder holder, int position) {
        try {
            // devicelist = mIRDeviceList.get(position);

            switch (mRemoteTypeList.get(position).toLowerCase()) {

                case "ac":
                    holder.ir_add_remote_name.setText("AC");
                    holder.ir_add_remote_img.setBackgroundResource(R.drawable.ac_remote_on);
                    break;

                case "tv":
                    holder.ir_add_remote_name.setText("TV");
                    holder.ir_add_remote_img.setBackgroundResource(R.drawable.tv_on);
                    break;

                case "dth":
                    holder.ir_add_remote_name.setText("DTH");
                    holder.ir_add_remote_img.setBackgroundResource(R.drawable.dth_on);
                    break;
            }

            holder.ir_rrot_click.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        irDeviceClikListener.onIRDeviceClick(mIRBlaster, position);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return mRemoteTypeList.size();
    }

    public interface IRDeviceClikListener {
        void onIRDeviceClick(IRDeviceDetailsRes.Data devicelist, int position);
    }

    class IRBlasterHolder extends RecyclerView.ViewHolder {

        ImageView ir_add_remote_img;
        TextView ir_add_remote_name;
        RelativeLayout ir_rrot_click;

        IRBlasterHolder(View itemView) {
            super(itemView);

            ir_rrot_click = itemView.findViewById(R.id.ir_rrot_click);

            ir_add_remote_img = itemView.findViewById(R.id.ir_add_remote_img);
            ir_add_remote_name = itemView.findViewById(R.id.ir_add_remote_name);
        }
    }
}
