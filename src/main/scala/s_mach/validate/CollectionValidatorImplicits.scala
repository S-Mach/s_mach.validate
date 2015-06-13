package s_mach.validate

import scala.language.higherKinds
import scala.reflect.ClassTag
import s_mach.validate.impl._

object CollectionValidatorImplicits extends CollectionValidatorImplicits
trait CollectionValidatorImplicits {
  /** @return an optional validator wrapper for any type that implicitly defines
    *         a validator */
  implicit def validator_Option[A](implicit
    va:Validator[A],
    ca:ClassTag[A]
  ) = OptionValidator[A](va)

  /** @return a collection validator wrapper for any type that implicitly defines
    *         a validator */
  implicit def validator_Traversable[
    M[AA] <: Traversable[AA],
    A
  ](implicit
    va:Validator[A],
    ca:ClassTag[A],
    cm:ClassTag[M[A]]
  ) = CollectionValidator[M,A](va)
}
