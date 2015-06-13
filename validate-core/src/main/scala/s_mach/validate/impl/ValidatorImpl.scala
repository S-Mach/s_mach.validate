package s_mach.validate.impl

import s_mach.validate.Validator

abstract class ValidatorImpl[A] extends Validator[A] {
  def and(other: Validator[A]) =
    other match {
      case CompositeValidator(validators) =>
        CompositeValidator(this :: validators)
      case _ =>
        CompositeValidator(this :: other :: Nil)
    }
}

