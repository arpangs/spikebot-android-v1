package com.spike.bot.ack;

import android.util.Log;

import com.spike.bot.ChatApplication;

import java.util.Timer;
import java.util.TimerTask;

import javax.net.ssl.SSLContext;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class Connection extends TimerTask {
    private Timer timer = new Timer(true);

    private Socket socket;
    private Status status = Status.connecting;
    private Object lock;
    private int count = 0;
    private static String TAG = "SocketTimerTask";

    enum Status {
        connecting,
        connected,
        disconnect,
        non,
    }

    public void start() {
        this.lock = new Object();
        timer.schedule(this, 0, 1 * 500);
    }

    @Override
    public void run() {
        if (status == Status.connecting) {
            Log.i(TAG, "on connecting");
            status = Status.non;
            if (socket == null) {
                try {
                    String uri = ChatApplication.url;
                    IO.Options opts = new IO.Options();
                    opts.forceNew = false;
                    opts.reconnection = false;
                    opts.sslContext = SSLContext.getDefault();
                    opts.transports = new String[]{"websocket"};

                    socket = IO.socket(uri, opts);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
                    @Override
                    public void call(Object... argsg) {
                        Log.i(TAG, "on connect " + count);
                        status = Status.connected;
                    }

                });
                socket.on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
                    @Override
                    public void call(Object... argsg) {
                        Log.i(TAG, "on disconnect " + count);
                        socket.off();
                        socket = null;
                        if (status == Status.disconnect) {
                            status = Status.connecting;
                        }
                    }

                });
                Log.i(TAG, "socket connect start");
                socket.connect();
            }
            count++;
        } else if (status == Status.connected) {
            status = Status.disconnect;
            if (socket != null) {
                Log.i(TAG, "disconnect start");
                socket.disconnect();
            }
        }
    }
}