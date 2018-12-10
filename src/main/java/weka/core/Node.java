package weka.core;

import java.io.Serializable;
import java.util.Vector;

public class Node implements Serializable {

    /** ID added to avoid warning */
    private static final long serialVersionUID = 7639483515789717908L;

    Node left;
    Node right;
    int leftInstanceIndex =-1;
    int rightInstanceIndex =-1;
    // 簇的centrosID
    double[] vector;
    //最小值和最大值(可选)
    double max;
    double min;
    //簇内均分误差 the error sum of squares
    double ess;
    double avg;

    //聚类样本的索引
    Vector<Integer> clusterInstIndex = new Vector<>();

    public Node() {

    }

    public Node(Node left, Node right, Vector<Integer> vector) {
        this.left = left;
        this.right = right;
        this.clusterInstIndex.addAll(vector);
    }

    public Vector<Integer> getClusterInstIndex() {
        return clusterInstIndex;
    }

    public void setClusterInstIndex(Vector<Integer> clusterInstIndex) {
        this.clusterInstIndex.addAll(clusterInstIndex);
    }

    public double getAvg() {
        return avg;
    }

    public void setAvg(double avg) {
        this.avg = avg;
    }

    public Node getLeft() {
        return left;
    }

    public void setLeft(Node left) {
        this.left = left;
    }

    public Node getRight() {
        return right;
    }

    public void setRight(Node right) {
        this.right = right;
    }

    public int getLeftInstanceIndex() {
        return leftInstanceIndex;
    }

    public void setLeftInstanceIndex(int leftInstanceIndex) {
        this.leftInstanceIndex = leftInstanceIndex;
    }

    public int getRightInstanceIndex() {
        return rightInstanceIndex;
    }

    public void setRightInstanceIndex(int rightInstanceIndex) {
        this.rightInstanceIndex = rightInstanceIndex;
    }

    public double[] getVector() {
        return vector;
    }

    public void setVector(double[] vector) {
        this.vector = vector;
    }

    public double getMax() {
        return max;
    }

    public void setMax(double max) {
        this.max = max;
    }

    public double getMin() {
        return min;
    }

    public void setMin(double min) {
        this.min = min;
    }

    public double getEss() {
        return ess;
    }

    public void setEss(double ess) {
        this.ess = ess;
    }
}