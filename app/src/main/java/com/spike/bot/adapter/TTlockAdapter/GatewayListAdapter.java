package com.spike.bot.adapter.TTlockAdapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.spike.bot.R;
import com.spike.bot.model.GatewayListModel;
import com.ttlock.bl.sdk.api.ExtendedBluetoothDevice;

import java.util.ArrayList;
import java.util.LinkedList;


/**
 * Created on  2019/4/12 0012 14:19
 *
 * @author theodre
 */
public class GatewayListAdapter extends RecyclerView.Adapter<GatewayListAdapter.DeviceViewHolder> {

    private Activity mContext;
    ArrayList<GatewayListModel> arrayList = new ArrayList<>();
    clickList clickList;

    public GatewayListAdapter(Activity context, ArrayList<GatewayListModel> lockObjs, clickList clickList) {
        mContext = context;
        this.arrayList = lockObjs;
        this.clickList = clickList;
    }


    @Override
    public DeviceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(mContext).inflate(R.layout.gateway_scan_list_item, parent, false);
        return new DeviceViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(DeviceViewHolder _holder, int position) {
        final GatewayListModel item = arrayList.get(position);
        _holder.Bind(item);

        _holder.tv_gateway_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickList.clickList(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class DeviceViewHolder extends RecyclerView.ViewHolder {

        View view;
        ImageView iv_setting_mode;
        TextView tv_gateway_name;

        public DeviceViewHolder(View itemView) {
            super(itemView);
            this.view = itemView;
            tv_gateway_name = itemView.findViewById(R.id.tv_gateway_name);
            iv_setting_mode = itemView.findViewById(R.id.iv_setting_mode);
        }

        public void Bind(GatewayListModel item) {
            tv_gateway_name.setText(item.getGatewayName());
            iv_setting_mode.setVisibility(View.GONE);

            iv_setting_mode.setOnClickListener(view -> {
                clickList.clickList(item);
            });
        }

    }

    public interface clickList {
        void clickList(GatewayListModel gatewayListModel);
    }
}
