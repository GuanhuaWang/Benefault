/*Sort.scala */
//package org.apache.spark

//package wghexperiments;
import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD

object sort_checkpoint{
	def main(args: Array[String]){
        val timestamp1: Long = System.currentTimeMillis
		val logFile = "file:///Users/guanhua/Desktop/500m.txt"
		val conf = new SparkConf().setAppName("Sort with checkpoint")
    	val sc = new SparkContext(conf)
    	val logData = sc.textFile(logFile)
    	sc.setCheckpointDir("data/checkpoint")
    	val rdds = logData
            .flatMap(_.split(" "))
            .map((_, 1))
            .reduceByKey(_+_)
        val sortedRDD = rdds.map(_.swap).sortByKey(false)
        val timestamp4: Long = System.currentTimeMillis
        sortedRDD.checkpoint()
        val timestamp2: Long = System.currentTimeMillis
        val checkpoint_time = timestamp2 - timestamp4
        println("===========================checkpoint happens "+checkpoint_time+"ms===========================")

    	val topdata = sortedRDD.take(5).foreach(println)
        val timestamp3: Long = System.currentTimeMillis
        val total_Time = timestamp3 - timestamp1
        println("===========================The total time is " + total_Time +"ms===========================")     
	}
}
