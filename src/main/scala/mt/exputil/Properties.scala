package mt.exputil

import java.io.File
import java.io.PrintWriter
import java.util.Calendar

import scala.collection.JavaConverters.asScalaSetConverter
import scala.collection.JavaConverters.mapAsJavaMapConverter
import scala.collection.immutable.ListMap
import scala.io.Source

class Properties

object Properties {
  
  implicit class PropertyListMap(val m: ListMap[String,String]) {
    def splitOn(key: String) = m.apply(key).split(",").map(f => (m - key) + (key -> f))
    def splitOn(keys: Seq[String]): Array[ListMap[String, String]] = 
      if (keys.length == 1) m.splitOn(keys(0)) else m.splitOn(keys(0)).splitOn(keys.slice(1, keys.length))
    def write(path: String)  = Properties.write(path, m)
  }
  
  implicit class PropertyListMapArray(val a: Array[ListMap[String, String]]) {
    def splitOn(key: String) = a.flatMap(f => f.splitOn(key))
    def splitOn(keys: Seq[String]): Array[ListMap[String, String]] = 
      if (keys.length == 1) a.splitOn(keys(0)) else a.splitOn(keys(0)).splitOn(keys.slice(1, keys.length))
  }

  val EXPERIMENT_NAME = "@experimentName"
  val START_TIME = "@startTime"
  val BASE_PROPERTIES = "@baseProperties"

  /**
   * Read a property mapping from a file.
   *
   * @param path path of the input file
   * @param name name of the experiment
   * @return property mapping specified in file
   */
  def read(path: String, name: String) =
    ListMap(Source.fromFile(path).getLines()
      .filter(p => !p.startsWith("#"))
      .map(f => {
        val s = f.split("=")
        (s(0).trim(), s(1).trim())
      }).toSeq:_*) +      // ListMap(..toSeq:_*) preserves order
      (EXPERIMENT_NAME -> name) +
      (START_TIME -> Calendar.getInstance().getTime().toString()) +
      (BASE_PROPERTIES -> path)

  def readJava(path: String, name: String) = read(path, name).asJava
  
  /**
   * Write a property mapping to a file
   *
   * @param path path of the output file
   * @param p property mapping
   */
  def write(path: String, p: Map[String, String]) {
    val l = p.keysIterator.map(_.length).max
    val w = new PrintWriter(new File(path))
    try {
      p.foreach(f => w.write(
        String.format("%1$" + l + "s = %2$s\n", f._1, f._2.toString)))
    } finally {
      if (w != null)
        w.close
    }
  }

  def writeJava(path: String, p: java.util.Map[String, String]) =
    write(path, ListMap(p.entrySet().asScala.toSeq.map(f => f.getKey -> f.getValue):_*))

}