/* Sorting_checkpointing.scala */
package wghexperiments;

import org.apache.spark.{SparkConf, SparkContext}

object Sorting_Checkpointing {
  def main(args: Array[String]) {
    // create Spark context with Spark configuration
    val sc = new SparkContext(new SparkConf().setAppName("Spark Sort with checkpoint"))
    sc.setCheckpointDir(("/checkpoint-dir"))

    val lines = sc.textFile("data.txt")
    val rdd = lines
      .flatMap(_.split(" "))
      .map((_, 1))
      .reduceByKey(_ + _)
    val sortedRDD = rdd.map(_.swap).sortByKey(false)
    sortedRDD.checkpoint()
    val data = sortedRDD.take(5)


    System.out.println(sortedRDD.collect().mkString(", "))
  }
}
