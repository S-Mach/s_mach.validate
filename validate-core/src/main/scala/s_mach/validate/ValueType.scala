package s_mach.validate

/**
 * A type-class for a user-defined value-type (UDVT) that allows creating
 * an instance of the UDVT from an instance of the underlying type and
 * extracting the value of the underlying type from an instance of the
 * UDVT.
 * @tparam V type of value type
 * @tparam A type of underlying type
 */
trait ValueType[V <: IsValueType[A],A] {
  def apply(a: A): V
  final def unapply(v: V): A = v.underlying
}

object ValueType {
  def apply[V <: IsValueType[A],A](
    apply: A => V
  ) = {
    val _apply = apply
    new ValueType[V,A] {
      def apply(a: A) = _apply(a)
    }
  }
}
