package s_mach

import scala.language.higherKinds
import scala.language.implicitConversions
import s_mach.validate.impl._
import scala.reflect.ClassTag

package object validate extends
  TupleValidatorImplicits with
  DataTypeValidatorImplicits with
  CollectionValidatorImplicits
{

  @inline implicit def valueClassToA[A](v: IsValueClass[A]) : A =
    v.underlying

  /** @return the Validator for the type */
  def validator[A](implicit v:Validator[A]) = v

  implicit class Net_SMach_PimpEverything[A](val self: A) extends AnyVal {
    /** @return list of rules that did not pass OR Nil if valid */
    def validate(implicit v:Validator[A]) : List[Rule] = v(self)
  }

  implicit class Net_SMach_PimpMyValidator[A](val self: Validator[A]) extends AnyVal {
    /** @return an optional validator wrapper of self */
    def optional(implicit ca:ClassTag[A]) = OptionValidator(self)
    /** @return a collection validator wrapper of self */
    def zeroOrMore(implicit ca:ClassTag[A]) = CollectionValidator(self)
  }
}
