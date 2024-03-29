package com.kushkumardhawan;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.kushkumardhawan.modal.DeformationModal;
import com.kushkumardhawan.modal.Distance;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import android.content.res.AssetManager;
import android.os.AsyncTask;

import Utilities.FileUtil;
import Utilities.Utilities;

// Other imports...

public class LoadAndPlotDataTask extends AsyncTask<Void, Void, List<DeformationModal>> {
    private WeakReference<MainActivity> activityReference;
    private LineChart lineChart;
    private AssetManager assetManager;
    private String Channel, SelectedFileName;

    private ProgressDialog progressDialog; // Add this line

    public LoadAndPlotDataTask(MainActivity activity, LineChart lineChart, AssetManager assetManager, String Channel_, String selectedFileName) {
        activityReference = new WeakReference<>(activity);
        this.lineChart = lineChart;
        this.assetManager = assetManager;
        this.Channel = Channel_;
        progressDialog = new ProgressDialog(activity);
        this.SelectedFileName = selectedFileName;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        // Show the ProgressDialog before starting the background task
        progressDialog.setMessage("Reading File and Plotting the Graph Data...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    @Override
    protected List<DeformationModal> doInBackground(Void... voids) {


        // Get the file path
        File selectedFile = new File(activityReference.get().getFilesDir() + "/downloaded_file.dat");


        // Read the file and perform calculations
       // String data = FileUtil.readLinesFromAsset(assetManager, "new_tdr.dat");

        // Read the contents of the file
        String data = readFileContents(selectedFile);

        // Continue with the rest of the code...

        Map<String, Map<String, Map<Integer, Double>>> dataMap = Utilities.saveDataInMap(data);
        int startWaveformNumber = 15;
        int endWaveformNumber = 265;
        String mux = Channel;
        List<String> timestampsToCalculate = Utilities.getSortedTimestampsForMux(dataMap, mux);

        Map<String, Map<Integer, Double>> deformationData = calculateAndStoreDeformationForTimeInterval(
                dataMap, mux, timestampsToCalculate, startWaveformNumber, endWaveformNumber);

         //  System.out.println("Deformation Data: " + deformationData);
         //Utilities.printNestedMap(deformationData);


        /**
         * Data for calculating the distance
         * For Mux = 1001
         */
        // Create a list to store the Distance objects
        ArrayList<Distance> distanceList = new ArrayList<>();
        // Iterate over each timestamp in the outer map
        for (String timestamp : deformationData.keySet()) {
            // Get the inner map for the current timestamp
            Map<Integer, Double> innerMap = deformationData.get(timestamp);

            if (innerMap != null) {
                // Sort the inner map based on keys (assuming keys represent time points)
                TreeMap<Integer, Double> sortedInnerMap = new TreeMap<>(innerMap);

                // Get the last 59 points
                int totalPoints = sortedInnerMap.size();
                int startPoint = Math.max(0, totalPoints - 59); // Adjust if total points are less than 59

                Map<Integer, Double> last59Points = new HashMap<>();

                // Iterate over the selected range and populate the last59Points map
                for (int i = startPoint; i < totalPoints; i++) {
                    int key = sortedInnerMap.keySet().toArray(new Integer[0])[i];
                    double value = sortedInnerMap.get(key);
                    last59Points.put(key, value);
                }

                // Find the maximum value among the last 59 points
                // double maxDeformation = last59Points.values().stream().mapToDouble(Double::doubleValue).max().orElse(0.0);

                // Apply the formula
                // Find the maximum value among the last 59 points
                double maxDeformation = last59Points.values().stream().mapToDouble(Double::doubleValue).max().orElse(0.0);

                // Find totalPointsBeforeMax using a loop
                int maxKey = -1;
                boolean foundMaxKey = false;

                for (int key : last59Points.keySet()) {
                    if (last59Points.get(key) == maxDeformation) {
                        maxKey = key;
                        foundMaxKey = true;
                        break; // Stop the loop when we find the key associated with the maximum value
                    }
                }

                if (foundMaxKey) {
                    // Apply the formula
                    int totalPointsBeforeMax = 0;

                    for (int key : last59Points.keySet()) {
                        if (key < maxKey) {
                            totalPointsBeforeMax++;
                        }
                    }

                    double result = 0.1 * totalPointsBeforeMax;

                    // Create a Distance object and add it to the list
                    Distance distanceObject = new Distance(timestamp, result);
                    distanceList.add(distanceObject);
                } else {
                    System.out.println("No key found for max value within the last 59 points.");
                }
            } else {
                System.out.println("No data found for timestamp '" + timestamp + "'.");
            }
        }

        System.out.println(distanceList.size());
        // Print or process the distanceList
        for (Distance distanceObject : distanceList) {
            System.out.println("Timestamp: " + distanceObject.getTimestamp() + ", Distance: " + distanceObject.getDistance());
        }


        Map<String, Map<Integer, Double>> maxDoubleMap = new TreeMap<>();
        for (Map.Entry<String, Map<Integer, Double>> entry : deformationData.entrySet()) {
            String outerKey = entry.getKey();
            Map<Integer, Double> innerMap = entry.getValue();
            double maxDoubleValue = findMaxDouble(innerMap);
            Map<Integer, Double> maxDoubleInnerMap = new HashMap<>();
            maxDoubleInnerMap.put(1, maxDoubleValue);
            maxDoubleMap.put(outerKey, maxDoubleInnerMap);
        }

        // Print the maxDoubleMap or use it as needed
        // printNestedMap(maxDoubleMap);
        // System.out.println(maxDoubleMap);

        /**
         * Final Array for Print
         */

        Map<String, Double> maxDoubleMap_ = new TreeMap<>();

        for (Map.Entry<String, Map<Integer, Double>> entry : deformationData.entrySet()) {
            String outerKey = entry.getKey();
            Map<Integer, Double> innerMap = entry.getValue();
            double maxDoubleValue = findMaxDouble(innerMap);
            maxDoubleMap_.put(outerKey, maxDoubleValue);
        }

        // Print the maxDoubleMap or use it as needed
        // printMap(maxDoubleMap_);
        // System.out.println(maxDoubleMap_);

        /**
         * Convert to Chart Object
         */

        List<DeformationModal> modal = new ArrayList<>();
        for (Map.Entry<String, Double> entry : maxDoubleMap_.entrySet()) {
            String timeStamp = entry.getKey();
            Double deformationMax = entry.getValue();


            DeformationModal deformationModal = new DeformationModal();
            deformationModal.setTimeStamp(timeStamp.trim());
            deformationModal.setDeformationMax(deformationMax);


            modal.add(deformationModal);
        }


        for (DeformationModal deformationModal : modal) {
            System.out.println(deformationModal);
        }

        return modal;  // Modify the return type based on your needs
    }

    private String readFileContents(File file) {
        StringBuilder contents = new StringBuilder();
        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new FileReader(file));
            String line;

            while ((line = reader.readLine()) != null) {
                contents.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return contents.toString();
    }

    @Override
    protected void onPostExecute(List<DeformationModal> deformationModalList) {
        super.onPostExecute(deformationModalList);

        // Dismiss the ProgressDialog after the background task is complete
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }

        // Get a reference to the MainActivity
        MainActivity activity = activityReference.get();
        if (activity == null || activity.isFinishing()) {
            return;
        }

        // Plot the data in the LineChart
        activity.plotDeformationData(deformationModalList);
    }

    /**
     * Working Fine
     */
    private static Map<String, Map<Integer, Double>> calculateAndStoreDeformationForTimeInterval(
            Map<String, Map<String, Map<Integer, Double>>> dataMap,
            String mux,
            List<String> timestamps,
            int startWaveformNumber,
            int endWaveformNumber) {

        Map<String, Map<Integer, Double>> deformationData = new TreeMap<>();
        int waveformNumber ;

        for (int i = 0; i < timestamps.size() - 1; i++) {
            String timestamp1 = timestamps.get(i);
            String timestamp2 = timestamps.get(i + 1);

            if (dataMap.containsKey(timestamp1) && dataMap.containsKey(timestamp2)) {
                Map<String, Map<Integer, Double>> muxDataMap1 = dataMap.get(timestamp1);
                Map<String, Map<Integer, Double>> muxDataMap2 = dataMap.get(timestamp2);

                if (muxDataMap1.containsKey(mux) && muxDataMap2.containsKey(mux)) {
                    Map<Integer, Double> deformationMap = new HashMap<>();

                    for ( waveformNumber = startWaveformNumber; waveformNumber <= endWaveformNumber; waveformNumber++) {
                        if (muxDataMap1.get(mux).containsKey(waveformNumber) &&
                                muxDataMap2.get(mux).containsKey(waveformNumber)) {

                            double value1 = muxDataMap1.get(mux).get(waveformNumber);
                            double value2 = muxDataMap2.get(mux).get(waveformNumber);
                            double difference = value2 - value1;
                            double rc = (difference) / (value2 + value1);

                            if(mux.equalsIgnoreCase("1001")||mux.equalsIgnoreCase("1002")||mux.equalsIgnoreCase("1003")||mux.equalsIgnoreCase("1004")){
                                double deformation = (rc + 0.0142)/0.0188;
                                deformationMap.put(waveformNumber, Math.abs(deformation));
//                                System.out.println("Timestamps: " + timestamp1 + " and " + timestamp2 +
//                                        ", Mux: " + mux +
//                                        ", Waveform " + waveformNumber +
//                                        ", Absolute Deformation: " + Math.abs(deformation));
                            }else{
                                double deformation = ( rc - 0.158)/0.0306;
                                deformationMap.put(waveformNumber, Math.abs(deformation));
//                                System.out.println("Timestamps: " + timestamp1 + " and " + timestamp2 +
//                                        ", Mux: " + mux +
//                                        ", Waveform " + waveformNumber +
//                                        ", Absolute Deformation: " + Math.abs(deformation));
                            }
                        }
                    }

                    deformationData.put(timestamp1 + "-" + timestamp2, deformationMap);
                }
            }
        }

        return deformationData;
    }


    private static void printMap(Map<String, Double> map) {
        // Iterate through the map and print key-value pairs
        for (Map.Entry<String, Double> entry : map.entrySet()) {
            // System.out.println("Key: " + entry.getKey() + ", Value: " + entry.getValue());
        }
    }

    private static double findMaxDouble(Map<Integer, Double> innerMap) {
        double maxDoubleValue = Double.MIN_VALUE;
        for (double value : innerMap.values()) {
            if (value > maxDoubleValue) {
                maxDoubleValue = value;
            }
        }
        return maxDoubleValue;
    }
}

