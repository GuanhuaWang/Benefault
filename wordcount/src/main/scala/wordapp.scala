/* WordCount.scala */
import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf

object wordcount {
	def main(args: Array[String]){
		val logFile = "file:///Users/guanhua/Desktop/spark-hadoop2.6/README.md"
		val conf = new SparkConf().setAppName("Word Count")
    	val sc = new SparkContext(conf)
    	val logData = sc.textFile(logFile, 2)
    	sc.setCheckpointDir("data/checkpoint")
    	val map_counts = logData.flatMap(line => line.split(" ")).map(word => (word, 1))
    	//map_counts.checkpoint()
    	//map_counts.collect().foreach(println) 
    	val reduce_counts = map_counts.reduceByKey(_ + _)
    	reduce_counts.saveAsTextFile("file:///Users/guanhua/Desktop/spark-hadoop2.6/wordcounts")
        //reduce_counts.collect().foreach(println)        
	}
}