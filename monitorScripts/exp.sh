#!/bin/bash

mkdir run{2..7}
cd run2
bash ../slaves   # timing
../../spark/bin/run-example GroupByTest 20 30000 20000 20
killall ssh
cd ..

cd run3
bash ../slaves   # timing
../../spark/bin/run-example GroupByTest 20 30000 30000 20
killall ssh
cd ..

cd run4
bash ../slaves   # timing
../../spark/bin/run-example GroupByTest 20 40000 30000 20
killall ssh
cd ..

cd run5
bash ../slaves   # timing
../../spark/bin/run-example GroupByTest 20 40000 40000 20
killall ssh
cd ..

cd run6
bash ../slaves   # timing
../../spark/bin/run-example GroupByTest 20 50000 40000 20
killall ssh
cd ..

cd run7
bash ../slaves   # timing
../../spark/bin/run-example GroupByTest 20 20000 20000 20
killall ssh
cd ..
