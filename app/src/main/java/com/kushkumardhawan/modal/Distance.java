package com.kushkumardhawan.modal;

public class Distance {
    private String timestamp;
    private double distance;

    public Distance(String timestamp, double distance) {
        this.timestamp = timestamp;
        this.distance = distance;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    @Override
    public String toString() {
        return "Distance{" +
                "timestamp='" + timestamp + '\'' +
                ", distance=" + distance +
                '}';
    }
}