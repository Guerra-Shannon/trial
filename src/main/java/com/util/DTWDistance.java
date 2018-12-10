package com.util;


import com.dtw.TimeWarpInfo;
import com.sun.javafx.font.Metrics;
import com.timeseries.TimeSeries;

public class DTWDistance implements DistanceFunction {



    public double calcDistance(double[] vector1, double[] vector2) {
        final TimeSeries tsI = new TimeSeries(vector1);
        final TimeSeries tsJ = new TimeSeries(vector2);
        final DistanceFunction distFn;
        distFn = DistanceFunctionFactory.getDistFnByName("EuclideanDistance");
        final TimeWarpInfo info = com.dtw.FastDTW.getWarpInfoBetween(tsI, tsJ, 2, distFn);
        return info.getDistance();


    }


    public boolean gte(double[] point1, double[] point2, double threshold) {
        return calcDistance(point1, point2) > threshold ? true : false;
    }



}


