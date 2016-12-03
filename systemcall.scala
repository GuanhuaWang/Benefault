//on the driver
def main(){
	val sc=new SparkContext()
	val rdd = sc.makeRDD()
	val finalRDD = rdd.transformation()
	val result = finalRDD.action()
}


//In the final action (finalRDD.action)
//Generate job stages and tasks
sc.runjob()
dagScheduler.runJob()
dagScheduler.submitJob()
dagSchedulerEventProcessActor ! Jobsubmitted
dagScheduler.handle.JobSubmitted()
finalStage = newStage()
mapOutputTracker.registerShuffle(shuffleId, rdd.partitions.size)
dagScheduler.submitStage()
missingStages = dagScheduler.getMissingParentStages()
dagScheduler.subMissingTasks(readyStage)

//add tasks to the taskScheduler
taskScheduler.submitTasks(new TaskSet(tasks))
fifoSchedulableBuilder.addTaskSetManager(taskSet)

//send tasks 
sparkDeploySchedulerBackend.reviveOffers()
driverActor ! ReviveOffers
sparkDeploySchedulerBackend.makeOffers()
sparkDeploySchedulerBackend.launchTasks()
foreach task:
	CoarseGrainedExecutorBackend(executorId) ! LaunchTask(serializedTask)




//On the worker node:
coarseGrainedExecutorBackend ! LaunchTask(serializedTask)
=> executor.launchTask()
=> executor.threadPool.execute(new TaskRunner(taskId,serializedTask))

In TaskRunner.run()
=> CoarseGrainedExecutorBackend.statusUpdate()
=> task = ser.deserialize(serializedTask)
=>value = task.run(taskId)
=>directResult = new directTaskResult(ser.serialize(value))
=>  if (directResult.size()>akkaFrameSize())
		indirectResult = blockManager.putBytes(taskId, directResult, MEMORY+DISK+SER)
	else
		return directResult
=> coarseGrainedExexcutorBackend.statusUpdate(result)
=> dirver ! StatusUpdate(executorId,taskId,result)

//map & reduce tasks on the slave nodes
In task.run(taskId)
//if the task is ShuffleMapTask
=> shuffleMaptTask.runTask(context)
=> shuffleWriterGroup = shuffleBlockManager.forMapTask(shuffleId,partitionId,numOutputSplits)
=> shuffleWriterGroup.writers(bucketId).write(rdd.iterator(split,context))
=> return MapStatus(blockManager.blockMangerId,Array[compressedSize(fileSegment)])

//if the task is ResultTask
=> return func(context,rdd.iterator(split,context))