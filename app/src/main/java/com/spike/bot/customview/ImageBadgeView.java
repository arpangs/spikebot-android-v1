package com.spike.bot.customview;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ImageView;

public class ImageBadgeView extends ImageView {

    private int mBadgeValue = 0;//Value to be displayed on the badge.
    private boolean mShowBadge = true;//Flag to show or hide badge.
    private int mBadgeColor = Color.WHITE;//Color of the badge.
    private int mBadgeTextColor = Color.parseColor("teal");//Color of the text in the badge.
    private int mBadgeRadius;//Badge radius in dp. Automatically adapts according to the width and
    // size of text.
    private int mBadgeTextSize = 9;//Size of text in dp.
    private int mTypeface = Typeface.BOLD;//Typeface of text.
    private float mScale;//Scale to convert pixel into dp and vice versa.
    private Paint mPaint;//Instance of Paint.
    private Context mContext;
    private boolean mLimitBadgeValue = true;//Badge Value Limit Flag. Set it true to show "+"
    // after a max value.
    private int mMaxBadgeValue = 9;//Max value to be displayed in badge if Badge Value Limit Flag
    // is set. For example in this case value greater than 9 will be displayed as "9+".


    public ImageBadgeView(Context context) {
        super(context);
        mContext = context;
        mBadgeValue = 0;
    }

    public ImageBadgeView(Context context,int badgeValue) {
        super(context);
        mContext = context;
        mBadgeValue = badgeValue;
    }

    public ImageBadgeView(Context context, AttributeSet attrs, int badgeValue) {
        super(context, attrs);
        mBadgeValue = badgeValue;
        
    }

    public ImageBadgeView(Context context, AttributeSet attrs, int defStyleAttr,int badgeValue) {
        super(context, attrs, defStyleAttr);
        mBadgeValue = badgeValue;
        
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ImageBadgeView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes,
                          int badgeValue) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mBadgeValue = badgeValue;
        
    }

    public void setBadgeValue(int badgeValue) {
        //Setter for badgeValue
        mBadgeValue = badgeValue;
        invalidate();
    }

    public int getBadgeValue() {
        //Getter for badgeValue
        return mBadgeValue;
    }

    public void showBadge(boolean showBadge){
        //Set false to hide badge.
        //Set true to show badge.
        mShowBadge = showBadge;
        invalidate();
    }

    public void setBadgeColor(int badgeColor) {
        //Setter for badge color
        mBadgeColor = badgeColor;
        invalidate();
    }

    public int getBadgeColor() {
        return mBadgeColor;
    }

    public void setBadgeTextColor(int badgeTextColor) {
        mBadgeTextColor = badgeTextColor;
        invalidate();
    }

    public int getBadgeTextColor() {
        return mBadgeTextColor;
    }

    public void setBadgeTextSize(int badgeTextSize) {
        mBadgeTextSize = badgeTextSize;
        mBadgeRadius = badgeTextSize*2/3;//Changing badge radius to fit new size.
        invalidate();
    }

    public int getBadgeTextSize() {
        return mBadgeTextSize;
    }

    public int getBadgeRadius() {
        return mBadgeRadius;
    }

    public void setTypeface(int typeface) {
        mTypeface = typeface;
        invalidate();
    }

    public void setMaxBadgeValue(int maxBadgeValue) {
        mMaxBadgeValue = maxBadgeValue;
        invalidate();
    }

    public int getMaxBadgeValue() {
        return mMaxBadgeValue;
    }

    public void setLimitBadgeValue(boolean badgeValueLimit){
        mLimitBadgeValue = badgeValueLimit;
        invalidate();
    }


    public int getTypeface() {
        return mTypeface;
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if(mShowBadge && mBadgeValue > 0 ){
            if(mPaint == null){
                mPaint = new Paint();
                Resources resources = mContext.getResources();
                mScale = resources.getDisplayMetrics().density;
                Typeface plain = mPaint.getTypeface();
                Typeface typeface = Typeface.create(plain, mTypeface);
                mPaint.setTypeface(typeface);
                mPaint.setTextSize(mBadgeTextSize*mScale);
            }
            float pivotX = getPivotX();
            float pivotY = getPivotY();
            int viewHeight = getMeasuredHeight();
            int viewWidth = getMeasuredWidth();
            mBadgeRadius = (mBadgeTextSize * 2) / 3;
            float textWidth;
            if (mLimitBadgeValue && mBadgeValue > mMaxBadgeValue) {
                textWidth = mPaint.measureText(mMaxBadgeValue + "+");
            } else {
                textWidth = mPaint.measureText(mBadgeValue + "");
            }
            if (textWidth > mBadgeRadius * 2 * mScale) {
                //changing radius of badge according to the width of text.
                mBadgeRadius = (int) ((textWidth * 2 / 3) / mScale);
            }
            float x = pivotX + viewWidth / 2 - mBadgeRadius * mScale;
            float y = pivotY - viewHeight / 2 + mBadgeRadius * mScale;
            mPaint.setColor(mBadgeColor);
            canvas.drawCircle(x, y, mBadgeRadius * mScale, mPaint);
            mPaint.setColor(mBadgeTextColor);
            if (mLimitBadgeValue && mBadgeValue > mMaxBadgeValue) {
                canvas.drawText(mMaxBadgeValue + "+", x - textWidth / 2,
                        y + mBadgeTextSize * mScale / 3, mPaint);
            } else {
                canvas.drawText(mBadgeValue + "", x - textWidth / 2,
                        y + mBadgeTextSize * mScale / 3, mPaint);
            }
        }
    }

    public void reset(){
        mMaxBadgeValue = 9;
        mBadgeValue = 0;
        mLimitBadgeValue = false;
        mTypeface = Typeface.BOLD;
        mBadgeColor = Color.WHITE;
        mBadgeTextSize = 12;
        mBadgeTextColor = Color.parseColor("teal");
        mShowBadge = true;
    }

}