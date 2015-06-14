package s_mach.validate.impl

import s_mach.validate._

import scala.reflect.ClassTag

case class ValueTypeValidator[V <: IsValueType[A],A](
  va: Validator[A]
)(implicit
  ca: ClassTag[A],
  cv: ClassTag[V]
) extends ValidatorImpl[V] {
  def apply(a: V) = va(a.underlying)
  def rules = va.rules
  def descendantSchema = va.descendantSchema
  // Note: using underlying type in Schema for better output in explain
  override val schema = Schema(Nil,ca.toString(),(1,1))
}
