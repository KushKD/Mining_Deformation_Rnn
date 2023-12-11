package com.kushkumardhawan;

import android.app.ProgressDialog;
import android.content.res.AssetManager;
import android.os.AsyncTask;

import com.github.mikephil.charting.charts.LineChart;
import com.kushkumardhawan.modal.DeformationModal;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import Utilities.Utilities;

// Other imports...

public class LoadAndPlotDataTaskDateTime extends AsyncTask<Void, Void, List<DeformationModal>> {
    private WeakReference<MainActivity> activityReference;
    private LineChart lineChart;
    private AssetManager assetManager;
    private String Channel, SelectedFileName;

    String fromDate, toDate;

    private ProgressDialog progressDialog; // Add this line

    public LoadAndPlotDataTaskDateTime(MainActivity activity, LineChart lineChart, AssetManager assetManager, String Channel_, String selectedFileName, String fromDate, String toDate) {
        activityReference = new WeakReference<>(activity);
        this.lineChart = lineChart;
        this.assetManager = assetManager;
        this.Channel = Channel_;
        progressDialog = new ProgressDialog(activity);
        this.SelectedFileName = selectedFileName;
        this.fromDate = fromDate;
        this.toDate = toDate;

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

       List<DeformationModal> RangeList =  getDeformationListInRange(modal, fromDate,toDate);
        for (DeformationModal deformationModal : RangeList) {
            System.out.println(deformationModal);
        }



        return RangeList;  // Modify the return type based on your needs
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
                                double deformation = 0.0188 * rc - 0.0142;
                                deformationMap.put(waveformNumber, Math.abs(deformation));
                            }else{
                                double deformation = 0.0306 * rc + 0.158;
                                deformationMap.put(waveformNumber, Math.abs(deformation));
                            }

//                            System.out.println("Timestamps: " + timestamp1 + " and " + timestamp2 +
//                                    ", Mux: " + mux +
//                                    ", Waveform " + waveformNumber +
//                                    ", Absolute Deformation: " + Math.abs(deformation));
                        }
                    }

                   // deformationData.put(timestamp1 + "-" + timestamp2, deformationMap);
                    deformationData.put( timestamp2.replace("\"", ""), deformationMap);
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

    public List<DeformationModal> getDeformationListInRange(List<DeformationModal> originalList, String fromDate, String toDate) {
        List<DeformationModal> resultList = new ArrayList<>();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        try {
            Date fromDateObj = dateFormat.parse(fromDate);
            Date toDateObj = dateFormat.parse(toDate);

            for (DeformationModal modal : originalList) {
                Date date = dateFormat.parse(modal.getTimeStamp());

                // Check if the date is within the specified range
                if (date.after(fromDateObj) && date.before(toDateObj)) {
                    resultList.add(modal);
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return resultList;
    }

}

