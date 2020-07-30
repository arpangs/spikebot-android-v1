package com.spike.bot.adapter.beacon;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.spike.bot.R;
import com.spike.bot.core.BeaconAPIDiffCallBack;
import com.spike.bot.core.BeaconDiffCallBack;
import com.spike.bot.model.IRDeviceDetailsRes;

import java.util.List;

import no.nordicsemi.android.support.v18.scanner.ScanResult;

public class ScannerAddListAdapter extends RecyclerView.Adapter<ScannerAddListAdapter.IRBlasterHolder> {


    List<IRDeviceDetailsRes.Data> mIRDeviceList;
    IRDeviceDetailsRes.Data devicelist;

    private BeaconDeviceClickListener irDeviceClikListener;

    public ScannerAddListAdapter(List<IRDeviceDetailsRes.Data> mIRDeviceList, BeaconDeviceClickListener irDeviceClikListener) {
        this.mIRDeviceList = mIRDeviceList;
        this.irDeviceClikListener = irDeviceClikListener;
    }

    @Override
    public IRBlasterHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_beacon_add_list, parent, false);
        return new IRBlasterHolder(view);
    }

    @Override
    public void onBindViewHolder(IRBlasterHolder holder, int position) {
        try {
                devicelist = mIRDeviceList.get(position);
                // holder.ir_add_remote_name.setText("Beacon");
            if(devicelist !=null) {
                holder.txtMacAddress.setText("Address : " + " " + devicelist.getMac());
                holder.txtRss.setText("Distance : " + " " + devicelist.getSs() + " m");
                holder.ir_rrot_click.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        irDeviceClikListener.onBeaconDeviceClick(devicelist);
                        irDeviceClikListener.onBeaconDeviceClick(mIRDeviceList.get(position));
                    }
                });
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        if(mIRDeviceList.size() > 0)
        return mIRDeviceList.size();
        else
            return 0;
    }

    class IRBlasterHolder extends RecyclerView.ViewHolder {

      //  ImageView ir_add_remote_img;
        TextView /*ir_add_remote_name,*/ /*txtName,*/txtMacAddress,txtRss;
        CardView ir_rrot_click;

        IRBlasterHolder(View itemView) {
            super(itemView);

            ir_rrot_click =  itemView.findViewById(R.id.ir_rrot_click);

         //   ir_add_remote_img =  itemView.findViewById(R.id.ir_add_remote_img);
          //  ir_add_remote_name =  itemView.findViewById(R.id.ir_add_remote_name);
           // txtName = itemView.findViewById(R.id.txtName);
            txtMacAddress = itemView.findViewById(R.id.textview_MacAddress);
            txtRss = itemView.findViewById(R.id.textview_Rss);
        }
    }

    public interface BeaconDeviceClickListener {
        void onBeaconDeviceClick(IRDeviceDetailsRes.Data devicelist);
    }

    public void updatebeaconlistitem(List<IRDeviceDetailsRes.Data> results) {
        final BeaconAPIDiffCallBack diffCallback = new BeaconAPIDiffCallBack(this.mIRDeviceList, results);
        final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);
        diffResult.convertNewPositionToOld(diffCallback.getNewListSize());
        diffResult.dispatchUpdatesTo(this);
    }

  /*  @Override
    public boolean equals(@Nullable Object obj) {
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }*/
}
