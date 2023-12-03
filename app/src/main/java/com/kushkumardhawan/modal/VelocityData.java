package com.kushkumardhawan.modal;

public class VelocityData {

    private String timeStamp;
    private Double velocity;
    private Double inverseVelocity;

    public VelocityData(String timeStamp, Double inverseVelocity) {
        this.timeStamp = timeStamp;
        //this.velocity = velocity;
        this.inverseVelocity = inverseVelocity;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public Double getVelocity() {
        return velocity;
    }

    public void setVelocity(Double velocity) {
        this.velocity = velocity;
    }

    public Double getInverseVelocity() {
        return inverseVelocity;
    }

    public void setInverseVelocity(Double inverseVelocity) {
        this.inverseVelocity = inverseVelocity;
    }

    @Override
    public String toString() {
        return "VelocityData{" +
                "timeStamp='" + timeStamp + '\'' +
                ", velocity=" + velocity +
                ", inverseVelocity=" + inverseVelocity +
                '}';
    }
}
