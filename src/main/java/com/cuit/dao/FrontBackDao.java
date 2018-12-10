package com.cuit.dao;

public class FrontBackDao {

    private  double dataPoint;
    private double predictValue;

    public FrontBackDao(double dataPoint, double predictValue) {
        this.dataPoint = dataPoint;
        this.predictValue = predictValue;
    }

    public double getDataPoint() {
        return dataPoint;
    }

    public void setDataPoint(double dataPoint) {
        this.dataPoint = dataPoint;
    }

    public double getPredictValue() {
        return predictValue;
    }

    public void setPredictValue(double predictValue) {
        this.predictValue = predictValue;
    }

    @Override
    public String toString() {
        return "FrontBackDao{" +
                "dataPoint=" + dataPoint +
                ", predictValue=" + predictValue +
                '}';
    }
}
