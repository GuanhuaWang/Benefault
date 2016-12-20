#!/bin/bash

end=$((SECONDS+50000))

while [ $SECONDS -lt $end ]; do
    echo `date +%s%3N` `cat /sys/class/net/eth0/statistics/rx_bytes` `cat /sys/class/net/eth0/statistics/tx_bytes` \
      `cat <(grep 'cpu ' /proc/stat) <(sleep 0.3 && grep 'cpu ' /proc/stat) | awk -v RS="" '{print ($13-$2+$15-$4)*100/(1+$13-$2+$15-$4+$16-$5)}'`
done
