import jcuda.driver.JCudaDriver._
import java.io._
import jcuda._
import jcuda.driver._
//import jcuda
import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf
import org.apache.spark.rdd._
import org.apache.spark.sql.SQLContext
import org.apache.spark.sql._
import org.apache.spark.sql.SQLContext._
//remove if not needed
import scala.collection.JavaConversions._


object JCudaDFVectorAdd {

  def main(args: Array[String]) {

    val conf = new SparkConf().setAppName("dataframe gpu")
    val sc = new SparkContext(conf)
    val sqlContext= new SQLContext(sc)
    import sqlContext.implicits._

    //prepare host data
    val numElements = 256*256
    val data1 = Array.ofDim[Int](numElements)
    val data2 = Array.ofDim[Int](numElements)
    for (i <- 0 until numElements) {
      data1(i) = i.toInt
      data2(i) = i.toInt
    }
    //val data1 = Array(1, 2, 3, 4, 5,6)
    //val data2 = Array(1, 2, 3, 4, 5,6)

    val distData1 = sc.parallelize(data1)
    val distData2 = sc.parallelize(data2)
    val df1 = distData1.toDF
    val df2 = distData2.toDF
    var hostInputA = Array.ofDim[Float](numElements)
    var hostInputB = Array.ofDim[Float](numElements)
    
    val timestamp0: Long = System.currentTimeMillis 
    val dfc1 = df1.collect()
    val dfc2 = df2.collect()

    val timestamp1: Long = System.currentTimeMillis 
    for( a <- 0 until numElements){
      hostInputA(a)=dfc1(a).getInt(0).toFloat;
      hostInputB(a)=dfc2(a).getInt(0).toFloat;
    }
    val timestamp2: Long = System.currentTimeMillis 
    val df_to_arr = (timestamp2 - timestamp1)
    val dfcollect = (timestamp1 - timestamp0)


    JCudaDriver.setExceptionsEnabled(true)

    val ptxFileName = preparePtxFile("JCudaVectorAddKernel.cu")
    cuInit(0)

    val device = new CUdevice()
    cuDeviceGet(device, 0)

    val context = new CUcontext()
    cuCtxCreate(context, 0, device)

    val module = new CUmodule()
    cuModuleLoad(module, ptxFileName)

    val function = new CUfunction()
    cuModuleGetFunction(function, module, "add")


    val timestamp7: Long = System.currentTimeMillis 

    val deviceInputA = new CUdeviceptr()
    val deviceInputB = new CUdeviceptr()
    val deviceOutput = new CUdeviceptr()
    cuMemAlloc(deviceInputA, numElements * Sizeof.FLOAT)
    cuMemAlloc(deviceInputB, numElements * Sizeof.FLOAT)
    cuMemAlloc(deviceOutput, numElements * Sizeof.FLOAT)

    val timestamp8: Long = System.currentTimeMillis 

    cuMemcpyHtoD(deviceInputA, Pointer.to(hostInputA), numElements * Sizeof.FLOAT)
    cuMemcpyHtoD(deviceInputB, Pointer.to(hostInputB), numElements * Sizeof.FLOAT)
    val timestamp3: Long = System.currentTimeMillis 

    val copytogpu = (timestamp3 - timestamp8)
    val alloc_mem = (timestamp8 - timestamp7)


    val kernelParameters = Pointer.to(Pointer.to(Array(numElements)), Pointer.to(deviceInputA), Pointer.to(deviceInputB), 
      Pointer.to(deviceOutput))
    val blockSizeX = 256
    val gridSizeX = Math.ceil(numElements.toDouble / blockSizeX).toInt

    val timestamp4: Long = System.currentTimeMillis 
    cuLaunchKernel(function, gridSizeX, 1, 1, blockSizeX, 1, 1, 0, null, kernelParameters, null)
    cuCtxSynchronize()
    val timestamp5: Long = System.currentTimeMillis 
    val calculating = (timestamp5 - timestamp4)


    val hostOutput = Array.ofDim[Float](numElements)
    cuMemcpyDtoH(Pointer.to(hostOutput), deviceOutput, numElements * Sizeof.FLOAT)
    val timestamp6: Long = System.currentTimeMillis 
    val copy_back = (timestamp6 - timestamp5)

    var passed = true
    for (i <- 0 until numElements) {
      val expected = i + i
      if (Math.abs(hostOutput(i) - expected) > 1e-5) {
        println("At index " + i + " found " + hostOutput(i) + " but expected " + 
          expected)
        passed = false
        //break
      }
    }
    println("Test " + (if (passed) "PASSED" else "FAILED"))
    println("DF.collect() " + dfcollect +"ms")
    println("DF.collect() to scala array " + df_to_arr +"ms")
    println("Allocate gpu memory" + alloc_mem +"ms")
    println("Copy from cpu to gpu " + copytogpu +"ms")
    println("Calculating Time " + calculating +"ms")
    println("Copying Back Time " + copy_back +"ms")


    cuMemFree(deviceInputA)
    cuMemFree(deviceInputB)
    cuMemFree(deviceOutput)
  }

  private def preparePtxFile(cuFileName: String): String = {
    var endIndex = cuFileName.lastIndexOf('.')
    if (endIndex == -1) {
      endIndex = cuFileName.length - 1
    }
    val ptxFileName = cuFileName.substring(0, endIndex + 1) + "ptx"
    val ptxFile = new File(ptxFileName)
    if (ptxFile.exists()) {
      return ptxFileName
    }
    val cuFile = new File(cuFileName)
    if (!cuFile.exists()) {
      throw new IOException("Input file not found: " + cuFileName)
    }
    val modelString = "-m" + System.getProperty("sun.arch.data.model")
    val command = "nvcc " + modelString + " -ptx " + cuFile.getPath + " -o " + 
      ptxFileName
    println("Executing\n" + command)
    val process = Runtime.getRuntime.exec(command)
    val errorMessage = new String(toByteArray(process.getErrorStream))
    val outputMessage = new String(toByteArray(process.getInputStream))
    var exitValue = 0
    try {
      exitValue = process.waitFor()
    } catch {
      case e: InterruptedException => {
        Thread.currentThread().interrupt()
        throw new IOException("Interrupted while waiting for nvcc output", e)
      }
    }
    if (exitValue != 0) {
      println("nvcc process exitValue " + exitValue)
      println("errorMessage:\n" + errorMessage)
      println("outputMessage:\n" + outputMessage)
      throw new IOException("Could not create .ptx file: " + errorMessage)
    }
    println("Finished creating PTX file")
    ptxFileName
  }

  private def toByteArray(inputStream: InputStream): Array[Byte] = {
    val baos = new ByteArrayOutputStream()
    val buffer = Array.ofDim[Byte](8192)
    while (true) {
      val read = inputStream.read(buffer)
      if (read == -1) {
        //break
      }
      baos.write(buffer, 0, read)
    }
    baos.toByteArray()
  }
}


















