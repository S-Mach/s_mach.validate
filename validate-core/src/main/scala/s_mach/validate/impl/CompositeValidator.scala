package s_mach.validate.impl

import s_mach.validate.Validator

/**
 * A validator that is composed of zero or more validators
 * @param validators composed validators
 * @tparam A type validated
 */
case class CompositeValidator[A](validators: List[Validator[A]]) extends ValidatorImpl[A] {
  def apply(a: A) = validators.flatMap(_(a))
  val rules = validators.flatMap(_.rules)
  val schema = validators.flatMap(_.schema)
  val explain = validators.flatMap(_.explain)
  override def and(other: Validator[A]) : CompositeValidator[A] =
    other match {
      case CompositeValidator(more) => copy(validators ::: more)
      case _ => copy(validators ::: List(other))
    }
}

