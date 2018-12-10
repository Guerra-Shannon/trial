package weka.core;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Vector;

/**
 * 该类仅仅实现了层次聚类
 */
public class HClusterOnly {
    /**
     * the various link types
     */
    final static int SINGLE = 0;
    final static int COMPLETE = 1;
    final static int AVERAGE = 2;
    final static int MEAN = 3;
    final static int CENTROID = 4;
    final static int WARD = 5;
    final static int ADJCOMPLETE = 6;
    final static int NEIGHBOR_JOINING = 7;

    private static final long serialVersionUID = 1L;
    public Node[] m_clusters;
    /**
     * Whether the distance represent node height (if false) or branch length (if
     * true).
     */
    protected boolean m_bDistanceIsBranchLength = false;
    /**
     * distance function used for comparing members of a cluster
     **/

    protected DistanceFunction m_DistanceFunction = new EuclideanDistance();
    /**
     * training data
     **/
    Instances m_instances;
    /**
     * number of clusters desired in clustering
     **/
    int m_nNumClusters = 1;
    /**
     * Holds the Link type used calculate distance between clusters
     */
    int m_nLinkType = AVERAGE;

    boolean m_Debug = false;
    int[] m_nClusterNr;

    /**
     * 单实例距离矩阵
     */
    double[][] distMatrix;

    public int getNumClusters() {
        return m_nNumClusters;
    }

    public void setNumClusters(int nClusters) {
        m_nNumClusters = Math.max(1, nClusters);
    }

    public DistanceFunction getDistanceFunction() {
        return m_DistanceFunction;
    }

    public void setDistanceFunction(DistanceFunction distanceFunction) {
        m_DistanceFunction = distanceFunction;
    }


    public int getLinkType() {
        return m_nLinkType;
    }

    /**
     * final static int SINGLE = 0;
     * final static int COMPLETE = 1;
     * final static int AVERAGE = 2;
     * final static int MEAN = 3;
     * final static int CENTROID = 4;
     * final static int WARD = 5;
     * final static int ADJCOMPLETE = 6;
     * final static int NEIGHBOR_JOINING = 7;
     *
     * @param newLinkType
     */
    public void setLinkType(int newLinkType) {

        m_nLinkType = newLinkType;
    }
    Vector<Integer>[] clusterVector;
    public Node getM_clusters() {
        return m_clusters[0];
    }

    public Node hcluster(Instances data) {

        //m_instances是成员变量
        m_instances = data;
        int nInstances = m_instances.numInstances();
        if (nInstances == 0) {
            return null;
        }
        m_DistanceFunction.setInstances(m_instances);
        // use array of integer vectors to store cluster indices,
        // starting with one cluster per instance
        @SuppressWarnings("unchecked")
        Vector<Integer>[] nClusterID = new Vector[data.numInstances()];
        clusterVector=nClusterID;
        for (int i = 0; i < data.numInstances(); i++) {
            nClusterID[i] = new Vector<Integer>();
            nClusterID[i].add(i);
        }
        // calculate distance matrix
        int nClusters = data.numInstances();

        // used for keeping track of hierarchy
        Node[] clusterNodes = new Node[nInstances];
        if (m_nLinkType == NEIGHBOR_JOINING) {
            neighborJoining(nClusters, nClusterID, clusterNodes);
        } else {
            doLinkClustering(nClusters, nClusterID, clusterNodes);
        }

        // move all clusters in m_nClusterID array
        // & collect hierarchy
        int iCurrent = 0;
        m_clusters = new Node[m_nNumClusters];
        m_nClusterNr = new int[nInstances];
        for (int i = 0; i < nInstances; i++) {
            if (nClusterID[i].size() > 0) {
                for (int j = 0; j < nClusterID[i].size(); j++) {
                    m_nClusterNr[nClusterID[i].elementAt(j)] = iCurrent;
                }
                m_clusters[iCurrent] = clusterNodes[i];
                iCurrent++;
            }
        }

        return m_clusters[0];
    } // buildClusterer

    /**
     * use neighbor joining algorithm for clustering This is roughly based on the
     * RapidNJ simple implementation and runs at O(n^3) More efficient
     * implementations exist, see RapidNJ (or my GPU implementation :-))
     *
     * @param nClusters
     * @param nClusterID
     * @param clusterNodes
     */
    void neighborJoining(int nClusters, Vector<Integer>[] nClusterID,
                         Node[] clusterNodes) {
        int n = m_instances.numInstances();

        double[][] fDist = new double[nClusters][nClusters];
        for (int i = 0; i < nClusters; i++) {
            fDist[i][i] = 0;
            for (int j = i + 1; j < nClusters; j++) {
                fDist[i][j] = getDistance0(nClusterID[i], nClusterID[j]);
                fDist[j][i] = fDist[i][j];
            }
        }

        double[] fSeparationSums = new double[n];
        double[] fSeparations = new double[n];
        int[] nNextActive = new int[n];

        // calculate initial separation rows
        for (int i = 0; i < n; i++) {
            double fSum = 0;
            for (int j = 0; j < n; j++) {
                fSum += fDist[i][j];
            }
            fSeparationSums[i] = fSum;
            fSeparations[i] = fSum / (nClusters - 2);
            nNextActive[i] = i + 1;
        }

        while (nClusters > 2) {
            // find minimum
            int iMin1 = -1;
            int iMin2 = -1;
            double fMin = Double.MAX_VALUE;
            if (m_Debug) {
                for (int i = 0; i < n; i++) {
                    if (nClusterID[i].size() > 0) {
                        double[] fRow = fDist[i];
                        double fSep1 = fSeparations[i];
                        for (int j = 0; j < n; j++) {
                            if (nClusterID[j].size() > 0 && i != j) {
                                double fSep2 = fSeparations[j];
                                double fVal = fRow[j] - fSep1 - fSep2;

                                if (fVal < fMin) {
                                    // new minimum
                                    iMin1 = i;
                                    iMin2 = j;
                                    fMin = fVal;
                                }
                            }
                        }
                    }
                }
            } else {
                int i = 0;
                while (i < n) {
                    double fSep1 = fSeparations[i];
                    double[] fRow = fDist[i];
                    int j = nNextActive[i];
                    while (j < n) {
                        double fSep2 = fSeparations[j];
                        double fVal = fRow[j] - fSep1 - fSep2;
                        if (fVal < fMin) {
                            // new minimum
                            iMin1 = i;
                            iMin2 = j;
                            fMin = fVal;
                        }
                        j = nNextActive[j];
                    }
                    i = nNextActive[i];
                }
            }
            // record distance
            double fMinDistance = fDist[iMin1][iMin2];
            nClusters--;
            double fSep1 = fSeparations[iMin1];
            double fSep2 = fSeparations[iMin2];
            double fDist1 = (0.5 * fMinDistance) + (0.5 * (fSep1 - fSep2));
            double fDist2 = (0.5 * fMinDistance) + (0.5 * (fSep2 - fSep1));
            if (nClusters > 2) {
                // update separations & distance
                double fNewSeparationSum = 0;
                double fMutualDistance = fDist[iMin1][iMin2];
                double[] fRow1 = fDist[iMin1];
                double[] fRow2 = fDist[iMin2];
                for (int i = 0; i < n; i++) {
                    if (i == iMin1 || i == iMin2 || nClusterID[i].size() == 0) {
                        fRow1[i] = 0;
                    } else {
                        double fVal1 = fRow1[i];
                        double fVal2 = fRow2[i];
                        double fDistance = (fVal1 + fVal2 - fMutualDistance) / 2.0;
                        fNewSeparationSum += fDistance;
                        // update the separationsum of cluster i.
                        fSeparationSums[i] += (fDistance - fVal1 - fVal2);
                        fSeparations[i] = fSeparationSums[i] / (nClusters - 2);
                        fRow1[i] = fDistance;
                        fDist[i][iMin1] = fDistance;
                    }
                }
                fSeparationSums[iMin1] = fNewSeparationSum;
                fSeparations[iMin1] = fNewSeparationSum / (nClusters - 2);
                fSeparationSums[iMin2] = 0;
                merge(iMin1, iMin2, fDist1, fDist2, nClusterID, clusterNodes);
                int iPrev = iMin2;
                // since iMin1 < iMin2 we havenActiveRows[0] >= 0, so the next loop
                // should be save
                while (nClusterID[iPrev].size() == 0) {
                    iPrev--;
                }
                nNextActive[iPrev] = nNextActive[iMin2];
            } else {
                merge(iMin1, iMin2, fDist1, fDist2, nClusterID, clusterNodes);
                break;
            }
        }

        for (int i = 0; i < n; i++) {
            if (nClusterID[i].size() > 0) {
                for (int j = i + 1; j < n; j++) {
                    if (nClusterID[j].size() > 0) {
                        double fDist1 = fDist[i][j];
                        if (nClusterID[i].size() == 1) {
                            merge(i, j, fDist1, 0, nClusterID, clusterNodes);
                        } else if (nClusterID[j].size() == 1) {
                            merge(i, j, 0, fDist1, nClusterID, clusterNodes);
                        } else {
                            merge(i, j, fDist1 / 2.0, fDist1 / 2.0, nClusterID, clusterNodes);
                        }
                        break;
                    }
                }
            }
        }
    } // neighborJoining

    /**
     * Perform clustering using a link method This implementation uses a priority
     * queue resulting in a O(n^2 log(n)) algorithm
     *
     * @param nClusters    number of clusters
     * @param nClusterID
     * @param clusterNodes
     */
    void doLinkClustering(int nClusters, Vector<Integer>[] nClusterID,
                          Node[] clusterNodes) {
        //获得样本数
        int nInstances = m_instances.numInstances();
        PriorityQueue<Tuple> queue = new PriorityQueue<Tuple>(nClusters * nClusters
                / 2, new TupleComparator());

        //fdistance0是簇间距离矩阵
        double[][] fDistance0 = new double[nClusters][nClusters];
        double[][] fClusterDistance = null;
        distMatrix=fDistance0;
        //初始化距离矩阵
        for (int i = 0; i < nClusters; i++) {
            fDistance0[i][i] = 0;
            for (int j = i + 1; j < nClusters; j++) {
                fDistance0[i][j] = getDistance0(nClusterID[i], nClusterID[j]);
                fDistance0[j][i] = fDistance0[i][j];
                //t.m_fDist=fDistance0[i][j]
                queue.add(new Tuple(fDistance0[i][j], i, j, 1, 1));
            }
        }

        //当簇的数量大于设定值时聚类
        while (nClusters > m_nNumClusters) {
            int iMin1 = -1;
            int iMin2 = -1;
            // find closest two clusters
            if (m_Debug) {
                /* simple but inefficient implementation */
                double fMinDistance = Double.MAX_VALUE;
                for (int i = 0; i < nInstances; i++) {
                    if (nClusterID[i].size() > 0) {
                        for (int j = i + 1; j < nInstances; j++) {
                            if (nClusterID[j].size() > 0) {
                                double fDist = fClusterDistance[i][j];
                                if (fDist < fMinDistance) {
                                    fMinDistance = fDist;
                                    iMin1 = i;
                                    iMin2 = j;
                                }
                            }
                        }
                    }
                }
                merge(iMin1, iMin2, fMinDistance, fMinDistance, nClusterID,
                        clusterNodes);
            } else {
                // use priority queue to find next best pair to cluster
                Tuple t;
                do {
                    t = queue.poll();
                } while (t != null
                        && (nClusterID[t.m_iCluster1].size() != t.m_nClusterSize1 || nClusterID[t.m_iCluster2]
                        .size() != t.m_nClusterSize2));
                //iMin1和iMin2代表距离最近的两个簇在nClusterID中的index
                iMin1 = t.m_iCluster1;
                iMin2 = t.m_iCluster2;
                merge(iMin1, iMin2, t.m_fDist, t.m_fDist, nClusterID, clusterNodes);
            }
            // merge clusters

            // update distances & queue
            for (int i = 0; i < nInstances; i++) {
                if (i != iMin1 && nClusterID[i].size() != 0) {
                    int i1 = Math.min(iMin1, i);
                    int i2 = Math.max(iMin1, i);
                    double fDistance = getDistance(fDistance0, nClusterID[i1],
                            nClusterID[i2]);
                    if (m_Debug) {
                        fClusterDistance[i1][i2] = fDistance;
                        fClusterDistance[i2][i1] = fDistance;
                    }
                    queue.add(new Tuple(fDistance, i1, i2, nClusterID[i1].size(),
                            nClusterID[i2].size()));
                }
            }

            nClusters--;
        }
    } // doLinkClustering

    void merge(int iMin1, int iMin2, double fDist1, double fDist2,
               Vector<Integer>[] nClusterID, Node[] clusterNodes) {

        //保证iMin1的坐标数值小于iMin2
        if (iMin1 > iMin2) {
            int h = iMin1;
            iMin1 = iMin2;
            iMin2 = h;
            double f = fDist1;
            fDist1 = fDist2;
            fDist2 = f;
        }
        //iMin1坐标的vector加入了iMin2中的元素，但是不能删除iMin2
        nClusterID[iMin1].addAll(nClusterID[iMin2]);
        nClusterID[iMin2].removeAllElements();

        // track hierarchy
        Node node = new Node();
        double[][] metrics = metrics(nClusterID[iMin1]);
        node.setMax(calcMax(metrics));
        node.setMin(calcMin(metrics));
        node.setAvg(avg(metrics));
        node.setEss(ess(node.getAvg(), metrics));
        node.setVector(calcCentroId(nClusterID[iMin1]));

        if (clusterNodes[iMin1] == null) {
            node.leftInstanceIndex = iMin1;
        } else {
            node.left = clusterNodes[iMin1];
            // clusterNodes[iMin1].parent = node;
        }
        if (clusterNodes[iMin2] == null) {
            node.rightInstanceIndex = iMin2;
        } else {
            node.right = clusterNodes[iMin2];
            //clusterNodes[iMin2].parent = node;
        }

        clusterNodes[iMin1] = node;
    } // merge

    /**
     * calculate distance the first time when setting up the distance matrix
     **/
    double getDistance0(Vector<Integer> cluster1, Vector<Integer> cluster2) {
        double fBestDist = Double.MAX_VALUE;
        switch (m_nLinkType) {
            case SINGLE:
            case NEIGHBOR_JOINING:
            case CENTROID:
            case COMPLETE:
            case ADJCOMPLETE:
            case AVERAGE:
            case MEAN:
                // set up two instances for distance function
                Instance instance1 = (Instance) m_instances.instance(
                        cluster1.elementAt(0)).copy();
                Instance instance2 = (Instance) m_instances.instance(
                        cluster2.elementAt(0)).copy();
                fBestDist = m_DistanceFunction.distance(instance1, instance2);
                break;
            case WARD: {
                // finds the distance of the change in caused by merging the cluster.
                // The information of a cluster is calculated as the error sum of squares
                // of the
                // centroids of the cluster and its members.
                double ESS1 = calcESS(cluster1);
                double ESS2 = calcESS(cluster2);
                Vector<Integer> merged = new Vector<Integer>();
                merged.addAll(cluster1);
                merged.addAll(cluster2);
                double ESS = calcESS(merged);
                fBestDist = ESS * merged.size() - ESS1 * cluster1.size() - ESS2
                        * cluster2.size();
            }
            break;
        }
        return fBestDist;
    } // getDistance0

    /**
     * calculate the distance between two clusters
     *
     * @param cluster1 list of indices of instances in the first cluster
     * @param cluster2 dito for second cluster
     * @return distance between clusters based on link type
     */
    double getDistance(double[][] fDistance, Vector<Integer> cluster1,
                       Vector<Integer> cluster2) {
        double fBestDist = Double.MAX_VALUE;
        switch (m_nLinkType) {
            case SINGLE:
                // find single link distance aka minimum link, which is the closest
                // distance between
                // any item in cluster1 and any item in cluster2
                fBestDist = Double.MAX_VALUE;
                for (int i = 0; i < cluster1.size(); i++) {
                    int i1 = cluster1.elementAt(i);
                    for (int j = 0; j < cluster2.size(); j++) {
                        int i2 = cluster2.elementAt(j);
                        double fDist = fDistance[i1][i2];
                        if (fBestDist > fDist) {
                            fBestDist = fDist;
                        }
                    }
                }
                break;
            case COMPLETE:
            case ADJCOMPLETE:
                // find complete link distance aka maximum link, which is the largest
                // distance between
                // any item in cluster1 and any item in cluster2
                fBestDist = 0;
                for (int i = 0; i < cluster1.size(); i++) {
                    int i1 = cluster1.elementAt(i);
                    for (int j = 0; j < cluster2.size(); j++) {
                        int i2 = cluster2.elementAt(j);
                        double fDist = fDistance[i1][i2];
                        if (fBestDist < fDist) {
                            fBestDist = fDist;
                        }
                    }
                }
                if (m_nLinkType == COMPLETE) {
                    break;
                }
                // calculate adjustment, which is the largest within cluster distance
                double fMaxDist = 0;
                for (int i = 0; i < cluster1.size(); i++) {
                    int i1 = cluster1.elementAt(i);
                    for (int j = i + 1; j < cluster1.size(); j++) {
                        int i2 = cluster1.elementAt(j);
                        double fDist = fDistance[i1][i2];
                        if (fMaxDist < fDist) {
                            fMaxDist = fDist;
                        }
                    }
                }
                for (int i = 0; i < cluster2.size(); i++) {
                    int i1 = cluster2.elementAt(i);
                    for (int j = i + 1; j < cluster2.size(); j++) {
                        int i2 = cluster2.elementAt(j);
                        double fDist = fDistance[i1][i2];
                        if (fMaxDist < fDist) {
                            fMaxDist = fDist;
                        }
                    }
                }
                fBestDist -= fMaxDist;
                break;
            case AVERAGE:
                // finds average distance between the elements of the two clusters
                fBestDist = 0;
                for (int i = 0; i < cluster1.size(); i++) {
                    int i1 = cluster1.elementAt(i);
                    for (int j = 0; j < cluster2.size(); j++) {
                        int i2 = cluster2.elementAt(j);
                        fBestDist += fDistance[i1][i2];
                    }
                }
                fBestDist /= (cluster1.size() * cluster2.size());
                break;
            case MEAN: {
                // calculates the mean distance of a merged cluster (akak Group-average
                // agglomerative clustering)
                Vector<Integer> merged = new Vector<Integer>();
                merged.addAll(cluster1);
                merged.addAll(cluster2);
                fBestDist = 0;
                for (int i = 0; i < merged.size(); i++) {
                    int i1 = merged.elementAt(i);
                    for (int j = i + 1; j < merged.size(); j++) {
                        int i2 = merged.elementAt(j);
                        fBestDist += fDistance[i1][i2];
                    }
                }
                int n = merged.size();
                fBestDist /= (n * (n - 1.0) / 2.0);
            }
            break;
            case CENTROID:
                // finds the distance of the centroids of the clusters
                double[] fValues1 = new double[m_instances.numAttributes()];
                for (int i = 0; i < cluster1.size(); i++) {
                    Instance instance = m_instances.instance(cluster1.elementAt(i));
                    for (int j = 0; j < m_instances.numAttributes(); j++) {
                        fValues1[j] += instance.value(j);
                    }
                }
                double[] fValues2 = new double[m_instances.numAttributes()];
                for (int i = 0; i < cluster2.size(); i++) {
                    Instance instance = m_instances.instance(cluster2.elementAt(i));
                    for (int j = 0; j < m_instances.numAttributes(); j++) {
                        fValues2[j] += instance.value(j);
                    }
                }
                for (int j = 0; j < m_instances.numAttributes(); j++) {
                    fValues1[j] /= cluster1.size();
                    fValues2[j] /= cluster2.size();
                }
                fBestDist = m_DistanceFunction.distance(m_instances.instance(0).copy(fValues1),
                        m_instances.instance(0).copy(fValues2));
                break;
            case WARD: {
                // finds the distance of the change in caused by merging the cluster.
                // The information of a cluster is calculated as the error sum of squares
                // of the
                // centroids of the cluster and its members.
                double ESS1 = calcESS(cluster1);
                double ESS2 = calcESS(cluster2);
                Vector<Integer> merged = new Vector<Integer>();
                merged.addAll(cluster1);
                merged.addAll(cluster2);
                double ESS = calcESS(merged);
                fBestDist = ESS * merged.size() - ESS1 * cluster1.size() - ESS2
                        * cluster2.size();
            }
            break;
        }
        return fBestDist;
    } // getDistance

    double[] calcCentroId(Vector<Integer> cluster) {
        double[] fValues1 = new double[m_instances.numAttributes()];
        //计算每一个维度的总和
        for (int i = 0; i < cluster.size(); i++) {
            Instance instance = m_instances.instance(cluster.elementAt(i));
            for (int j = 0; j < m_instances.numAttributes(); j++) {
                fValues1[j] += instance.value(j);
            }
        }
        for (int j = 0; j < m_instances.numAttributes(); j++) {
            fValues1[j] /= cluster.size();
        }
        return fValues1;
    }

    public double[][] metrics(Vector<Integer> cluster1) {
        double[][] dist = new double[cluster1.size()][cluster1.size()];
        for (int i = 0; i < cluster1.size(); i++) {
            Instance instance1 = m_instances.instance(cluster1.elementAt(i));
            dist[i][i] = 0;
            for (int j = i + 1; j < cluster1.size(); j++) {
                Instance instance2 = m_instances.instance(cluster1.elementAt(j));
                double fDist = m_DistanceFunction.distance(instance1, instance2);
                dist[i][j] = fDist;
                dist[j][i] = fDist;
            }

        }

        return dist;
    }

    public double calcMax(double[][] dist) {
        double fBestDist = Double.MIN_VALUE;
        for (int i = 0; i < dist.length; i++) {
            for (int j = i + 1; j < dist.length; j++) {
                if (fBestDist < dist[i][j]) {
                    fBestDist = dist[i][j];
                }
            }
        }
        return fBestDist;
    }

    public double calcMin(double[][] dist) {
        double fBestDist = Double.MAX_VALUE;
        for (int i = 0; i < dist.length; i++) {
            for (int j = i + 1; j < dist.length; j++) {
                if (fBestDist > dist[i][j]) {
                    fBestDist = dist[i][j];
                }
            }
        }
        return fBestDist;
    }


    public double avg(double[][] dist) {
        double fBestDist = 0;
        int count = 0;
        for (int i = 0; i < dist.length; i++) {
            for (int j = i + 1; j < dist.length; j++) {
                fBestDist += dist[i][j];
                count++;
            }
        }
        return fBestDist / count;
    }

    public double ess(double avg, double[][] dist) {
        double fBestDist = 0;
        int count = 0;
        for (int i = 0; i < dist.length; i++) {
            for (int j = i + 1; j < dist.length; j++) {
                fBestDist += (dist[i][j] - avg) * (dist[i][j] - avg);
                count++;
            }
        }
        return Math.sqrt(fBestDist) / count;
    }


    /**
     * calculated error sum-of-squares for instances wrt centroid
     **/
    double calcESS(Vector<Integer> cluster) {
        double[] fValues1 = new double[m_instances.numAttributes()];
        //计算每一个维度的总和
        for (int i = 0; i < cluster.size(); i++) {
            Instance instance = m_instances.instance(cluster.elementAt(i));
            for (int j = 0; j < m_instances.numAttributes(); j++) {
                fValues1[j] += instance.value(j);
            }
        }
        for (int j = 0; j < m_instances.numAttributes(); j++) {
            fValues1[j] /= cluster.size();
        }
        // set up instance for distance function
        Instance centroid = m_instances.instance(cluster.elementAt(0)).copy(fValues1);
        double fESS = 0;
        for (int i = 0; i < cluster.size(); i++) {
            Instance instance = m_instances.instance(cluster.elementAt(i));
            fESS += m_DistanceFunction.distance(centroid, instance);
        }
        return fESS / cluster.size();
    } // calcESS


    /**
     * 抛弃使用，方法体为空
     *
     * @param data
     * @throws Exception
     */
    @Deprecated
    public void buildClusterer(Instances data) throws Exception {

    }


    /**
     * instances are assigned a cluster by finding the instance in the training data
     * with the closest distance to the instance to be clustered. The cluster index of
     * the training data point is taken as the cluster index.
     */
    public int clusterInstance(Instance instance) throws Exception {
        if (m_instances.numInstances() == 0) {
            return 0;
        }
        double fBestDist = Double.MAX_VALUE;
        int iBestInstance = -1;
        for (int i = 0; i < m_instances.numInstances(); i++) {
            double fDist = m_DistanceFunction.distance(instance,
                    m_instances.instance(i));
            if (fDist < fBestDist) {
                fBestDist = fDist;
                iBestInstance = i;
            }
        }
        return m_nClusterNr[iBestInstance];
    }

    /**
     * 获取叶子节点位置
     *
     * @param node
     * @return
     */
    public ArrayList<Integer> getLeafNode(Node node) {

        if (node.getLeft() == null && node.getRight() == null) {
            ArrayList<Integer> list = new ArrayList();
            list.add(node.getLeftInstanceIndex());
            list.add(node.getRightInstanceIndex());
            return list;
        }
        if (node.getLeft() == null && node.getRight() != null) {
            ArrayList<Integer> list = new ArrayList();
            list.add(node.getLeftInstanceIndex());
            list.addAll(getLeafNode(node.getRight()));
            return list;
        }
        if (node.getLeft() != null && node.getRight() == null) {
            ArrayList<Integer> list = new ArrayList();
            list.add(node.getRightInstanceIndex());
            list.addAll(getLeafNode(node.getLeft()));
            return list;
        } else {
            ArrayList<Integer> left = new ArrayList();
            ArrayList<Integer> right = new ArrayList();
            if (node.getLeft() != null) {
                left = getLeafNode(node.getLeft());
            }
            if (node.getRight() != null) {
                right = getLeafNode(node.getRight());
            }
            left.addAll(right);
            return left;
        }

    }

    /**
     * used for priority queue for efficient retrieval of pair of clusters to
     * merge
     **/
    class Tuple {
        double m_fDist;
        int m_iCluster1;
        int m_iCluster2;
        int m_nClusterSize1;
        int m_nClusterSize2;

        public Tuple(double d, int i, int j, int nSize1, int nSize2) {
            m_fDist = d;
            m_iCluster1 = i;
            m_iCluster2 = j;
            m_nClusterSize1 = nSize1;
            m_nClusterSize2 = nSize2;
        }
    }

    /**
     * comparator used by priority queue
     **/
    class TupleComparator implements Comparator<Tuple> {
        @Override
        public int compare(Tuple o1, Tuple o2) {
            if (o1.m_fDist < o2.m_fDist) {
                return -1;
            } else if (o1.m_fDist == o2.m_fDist) {
                return 0;
            }
            return 1;
        }
    }


    public Vector<Double> evaluate() {

        ArrayList<Vector<Integer>> list=new ArrayList();

        for( int count =0;count<clusterVector.length;count++){

            if(clusterVector[count].size()>0){
                Vector<Integer> vector = new Vector<>();
                vector.addAll(clusterVector[count]);
                list.add(vector);
            }

        }
        Vector<Integer>[] vectors = new Vector[list.size()];
        list.toArray(vectors);
        Vector<Double> soVector=new Vector<>();
        for (Vector vector : vectors) {

            for (int i = 0; i < vector.size(); i++) {
                double ao = inner(vector, i);//计算簇内平均距离
                double bo = outer(vectors,vector, i);//计算该点和其他簇点的距离
                double so = (bo - ao) / Math.max(bo, ao);//轮廓系数
                soVector.add(so);
            }


        }
        return soVector ;
    }



    double  inner(Vector<Integer> vector,int i) {
        double ao = 0;
        for (int j = 0; j < vector.size(); j++) {
            ao += distMatrix[vector.get(i)][vector.get(j)];
        }
        return  ao / (vector.size() - 1);//计算簇内平均距离

    }


    double outer(Vector<Integer>[] vectors,Vector<Integer> vector, int i) {
        double[] bo = new double[vectors.length - 1];
        int count=0;
        for (int v = 0; v < vectors.length; v++) {
            if (vectors[v]!=vector) {
                Vector<Integer> temp = vectors[v];
                double tmp = 0;
                for (int i2 = 0; i2 < temp.size(); i2++) {
                    tmp += distMatrix[vector.get(i)][temp.get(i2)];
                }
                bo[count++] = tmp / temp.size();
            }
        }
        return min(bo);
    }



    public double min(double[] dist) {
        double fBestDist = Double.MAX_VALUE;
        for (int i = 0; i < dist.length; i++) {
            if (fBestDist > dist[i]) {
                fBestDist = dist[i];
            }
        }
        return fBestDist;
    }

    public void  evaluate2Text(){
        Vector<Double> evaluate = evaluate();
        double positiveSum = 0;
        int positive = 0;
        for (int id = 0; id < evaluate.size(); id++) {
            if (evaluate.get(id) > 0) {
                positiveSum += evaluate.get(id);
                positive++;
            }
        }
        System.out.println(positive + " " + positiveSum / positive + " " + (m_instances.numInstances() - positive));


    }


}
