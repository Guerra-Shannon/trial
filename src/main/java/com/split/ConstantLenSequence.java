package com.split;

import com.sun.javafx.font.Metrics;
import com.util.DistanceFunction;
import com.util.EuclideanDistance;

import java.io.EOFException;
import java.util.List;
import java.util.Queue;

/**
 * 定长切分序列的类
 */
public class ConstantLenSequence {

    private DistanceFunction metrics = new EuclideanDistance();

    public DistanceFunction getMetrics() {
        return metrics;
    }

    public void setMetrics(DistanceFunction metrics) {
        this.metrics = metrics;
    }

    /**
     * @param data             数据源
     * @param sequenceSavePath 序列保存路径
     */
    public void sequence(Queue<Double> data, List<double[]> sequenceSavePath) {


        EnhanceDeque queue;
        queue = EnhanceDeque.getInstance();
        while (true) {
            try {
                //填充N个元素到队列
                get(data, queue, Parameter.skipSize);
            } catch (EOFException e) {
                break;
            }
            if (queue.size() > Parameter.windowSize) {
                //获取队列前N个元素和后N个元素
                double[] previouN = queue.getPreviouN(Parameter.windowSize);
                double[] lastN = queue.getLastN(Parameter.windowSize);
                if (metrics.gte(previouN, lastN, Parameter.nonTrivalMatch)) {
                    sequenceSavePath.add(previouN);
                    //删除previouN对应的队列元素
                    queue.popN(previouN.length);
                }
            }

        }


    }

    public void sequence(Queue<Double> data, String sequenceSavePath) {
        EnhanceDeque queue= EnhanceDeque.getInstance();
        while (true) {
            try {
                //填充N个元素到队列
                get(data, queue, Parameter.skipSize);
            } catch (EOFException e) {
                break;
            }


            if (queue.size() > Parameter.windowSize) {
                //获取队列前N个元素和后N个元素
                double[] previouN = queue.getPreviouN(Parameter.windowSize);
                double[] lastN = queue.getLastN(Parameter.windowSize);
                if (metrics.gte(previouN, lastN, Parameter.nonTrivalMatch)) {

                    queue.save(sequenceSavePath, previouN);
                    //删除previouN对应的队列元素
                    queue.popN(previouN.length);
                }
            }

        }


    }

    void get(Queue<Double> data, EnhanceDeque queue, int numbers) throws EOFException {

        for (int i = 0; i < numbers; i++) {
            Double tmp = null;
            if ((tmp = data.poll()) != null) {
                queue.add(tmp);
            } else
                throw new EOFException("读取完毕");
        }


    }


    double[] get(Queue<Double> data, int numbers) throws EOFException {

        double[] tmpArray = new double[numbers];
        for (int i = 0; i < numbers; i++) {
            Double tmp = null;
            if ((tmp = data.poll()) != null) {
                tmpArray[i] = tmp;
            } else
                throw new EOFException("读取完毕");
        }
        return tmpArray;

    }


    /**
     * 固定块切分
     *
     * @param data
     * @param sequenceSavePath
     */
    public void chunking(Queue<Double> data, List<double[]> sequenceSavePath) {
        while (true) {
            try {
                sequenceSavePath.add(get(data, Parameter.windowSize));
            } catch (EOFException e) {
                break;
            }
        }

    }

    public void chunking(Queue<Double> data, String sequenceSavePath) {
        EnhanceDeque queue = EnhanceDeque.getInstance();
        while (true) {
            try {
                queue.save(sequenceSavePath, get(data, Parameter.windowSize));
            } catch (EOFException e) {
                break;
            }
        }

    }


}




