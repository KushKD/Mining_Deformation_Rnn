package com.kushkumardhawan.modal;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DeformationModal implements Serializable {

    private String timeStamp;
    private Double deformationMax;

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public Double getDeformationMax() {
        return deformationMax;
    }

    public void setDeformationMax(Double deformationMax) {
        this.deformationMax = deformationMax;
    }

    @Override
    public String toString() {
        return "DeformationModal{" +
                "timeStamp='" + timeStamp + '\'' +
                ", deformationMax=" + deformationMax +
                '}';
    }

    public boolean isTimestampInRange(String fromDate, String toDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        try {
            Date date = dateFormat.parse(timeStamp);
            Date fromDateObj = dateFormat.parse(fromDate);
            Date toDateObj = dateFormat.parse(toDate);

            // Check if the date is within the specified range
            return date.after(fromDateObj) && date.before(toDateObj);
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }
}
