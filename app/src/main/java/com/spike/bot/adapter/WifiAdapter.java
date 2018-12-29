package com.spike.bot.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.kp.core.DateHelper;
import com.spike.bot.R;
import com.spike.bot.adapter.filter.CameraNotificationAdapter;
import com.spike.bot.core.Constants;
import com.spike.bot.customview.OnSwipeTouchListener;
import com.spike.bot.listener.UpdateCameraAlert;
import com.spike.bot.listener.WifiListner;
import com.spike.bot.model.CameraAlertList;
import com.spike.bot.model.CameraVO;
import com.spike.bot.model.CameraViewModel;
import com.spike.bot.model.WifiModel;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Sagar on 4/12/18.
 * Gmail : jethvasagar2@gmail.com
 */
public class WifiAdapter extends RecyclerView.Adapter<WifiAdapter.SensorViewHolder> {

    private Context mContext;
    public ArrayList<WifiModel.WiFiList> arrayList=new ArrayList<>();
    public WifiListner wifiListner;


    public WifiAdapter(Context context, ArrayList<WifiModel.WiFiList> arrayList,WifiListner wifiListner) {
        this.mContext = context;
        this.arrayList = arrayList;
        this.wifiListner=wifiListner;

    }

    @Override
    public SensorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_wifi_list, parent, false);
        return new SensorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SensorViewHolder holder, final int position) {

        holder.txtWifiName.setText(arrayList.get(position).getNetworkName());

        holder.linearWifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wifiListner.wifiClick(arrayList.get(position));
            }
        });


    }



    @Override
    public int getItemCount() {
        return arrayList.size();
    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public class SensorViewHolder extends RecyclerView.ViewHolder {

        public TextView txtWifiName;
        public LinearLayout linearWifi;

        public SensorViewHolder(View view) {
            super(view);
            txtWifiName = (TextView) itemView.findViewById(R.id.txtWifiName);
            linearWifi = (LinearLayout) itemView.findViewById(R.id.linearWifi);
        }
    }

}
