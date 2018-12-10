package weka.core;

import weka.core.converters.CSVLoader;

import java.io.File;
import java.util.Vector;

public class HierarchicalClustererTest {


    public static void main(String[] args) throws Exception {
        int[] n = {5,10,25, 50, 100};
        for (int i : n) {
            new HierarchicalClustererTest().controll(i);
        }


    }

    public void controll(int n_i) throws Exception {


        CSVLoader csvLoader = new CSVLoader();
        csvLoader.setSource(new File("F:\\sequence2.csv"));
        Instances instances = csvLoader.getDataSet();
        HCluster HCluster = new HCluster();
        HCluster.setLinkType(2);


        HCluster.setNumClusters(n_i);
        HCluster.setDistanceFunction(new EuclideanDistance());
        long startTime = System.currentTimeMillis();    //获取开始时间
        HCluster.hcluster(instances);
        long endTime = System.currentTimeMillis();    //获取结束时间
        System.out.println("程序运行时间：" + (endTime - startTime) / 1000 + "s");    //输出程序运行时间


        // Node m_clusters = HClusterOnly.getM_clusters();
        //ArrayList list= HClusterOnly.getLeafNode(m_clusters);
        Vector<Double> evaluate = HCluster.evaluate();
        double error = 0;
        int count = 0;
        for (int id = 0; id < evaluate.size(); id++) {
            if (evaluate.get(id) > 0) {
                error += evaluate.get(id);
                count++;
            }
        }
        System.out.println(error + "  " + count + " " + error / count + " " + (2502 - count));

    }


}

