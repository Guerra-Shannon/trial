package com.split;

/**
 * 这是切分时间序列的参数类
 */
public class Parameter {

    /**
     * 滑动窗口的大小
     */
    public static  int windowSize = 20;

    /**
     * non trival match 的阈值
     */
    public static  double nonTrivalMatch = 12;

    /**
     * 步长，默认是windowSize的1/4
     */
    public static  int skipSize = Parameter.windowSize/4;





}
