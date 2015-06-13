package s_mach.validate.impl

import s_mach.validate.Rule

/**
 * A validator that tests a constraint
 * @param message text to explain what the constraint tests
 * @param f tests the constraint
 * @tparam A type validated
 */
case class EnsureValidator[A](message: String, f: A => Boolean) extends ValidatorImpl[A] {
  def apply(a: A) = if(f(a)) Nil else rules
  val rules = Rule(Nil,message) :: Nil
  val schema = Nil
  val explain = rules
}

