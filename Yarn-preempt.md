#Preemption steps in Yarn
1. Preemption Monitor (Yarn) check queues' status periodically (10s of secondes) => calculate list of preempted containers.
2. Notify AM -> `PreemptionMessage` through heartbeat, including `StrictPreemptionContract` and `Preemption Contract`
3. Wait before forceful termination -- `yarn.resourcesmanager.monitor.capacity.preemption.monitoring_interval`. If the container under the preempt list not set free, RM will force it terminate (`ContainerExitStatus=Preempted`). And notify corresponding AM in the next round HeartBeat.

## Configurations (yarn-site.xml):
`yarn.resourcemanager.scheduler.monitor.enable` = true

`yarn.resourcemanager.scheduler.monitor.policies`

enalbe preemption => `org.apache.hadoop.yarn.server.resourcemanager.monitor.capacity.ProportionalCapacityPreemptionPolicy`

`yarn.resourcemanager.monitor.capacity.preemption.monitoring_interval` like heartbeat interval for system monitoring.

`yarn.resourcemanager.monitor.capacity.preemption.max_wait_before_kill` = force_kill_time - mark_preempted_time

`yarn.resourcemanager.monitor.capacity.preemption.total_preemption_per_round`, here round = each `monitoring_interval`

`yarn.resourcemanager.monitor.capacity.preemption.max_ignored_over_capacity`  Quene A `configured_capacity`= (1+`max_ignored_over_capacity`) x, before being considered for preemption

`yarn.resourcemanager.monitor.capacity.preemption.natural_termination_factor`: natural_termination_factor=0.2(default), each round will preempt 20% of this queue's resources.
