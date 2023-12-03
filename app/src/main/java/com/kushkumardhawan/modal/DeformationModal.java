package com.kushkumardhawan.modal;

import java.io.Serializable;

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
}
