package com.kushkumardhawan;

import android.content.Context;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

import java.util.Map;

// Custom MarkerView class
public class MyMarkerView extends MarkerView {
    public MyMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);
    }

    // Override this method to customize the marker appearance
    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        // Customize the content if needed
    }

    // Override this method to set the offset of the marker
    @Override
    public MPPointF getOffset() {
        return new MPPointF(-getWidth() / 2, -getHeight());
    }
}