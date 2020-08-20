package com.spike.bot.activity.HeavyLoad;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.text.DecimalFormat;

public class MyFormater implements IAxisValueFormatter {

    @Override
    public String getFormattedValue(float value, AxisBase axis) {

        return "" + Math.round(value);
    }
}
