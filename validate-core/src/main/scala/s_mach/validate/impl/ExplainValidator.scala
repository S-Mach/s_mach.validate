package s_mach.validate.impl

import s_mach.validate.Rule

import scala.reflect.ClassTag

/**
 * A validator that adds a rule for display but not check
 * @param r rule to display but not check
 * @tparam A type validated
 */
case class ExplainValidator[A](
  r: Rule
)(implicit
  ca:ClassTag[A]
) extends ValidatorImpl[A] {
  def apply(a: A) = Nil
  val rules = r  :: Nil
  val descendantSchema = Nil
}

