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


//Aftere drvier receivers StatusUpdate(result)
driver receives StatusUpdate(result)
=> taskScheduler.statusUpdate(taskId,State,result.value)
=> taskResultGetter.enqueueSuccessfulTask(taskSet, tid, result)
=> if result is indirectResult	
		serializedTaskResult = blockManager.ggetRemoteBytes(IndirectResult.blockId)
=> scheduler.handleSuccessfulTask(taskSetManager, tid,result)
=> taskSetManager.handleSuccessfulTask(tid,taskResult)
=> dagScheduler.taskEnded(result.value,result.accumUpdates)
=> dagSchedulerEventProcessActor ! CompletionEvnet(result,accumUpdates)
=> dageScheduler.handleTaskCompletion(completion)
=> Accumulator.add(event.accumUpdates)

//If the finished task is ResultTask
=> if (job.numFinished == job.numPartitions)
		listenerBus.post(SparkListenerJobEnd(job.jobId,JobSucceeded))
=> job.listener.taskSucceeded(outputId,result)
=> jobWaiter.taskSucceeded(index,result)
resultHandler(index,result)

//if the finished task is SHuffleMapTask()
=> stage.addOutputLoc(smt.partitionId,status)
=> if (all tasks in current stage have finished)
		mapOutputTrackerMaster.registerMapOutputs(shuffleId,Array[MapStatus])
		mapStatuses.put(shuffleId,Array[MapStatus]()++statuses)
=> submitStage(stage)


//shuffle read
rdd.iterator()
=> rdd(e.g. ShuffledRDD/CoGroupedRDD).compute()
=> SparkEnv.get.shuffleFetcher.fetch(shuffleedId,split.index,context,ser)
=>blockStoreSHuffleFetcher.fetch(shuffleId,reduceId,context,serializer)
=>statuses = MapOutputTrackerWorker.getServersStatuses(shuffleId, reduceId)

=> blocksByAddress: Seq[(BlockManagerId,Seq[(BlockId,Long)])] = compute(statuses)
=> basicBlockFetcherIterator = blockManager.getMultiple(blocksByAddress,serializer)
=> itr = basicBlockFetcherIterator.flatMap(unpackBlock)

In basicBlockFetcherIterator:
//generate the fetch requests
=> basicBlockFetcherIterator.initialize()
=> remoteRequests = splitLocalRemoteBlocks()
=> fetchRequests ++= Utils.randomize(remoteRequests)

//fetch remote block
=> sendRequest(fetchRequests.dequeue()) until Size(fetchRequests) > maxBytesInFlight
=> blockManger.connectionManager.sendMessageReliably(cmId,blockMessageArray,toBufferMessage)
=> fetchResults.put(new FetchResult(blockId,sizeMap(blockId)))
=> dataDeserialize(blockId, blockMessage.getDat, serializer)

//fetch local block
=> getLocalBlocks()
=> fetchResults.put(new FetchResult(id, 0, ()) => iter))