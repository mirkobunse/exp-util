package exp.util

import java.io.File
import java.io.PrintWriter
import java.util.Calendar

import scala.io.Source
import scala.collection.immutable.ListMap
import scala.collection.parallel.mutable.ParArray
import scala.collection.parallel.ForkJoinTaskSupport
import scala.concurrent.forkjoin.ForkJoinPool

class Properties

object Properties {
  
  implicit class PropertyListMap(val m: ListMap[String, Property]) {
    def splitOn(key: String) = m apply key match {
      case ListProperty(value)  => value.map(f => (m - key) + (key -> f)).toArray
      case RangeProperty(value) => value.toList.map(f => (m - key) + (key -> IntProperty(f))).toArray
      case _ => Array(m)
    }
    def splitOn(keys: Seq[String]): Array[ListMap[String, Property]] = 
      if (keys.length == 1) m.splitOn(keys(0)) else m.splitOn(keys(0)).splitOn(keys.slice(1, keys.length))
    def write(path: String)  = Properties.write(path, m)
  }
  
  implicit class PropertyListMapArray(val a: Array[ListMap[String, Property]]) {
    def splitOn(key: String) = a.flatMap(_.splitOn(key))
    def splitOn(keys: Seq[String]): Array[ListMap[String, Property]] = 
      if (keys.length == 1) a.splitOn(keys(0)) else a.splitOn(keys(0)).splitOn(keys.slice(1, keys.length))
  }
  
  implicit class ConfParArray(val p: ParArray[ListMap[String, Property]]) {
    private[this] def level(parallelism: Int): ParArray[ListMap[String, Property]] = {
      p.tasksupport = new ForkJoinTaskSupport(new ForkJoinPool(parallelism))
      p
    }
    def level(default: Int, enforce: Boolean): ParArray[ListMap[String, Property]] =
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
  def read(path: String, name: String): ListMap[String, Property] =
    ListMap(Source.fromFile(path).getLines()
      .filter(p => p.trim.length > 0 && !p.startsWith("#"))
      .map(f => {
        val s = f.split("=")
        (s(0).trim(), StringProperty(s(1).trim()))
      }).toSeq:_*) +      // ListMap(..toSeq:_*) preserves order
      (EXPERIMENT_NAME -> StringProperty(name)) +
      (START_TIME      -> StringProperty(Calendar.getInstance().getTime().toString())) +
      (BASE_PROPERTIES -> StringProperty(path))
  
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