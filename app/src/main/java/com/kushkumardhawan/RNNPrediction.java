package com.kushkumardhawan;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.AssetManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.ScatterChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.data.ScatterDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.kushkumardhawan.modal.DeformationModal;
import com.kushkumardhawan.modal.VelocityData;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RNNPrediction extends AppCompatActivity {


    private CombinedChart combinedChart;
    String selectedValue = null;

    private Handler handler;
    private boolean isBlinking = false;

    Spinner channel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rnnprediction);

        // Retrieve the file name from the Intent
        String selectedFileName = getIntent().getStringExtra("FILE_NAME");

        combinedChart = findViewById(R.id.combinedChart);
        channel = findViewById(R.id.channel);
        AssetManager assetManager = getAssets();
        combinedChart.setScaleEnabled(true);
        combinedChart.setPinchZoom(true);
        combinedChart.setDrawOrder(new CombinedChart.DrawOrder[]{
                CombinedChart.DrawOrder.LINE, CombinedChart.DrawOrder.SCATTER
        });
       // new LoadAndPlotRNNTask(RNNPrediction.this, combinedChart, assetManager, "1001", selectedFileName).execute();



        channel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Handle the selected item change here
                selectedValue = channel.getSelectedItem().toString();

                if(selectedValue.equalsIgnoreCase("Probe 1")){
                    selectedValue ="1001";
                }
                if(selectedValue.equalsIgnoreCase("Probe 2")){
                    selectedValue ="2001";
                }
                if(selectedValue.equalsIgnoreCase("Probe 3")){
                    selectedValue ="3001";
                }
                if(selectedValue.equalsIgnoreCase("Probe 4")){
                    selectedValue ="4001";
                }
                if(selectedValue.equalsIgnoreCase("Probe 5")){
                    selectedValue ="5001";
                }
                if(selectedValue.equalsIgnoreCase("Probe 6")){
                    selectedValue ="6001";
                }
                if(selectedValue.equalsIgnoreCase("Probe 7")){
                    selectedValue ="7001";
                }
                if(selectedValue.equalsIgnoreCase("Probe 8")){
                    selectedValue ="8001";
                }
                // Do something with the selected value
                //Toast.makeText(getApplicationContext(), "Selected: " + selectedValue, Toast.LENGTH_SHORT).show();
               new LoadAndPlotRNNTask(RNNPrediction.this, combinedChart, assetManager, selectedValue, selectedFileName).execute();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Handle the case where nothing is selected (optional)
            }
        });
    }

    public void plotInverseVelocityWithPrediction(List<VelocityData> velocityDataList) throws ParseException {
        if (velocityDataList == null || velocityDataList.isEmpty()) {
            // Handle empty or null list
            return;
        }

        // Creating an Entry list for LineChart
        List<Entry> lineEntries = new ArrayList<>();
        List<Entry> scatterEntries = new ArrayList<>();

        for (int i = 0; i < velocityDataList.size(); i++) {
            VelocityData modal = velocityDataList.get(i);
            long timestampInMillis = convertDateStringToTimestamp(modal.getTimeStamp());
            lineEntries.add(new Entry(timestampInMillis, modal.getInverseVelocity().floatValue()));
            //scatterEntries.add(new Entry(timestampInMillis, modal.getInverseVelocity().floatValue()));
        }

        long specificTimestamp = convertDateStringToTimestamp("12/17/2029 23:00");
        //lineEntries.add(new Entry(specificTimestamp, 0f));
        scatterEntries.add(new Entry(specificTimestamp, 0f));

        LineDataSet lineDataSet = new LineDataSet(lineEntries, "Actual Inverse Velocity (1/mm/sec)");


//        if(selectedValue.equalsIgnoreCase("1001")){
            lineDataSet.setColor(Color.parseColor("#2596be"));
            lineDataSet.setCircleColor(Color.parseColor("#1f77b4"));
        //}
//        if(selectedValue.equalsIgnoreCase("2001")){
//            lineDataSet.setColor(Color.RED);
//            lineDataSet.setCircleColor(Color.RED);
//        }
//        if(selectedValue.equalsIgnoreCase("3001")){
//            lineDataSet.setColor(Color.GREEN);
//            lineDataSet.setCircleColor(Color.GREEN);
//        }
//        if(selectedValue.equalsIgnoreCase("4001")){
//            lineDataSet.setColor(Color.BLUE);
//            lineDataSet.setColor(Color.BLUE);
//        }
//        if(selectedValue.equalsIgnoreCase("5001")){
//            lineDataSet.setColor(Color.MAGENTA);
//            lineDataSet.setColor(Color.MAGENTA);
//        }
//        if(selectedValue.equalsIgnoreCase("6001")){
//            lineDataSet.setColor(Color.CYAN);
//            lineDataSet.setColor(Color.CYAN);
//        }
//        if(selectedValue.equalsIgnoreCase("7001")){
//            lineDataSet.setColor(Color.BLACK);
//            lineDataSet.setColor(Color.BLACK);
//        }
//        if(selectedValue.equalsIgnoreCase("8001")){
//            lineDataSet.setColor(Color.LTGRAY);
//            lineDataSet.setColor(Color.LTGRAY);
//        }

        ScatterDataSet scatterDataSet = new ScatterDataSet(scatterEntries, "Prediction Failure Point");
        scatterDataSet.setColor(Color.RED);
        scatterDataSet.setScatterShape(ScatterChart.ScatterShape.CIRCLE);
        scatterDataSet.setScatterShapeSize(50f);

        LineData lineData = new LineData(lineDataSet);
        ScatterData scatterData = new ScatterData(scatterDataSet);

        CombinedData combinedData = new CombinedData();
        combinedData.setData(lineData);
        combinedData.setData(scatterData);

        combinedChart.setData(combinedData);

        XAxis xAxis = combinedChart.getXAxis();
        xAxis.setDrawGridLines(false); // Hide grid lines
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); // Place labels at the bottom
        xAxis.setGranularity(1f); // Set the granularity for labels
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                // Format the X-axis label as needed
                long timestampMillis = (long) value;
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.getDefault());
                return sdf.format(new Date(timestampMillis));
            }
        });

        YAxis leftAxis = combinedChart.getAxisLeft();
        leftAxis.setDrawGridLines(false); // Hide grid lines
        leftAxis.setLabelCount(5); // Set the number of labels you want
        leftAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                // Format the Y-axis label as needed
                return String.format(Locale.getDefault(), "%.2f", value);
            }
        });

        // Set a custom visible range for Y-axis (if needed)
      //  leftAxis.setAxisMinimum(0f); // Set the minimum value
        //leftAxis.setAxisMaximum(10f); // Set the maximum value

        // Add padding to the chart
       // combinedChart.setExtraOffsets(20f, 20f, 20f, 20f);

        // Set the visible Y-axis range
        //combinedChart.setVisibleYRangeMaximum(10f, YAxis.AxisDependency.LEFT);


        // startBlinkingEffect(lineEntries, scatterEntries);
        combinedChart.invalidate();
    }

    private static long convertDateStringToTimestamp(String dateString) throws ParseException {
        SimpleDateFormat inputFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.getDefault());
        Date date = inputFormat.parse(dateString);
        return date.getTime();
    }

    private void startBlinkingEffect(List<Entry> entries, List<Entry> entries_) {
        handler = new Handler(Looper.getMainLooper());
        isBlinking = true;

        handler.postDelayed(new Runnable() {
            private boolean isVisible = true;

            @Override
            public void run() {
                if (!entries.isEmpty()) {
                    int lastEntryIndex = entries.size() - 1;

                    ScatterDataSet scatterDataSet = new ScatterDataSet(null, "Prediction Failure Point");
                    scatterDataSet.setColor(Color.RED);
                    scatterDataSet.setScatterShape(ScatterChart.ScatterShape.CIRCLE);
                    scatterDataSet.setScatterShapeSize(10f);

                    // Add a scatter point for the last entry
                    scatterDataSet.addEntry(entries.get(lastEntryIndex));

                    ScatterData scatterData = new ScatterData(scatterDataSet);
                    CombinedData combinedData = combinedChart.getData();
                    combinedData.setData(scatterData);

                    // Highlight the last entry
                  //  combinedChart.highlightValue(lastEntryIndex, 0);

                    if (isBlinking) {
                        handler.postDelayed(this, 500);
                    }
                }
            }
        }, 500);
    }




    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopBlinkingEffect();
    }

    private void stopBlinkingEffect() {
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            isBlinking = false;
        }
    }
}