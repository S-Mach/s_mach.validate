package s_mach.validate.impl

import s_mach.validate.Schema

/**
 * A validator that adds a schema
 * @param s schema to add
 * @tparam A type validated
 */
case class SchemaValidator[A](s: Schema) extends ValidatorImpl[A] {
  def apply(a: A) = Nil
  val rules = Nil
  val schema = s :: Nil
  val explain = schema
}

