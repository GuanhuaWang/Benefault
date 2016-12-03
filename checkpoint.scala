//spark shell

sc.setCheckpointDir("data/checkpoint")
val rddt = sc.parallelize(Array((1,2),(3,4),(5,6)),2)
rddt.checkpoint()
rddt.count()

package org.apache.spark
import org.apache.spark.rdd.RDD
object  RDDUtilsInSpark {
	def getCheckpointRDD[T](sc:SparkContext, path:String) = {
		val result : RDD[Any] = sc.checkpointFile(path)
		resutl.asInstanceOf[T] 
	}
	
}

val rdd : RDD[(Int,Int)]=RDDUtilsInSpark.getCheckpointRDD(sc, "data/checkpoint/xxxxx")
println(rdd.count())
rdd.collect().foreach(println)