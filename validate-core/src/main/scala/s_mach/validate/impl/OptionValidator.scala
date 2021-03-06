package s_mach.validate.impl

import s_mach.validate._

import scala.reflect.ClassTag

/**
 * A validator for an Option[A] that always passes if set to None
 * @param va the validator for A
 * @param ca class tag for A
 * @tparam A type validated
 */
case class OptionValidator[A](
  va:Validator[A]
)(implicit
  ca:ClassTag[A]
) extends ValidatorImpl[Option[A]] {
  def apply(oa: Option[A]) = oa.fold(List.empty[Rule])(a => va(a))
  val rules = va.rules
  override val schema = va.schema.copy(cardinality = (0,1))
  val descendantSchema = va.descendantSchema
}

