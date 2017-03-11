package exp.util

import java.io.File
import java.io.PrintWriter
import java.util.Calendar

import scala.io.Source
import scala.collection.immutable.ListMap
import scala.collection.parallel.ParSeq
import scala.collection.parallel.ForkJoinTaskSupport
import scala.concurrent.forkjoin.ForkJoinPool
import scala.util.Try

class Properties

object Properties {
  
  implicit class PropertyListMap(val m: ListMap[String, Property]) {

    def getString(key: String) = m.get(key).get.asString

    def getDouble(key: String) = m.get(key).get.asDouble

    def getInt(key: String) = m.get(key).get.asInt

    def getList(key: String) = m.get(key).get.asList
  
    def splitOn(key: String) = m.getList(key).map(f => (m - key) + (key -> f) ) toSeq
    
    def splitOn(keys: Seq[String]): Seq[ListMap[String, Property]] = 
      if (keys.length == 1) m.splitOn(keys(0)) else m.splitOn(keys(0)).splitOn(keys.slice(1, keys.length))
    
    def where(kv: (String, String)) = m + (kv._1 -> new Property(kv._2))
        
    def write(path: String)  = Properties.write(path, m)
    
  }
  
  implicit class PropertyListMapSeq(val a: Seq[ListMap[String, Property]]) {
    
    def splitOn(key: String) = a.flatMap(_.splitOn(key))
    
    def splitOn(keys: Seq[String]): Seq[ListMap[String, Property]] = 
      if (keys.length == 1) a.splitOn(keys(0)) else a.splitOn(keys(0)).splitOn(keys.slice(1, keys.length))
      
  }
  
  implicit class ConfParSeq(val p: ParSeq[ListMap[String, Property]]) {
    
    private def level(parallelism: Int): ParSeq[ListMap[String, Property]] = {
      p.tasksupport = new ForkJoinTaskSupport(new ForkJoinPool(parallelism))
      p
    }
    
    def level(default: Int, enforce: Boolean): ParSeq[ListMap[String, Property]] =
      level(if (enforce) default else Try(p(0).getInt(PARALLELISM_LEVEL)).getOrElse(default))
      
  }

  val EXPERIMENT_NAME = "@experimentName"
  val START_TIME = "@startTime"
  val BASE_PROPERTIES = "@baseProperties"
  
  val PARALLELISM_LEVEL = "parallelismLevel"

  /**
   * Read a property mapping from a file.
   *
   * @param path path of the input file
   * @param name name of the experiment
   * @return property mapping specified in file
   */
  def read(path: String, name: String): ListMap[String, Property] =
    ListMap(Source.fromFile(path).getLines()
      .filter(p => p.trim.length > 0 && !p.startsWith("#"))  // filter comment lines
      .map(f => {
        // split into property name and value
        val s = f.split("=").map(_.split("#")(0).trim)
        (s(0), new Property(s(1)))
      }).toSeq:_*) where      // ListMap(..toSeq:_*) preserves order
      (EXPERIMENT_NAME -> name) where
      (START_TIME      -> Calendar.getInstance().getTime().toString()) where
      (BASE_PROPERTIES -> path)
  
  /**
   * Write a property mapping to a file
   *
   * @param path path of the output file
   * @param p property mapping
   */
  def write(path: String, p: Map[String, Property]) {
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

}