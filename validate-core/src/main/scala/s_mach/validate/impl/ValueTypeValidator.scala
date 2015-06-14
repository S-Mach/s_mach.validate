package s_mach.validate.impl

import s_mach.validate._

import scala.reflect.ClassTag

case class ValueTypeValidator[V <: IsValueType[A],A](
  va: Validator[A]
)(implicit
  vt: ValueType[V,A],
  ca: ClassTag[A],
  cv: ClassTag[V]
) extends ValidatorImpl[V] {
  def apply(a: V) = va(vt.unapply(a))
  def rules = va.rules
  def descendantSchema = va.descendantSchema
  override val schema = Schema(Nil,ca.toString(),(1,1))
}
