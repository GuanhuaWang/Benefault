//for scala 1.51:  bin/spark-shell --packages com.databricks:spark-csv_1.51:1.3.0
//for scala 2.10:  bin/spark-shell --packages com.databricks:spark-csv_2.10:1.3.0
//for scala 2.11:  bin/spark-shell --packages com.databricks:spark-csv_2.11:1.3.0

import org.apache.spark.sql.hive.HiveContext
import sqlContext.implicits._
import org.apache.spark.sql._

val sqlContext = new org.apache.spark.sql.SQLContext(sc)
val phaseText = sc.textFile("xxx.csv")
phaseText.first()

case class Auction(auctionid: String, bid: Int)

val phase = phaseText.map(_.split(",")).map(p => Auction(p(0),p(1).toInt))
val phaseDF = phase.toDF()

phaseDF.groupBy("auctionid").sum("bid").write.format("com.databricks.spark.csv").option("header", "false").save("yyy.csv")


//sum counts with same phase.
awk -F, '{a[$1]+=$2;}END{for(i in a)print i", "a[i];}' xxx.csv > yyy.csv

//sort csv
sort -t"," -n -k2 collect.csv > sort.csv
//Descending order
tail -r sort.csv > descend.csv
//convert to .tsv
cat desc.csv | tr "," "\\t" > desc.tsv
