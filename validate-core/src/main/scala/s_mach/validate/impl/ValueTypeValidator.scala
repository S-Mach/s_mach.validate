package s_mach.validate.impl

import s_mach.validate._

case class ValueTypeValidator[V <: IsValueType[A],A](
  va: Validator[A]
)(implicit
  vt: ValueType[V,A]
) extends ValidatorImpl[V] {
  def apply(a: V) = va(vt.unapply(a))
  def rules = va.rules
  def schema = va.schema
  def explain = va.explain
}
