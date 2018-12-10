package weka.core;


import com.dtw.TimeWarpInfo;
import com.timeseries.TimeSeries;
import com.util.DistanceFunctionFactory;
import weka.core.neighboursearch.PerformanceStats;

import java.util.Enumeration;

/**
 * 实现动态时间规整的类
 */
public class FastDTWDistance implements DistanceFunction {


    @Override
    public void setInstances(Instances insts) {

    }

    @Override
    public Instances getInstances() {
        return null;
    }

    @Override
    public void setAttributeIndices(String value) {

    }

    @Override
    public String getAttributeIndices() {
        return null;
    }

    @Override
    public void setInvertSelection(boolean value) {

    }

    @Override
    public boolean getInvertSelection() {
        return false;
    }

    @Override
    public double distance(Instance first, Instance second) {
        final TimeSeries tsI = new TimeSeries(first.toDoubleArray());
        final TimeSeries tsJ = new TimeSeries(second.toDoubleArray());

        final com.util.DistanceFunction distFn;
        distFn = DistanceFunctionFactory.getDistFnByName("EuclideanDistance");
        final TimeWarpInfo info = com.dtw.FastDTW.getWarpInfoBetween(tsI, tsJ, 2, distFn);
        return info.getDistance();


    }

    @Override
    public double distance(Instance first, Instance second, PerformanceStats stats) throws Exception {
        return 0;
    }

    @Override
    public double distance(Instance first, Instance second, double cutOffValue) {
        return 0;
    }

    @Override
    public double distance(Instance first, Instance second, double cutOffValue, PerformanceStats stats) {
        return 0;
    }

    @Override
    public void postProcessDistances(double[] distances) {

    }

    @Override
    public void update(Instance ins) {

    }

    @Override
    public void clean() {

    }

    @Override
    public Enumeration<Option> listOptions() {
        return null;
    }

    @Override
    public void setOptions(String[] options) throws Exception {

    }

    @Override
    public String[] getOptions() {
        return new String[0];
    }
}
