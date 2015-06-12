package s_mach.validate

import scala.language.higherKinds
import s_mach.validate.ValidatorBuilder._
import scala.reflect.ClassTag

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
}

object Validator {
  private[this] val _empty = new Validator[Any] {
    def apply(a: Any) = Nil
    def rules = Nil
    def schema = Nil
    def explain = Nil
  }
  /** @return validator that has no issues or schema and never fails */
  def empty[A] = _empty.asInstanceOf[Validator[A]]

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

  /**
   * A builder for a Validator for a type A
   * @param ca class tag for A used to build default Schema
   * @tparam A type validated
   */
  def builder[A](implicit ca: ClassTag[A]) = ValidatorBuilder[A]()

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
  ) = TraversableValidator[M,A](va)

  /** @return an optional validator wrapper for any type that implicitly defines
    *         a validator */
  implicit def validator_Option[A](implicit
    va:Validator[A] = Validator.empty[A],
    ca:ClassTag[A]
  ) = OptionValidator[A](va)

  /** @return a collection validator wrapper for any type that implicitly defines
    *         a validator */
  implicit def validator_Traversable[
    M[AA] <: Traversable[AA],
    A
  ](implicit
    va:Validator[A] = Validator.empty[A],
    ca:ClassTag[A],
    cm:ClassTag[M[A]]
  ) = TraversableValidator[M,A](va)

// Note: if Validator[-A] is used this is required
//  implicit val validator_String = empty[String]
}
