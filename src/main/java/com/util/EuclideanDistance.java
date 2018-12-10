/*
 * Arrays.java   Jul 14, 2004
 *
 * Copyright (c) 2004 Stan Salvador
 * stansalvador@hotmail.com
 */

package com.util;


public class EuclideanDistance implements DistanceFunction
{
   public EuclideanDistance()
   {
      
   }
   
   public double calcDistance(double[] vector1, double[] vector2)
   {
      if (vector1.length != vector2.length)
         throw new InternalError("ERROR:  cannot calculate the distance "
                                 + "between vectors of different sizes.");

      double sqSum = 0.0;
      for (int x=0; x<vector1.length; x++)
          sqSum += Math.pow(vector1[x]-vector2[x], 2.0);

      return Math.sqrt(sqSum);
   }  // end class euclideanDist(..)

   /**
    * 比较两个时间序列的距离是否大于等于阈值。
    * @param timeSeries1
    * @param timeSeries2
    * @param threshold
    * @return 如果距离大于等于阈值就返回true,否则返回false
    */
   public boolean gte(double[] timeSeries1, double[] timeSeries2, double threshold) {
      return calcDistance(timeSeries1, timeSeries2) >= threshold ? true : false;
   }

}