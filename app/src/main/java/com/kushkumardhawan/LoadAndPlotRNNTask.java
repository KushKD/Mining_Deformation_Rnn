package com.kushkumardhawan;

import android.app.ProgressDialog;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.CombinedData;
import com.kushkumardhawan.modal.DeformationModal;
import com.kushkumardhawan.modal.VelocityData;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import Utilities.Utilities;

// Other imports...

public class LoadAndPlotRNNTask extends AsyncTask<Void, Void, List<VelocityData>> {
    private WeakReference<RNNPrediction> activityReference;
    private CombinedChart lineChart;
    private AssetManager assetManager;
    private String Channel, SelectedFileName;

    private ProgressDialog progressDialog; // Add this line

    public LoadAndPlotRNNTask(RNNPrediction activity, CombinedChart lineChart, AssetManager assetManager, String Channel_, String selectedFileName) {
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
        progressDialog.setMessage("Reading File and Plotting the Inverse Velocity and performing the Prediction using the RNN Model . Connecting to Server Please Wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    //@RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected List<VelocityData> doInBackground(Void... voids) {


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

        //   System.out.println("Deformation Data: " + deformationData);
        // Utilities.printNestedMap(deformationData);

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

        /**
         * Calculate Velocity  Working
         */
        for (int i = 0; i < modal.size(); i++) {
            Double deformationMax = modal.get(i).getDeformationMax();
            String TimeStamp = modal.get(i).getTimeStamp();

            // Ensure that Velocity is not zero to avoid division by zero
            if (deformationMax != 0) {
                Double velocity = deformationMax / 300;
                Double inverseVelocity = 1 / velocity;

                System.out.println("TimeStamp: " + TimeStamp +" Velocity: " + velocity + ", Inverse Velocity: " + inverseVelocity);




            } else {
                System.out.println("Cannot calculate inverse velocity because deformationMax is zero.");
            }
        }

        /**
         * Calculate Velocity and Predict Year
         */
        List<VelocityData> velocityDataList = new ArrayList<>();


        for (int i = 0; i < modal.size(); i++) {
            Double deformationMax = modal.get(i).getDeformationMax();
            String timeStamp = modal.get(i).getTimeStamp().replace("\"","");
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

            System.out.println(timeStamp);


            // System.out.println(timeStamp);

            // Parse the original string
//            LocalDateTime dateTime = null;
//            dateTime = LocalDateTime.parse(timeStamp, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
//            String formattedString = dateTime.format(DateTimeFormatter.ofPattern("M/d/yyyy HH:mm"));

            Date date;
            String  formattedString = null;
            try {
                date = inputFormat.parse(timeStamp);

                // Format the Date to the desired format
                SimpleDateFormat outputFormat = new SimpleDateFormat("M/d/yyyy HH:mm", Locale.getDefault());
                 formattedString = outputFormat.format(date);


                // Now you have the formatted string
                System.out.println(formattedString);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            // Display the result
            //System.out.println("Original String: " + timeStamp);
            //System.out.println("Formatted String: " + formattedString);

            // Ensure that Velocity is not zero to avoid division by zero
            if (deformationMax != 0) {
                Double velocity = deformationMax / 300;
                Double inverseVelocity = 1 / velocity;

                // Create VelocityData object and add to the list
                VelocityData velocityData = new VelocityData(formattedString, inverseVelocity);
                velocityDataList.add(velocityData);

                System.out.println("TimeStamp: " + timeStamp + " Velocity: " + velocity + ", Inverse Velocity: " + inverseVelocity);
            } else {
                System.out.println("Cannot calculate inverse velocity because deformationMax is zero.");
            }
        }

        /**
         * Print the Object
         */

        System.out.println(velocityDataList);
        for (VelocityData velocityData : velocityDataList) {
            System.out.println(velocityData.getTimeStamp()+" "+velocityData.getInverseVelocity());
        }

        return velocityDataList;  // Modify the return type based on your needs
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
    protected void onPostExecute(List<VelocityData> inverseVelocityList) {
        super.onPostExecute(inverseVelocityList);

        // Dismiss the ProgressDialog after the background task is complete
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }

        // Get a reference to the MainActivity
        RNNPrediction activity = activityReference.get();
        if (activity == null || activity.isFinishing()) {
            return;
        }

        // Plot the data in the LineChart
        try {
            activity.plotInverseVelocityWithPrediction(inverseVelocityList);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
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

