package com.spike.bot.listener;

/**
 * Created by Sagar on 2/6/18.
 * Gmail : jethvasagar2@gmail.com
 */

public interface SocketConnectListener {
    void onSocketConnect();
    void onSocketDisconnect();
    void onSocketFail();
}
