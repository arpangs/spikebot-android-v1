package com.spike.bot.customview;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * Created by Sagar on 5/3/18.
 * Gmail : jethvasagar2@gmail.com
 */

public class WrapContentGriLayoutManager extends GridLayoutManager{
    public WrapContentGriLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public WrapContentGriLayoutManager(Context context, int spanCount) {
        super(context, spanCount);
    }

    public WrapContentGriLayoutManager(Context context, int spanCount, int orientation, boolean reverseLayout) {
        super(context, spanCount, orientation, reverseLayout);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        try {
            super.onLayoutChildren(recycler, state);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }

    @Override public boolean supportsPredictiveItemAnimations() { return false; }
}
