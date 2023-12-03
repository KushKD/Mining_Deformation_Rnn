package com.kushkumardhawan;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

public class CustomMarkerView extends MarkerView {

    private TextView tvContent;
    private Drawable arrow;

    public CustomMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);

        // Initialize UI components
        tvContent = findViewById(R.id.tvContent);
        arrow = getResources().getDrawable(R.drawable.ic_arrow);
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        // Set the content to be displayed in the marker
        tvContent.setText("Y: " + e.getY());

        // Call super to refresh the content
        super.refreshContent(e, highlight);
    }

    @Override
    public void draw(Canvas canvas, float posX, float posY) {
        // Translate the marker view to the correct position
        posX += getWidth() / 2;
        posY -= getHeight();

        // Draw the marker background
        super.draw(canvas, posX, posY);

        // Draw the arrow at the top of the marker
        arrow.setBounds((int) (posX - arrow.getIntrinsicWidth() / 2), (int) posY, (int) (posX + arrow.getIntrinsicWidth() / 2), (int) posY + arrow.getIntrinsicHeight());
        arrow.draw(canvas);
    }

    @Override
    public MPPointF getOffset() {
        // Adjust the offset of the marker view
        return new MPPointF(-(getWidth() / 2), -getHeight());
    }
}

