/* WordCount.scala */
package org.apache.spark
import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD

object wordcount {
	def main(args: Array[String]){
		val logFile = "file:///Users/guanhua/Desktop/spark-hadoop2.6/README.md"
		val conf = new SparkConf().setAppName("Word Count")
    	val sc = new SparkContext(conf)
    	val logData = sc.textFile(logFile, 2)
    	sc.setCheckpointDir("data/checkpoint")
    	val map_counts = logData.flatMap(line => line.split(" ")).map(word => (word, 1))
    	map_counts.checkpoint
    	//map_counts.collect().foreach(println) 
        //val result : RDD[Any]= sc.checkpointFile("data/checkpoint/07ebd902-7bf9-4df3-9c35-c5c43cd4c73c/rdd-3")
    	//result.asInstanceOf[T]
        val rdd : RDD[(String, Int)]= RDDUtilsInSpark.getCheckpointRDD(sc, "data/checkpoint/07ebd902-7bf9-4df3-9c35-c5c43cd4c73c/rdd-3")
        val reduce_counts = rdd.reduceByKey(_ + _)
    	reduce_counts.saveAsTextFile("file:///Users/guanhua/Desktop/spark-hadoop2.6/wordcounts")
        //reduce_counts.collect().foreach(println)        
	}
}
