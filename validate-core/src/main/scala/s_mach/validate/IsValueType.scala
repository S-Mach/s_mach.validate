package s_mach.validate

trait IsValueType[A] extends Any {
  def underlying: A
  override def toString = underlying.toString
  override def hashCode = underlying.hashCode
  override def equals(a: Any) = underlying.equals(a)
}