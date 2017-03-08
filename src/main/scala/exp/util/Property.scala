package exp.util

abstract class Property

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