package com.spike.bot.adapter.TTlockAdapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.model.LockObj;

import java.util.ArrayList;

public class UserLockListAdapter extends RecyclerView.Adapter<UserLockListAdapter.DeviceViewHolder> {

    public ArrayList<LockObj> mDataList = new ArrayList<>();
    private Context mContext;

    public UserLockListAdapter(Context context, ArrayList<LockObj> arrayList) {
        mContext = context;
        this.mDataList = arrayList;
    }

    @Override
    public DeviceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(mContext).inflate(R.layout.lock_add_list_item, parent, false);
        return new DeviceViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(DeviceViewHolder _holder, int position) {
        final LockObj item = mDataList.get(position);
        _holder.Bind(item);
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    class DeviceViewHolder extends RecyclerView.ViewHolder {


        View view;
        ImageView iv_setting_mode, imgLock;
        TextView tv_lock_name;

        public DeviceViewHolder(View itemView) {
            super(itemView);
            this.view = itemView;
            tv_lock_name = itemView.findViewById(R.id.tv_lock_name);
            iv_setting_mode = itemView.findViewById(R.id.iv_setting_mode);
            imgLock = itemView.findViewById(R.id.imgLock);

        }

        public void Bind(LockObj item) {

            imgLock.setVisibility(View.VISIBLE);
            iv_setting_mode.setVisibility(View.GONE);
            tv_lock_name.setText(item.getLockAlias());
            tv_lock_name.setOnClickListener(view -> {
                ChatApplication.getApp().saveChoosedLock(item);
            });
        }

    }
}
