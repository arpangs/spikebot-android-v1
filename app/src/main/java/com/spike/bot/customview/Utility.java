package com.spike.bot.customview;

import android.content.Context;
import android.util.DisplayMetrics;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utility {
    /* below layout height and width connected with sectionexpandablegridadpater and roomdetailactivity*/
    public static int calculateNoOfColumns(Context context, float columnWidthDp) { // For example columnWidthdp=180
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float screenWidthDp = displayMetrics.widthPixels / displayMetrics.density;
        int noOfColumns = (int) ((screenWidthDp / columnWidthDp) + (0.5)) ; // +0.5 for correct rounding to int.
        return noOfColumns;
    }

    public static String capitalizeFirstLetterWord(String capString) { /*dev arp add on 22 aug 2020*/
        StringBuffer capBuffer = new StringBuffer();
        Matcher capMatcher = Pattern.compile("([a-z])([a-z]*)", Pattern.CASE_INSENSITIVE).matcher(capString);
        while (capMatcher.find()) {
            capMatcher.appendReplacement(capBuffer, capMatcher.group(1).toUpperCase() + capMatcher.group(2).toLowerCase());
        }

        return capMatcher.appendTail(capBuffer).toString();
    }
}
