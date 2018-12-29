package com.spike.bot.ack;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class SocketManager {

    /**
     * The constant STATE_CONNECTING.
     */
    public static final int STATE_CONNECTING = 1;
    /**
     * The constant STATE_CONNECTED.
     */
    public static final int STATE_CONNECTED = 2;
    /**
     * The constant STATE_DISCONNECTED.
     */
    public static final int STATE_DISCONNECTED = 3;

    /**
     * The constant CONNECTING.
     */
    public static final String CONNECTING = "Connecting";
    /**
     * The constant CONNECTED.
     */
    public static final String CONNECTED = "Connected";
    /**
     * The constant DISCONNECTED.
     */
    public static final String DISCONNECTED = "Disconnected";

    private static SocketManager instance;

    private SocketManager() {
    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public synchronized static SocketManager getInstance() {
        if (instance == null) {
            instance = new SocketManager();
        }
        return instance;
    }

    /**
     * The constant TAG.
     */
    public static final String TAG = "SocketManager";
    private Socket socket;
    private List<OnSocketConnectionListener> onSocketConnectionListenerList;

    /**
     * Connect socket.
     *
     * @param host   the host
     */
    public void connectSocket(String host) {
        try {
            if(socket==null){
                IO.Options opts = new IO.Options();
                opts.forceNew = true;
                opts.reconnection = true;
                opts.reconnectionAttempts=5;
                opts.secure = true;
                //opts.query = "token=" + token + "&" + "user_id=" + userId;
                socket = IO.socket(host, opts);

                socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        fireSocketStatus(SocketManager.STATE_CONNECTED);
                        Log.i(TAG, "socket connected : " + socket.id());
                    }
                }).on(Socket.EVENT_RECONNECTING, new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        Log.e(TAG, "Socket reconnecting");
                        fireSocketStatus(SocketManager.STATE_CONNECTING);
                    }
                }).on(Socket.EVENT_RECONNECT_FAILED, new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        Log.e(TAG, "Socket reconnection failed");
//                        fireSocketStatusIntent(SocketManager.STATE_DISCONNECTED);
                    }
                }).on(Socket.EVENT_RECONNECT_ERROR, new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        Log.e(TAG, "Socket reconnection error");
//                        fireSocketStatus(SocketManager.STATE_DISCONNECTED);
                    }
                }).on(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        Log.e(TAG, "Socket connect error");
                        fireSocketStatus(SocketManager.STATE_DISCONNECTED);
                        socket.disconnect();
                    }
                }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        Log.e(TAG, "Socket disconnect event");
                        fireSocketStatus(SocketManager.STATE_DISCONNECTED);
                    }
                }).on(Socket.EVENT_ERROR, new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        try {
                            final String error = (String) args[0];
                            Log.e(TAG,"error EVENT_ERROR :" + error);
                            if (error.contains("Unauthorized") && !socket.connected()) {
                                if (onSocketConnectionListenerList != null) {
                                    for (final OnSocketConnectionListener listener : onSocketConnectionListenerList) {
                                        new Handler(Looper.getMainLooper())
                                                .post(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        listener.onSocketEventFailed();
                                                    }
                                                });
                                    }
                                }
                            }
                        } catch (Exception e) {
                            Log.e(TAG, e.getMessage() != null ? e.getMessage() : "");
                        }
                    }
                }).on("Error", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        Log.d(TAG, " Error");
                    }
                });
                socket.connect();
            }else if(!socket.connected()){
                socket.connect();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int lastState = -1;

    /**
     * Fire socket status intent.
     *
     * @param socketState the socket state
     */
    public synchronized void fireSocketStatus(final int socketState) {
        if(onSocketConnectionListenerList !=null && lastState!=socketState){
            lastState = socketState;
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    for(OnSocketConnectionListener listener: onSocketConnectionListenerList){
                        listener.onSocketConnectionStateChange(socketState);
                    }
                }
            });
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    lastState=-1;
                }
            },1000);
        }
    }

    /**
     * Fire internet status intent.
     *
     * @param socketState the socket state
     */
    public synchronized void fireInternetStatusIntent(final int socketState) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if(onSocketConnectionListenerList !=null){
                    for(OnSocketConnectionListener listener: onSocketConnectionListenerList){
                        listener.onInternetConnectionStateChange(socketState);
                    }
                }
            }
        });
    }

    /**
     * Gets socket.
     *
     * @return the socket
     */
    public Socket getSocket() {
        return socket;
    }

    /**
     * Sets socket.
     *
     * @param socket the socket
     */
    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    /**
     * Destroy.
     */
    public void destroy(){
        if (socket != null) {
            socket.off();
            socket.disconnect();
            socket.close();
            socket=null;
        }
    }

    /**
     * Sets socket connection listener.
     *
     * @param onSocketConnectionListenerListener the on socket connection listener listener
     */
    public void setSocketConnectionListener(OnSocketConnectionListener onSocketConnectionListenerListener) {
        if(onSocketConnectionListenerList ==null){
            onSocketConnectionListenerList = new ArrayList<>();
            onSocketConnectionListenerList.add(onSocketConnectionListenerListener);
        }else if(!onSocketConnectionListenerList.contains(onSocketConnectionListenerListener)){
            onSocketConnectionListenerList.add(onSocketConnectionListenerListener);
        }
    }

    /**
     * Remove socket connection listener.
     *
     * @param onSocketConnectionListenerListener the on socket connection listener listener
     */
    public void removeSocketConnectionListener(OnSocketConnectionListener onSocketConnectionListenerListener) {
        if(onSocketConnectionListenerList !=null
                && onSocketConnectionListenerList.contains(onSocketConnectionListenerListener)){
            onSocketConnectionListenerList.remove(onSocketConnectionListenerListener);
        }
    }

    /**
     * Remove all socket connection listener.
     */
    public void removeAllSocketConnectionListener() {
        if(onSocketConnectionListenerList !=null){
            onSocketConnectionListenerList.clear();
        }
    }

    /**
     * The type Net receiver.
     */
    public static class NetReceiver extends BroadcastReceiver {

        /**
         * The Tag.
         */
        public final String TAG = NetReceiver.class.getSimpleName();

        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager cm =
                    (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null &&
                    activeNetwork.isConnectedOrConnecting();

            SocketManager.getInstance().fireInternetStatusIntent(
                    isConnected?SocketManager.STATE_CONNECTED:SocketManager.STATE_DISCONNECTED);
            if (isConnected) {
                if(SocketManager.getInstance().getSocket()!=null
                        && !SocketManager.getInstance().getSocket().connected()){
                    SocketManager.getInstance().fireSocketStatus(SocketManager.STATE_CONNECTING);
                }
                PowerManager powerManager =
                        (PowerManager) context.getSystemService(Context.POWER_SERVICE);
                boolean isScreenOn;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT_WATCH) {
                    isScreenOn = powerManager.isInteractive();
                }else{
                    //noinspection deprecation
                    isScreenOn = powerManager.isScreenOn();
                }

                if (isScreenOn && SocketManager.getInstance().getSocket() !=null) {
                    Log.d(TAG, "NetReceiver: Connecting Socket");
                    if(!SocketManager.getInstance().getSocket().connected()){
                        SocketManager.getInstance().getSocket().connect();
                    }
                }
            }else{
                SocketManager.getInstance().fireSocketStatus(SocketManager.STATE_DISCONNECTED);
                if(SocketManager.getInstance().getSocket() !=null){
                    Log.d(TAG, "NetReceiver: disconnecting socket");
                    SocketManager.getInstance().getSocket().disconnect();
                }
            }
        }
    }
}