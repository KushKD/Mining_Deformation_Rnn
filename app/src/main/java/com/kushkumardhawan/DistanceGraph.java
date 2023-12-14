package com.kushkumardhawan;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.AssetManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.kushkumardhawan.modal.DeformationModal;
import com.kushkumardhawan.modal.Distance;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DistanceGraph extends AppCompatActivity {

    private LineChart lineChart;
    String selectedValue = null;
    private Handler handler;
    Spinner channel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_distance_graph);

        // Retrieve the file name from the Intent
        String selectedFileName = getIntent().getStringExtra("FILE_NAME");

        lineChart = findViewById(R.id.combinedChart);
        AssetManager assetManager = getAssets();
        lineChart.setScaleEnabled(true);
        lineChart.setPinchZoom(true);
        channel = findViewById(R.id.channel);

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



                    new LoadAndPlotDistance(DistanceGraph.this, lineChart, assetManager, selectedValue, selectedFileName).execute();


            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Handle the case where nothing is selected (optional)
            }
        });
    }

    public void plotDeformationData(List<Distance> deformationModalList) {
        if (deformationModalList == null || deformationModalList.isEmpty()) {
            // Handle empty or null list
            return;
        }

        // Creating an Entry list for LineChart
        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < deformationModalList.size(); i++) {
            Distance modal = deformationModalList.get(i);

            // Convert timestamp to a numerical value (you might want to use a timestamp formatter)
            long timestampInMillis = convertTimestampToMillis(modal.getTimestamp());

            // Add an Entry with timestamp on the X-axis and deformationMax on the Y-axis
            entries.add(new Entry(timestampInMillis, (float) modal.getDistance()));
        }

        // Creating a LineDataSet
        LineDataSet dataSet = new LineDataSet(entries, "Total Distance (mm)");

        if(selectedValue.equalsIgnoreCase("1001")){
            dataSet.setColor(Color.RED);
            dataSet.setCircleColor(Color.RED);
        }
        if(selectedValue.equalsIgnoreCase("2001")){
            dataSet.setColor(Color.GREEN);
            dataSet.setCircleColor(Color.GREEN);
        }
        if(selectedValue.equalsIgnoreCase("3001")){
            dataSet.setColor(Color.DKGRAY);
            dataSet.setCircleColor(Color.DKGRAY);
        }
        if(selectedValue.equalsIgnoreCase("4001")){
            dataSet.setColor(Color.BLUE);
            dataSet.setCircleColor(Color.BLUE);
        }
        if(selectedValue.equalsIgnoreCase("5001")){
            dataSet.setColor(Color.MAGENTA);
            dataSet.setCircleColor(Color.MAGENTA);
        }
        if(selectedValue.equalsIgnoreCase("6001")){
            dataSet.setColor(Color.CYAN);
            dataSet.setCircleColor(Color.CYAN);
        }
        if(selectedValue.equalsIgnoreCase("7001")){
            dataSet.setColor(Color.BLACK);
            dataSet.setCircleColor(Color.BLACK);
        }
        if(selectedValue.equalsIgnoreCase("8001")){
            dataSet.setColor(Color.LTGRAY);
            dataSet.setCircleColor(Color.LTGRAY);
        }


        // Creating a LineData object
        LineData lineData = new LineData(dataSet);

        // Setting data to the LineChart
        lineChart.setData(lineData);

        // Setting X-axis value formatter for timestamps
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setValueFormatter(new TimestampAxisValueFormatter());

        // Notify the chart that the data has changed
        lineChart.invalidate();
    }



    private long convertTimestampToMillis(String timestamp) {
        // Remove double quotes from the timestamp string
        timestamp = timestamp.replace("\"", "");

        // Implement your timestamp to milliseconds conversion logic
        // Example: Using SimpleDateFormat
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date date = sdf.parse(timestamp);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }
}