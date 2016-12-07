/* WordCount.scala */
//package org.apache.spark
import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD

object groupByTest {

   def main(args: Array[String]) {
    val conf = new SparkConf().setAppName("GroupByKey").setMaster("local")
    val sc = new SparkContext(conf) 
    sc.setCheckpointDir("data/checkpoint")

    val data = Array[(Int, Char)]((1, 'a'), (2, 'b'),
                                     (3, 'c'), (4, 'd'),
                                     (5, 'e'), (3, 'f'),
                                     (2, 'g'), (1, 'h')
                                    )                               
    val pairs = sc.parallelize(data, 3)

    pairs.checkpoint()
    pairs.count

    val result = pairs.groupByKey(2)

    result.foreachWith(i => i)((x, i) => println("[PartitionIndex " + i + "] " + x))
    val timestamp3: Long = System.currentTimeMillis
    println(result.toDebugString)
   }
}