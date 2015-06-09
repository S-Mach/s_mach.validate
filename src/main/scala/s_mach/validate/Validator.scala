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
   * @return list of issues that failed to validate or
   *         Nil if the instance passes all validation
   */
  def apply(a: A) : List[Issue]

  /** @return list of issues that are validated */
  def issues : List[Issue]

  /** @return list of schema for fields or just one */
  def schema: List[Schema]
}

object Validator {
  private[this] val _empty = new Validator[Any] {
    def apply(a: Any) = Nil
    def issues = Nil
    def schema = Nil
  }
  /** @return validator that has no issues or schema and never fails */
  def empty[A] = _empty.asInstanceOf[Validator[A]]

  /**
   * A validator that is composed of zero or more validators
   * @param validators composed validators
   * @tparam A type validated
   */
  def apply[A](validators: List[Validator[A]]) : Validator[A] =
    CompositeValidator[A](validators)

  /**
   * A validator that tests a constraint
   * @param message text to explain what the constraint tests
   * @param f tests the constraint
   * @tparam A type validated
   */
  def ensure[A](message: String)(f: A => Boolean) : Validator[A] =
    EnsureValidator(message,f)

  /**
   * A validator that adds a message to issues
   * @param i issue to add
   * @tparam A type validated
   */
  def explain[A](i: Issue) : Validator[A] =
    ExplainValidator(i)

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
  ) : Validator[Option[A]] = OptionValidator(va)

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
    ca:ClassTag[A]
  ) : Validator[M[A]] = TraversableValidator(va)

  implicit def validator_Option[A](implicit
    va:Validator[A] = Validator.empty[A],
    ca:ClassTag[A]
  ) : Validator[Option[A]] = OptionValidator(va)

  implicit def validator_Traversable[
    M[AA] <: Traversable[AA],
    A
  ](implicit
    va:Validator[A] = Validator.empty[A],
    ca:ClassTag[A]
  ) : Validator[M[A]] = TraversableValidator(va)

}
