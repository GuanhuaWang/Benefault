import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf

object SparkWordCount{
	def def main(args: Array[String]) {
	  val sc = new SparkContext(new SparkConf().setAppName("Spark Count"))
	  val tokenized = sc.textFile(args(0)).flatMap(_.split(" "))
	  val wordCounts = tokenized.map((_,1)).reduceByKey(_+_)
	  System.out.println(wordCounts.collect().mkString(", "))
	}
	
}


## on spark shell

val inputFile = sc.textFile("tmp/xxx.txt")