package s_mach.validate

/**
 * A base trait for a user-defined value-class (UDVC) that is used to
 * constrain the value space of the underlying type . The UDVC attempts
 * to behave exactly as the underlying type in code. Methods
 * such as toString, hashCode and equals pass-thru to the underlying
 * type. For other methods, implicit conversion from the UDVC to the
 * underling type is provided in the s_mach.validate package object.
 * Zero-runtime cost conversion to the underlying type is provided
 * automatically through Scala's value-class (see
 * http://docs.scala-lang.org/overviews/core/value-classes.html).
 *
 * Example:
 *
 * implicit class Name(underlying: String) extends AnyVal with IsValueType[String]
 *
 * @tparam A type of underlying value class (Note: this parameter does not require
 *           inheritance from AnyVal since this would prevent using the trait with
 *           java.lang.String which does not inherit AnyVal)
 */
trait IsValueClass[A] extends Any {
  def underlying: A

  override def toString = underlying.toString
  override def hashCode = underlying.hashCode
  override def equals(a: Any) = underlying.equals(a)
}