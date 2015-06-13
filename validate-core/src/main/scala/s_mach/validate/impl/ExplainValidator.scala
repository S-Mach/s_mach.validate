package s_mach.validate.impl

import s_mach.validate.Rule

/**
 * A validator that adds a rule for display but not check
 * @param r rule to display but not check
 * @tparam A type validated
 */
case class ExplainValidator[A](r: Rule) extends ValidatorImpl[A] {
  def apply(a: A) = Nil
  val rules = r  :: Nil
  val schema = Nil
  val explain = rules
}

