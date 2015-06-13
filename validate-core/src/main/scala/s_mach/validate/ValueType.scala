package s_mach.validate

trait ValueType[V <: IsValueType[A],A] {
  def apply(a: A): V
  def unapply(v: V): A
}

object ValueType {
  def apply[V <: IsValueType[A],A](
    apply: A => V,
    unapply: V => A
  ) = {
    val _apply = apply
    val _unapply = unapply
    new ValueType[V,A] {
      def apply(a: A) = _apply(a)
      def unapply(v: V) = _unapply(v)
    }
  }
}
