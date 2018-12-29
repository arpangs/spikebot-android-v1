package com.spike.bot.ack;

/**
 * Created by Sagar on 6/7/18.
 * Gmail : jethvasagar2@gmail.com
 */
public interface OnSocketConnectionListener {
    void onSocketEventFailed();
    void onSocketConnectionStateChange(int socketState);
    void onInternetConnectionStateChange(int socketState);
}
