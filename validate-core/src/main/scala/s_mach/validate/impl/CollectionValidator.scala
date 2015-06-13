package s_mach.validate.impl

import scala.language.higherKinds
import scala.reflect.ClassTag
import s_mach.validate._

/**
 * A validator for a collection of A
 * @param va the validator for A
 * @param ca the class tag for A
 * @tparam M the collection type
 * @tparam A the type validated
 */
case class CollectionValidator[
  M[AA] <: Traversable[AA],
  A
](
  va:Validator[A]
)(implicit
  ca:ClassTag[A],
  cm:ClassTag[M[A]]
) extends ValidatorImpl[M[A]] {
  def apply(ma: M[A]) =
    ma.toList.zipWithIndex
      .flatMap { case (a,i) =>
        va(a).map(_.pushPath(i.toString))
      }
  val rules = va.rules
  val schema = va.schema.map {
    case s@Schema(Nil,_,_) => s.copy(cardinality = (0,Int.MaxValue))
    case other => other
  }
  val explain = va.explain.map {
    case s@Schema(Nil,_,_) => s.copy(cardinality = (0,Int.MaxValue))
    case other => other
  }
}

