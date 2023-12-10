package com.kushkumardhawan;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

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
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RNNPrediction extends AppCompatActivity {


    private CombinedChart combinedChart;
    String selectedValue = null;

    private Handler handler;
    private boolean isBlinking = false;

    Spinner channel;

    private EditText dateOneEditText, timeOneEditText, dateTwoEditText, timeTwoEditText;
    private Button viewViaTimeStampButton;
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rnnprediction);

        // Retrieve the file name from the Intent
        String selectedFileName = getIntent().getStringExtra("FILE_NAME");

        dateOneEditText = findViewById(R.id.date_one);
        timeOneEditText = findViewById(R.id.time_one);
        dateTwoEditText = findViewById(R.id.date_two);
        timeTwoEditText = findViewById(R.id.time_two);

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

                String fromDate = getFormattedDateTime(dateOneEditText, timeOneEditText);
                String toDate = getFormattedDateTime(dateTwoEditText, timeTwoEditText);

                if(!fromDate.isEmpty() && !toDate.isEmpty()){

                    /**
                     * pass the date and Time too
                     */
                    Toast.makeText(RNNPrediction.this, fromDate + "\t" + toDate, Toast.LENGTH_SHORT).show();
                    System.out.println(fromDate+":00");
                    System.out.println(toDate+":00");
                    new LoadAndPlotRNNTaskDateTime(RNNPrediction.this, combinedChart, assetManager, selectedValue, selectedFileName, fromDate+":00", toDate+":00").execute();

                }else{
                    new LoadAndPlotRNNTask(RNNPrediction.this, combinedChart, assetManager, selectedValue, selectedFileName).execute();

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
//            long timestampInMillis = convertDateStringToTimestamp(modal.getTimeStamp());
//            lineEntries.add(new Entry(timestampInMillis, modal.getInverseVelocity().floatValue()));

            // Convert timestamp to a numerical value (you might want to use a timestamp formatter)
            long timestampInMillis = convertTimestampToMillis(modal.getTimeStamp());

            // Add an Entry with timestamp on the X-axis and deformationMax on the Y-axis
            lineEntries.add(new Entry(timestampInMillis, modal.getInverseVelocity().floatValue()));
            //scatterEntries.add(new Entry(timestampInMillis, modal.getInverseVelocity().floatValue()));
        }

        long specificTimestamp = convertDateStringToTimestamp("17-12-2029 23:00:00");
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


    private static long convertDateStringToTimestamp(String dateString) throws ParseException {
        SimpleDateFormat inputFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
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