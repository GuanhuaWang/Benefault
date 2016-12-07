/* WordCount.scala */
//package wghexperiments;
import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD

object wordcount {
	def main(args: Array[String]){
        val timestamp1: Long = System.currentTimeMillis
		val logFile = "file:///Users/guanhua/Desktop/500m.txt"
		val conf = new SparkConf().setAppName("Word Count")
    	val sc = new SparkContext(conf)
    	val logData = sc.textFile(logFile, 5)
    	sc.setCheckpointDir("data/checkpoint")
    	val map_counts = logData.flatMap(line => line.split(" ")).map(word => (word, 1))//.cache()
        val timestamp4: Long = System.currentTimeMillis
    	map_counts.checkpoint()
        val timestamp2: Long = System.currentTimeMillis
        val checkpoint_time = timestamp2 - timestamp4
        println("===========================checkpoint happens "+checkpoint_time+"ms===========================")
    	//map_counts.collect().foreach(println) 
        //val result : RDD[Any]= sc.checkpointFile("data/checkpoint/07ebd902-7bf9-4df3-9c35-c5c43cd4c73c/rdd-3")
    	//result.asInstanceOf[T]
        //val rdd : RDD[(String, Int)]= RDDUtilsInSpark.getCheckpointRDD(sc, "data/checkpoint/07ebd902-7bf9-4df3-9c35-c5c43cd4c73c/rdd-3")
        val reduce_counts = map_counts.reduceByKey(_ + _)
    	reduce_counts.saveAsTextFile("file:///Users/guanhua/Desktop/spark-hadoop2.6/wordcounts")
        val timestamp3: Long = System.currentTimeMillis
        val total_Time = timestamp3 - timestamp1
        println("===========================The total time is " + total_Time +"ms===========================")
        //reduce_counts.collect().foreach(println)        
	}
}
