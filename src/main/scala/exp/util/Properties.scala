package exp.util

import java.io.File
import java.io.PrintWriter
import java.util.Calendar

import scala.io.Source
import scala.collection.immutable.ListMap
import scala.collection.parallel.mutable.ParArray
import scala.collection.parallel.ForkJoinTaskSupport
import scala.concurrent.forkjoin.ForkJoinPool
import scala.util.Try

class Properties

object Properties {
  
  implicit class PropertyListMap(val m: ListMap[String, String]) {

    val _matchRange = """([0-9]+)\s*to\s*([0-9]+)""".r
    val _matchList = """\{\s*(.*)\s*\}""".r

    def getAsString(key: String) = m.get(key)

    def getAsDouble(key: String): Option[Double] = m.get(key) match {
      case Some(property) if Try(property.toDouble).isSuccess => Option(property.toDouble)
      case _ => None
    }

    def getAsInt(key: String): Option[Int] = m.get(key) match {
      case Some(property) if Try(property.toInt).isSuccess => Option(property.toInt)
      case _ => None
    }

    def getAsList(key: String): Option[List[String]] = m.get(key) match {
      case Some(v) => v match {
        case _matchRange(l, u) => Option(l.toInt to u.toInt map (_.toString) toList)
        case _matchList(inner) => Option(inner split (",") toList)
        case _ => None
      }
      case _ => None
    }
  
    def splitOn(key: String) = m getAsList key match {
      case Some(list) => list.map(f => (m - key) + (key -> f) ) toArray
      case _ => Array(m)
    }
    
    def splitOn(keys: Seq[String]): Array[ListMap[String, String]] = 
      if (keys.length == 1) m.splitOn(keys(0)) else m.splitOn(keys(0)).splitOn(keys.slice(1, keys.length))
      
    def write(path: String)  = Properties.write(path, m)
    
  }
  
  implicit class PropertyListMapArray(val a: Array[ListMap[String, String]]) {
    
    def splitOn(key: String) = a.flatMap(_.splitOn(key))
    
    def splitOn(keys: Seq[String]): Array[ListMap[String, String]] = 
      if (keys.length == 1) a.splitOn(keys(0)) else a.splitOn(keys(0)).splitOn(keys.slice(1, keys.length))
      
  }
  
  implicit class ConfParArray(val p: ParArray[ListMap[String, String]]) {
    
    private[this] def level(parallelism: Int): ParArray[ListMap[String, String]] = {
      p.tasksupport = new ForkJoinTaskSupport(new ForkJoinPool(parallelism))
      p
    }
    
    def level(default: Int, enforce: Boolean): ParArray[ListMap[String, String]] =
      level(if (enforce) default else p(0).getOrElse(PARALLELISM_LEVEL, default).toString.toInt)
      
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
  def read(path: String, name: String): ListMap[String, String] =
    ListMap(Source.fromFile(path).getLines()
      .filter(p => p.trim.length > 0 && !p.startsWith("#"))  // filter comment lines
      .map(f => {
        // split into property name and value
        val s = f.split("=").map(_.trim)
        (s(0), s(1))
      }).toSeq:_*) +      // ListMap(..toSeq:_*) preserves order
      (EXPERIMENT_NAME -> name) +
      (START_TIME      -> Calendar.getInstance().getTime().toString()) +
      (BASE_PROPERTIES -> path)
  
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

}