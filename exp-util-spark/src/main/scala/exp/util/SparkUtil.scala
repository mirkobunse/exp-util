package exp.util

import org.apache.spark.SparkContext
import org.apache.spark.SparkFiles

object SparkUtil {
  
  implicit class PropertiesSparkContext(val sc: SparkContext) {
    
    def readProperties(path: String) = SparkUtil.readProperties(sc, path)
    
  }
  
  /**
   * Read the properties file with the given path. Because the --files argument
   * of spark-submit only distributes to worker nodes but not to the driver,
   * reading the properties file is done inside a task.
   * The experiment name property is set to the 'spark.app.name' configuration.
   */
  def readProperties(sc: SparkContext, path: String) = 
    sc.parallelize(Seq( sc.getConf.get("spark.app.name") )).
    map( name => Properties.read( SparkFiles.get(path), name )).
    first
  
}