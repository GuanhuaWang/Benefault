#!/bin/bash

mkdir run5_true
cd run5_true
bash ../slaves   # timing

# ../../spark/bin/run-example GroupByTest 20 40000 40000 20 > log1.txt 2>&1 &
../../spark/bin/run-example GroupByTest 20 40000 40000 20 > log2.txt 2>&1 &
../../spark/bin/run-example GroupByTest 20 40000 40000 20

for job in `jobs -p`
do
  echo wait for $job
  wait $job
done

killall ssh
cd ..
