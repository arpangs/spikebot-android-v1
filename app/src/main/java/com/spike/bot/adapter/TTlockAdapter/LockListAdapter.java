package com.spike.bot.adapter.TTlockAdapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.ttlock.bl.sdk.api.ExtendedBluetoothDevice;

import java.util.LinkedList;


/**
 * Created on  2019/4/12 0012 14:19
 *
 * @author theodre
 */
public class LockListAdapter extends RecyclerView.Adapter<LockListAdapter.DeviceViewHolder> {

    private LinkedList<ExtendedBluetoothDevice> mDataList = new LinkedList<>();

    private Activity mContext;
    private static final int TIMEOUT = 5000;
    private LinkedList<ExtendedBluetoothDevice> mAddStatusList = new LinkedList<>();
    private LinkedList<ExtendedBluetoothDevice> mNormalStatusList = new LinkedList<>();
    private long lastSyncTimeStamp = 0;
    private onLockItemClick mListener;
    boolean isContained = false;

    public interface onLockItemClick {
        void onClick(ExtendedBluetoothDevice device);
    }

    public void setOnLockItemClick(onLockItemClick click) {
        this.mListener = click;
    }

    public LockListAdapter(Activity context) {
        mContext = context;
    }

    public synchronized void updateData(ExtendedBluetoothDevice device) {
        if (device != null) {
            if (device.isSettingMode()) {
                addOrSortLock(device, mAddStatusList);
                removeOtherStatusLock(device, mNormalStatusList);
            } else {
                addOrSortLock(device, mNormalStatusList);
                removeOtherStatusLock(device, mAddStatusList);
            }

            long currentTime = System.currentTimeMillis();
            if ((currentTime - lastSyncTimeStamp) >= 800) {
                if (!mDataList.isEmpty()) {
                    mDataList.clear();
                }

                mDataList.addAll(0, mAddStatusList);
                mDataList.addAll(mNormalStatusList);
                notifyDataSetChanged();
                lastSyncTimeStamp = currentTime;
            }
        }
    }


    /**
     * you can sort the lock that be discovered by signal value.
     */
    private void addOrSortLock(ExtendedBluetoothDevice scanDevice, LinkedList<ExtendedBluetoothDevice> lockList) {
        int length = lockList.size();
        ExtendedBluetoothDevice mTopOneDevice;
        scanDevice.setDate(System.currentTimeMillis());
        if (length > 0) {

            mTopOneDevice = lockList.get(0);

            for (int i = 0; i < length; i++) {

                ExtendedBluetoothDevice currentDevice = lockList.get(i);
                if (scanDevice.getAddress().equals(currentDevice.getAddress())) {
                    isContained = true;
                    if (i != 0 && scanDevice.getRssi() > mTopOneDevice.getRssi()) {
                        lockList.remove(i);
                        lockList.add(0, scanDevice);
                    } else {
                        currentDevice.setDate(System.currentTimeMillis());
                        lockList.set(i, currentDevice);
                    }
                } else {
                    if (System.currentTimeMillis() - currentDevice.getDate() >= TIMEOUT) {
                        lockList.remove(i);
                        length = lockList.size();
                    }
                }
            }

            if (!isContained) {
                if (scanDevice.getRssi() > mTopOneDevice.getRssi()) {
                    lockList.add(0, scanDevice);
                } else {
                    lockList.add(scanDevice);
                }
            }

        } else {
            lockList.add(scanDevice);
        }

    }

    /**
     * the lock mode will be changed,so should update the list when lock mode changed.
     *
     * @param scanDevice the lock that be discovered.
     */
    private void removeOtherStatusLock(ExtendedBluetoothDevice scanDevice, LinkedList<ExtendedBluetoothDevice> lockList) {
        if (!lockList.isEmpty()) {
            int length = lockList.size();
            for (int i = 0; i < length; i++) {
                ExtendedBluetoothDevice device = lockList.get(i);
                if (device.getAddress().equals(scanDevice.getAddress())) {
                    lockList.remove(i);
                    length--;
                } else {
                    if (System.currentTimeMillis() - device.getDate() >= TIMEOUT) {
                        lockList.remove(i);
                        length--;
                    }
                }
            }
        }
    }

    @Override
    public DeviceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.lock_add_list_item, parent, false);
        return new DeviceViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(DeviceViewHolder _holder, int position) {
        final ExtendedBluetoothDevice item = mDataList.get(position);

        _holder.tv_lock_name.setText(item.getName());
        _holder.iv_setting_mode.setVisibility(item.isSettingMode() ? View.VISIBLE : View.GONE);

        if (item.isSettingMode()) {
            ChatApplication.logDisplay("item click mode ");
            _holder.tv_lock_name.setOnClickListener(view -> {
                if (!item.isSettingMode()) {
                    return;
                }
                if (mListener != null) {
                    mListener.onClick(item);
                }

            });
            _holder.iv_setting_mode.setOnClickListener(view -> {
                if (!item.isSettingMode()) {
                    return;
                }
                if (mListener != null) {
                    mListener.onClick(item);
                }

            });
        }
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    class DeviceViewHolder extends RecyclerView.ViewHolder {

        View view;
        ImageView iv_setting_mode;
        TextView tv_lock_name;

        public DeviceViewHolder(View itemView) {
            super(itemView);
            this.view = itemView;
            tv_lock_name = itemView.findViewById(R.id.tv_lock_name);
            iv_setting_mode = itemView.findViewById(R.id.iv_setting_mode);

        }

    }

}
