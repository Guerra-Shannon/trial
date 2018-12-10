package weka.core;

import weka.core.converters.CSVLoader;

import java.io.File;
import java.util.Vector;

class KmeansAndHClusterTest {

    public static void main(String[] args) throws Exception {

        int [] n={1,10,25,50,100};

        for( int n_i:n){
            new KmeansAndHClusterTest().controll(n_i);
        }




    }

    public void controll(int n_i) throws Exception {
        CSVLoader csvLoader = new CSVLoader();
        csvLoader.setSource(new File("F:\\sequence2.csv"));
        csvLoader.setNoHeaderRowPresent(true);
        Instances dataSet = csvLoader.getDataSet();
        HCluster HCluster = new HCluster();
        HCluster.setDistanceFunction(new EuclideanDistance());
        HCluster.setNumClusters(n_i);
        long startTime = System.currentTimeMillis();    //获取开始时间
        Node[]  hcluster = HCluster.hcluster(dataSet);
        long endTime = System.currentTimeMillis();    //获取结束时间
        System.out.println("程序运行时间：" + (endTime - startTime)/1000 + "s");    //输出程序运行时间
        Vector<Double> evaluate = HCluster.evaluate();

        double error=0;
        int count=0;
        for(int id=0;id<evaluate.size();id++){

            if(evaluate.get(id)>0)
            {
                error+=evaluate.get(id);
                count++;
            }
        }

        System.out.println(error+"  "+count+" "+(error/count));


    }

}