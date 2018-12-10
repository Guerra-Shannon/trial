package com.cuit.service;


import com.cuit.dao.DB2conn;
import com.split.ConstantLenSequence;
import com.util.DTWDistance;
import com.split.CreateInstances;
import com.split.Parameter;
import org.junit.jupiter.api.Test;
import weka.core.*;

import java.util.ArrayList;
import java.util.Queue;
class SelectDataServletTest {

    @Test
    void doPost() {
        long startTime = System.currentTimeMillis();    //获取开始时间
         DB2conn db2conn = new DB2conn();

        Queue<Double> data = null;
        try {
            data = db2conn.getData(" YWWATER.SK_WAT_HOUR_SUMDATA_ "," AVG_VALUE_ ", "嘉陵江西湾水厂","水温");
        } catch (Exception e) {
            e.printStackTrace();
        }
        long endTime = System.currentTimeMillis();    //获取结束时间
        System.out.println("从数据库取数据：" + (endTime - startTime)/1000 + "s");    //输出程序运行时间
        long startTime2 = System.currentTimeMillis();    //获取开始时间
        ArrayList<double[]> list=new ArrayList();

        //定长切分
        ConstantLenSequence constantLenSequence = new ConstantLenSequence();
        constantLenSequence.setMetrics(new DTWDistance());
        //constantLenSequence.sequence(data,"F:\\hua.csv");
        constantLenSequence.sequence(data,list);

        Instances insts = new CreateInstances().getInstances(Parameter.windowSize, list);
        long endTime2 = System.currentTimeMillis();    //获取结束时间
        System.out.println("切分时间序列：" + (endTime2 - startTime2)/1000 + "s");    //输出程序运行时间

        long startTime3 = System.currentTimeMillis();    //获取开始时间

        try {

            HCluster hCluster = new HCluster();
            hCluster.setNumClusters(1);
            Node[] hcluster = hCluster.hcluster(insts);
            hCluster.evaluate2Text();

        } catch (Exception e) {
            e.printStackTrace();
        }

        long endTime3 = System.currentTimeMillis();    //获取结束时间
        System.out.println("算法：" + (endTime3 - startTime3)/1000 + "s");    //输出程序运行时间


    }
}