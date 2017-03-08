package exp.util

abstract class Property {
  
  def asString() = this match {
    case StringProperty(value) => value
    case _ => throw new ClassCastException("This property is not a StringProperty")
  }
  
  def asDouble() = this match {
    case DoubleProperty(value) => value
    case _ => throw new ClassCastException("This property is not a DoubleProperty")
  }
  
  def asInt() = this match {
    case IntProperty(value) => value
    case _ => throw new ClassCastException("This property is not a IntProperty")
  }
  
  def asList() = this match {
    case ListProperty(value) => value
    case _ => throw new ClassCastException("This property is not a ListProperty")
  }
  
  def asRange() = this match {
    case RangeProperty(value) => value
    case _ => throw new ClassCastException("This property is not a RangeProperty")
  }
  
}

case class StringProperty(value: String)       extends Property {
  override def toString = value
  override def equals(that: Any) = value equals that
}

case class DoubleProperty(value: Double)       extends Property {
  override def toString = value.toString
  override def equals(that: Any) = value equals that
}

case class IntProperty(value: Int)             extends Property {
  override def toString = value.toString
  override def equals(that: Any) = value equals that
}

case class ListProperty(value: List[Property]) extends Property {
  override def toString = value.toString
  override def equals(that: Any) = value equals that
}

case class RangeProperty(value: Range)         extends Property {
  override def toString = value.toString
  override def equals(that: Any) = value equals that
}