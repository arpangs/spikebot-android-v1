package com.spike.bot.core;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import java.util.List;

import no.nordicsemi.android.support.v18.scanner.ScanResult;

public class BeaconDiffCallBack extends DiffUtil.Callback {

    private List<ScanResult> mOldscanresultList;
    private List<ScanResult> mNewscanresultList;

    public BeaconDiffCallBack(List<ScanResult> oldscanresultList, List<ScanResult> newscanresultList) {
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
        return mOldscanresultList.get(oldItemPosition).getDevice().getAddress() == mNewscanresultList.get(newItemPosition).getDevice().getAddress();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        final ScanResult oldresult = mOldscanresultList.get(oldItemPosition);
        final ScanResult newresult = mNewscanresultList.get(newItemPosition);

        return oldresult.getDevice().getAddress().equals(newresult.getDevice().getAddress());
    }

   /* @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return mNewscanresultList.get(newItemPosition).getDevice().getAddress() == mOldscanresultList.get(oldItemPosition).getDevice().getAddress();
    }*/


    /*@Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        // Implement method if you're going to use ItemAnimator
     //   return super.getChangePayload(oldItemPosition, newItemPosition);

        ScanResult newScanresult = mNewscanresultList.get(newItemPosition);
        ScanResult oldScanresult = mOldscanresultList.get(oldItemPosition);

        Bundle diff = new Bundle();

        if (newScanresult.getDevice().getAddress() != (oldScanresult.getDevice().getAddress())) {
            diff.putString("address", newScanresult.getDevice().getAddress());
        }
        if (diff.size() == 0) {
            return null;
        }
        return diff;
    }*/


    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        // Implement method if you're going to use ItemAnimator
        return super.getChangePayload(oldItemPosition, newItemPosition);
    }
}

