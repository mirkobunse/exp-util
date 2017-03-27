package exp.util

import scala.language.postfixOps

class Property(val s: String) extends Serializable {

  def asString() = s

  def asDouble() = s.toDouble

  def asInt() = s.toInt

  def asList(): List[Property] = s match {
    case Property.matchRange(l, u, null, null) => l.toInt to u.toInt map (f => new Property(f.toString)) toList
    case Property.matchRange(l, u, _, b) => l.toInt to u.toInt by b.toInt map (f => new Property(f.toString)) toList
    case Property.matchList(inner) => inner split (",") map (f => new Property(f.trim)) toList
    case _ => throw new ClassCastException("Property '%s' not formatted as list or range".format(s))
  }
  
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
  private val matchRange = """([0-9]+)\s*to\s*([0-9]+)(\s*by\s*)?([0-9]+)?""".r
  private val matchList = """\{\s*(.*)\s*\}""".r
}