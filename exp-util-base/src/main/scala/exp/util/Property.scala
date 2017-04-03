package exp.util

import scala.language.postfixOps
import org.slf4j.LoggerFactory

class Property(val s: String) extends Serializable {

  def asString() = s

  def asDouble() = s.toDouble

  def asInt() = s.toInt
  
  def asList(): List[Property] = (s match {
    
    case Property.matchDoubleRange(l, u, null, null) =>
      l.toDouble to u.toDouble by 1.0 map(_.toString) toList
      
    case Property.matchDoubleRange(l, u, _, b) =>
      l.toDouble to u.toDouble by b.toDouble map(_.toString) toList
      
    case Property.matchIntRange(l, u, null, null) =>
      l.toInt to u.toInt map(_.toString) toList
      
    case Property.matchIntRange(l, u, _, b) =>
      l.toInt to u.toInt by b.toInt map(_.toString) toList
      
    case Property.matchList(inner) => inner split (",") toList
    
    case _ => {
      Property.log.warn("Property '%s' not formatted as list or range. ".format(s) +
          "asList() returns single-element list '{ %s }'.".format(s))
      List(s)
    }
    
  }) map (f => new Property(f.trim))
  
  override def toString = s
  
  override def equals(that: Any) =
    if (that.isInstanceOf[Property])
      that.asInstanceOf[Property].s.equals(s)
    else if (that.isInstanceOf[String])
      that.equals(s)
    else
      false

}

object Property {
  
  private val log = LoggerFactory.getLogger( classOf[Property] )
  
  private val matchDoubleRange = """(-?\d+\.\d+)\s*to\s*(-?\d+\.\d+)(\s*by\s*)?(-?\d+\.\d+)?""".r
  private val matchIntRange = """(-?\d+)\s*to\s*(-?\d+)(\s*by\s*)?(-?\d+)?""".r
  private val matchList = """\{\s*(.*)\s*\}""".r
  
}
