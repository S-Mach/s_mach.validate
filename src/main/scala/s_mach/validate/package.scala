package s_mach

import s_mach.validate.ValidatorBuilder._
import scala.reflect.ClassTag

package object validate {

  def validator[A](implicit v:Validator[A]) = v

  implicit class Net_SMach_PimpEverything[A](val self: A) extends AnyVal {
    def validate(implicit v:Validator[A]) : List[Issue] = v(self)
  }

  implicit class Net_SMach_PimpMyValidator[A](val self: Validator[A]) extends AnyVal {
    import self._

    def optional(implicit ca:ClassTag[A]) = OptionValidator(self)
    def zeroOrMore(implicit ca:ClassTag[A]) = TraversableValidator(self)
    def explain : List[Explain] = issues ++ schema
    def isEmpty = issues.isEmpty && schema.isEmpty
  }

}
