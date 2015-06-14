package s_mach.validate.impl

import s_mach.validate.Validator

import scala.reflect.ClassTag

case class FieldValidator[A,B](
  fieldName: String,
  unapply: A => B,
  vb: Validator[B]
)(implicit
  ca:ClassTag[A]
) extends ValidatorImpl[A] {
  def apply(a: A) = vb(unapply(a)).map(_.pushPath(fieldName))
  val rules = vb.rules.map(_.pushPath(fieldName))
  val descendantSchema = vb.descendantSchema.map(_.pushPath(fieldName))
}
