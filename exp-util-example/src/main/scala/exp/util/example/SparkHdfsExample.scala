package exp.util.example

import org.apache.spark.SparkConf
import org.apache.spark.SparkFiles
import org.apache.spark.SparkContext
import exp.util.Properties
import exp.util.Properties._
import exp.util.SparkUtil

class SparkHdfsExample

/**
 * Example of exp-util on Spark/YARN with HDFS. Have a look at the README.md of the
 * spark-util-example module for a usage introduction.
 */
object SparkHdfsExample {
  
  def main(args: Array[String]) = {
    
    val sc = new SparkContext(new SparkConf().setAppName( classOf[SparkHdfsExample] getSimpleName ))
    val props = SparkUtil.readProperties(sc, "example.properties")
        
    println(props.format)
    
    /*
     * When the properties are printed correctly, you are free to proceed as can be
     * seen in the basic example.
     */
    
  }
  
}