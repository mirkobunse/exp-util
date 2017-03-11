package exp.util


import scala.collection.immutable.ListMap
import scala.collection.JavaConverters._
import java.util.function.Consumer
import exp.util.Properties._

object JavaProperties {

  def read(path: String, name: String) = Properties.read(path, name).asJava

  def write(path: String, p: java.util.Map[String, Property]) =
    Properties.write(path, ListMap(p.entrySet().asScala.toSeq.map(f => f.getKey -> f.getValue): _*))

  /**
   * Conduct an experiment with Java
   *
   * @param path path of the properties input file
   * @param name name of the experiment
   * @param splitOn properties to split for individual experiments
   * @param defaultParLevel level of parallelism (how many experiments are conducted in parallel)
   * @param enforceParLevel if false, defaultParLevel is overwritten by the property 'parallelismLevel'
   *                        (if present)
   * @param experiment Java function conducting each individual experiment
   */
  def runExperiments(path: String, name: String, splitOn: java.util.List[String],
                  defaultParLevel: Int, enforceParLevel: Boolean,
                  experiment: java.util.function.Consumer[java.util.Map[String,Property]]) =
    Properties.read(path, name).splitOn(splitOn.asScala).
      par.level(defaultParLevel, enforceParLevel).
      foreach(f => experiment.accept(f.asJava))

}