# Benefault

[![License](https://img.shields.io/badge/license-BSD-blue.svg)](LICENSE)

A way for task preemption in Big data analytics platform
## What we have done
* a simple shell script for monitoring node's metadata (e.g. disk access, network Tx Rx etc) in a cluster
* read and write for chekcpointing data (note: checkpointRead is private in spark, we need to package function into org.apache.spark)

### We have already done some simulation about the JCT gain we can get using Benefault

### We test latency in varied scenarios
* Measure checkpoint latency using Spark
* Word Count with checkpointing
* Sorting with checkpoint
* GroupByKey with Checkpointing
* DecisionTree with periodic Checkpointing
* We now design schemes for evaluate best gain we can get using Benefault
* find sweet spot for whether kill or preempt
