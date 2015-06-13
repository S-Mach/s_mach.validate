package s_mach.validate


import scala.language.higherKinds
import scala.language.experimental.macros
import scala.reflect.macros.blackbox
import scala.reflect.ClassTag
import s_mach.validate.impl._

/**
 * A type-class for validating instances of a type
 * @tparam A type validated
 */
trait Validator[A] {
  /**
   * Validate an instance
   * @param a instance to validate
   * @return list of rules that failed to validate or
   *         Nil if the instance passes all validation
   */
  def apply(a: A) : List[Rule]

  /** @return list of rules that this validator tests */
  def rules : List[Rule]

  /** @return list of schema for type A and any fields of A (recursively) */
  def schema: List[Schema]

  /** @return list of rules and schema for type A */
  def explain: List[Explain]

  /**
   * Compose two validators
   * @param other validtor to compose with this
   * @return a new validator composed of this and other
   */
  def and(other: Validator[A]) : Validator[A]
}

object Validator {
  /**
   * Generate a DataDiff implementation for a product type
   * @tparam A the value type
   * @return the DataDiff implementation
   */
  def forProductType[A <: Product] : Validator[A] =
    macro macroForProductType[A]

  // Note: Scala requires this to be public
  def macroForProductType[A:c.WeakTypeTag](
    c: blackbox.Context
  ) : c.Expr[Validator[A]] = {
    val builder = new impl.ValidateMacroBuilderImpl(c)
    builder.build[A]().asInstanceOf[c.Expr[Validator[A]]]
  }

  /**
   *
   * @param other
   * @param va
   * @param vt
   * @tparam V
   * @tparam A
   * @return
   */
  def forValueType[V <: IsValueType[A],A](other: Validator[A])(implicit
    va:Validator[A],
    vt: ValueType[V,A]
  ) = ValueTypeValidator[V,A](
    va and other
  )

  /**
   * A validator that is composed of zero or more validators
   * @param validators composed validators
   * @tparam A type validated
   */
  def apply[A](validators: Validator[A]*) : CompositeValidator[A] =
    CompositeValidator[A](validators.toList)

  /**
   * A validator that tests a constraint
   * @param message text to explain what the constraint tests
   * @param f tests the constraint
   * @tparam A type validated
   */
  def ensure[A](message: String)(f: A => Boolean) =
    EnsureValidator[A](message,f)

  /**
   * A validator that adds a comment to rules
   * @param message comment
   * @tparam A type validated
   */
  def comment[A](message: String) =
    ExplainValidator[A](Rule(Nil,message))

  def schema[A](
    typeName: String,
    cardinality: (Int,Int) = (1,1)
  ) =
    SchemaValidator[A](Schema(Nil, typeName, cardinality))

  def schema[A](
    cardinality: (Int,Int)
  )(implicit
    ca: ClassTag[A]
  ) =
    SchemaValidator[A](Schema(Nil, ca.toString(), cardinality))

  def schema[A]()(implicit
    ca: ClassTag[A]
  ) =
    SchemaValidator[A](Schema(Nil, ca.toString(), (1,1)))

  /**
   * A validator for an Option[A] that always passes if set to None
   * @param va the validator for A
   * @param ca class tag for A
   * @tparam A type validated
   */
  def optional[A](
    va: Validator[A]
  )(implicit
    ca:ClassTag[A]
  ) = OptionValidator[A](va)

  /**
   * A validator for a collection of A
   * @param va the validator for A
   * @param ca the class tag for A
   * @tparam M the collection type
   * @tparam A the type validated
   */
  def zeroOrMore[
    M[AA] <: Traversable[AA],
    A
  ](
    va: Validator[A]
  )(implicit
    ca:ClassTag[A],
    cm:ClassTag[M[A]]
  ) = CollectionValidator[M,A](va)
}
