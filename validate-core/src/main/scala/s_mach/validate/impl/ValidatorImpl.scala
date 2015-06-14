package s_mach.validate.impl

import s_mach.validate.{Schema, Validator}

import scala.reflect.ClassTag

abstract class ValidatorImpl[A](implicit ca:ClassTag[A]) extends Validator[A] {
  val schema = Schema(Nil,ca.toString(),(1,1))

  def and(other: Validator[A]) =
    other match {
      case CompositeValidator(validators) =>
        CompositeValidator(this :: validators)
      case _ =>
        CompositeValidator(this :: other :: Nil)
    }
}

