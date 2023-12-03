package com.kushkumardhawan;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimestampAxisValueFormatter extends ValueFormatter {
    @Override
    public String getAxisLabel(float value, AxisBase axis) {
        // Convert timestamp in milliseconds to a formatted string
        long timestampMillis = (long) value;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date(timestampMillis));
    }
}