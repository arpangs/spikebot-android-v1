package com.spike.bot.core;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import com.spike.bot.model.IRDeviceDetailsRes;

import java.util.List;

public class BeaconAPIDiffCallBack extends DiffUtil.Callback {

    private List<IRDeviceDetailsRes.Data> mOldscanresultList;
    private List<IRDeviceDetailsRes.Data> mNewscanresultList;

    public BeaconAPIDiffCallBack(List<IRDeviceDetailsRes.Data> oldscanresultList, List<IRDeviceDetailsRes.Data> newscanresultList) {
        this.mOldscanresultList = oldscanresultList;
        this.mNewscanresultList = newscanresultList;
    }

    @Override
    public int getOldListSize() {
        return mOldscanresultList.size();
    }

    @Override
    public int getNewListSize() {
        return mNewscanresultList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return mOldscanresultList.get(oldItemPosition).getMac() == mNewscanresultList.get(newItemPosition).getMac();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        final IRDeviceDetailsRes.Data oldresult = mOldscanresultList.get(oldItemPosition);
        final IRDeviceDetailsRes.Data newresult = mNewscanresultList.get(newItemPosition);

        return oldresult.getMac().equals(newresult.getMac());
    }


    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        // Implement method if you're going to use ItemAnimator
        return super.getChangePayload(oldItemPosition, newItemPosition);
    }

}