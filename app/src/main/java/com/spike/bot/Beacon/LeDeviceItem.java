package com.spike.bot.Beacon;

import uk.co.alt236.bluetoothlelib.device.BluetoothLeDevice;

public class LeDeviceItem implements RecyclerViewItem {

    private final BluetoothLeDevice device;

    public boolean isRssRange() {
        return rssRange;
    }

    public void setRssRange(boolean rssRange) {
        this.rssRange = rssRange;
    }

    private  boolean rssRange =false;

    public boolean isOnOff() {
        return onOff;
    }

    public void setOnOff(boolean onOff) {
        this.onOff = onOff;
    }

    private boolean onOff;

    public LeDeviceItem(final BluetoothLeDevice device) {
        this.device = device;
    }

    public BluetoothLeDevice getDevice() {
        return device;
    }
}