#Preemption steps in Yarn
1. Preemption Monitor (Yarn) check queues' status periodically (10s of secondes) => calculate list of preempted containers.
2. Notify AM -> `PreemptionMessage` through heartbeat, including `StrictPreemptionContract` and `Preemption Contract`
3. Wait before forceful termination -- `yarn.resourcesmanager.monitor.capacity.preemption.monitoring_interval`. If the container under the preempt list not set free, RM will force it terminate (`ContainerExitStatus=Preempted`). And notify corresponding AM in the next round HeartBeat.



