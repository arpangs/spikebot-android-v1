package com.spike.bot.activity.HeavyLoad;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.text.DecimalFormat;

public class MyFormater implements IAxisValueFormatter {

    private DecimalFormat mFormat;

    public MyFormater() {
        mFormat = new DecimalFormat("###,###,##0");
    }


    @Override
    public String getFormattedValue(float value, AxisBase axis) {

        return "" + (int) (value);
    }
}
