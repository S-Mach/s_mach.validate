package s_mach

import s_mach.validate.ValidatorBuilder._
import scala.reflect.ClassTag

package object validate {

  /** @return the Validator for the type */
  def validator[A](implicit v:Validator[A]) = v

  implicit class Net_SMach_PimpEverything[A](val self: A) extends AnyVal {
    /** @return list of rules that did not pass OR Nil if valid */
    def validate(implicit v:Validator[A]) : List[Rule] = v(self)
  }

  implicit class Net_SMach_PimpMyValidator[A](val self: Validator[A]) extends AnyVal {
    import self._

    /** @return an optional validator wrapper of self */
    def optional(implicit ca:ClassTag[A]) = OptionValidator(self)
    /** @return a collection validator wrapper of self */
    def zeroOrMore(implicit ca:ClassTag[A]) = TraversableValidator(self)
    /** @return a list of all rules and schema */
    def explain : List[Explain] = rules ++ schema
    /** @return TRUE if the validator has no rules or schema */
    def isEmpty = rules.isEmpty && schema.isEmpty
  }

}
