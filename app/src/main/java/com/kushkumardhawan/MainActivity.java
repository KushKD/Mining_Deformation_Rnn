package com.kushkumardhawan;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.os.Bundle;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.kushkumardhawan.modal.DeformationModal;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private LineChart lineChart;
      String selectedValue = null;

      FloatingActionButton rnnFab;

    Spinner channel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Retrieve the file name from the Intent
        String selectedFileName = getIntent().getStringExtra("FILE_NAME");
       // System.out.println(selectedFileName);

        rnnFab = findViewById(R.id.rnn);



        lineChart = findViewById(R.id.lineChart);
        channel = findViewById(R.id.channel);
        AssetManager assetManager = getAssets();
        lineChart.setScaleEnabled(true);
        lineChart.setPinchZoom(true);

        rnnFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /**
                 * Show Velocity Chart
                 */
                Intent mainIntent = new Intent(MainActivity.this, RNNPrediction.class);
                mainIntent.putExtra("FILE_NAME", selectedFileName);
                startActivity(mainIntent);


            }
        });

        channel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Handle the selected item change here
                 selectedValue = channel.getSelectedItem().toString();

                if(selectedValue.equalsIgnoreCase("Channel 1")){
                    selectedValue ="1001";
                }
                if(selectedValue.equalsIgnoreCase("Channel 2")){
                    selectedValue ="2001";
                }
                if(selectedValue.equalsIgnoreCase("Channel 3")){
                    selectedValue ="3001";
                }
                if(selectedValue.equalsIgnoreCase("Channel 4")){
                    selectedValue ="4001";
                }
                if(selectedValue.equalsIgnoreCase("Channel 5")){
                    selectedValue ="5001";
                }
                if(selectedValue.equalsIgnoreCase("Channel 6")){
                    selectedValue ="6001";
                }
                if(selectedValue.equalsIgnoreCase("Channel ")){
                    selectedValue ="7001";
                }
                if(selectedValue.equalsIgnoreCase("Channel 8")){
                    selectedValue ="8001";
                }
                // Do something with the selected value
                //Toast.makeText(getApplicationContext(), "Selected: " + selectedValue, Toast.LENGTH_SHORT).show();
                new LoadAndPlotDataTask(MainActivity.this, lineChart, assetManager, selectedValue, selectedFileName).execute();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Handle the case where nothing is selected (optional)
            }
        });







        /**
         * Calculate Velocity
         */
//        for (int i = 0; i < modal.size(); i++) {
//            Double deformationMax = modal.get(i).getDeformationMax();
//            String TimeStamp = modal.get(i).getTimeStamp();
//
//            // Ensure that Velocity is not zero to avoid division by zero
//            if (deformationMax != 0) {
//                Double velocity = deformationMax / 300;
//                Double inverseVelocity = 1 / velocity;
//
//                System.out.println("TimeStamp: " + TimeStamp +" Velocity: " + velocity + ", Inverse Velocity: " + inverseVelocity);
//            } else {
//                System.out.println("Cannot calculate inverse velocity because deformationMax is zero.");
//            }
//        }


    }



    // Method to plot the deformation data in the LineChart

    public void plotDeformationData(List<DeformationModal> deformationModalList) {
        if (deformationModalList == null || deformationModalList.isEmpty()) {
            // Handle empty or null list
            return;
        }

        // Creating an Entry list for LineChart
        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < deformationModalList.size(); i++) {
            DeformationModal modal = deformationModalList.get(i);

            // Convert timestamp to a numerical value (you might want to use a timestamp formatter)
            long timestampInMillis = convertTimestampToMillis(modal.getTimeStamp());

            // Add an Entry with timestamp on the X-axis and deformationMax on the Y-axis
            entries.add(new Entry(timestampInMillis, modal.getDeformationMax().floatValue()));
        }

        // Creating a LineDataSet
        LineDataSet dataSet = new LineDataSet(entries, "Deformation Data");

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