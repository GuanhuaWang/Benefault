package org.apache.spark
import org.apache.spark.rdd.RDD
import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf
object RDDUtilsInSpark {
  def getCheckpointRDD[T](sc:SparkContext, path:String) = {
    val result : RDD[Any] = sc.checkpointFile(path)
    result.asInstanceOf[T]
  }
}