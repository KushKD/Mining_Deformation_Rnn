package Utilities;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Utilities {

    public static String readDataFromFile(String filePath) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
        }

        return stringBuilder.toString();
    }

    /**
     * Save Data in Map
     */

    public static Map<String, Map<String, Map<Integer, Double>>> saveDataInMap(String data) {
        String[] lines = data.split("\n");
        System.out.println(lines.length);
        Map<String, Map<String, Map<Integer, Double>>> dataMap = new HashMap<>();

        for (int i = 4; i < lines.length; i++) {
            try {
                String[] fields = lines[i].split(",");
                String timestamp = fields[0];
                String mux = fields[2];
                Map<Integer, Double> waveformMap = new HashMap<>();

                for (int j = 15; j < 265; j++) {
                    waveformMap.put(j, Double.parseDouble(fields[j]));
                }

                if (!dataMap.containsKey(timestamp)) {
                    dataMap.put(timestamp, new HashMap<>());
                }
                if (!dataMap.get(timestamp).containsKey(mux)) {
                    dataMap.get(timestamp).put(mux, waveformMap);
                }
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                System.err.println("Error processing line: " + lines[i]);
            }
        }
        return dataMap;
    }

    /**
     * Get Sorted Timstamps for MUX
     */
    public static List<String> getSortedTimestampsForMux(Map<String, Map<String, Map<Integer, Double>>> dataMap, String mux) {
        List<String> timestamps = new ArrayList<>();

        for (Map.Entry<String, Map<String, Map<Integer, Double>>> entry : dataMap.entrySet()) {
            String timestamp = entry.getKey();
            Map<String, Map<Integer, Double>> muxDataMap = entry.getValue();

            if (muxDataMap.containsKey(mux)) {
                timestamps.add(timestamp);
            }
        }

        // Sort timestamps in ascending order
        timestamps.sort(Comparator.naturalOrder());

        return timestamps;
    }


    //    private static void calculateAndPrintDifference(Map<String, Map<String, Map<Integer, Double>>> dataMap, String mux, List<String> timestamps, int waveformNumber, List<Double> deformationList) {
//        for (int i = 0; i < timestamps.size() - 1; i++) {
//            String timestamp1 = timestamps.get(i);
//            String timestamp2 = timestamps.get(i + 1);
//
//            if (dataMap.containsKey(timestamp1) && dataMap.containsKey(timestamp2)) {
//                Map<String, Map<Integer, Double>> muxDataMap1 = dataMap.get(timestamp1);
//                Map<String, Map<Integer, Double>> muxDataMap2 = dataMap.get(timestamp2);
//
//                if (muxDataMap1.containsKey(mux) && muxDataMap2.containsKey(mux)) {
//                    Map<Integer, Double> waveformMap1 = muxDataMap1.get(mux);
//                    Map<Integer, Double> waveformMap2 = muxDataMap2.get(mux);
//
//                    if (waveformMap1.containsKey(waveformNumber) && waveformMap2.containsKey(waveformNumber)) {
//                        double value1 = waveformMap1.get(waveformNumber);
//                        double value2 = waveformMap2.get(waveformNumber);
//
//                        double difference = value2 - value1;
//                        double rc = (difference) / (value2 + value1);
//                        double deformation = 0.0188 * rc - 0.0142;
//
//                        // Store absolute deformation in the map for each mux
//                        deformationList.add(Math.abs(deformation));
//
//                        System.out.println("Timestamps: " + timestamp1 + " and " + timestamp2 +
//                                ", Mux: " + mux +
//                                ", Waveform " + waveformNumber +
//                                ", Absolute Deformation: " + Math.abs(deformation));
//                    }
//                }
//            }
//        }
//    }


    /**
     * Search and Print Data
     */
    public static void searchAndPrintData(Map<String, Map<String, Map<Integer, Double>>> dataMap, String timestamp, String mux) {
        if (dataMap.containsKey(timestamp)) {
            Map<String, Map<Integer, Double>> muxDataMap = dataMap.get(timestamp);
            if (muxDataMap.containsKey(mux)) {
                Map<Integer, Double> waveformMap = muxDataMap.get(mux);
                System.out.println("Data found for Timestamp: " + timestamp + " and Mux: " + mux);
                for (Map.Entry<Integer, Double> waveformEntry : waveformMap.entrySet()) {
                    System.out.println("  Waveform " + waveformEntry.getKey() + ": " + waveformEntry.getValue());
                }
            } else {
                System.out.println("No data found for Mux: " + mux + " at Timestamp: " + timestamp);
            }
        } else {
            System.out.println("No data found for Timestamp: " + timestamp);
        }
    }


    /**
     * Print Data Maps
     */
    public static void printDataMap(Map<String, Map<String, Map<Integer, Double>>> dataMap) {
        // Example: Print the data stored in the map
        for (Map.Entry<String, Map<String, Map<Integer, Double>>> entry : dataMap.entrySet()) {
            System.out.println("Timestamp: " + entry.getKey());
            Map<String, Map<Integer, Double>> muxDataMap = entry.getValue();
            for (Map.Entry<String, Map<Integer, Double>> muxEntry : muxDataMap.entrySet()) {
                System.out.println("  Mux: " + muxEntry.getKey());
                Map<Integer, Double> waveformMap = muxEntry.getValue();
                for (Map.Entry<Integer, Double> waveformEntry : waveformMap.entrySet()) {
                    System.out.println("    Waveform " + waveformEntry.getKey() + ": " + waveformEntry.getValue());
                }
            }
            System.out.println();
        }
    }

    public static void printNestedMap(Map<String, Map<Integer, Double>> outerMap) {
        for (Map.Entry<String, Map<Integer, Double>> entry : outerMap.entrySet()) {
            String outerKey = entry.getKey();
            Map<Integer, Double> innerMap = entry.getValue();

            System.out.println("Outer Key: " + outerKey);

            for (Map.Entry<Integer, Double> innerEntry : innerMap.entrySet()) {
                Integer innerKey = innerEntry.getKey();
                Double innerValue = innerEntry.getValue();

                System.out.println("  Inner Key: " + innerKey + ", Inner Value: " + innerValue);
            }
        }
    }

}
