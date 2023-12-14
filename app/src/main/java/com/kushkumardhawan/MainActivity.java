package com.kushkumardhawan;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.os.Bundle;
import android.widget.TimePicker;
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
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private LineChart lineChart;
      String selectedValue = null;

      FloatingActionButton rnnFab , distance;

    Spinner channel;

    private EditText dateOneEditText, timeOneEditText, dateTwoEditText, timeTwoEditText;
    private Button viewViaTimeStampButton;
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Retrieve the file name from the Intent
        String selectedFileName = getIntent().getStringExtra("FILE_NAME");
       // System.out.println(selectedFileName);

        rnnFab = findViewById(R.id.rnn);
        distance = findViewById(R.id.distance);


        dateOneEditText = findViewById(R.id.date_one);
        timeOneEditText = findViewById(R.id.time_one);
        dateTwoEditText = findViewById(R.id.date_two);
        timeTwoEditText = findViewById(R.id.time_two);


        lineChart = findViewById(R.id.lineChart);
        channel = findViewById(R.id.channel);
        AssetManager assetManager = getAssets();
        lineChart.setScaleEnabled(true);
        lineChart.setPinchZoom(true);

        // Set up Calendar instance
        calendar = Calendar.getInstance();

        // Set onClickListener for Date and Time pickers
        setDateTimePickerListeners();

        viewViaTimeStampButton = findViewById(R.id.old);
        viewViaTimeStampButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {




            }
        });

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



        distance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /**
                 * Show Distance Chart
                 */
                Intent mainIntent = new Intent(MainActivity.this, DistanceGraph.class);
                mainIntent.putExtra("FILE_NAME", selectedFileName);
                startActivity(mainIntent);
            }
        });

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

                // Get the values and add them to the list
                String fromDate = getFormattedDateTime(dateOneEditText, timeOneEditText);
                String toDate = getFormattedDateTime(dateTwoEditText, timeTwoEditText);

                if(!fromDate.isEmpty() && !toDate.isEmpty()){

                    /**
                     * pass the date and Time too
                     */
                    Toast.makeText(MainActivity.this, fromDate + "\t" + toDate, Toast.LENGTH_SHORT).show();
                    System.out.println(fromDate+":00");
                    System.out.println(toDate+":00");
                    new LoadAndPlotDataTaskDateTime(MainActivity.this, lineChart, assetManager, selectedValue, selectedFileName, fromDate+":00", toDate+":00").execute();

                }else{
                    new LoadAndPlotDataTask(MainActivity.this, lineChart, assetManager, selectedValue, selectedFileName).execute();
                }

 }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Handle the case where nothing is selected (optional)
            }
        });







    }

    private void setDateTimePickerListeners() {
        dateOneEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(dateOneEditText);
            }
        });

        timeOneEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker(timeOneEditText);
            }
        });

        dateTwoEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(dateTwoEditText);
            }
        });

        timeTwoEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker(timeTwoEditText);
            }
        });
    }

    private void showDatePicker(final EditText editText) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        calendar.set(year, month, dayOfMonth);
                        updateEditText(editText);
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void showTimePicker(final EditText editText) {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);
                        updateEditText(editText);
                    }
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                false
        );
        timePickerDialog.show();
    }

    private void updateEditText(EditText editText) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        if (editText == dateOneEditText || editText == dateTwoEditText) {
            editText.setText(dateFormat.format(calendar.getTime()));
        } else if (editText == timeOneEditText || editText == timeTwoEditText) {
            editText.setText(timeFormat.format(calendar.getTime()));
        }
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
        LineDataSet dataSet = new LineDataSet(entries, "Deformation Data (mm)");

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

    private String getFormattedDateTime(EditText dateEditText, EditText timeEditText) {
        String dateText = dateEditText.getText().toString();
        String timeText = timeEditText.getText().toString();

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

            Calendar dateTimeCalendar = Calendar.getInstance();
            dateTimeCalendar.setTime(dateFormat.parse(dateText));
            dateTimeCalendar.set(Calendar.HOUR_OF_DAY, timeFormat.parse(timeText).getHours());
            dateTimeCalendar.set(Calendar.MINUTE, timeFormat.parse(timeText).getMinutes());
            // Set seconds to 00
            dateTimeCalendar.set(Calendar.SECOND, 0);

            return dateFormat.format(dateTimeCalendar.getTime()) + " " + timeFormat.format(dateTimeCalendar.getTime());
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }


}