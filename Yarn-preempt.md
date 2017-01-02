#Preemption steps in Yarn
1. Preemption Monitor (Yarn) check queues' status periodically (10s of secondes) => calculate list of preempted containers.
2. Notify AM -> `PreemptionMessage` through heartbeat, including `StrictPreemptionContract` and `Preemption Contract`
3. Wait before forceful termination -- `yarn.resourcesmanager.monitor.capacity.preemption.monitoring_interval`. If the container under the preempt list not set free, RM will force it terminate (`ContainerExitStatus=Preempted`). And notify corresponding AM in the next round HeartBeat.

## Configurations (yarn-site.xml):
`yarn.resourcemanager.scheduler.monitor.enable` = true

`yarn.resourcemanager.scheduler.monitor.policies`

enalbe preemption => `org.apache.hadoop.yarn.server.resourcemanager.monitor.capacity.ProportionalCapacityPreemptionPolicy`

`yarn.resourcemanager.monitor.capacity.preemption.monitoring_interval` like heartbeat interval for system monitoring (default 3000).

`yarn.resourcemanager.monitor.capacity.preemption.max_wait_before_kill` = force_kill_time - mark_preempted_time (default 15000)

`yarn.resourcemanager.monitor.capacity.preemption.total_preemption_per_round`, here round = each `monitoring_interval`

`yarn.resourcemanager.monitor.capacity.preemption.max_ignored_over_capacity`  Quene A `configured_capacity`= (1+`max_ignored_over_capacity`) x, before being considered for preemption

`yarn.resourcemanager.monitor.capacity.preemption.natural_termination_factor`: natural_termination_factor=0.2(default), each round will preempt 20% of this queue's resources.


##Previous works on Preemption

1. "Global Preemption" => Instead of kill most recently launched tasks of each job, they select and kill the most recently launched task GLOBALLY. (keep long tasks run and kill short tasks first)

2. "OS-assisted" => Using OS suspension and paging for task preemption.

3. "Natjam"=> Focus on reduce tasks, on-demand checkpointing (preempt and resume)
    (1)For stateful tasks, when preempted, inter-key datastructures are serialized and copied to HDFS.
    (2)When resume, deserializes checkpoint and skips to current key counter. 
    (3)However, since application-dependent, need programmer to write them.
    
4. "Amoeba" => Focus on Reduce tasks. split long task into short ones. Transfering/Carrying states is unpredictable and maybe siginificant. Key-based periodic checkpointing report to AM. When preempt, AM send request to stop Task and resume the task of unprocessed keys on another node.

5. Randomized weighted majority. Choose the majority vote for whether to checkpointing after each iteration.

6. fixed re-start point. E.g. sum of probability == 1 . Leverage the machine learning robustness of convergence.

7. Asynchronous fault recovery. Operation based. Using the persistent data of previous stage to achieve partial recovery. 

8. Spark streaming support periodcally checkpointing. asynchronous replicating to other worker nodes.         
