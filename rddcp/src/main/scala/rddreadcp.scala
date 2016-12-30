package org.apache.spark

import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD

object RDDUtilsInSpark {
  def getCheckpointRDD[T](sc:SparkContext, path:String) = {
    val result : RDD[Any] = sc.checkpointFile(path)
    result.asInstanceOf[T]
  }
}