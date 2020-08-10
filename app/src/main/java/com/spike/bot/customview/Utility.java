package com.spike.bot.customview;

import android.content.Context;
import android.util.DisplayMetrics;

public class Utility {
    /* below layout height and width connected with sectionexpandablegridadpater and roomdetailactivity*/
    public static int calculateNoOfColumns(Context context, float columnWidthDp) { // For example columnWidthdp=180
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float screenWidthDp = displayMetrics.widthPixels / displayMetrics.density;
        int noOfColumns = (int) (screenWidthDp / columnWidthDp) ; // +0.5 for correct rounding to int.
        return noOfColumns;
    }
}