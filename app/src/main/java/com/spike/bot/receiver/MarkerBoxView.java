package com.spike.bot.receiver;

import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import com.spike.bot.R;

public class MarkerBoxView extends MarkerView {

    private TextView tvContent;

//    public MyMarkerView(Context context, int layoutResource) {
//        super(context, layoutResource);
//
//
//    }

    /**
     * Constructor. Sets up the MarkerView with a custom layout resource.
     *
     * @param context
     * @param layoutResource the layout resource to use for the MarkerView
     */
    public MarkerBoxView(Context context, int layoutResource) {
        super(context, layoutResource);
        // find your layout components
        tvContent =  findViewById(R.id.txtCurrentValue);
    }

    // callbacks everytime the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    @Override
    public void refreshContent(Entry e, Highlight highlight) {

        int value= (int) e.getY();
//        int valuex= (int) e.getX();

        tvContent.setText(""+value);

        // this will perform necessary layouting
        super.refreshContent(e, highlight);
    }

    private MPPointF mOffset;

    @Override
    public MPPointF getOffset() {

        if(mOffset == null) {
           // center the marker horizontally and vertically
           mOffset = new MPPointF(-(getWidth() / 2), -getHeight());
        }

        return mOffset;
    }
}