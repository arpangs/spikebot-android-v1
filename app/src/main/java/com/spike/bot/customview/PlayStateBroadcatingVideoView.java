package com.spike.bot.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

public class PlayStateBroadcatingVideoView extends VideoView{
    public interface PlayPauseListener {
        void onPlay();
        void onPause();
    }

    private PlayPauseListener mListener;

    public PlayStateBroadcatingVideoView(Context context) {
        super(context);
    }

    public PlayStateBroadcatingVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PlayStateBroadcatingVideoView(Context context, AttributeSet attrs, int theme) {
        super(context, attrs, theme);
    }

    @Override
    public void pause() {
        super.pause();
        if(mListener != null) {
            mListener.onPause();
        }
    }

    @Override
    public void start() {
        super.start();
        if(mListener != null) {
            mListener.onPlay();
        }
    }

    public void setPlayPauseListener(PlayPauseListener listener) {
        mListener = listener;
    }

}