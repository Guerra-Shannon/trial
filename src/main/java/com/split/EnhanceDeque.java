package com.split;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * EnhanceDeque类是对双向队列Deque的增强，它继承了双向队列LinkedBlockingDeque
 *
 * 在时间序列切分过程中使用单例模式保证单例，可以通过getInstance()方法获得该实例
 */
public class EnhanceDeque extends LinkedBlockingDeque {
    private static EnhanceDeque queue = null;
    private EnhanceDeque() {
    }

    /**
     * 获得EnhanceDeque的单个实例
     * @return 实例
     */
    public  static EnhanceDeque getInstance() {
        if (queue == null)
            queue = new EnhanceDeque();
        return queue;

    }


    /**
     * 获得队列前N个元素，这N个元素并不会从队列中删除。
     * @param N
     * @return  返回N个数据点组成的一维数组
     */
    public double[] getPreviouN(int N) {

        Iterator<Double> iterator = queue.iterator();
        double[] d = new double[N];

        for (int i = 0; i < N; i++) {
            d[i] = iterator.next();

        }

        return d;

    }

    /**
     * 获得队列从后向前数N个元素，队里不会删除这些元素
     * @param N
     * @return
     */
    public double[] getLastN(int N) {
        double[] d = new double[N];
        //The elements will be returned in order from last (tail) to first (head).
        Iterator<Double> descendingIterator = queue.descendingIterator();

        for (int i = d.length - 1; i >= 0; i--) {
            d[i] = descendingIterator.next();

        }
        return d;


    }

    /**
     * Retrieves and removes the head of the queue represented by this deque of N
     * @param length
     */
    public void popN(int length) {

        while(length>0){

            queue.poll();
            --length;
        }

    }


    /**
     * 追加写时间序列到指定文件路径
     * @param dest
     * @param data
     */
    public void save(String dest, double[] data) {
        try {

            PrintWriter pw = new PrintWriter(new FileWriter(dest, true));
            pw.println(toString(data));
            pw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void save(String dest, double data) {
        try {

            PrintWriter pw = new PrintWriter(new FileWriter(dest, true));
            pw.println(String.valueOf(data));
            pw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private String toString(double[] d) {
        StringBuffer sb = new StringBuffer(String.valueOf(d[0]));
        for (int i = 1; i < d.length; i++) {

            sb.append(",");
            sb.append(String.valueOf(String.valueOf(d[i])));
        }
        return sb.toString();
    }




}
