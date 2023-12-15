package com.kushkumardhawan;

import android.app.ProgressDialog;
import android.content.res.AssetManager;
import android.os.AsyncTask;

import com.github.mikephil.charting.charts.LineChart;
import com.kushkumardhawan.modal.Distance;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import Utilities.Utilities;

// Other imports...

public class LoadAndPlotDistance extends AsyncTask<Void, Void, List<Distance>> {
    private WeakReference<DistanceGraph> activityReference;
    private LineChart lineChart;
    private AssetManager assetManager;
    private String Channel, SelectedFileName;

    private ProgressDialog progressDialog; // Add this line

    public LoadAndPlotDistance(DistanceGraph activity, LineChart lineChart, AssetManager assetManager, String Channel_, String selectedFileName) {
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
    protected List<Distance> doInBackground(Void... voids) {


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

        List<Distance> modal = new ArrayList<>();

        /**
         * Data for calculating the distance
         * For Mux = 1001
         */
        // Create a list to store the Distance objects
        ArrayList<Distance> distanceListPlot = null;

        if(Channel.equalsIgnoreCase("1001")){
            distanceListPlot = new ArrayList<>();
            distanceListPlot = calculateDistance(deformationData, 59, 0.1);
            System.out.println(distanceListPlot.size());
        }
        if(Channel.equalsIgnoreCase("2001")){
            distanceListPlot = new ArrayList<>();
            distanceListPlot = calculateDistance(deformationData, 69, 0.01);
            System.out.println(distanceListPlot.size());
        }
        if(Channel.equalsIgnoreCase("3001")){
            distanceListPlot = new ArrayList<>();
            distanceListPlot = calculateDistance(deformationData, 88, 0.07);
            System.out.println(distanceListPlot.size());
        }
        if(Channel.equalsIgnoreCase("4001")){
            distanceListPlot = new ArrayList<>();
            distanceListPlot = calculateDistance(deformationData, 109, 0.06);
            System.out.println(distanceListPlot.size());
        }
        if(Channel.equalsIgnoreCase("5001")){
            distanceListPlot = new ArrayList<>();
            distanceListPlot = calculateDistance(deformationData, 127, 0.05);
            System.out.println(distanceListPlot.size());
        }
        if(Channel.equalsIgnoreCase("6001")){
            distanceListPlot = new ArrayList<>();
            distanceListPlot = calculateDistance(deformationData, 84, 0.07);
            System.out.println(distanceListPlot.size());
        }
        if(Channel.equalsIgnoreCase("7001")){
            distanceListPlot = new ArrayList<>();
            distanceListPlot = calculateDistance(deformationData, 69, 0.09);
            System.out.println(distanceListPlot.size());
        }
        if(Channel.equalsIgnoreCase("8001")){
            distanceListPlot = new ArrayList<>();
            distanceListPlot = calculateDistance(deformationData, 54, 0.1);
            System.out.println(distanceListPlot.size());
        }

        return distanceListPlot;  // Modify the return type based on your needs
    }

    private ArrayList<Distance> calculateDistance(Map<String, Map<Integer, Double>> deformationData, int totalPoints_, double MultiplicationFactor) {
        ArrayList<Distance> distanceList = new ArrayList<>();
        // Iterate over each timestamp in the outer map
        for (String timestamp : deformationData.keySet()) {
            // Get the inner map for the current timestamp
            Map<Integer, Double> innerMap = deformationData.get(timestamp);

            if (innerMap != null) {
                // Sort the inner map based on keys (assuming keys represent time points)
                TreeMap<Integer, Double> sortedInnerMap = new TreeMap<>(innerMap);
                    // Get the last 59 points 1001
                    int totalPoints = sortedInnerMap.size();

                    int startPoint = Math.max(0, totalPoints - totalPoints_); // Adjust if total points are less than 59
                    Map<Integer, Double> last59Points = new HashMap<>();
                    for (int i = startPoint; i < totalPoints; i++) {
                        int key = sortedInnerMap.keySet().toArray(new Integer[0])[i];
                        double value = sortedInnerMap.get(key);
                        last59Points.put(key, value);
                    }
                System.out.println(totalPoints+ "\t"+ totalPoints_+ "\t" +last59Points.size());
                    double maxDeformation = last59Points.values().stream().mapToDouble(Double::doubleValue).max().orElse(0.0);
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
                       // System.out.println(totalPointsBeforeMax);
                        //System.out.println(MultiplicationFactor);
                        double result = MultiplicationFactor * totalPointsBeforeMax;
                        Distance distanceObject = new Distance(timestamp, result);
                        distanceList.add(distanceObject);
                    }
                else {
                    System.out.println("No key found for max value within the last 59 points.");
                }
            } else {
                System.out.println("No data found for timestamp '" + timestamp + "'.");
            }
        }

        return distanceList;
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
    protected void onPostExecute(List<Distance> deformationModalList) {
        super.onPostExecute(deformationModalList);

        // Dismiss the ProgressDialog after the background task is complete
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }

        // Get a reference to the MainActivity
        DistanceGraph activity = activityReference.get();
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

