package com.split;


import com.util.DTWDistance;
import com.util.DistanceFunction;

import java.io.EOFException;
import java.util.List;
import java.util.Queue;

public class VaryingLenSequence  {

   private DistanceFunction metrics=new DTWDistance();

    public DistanceFunction getMetrics() {
        return metrics;
    }

    public void setMetrics(DistanceFunction metrics) {
        this.metrics = metrics;
    }

    /**
     * 变长序列，保存队列中所有数据
     *
     * @param sequenceSavePath
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
                    sequenceSavePath.add(queue.getPreviouN(queue.size()));
                    //删除所有的队列元素
                    queue.clear();
                }
            }

        }


    }

    public void sequence(Queue<Double> data, String sequenceSavePath) {
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

                    queue.save(sequenceSavePath, queue.getPreviouN(queue.size()));
                    queue.clear();
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

}

