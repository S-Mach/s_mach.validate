package s_mach.validate.impl

import s_mach.validate.{Schema, Validator}

import scala.reflect.ClassTag

/**
 * A validator that is composed of zero or more validators
 * @param validators composed validators
 * @tparam A type validated
 */
case class CompositeValidator[A](
  validators: List[Validator[A]]
)(implicit
  ca:ClassTag[A]
) extends ValidatorImpl[A] {
  def apply(a: A) = validators.flatMap(_(a))
  val rules = validators.flatMap(_.rules)
  val descendantSchema = validators.flatMap(_.descendantSchema)

  override def and(other: Validator[A]) : CompositeValidator[A] =
    other match {
      case CompositeValidator(more) => copy(validators ::: more)
      case _ => copy(validators ::: List(other))
    }
}

