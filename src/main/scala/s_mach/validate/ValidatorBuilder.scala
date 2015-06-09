package s_mach.validate

import scala.language.higherKinds
import s_mach.validate.ValidatorBuilder._
import scala.reflect.ClassTag

/**
 * A builder for a Validator for a type A
 * @param validators current list of validators
 * @param ca class tag for A used to build default Schema
 * @tparam A type validated
 */
case class ValidatorBuilder[A](
  validators: List[Validator[A]] = Nil
)(implicit
  ca: ClassTag[A]
) { self =>

  /**
   * Override the default schema (built from ClassTag[A])
   * @param s schema to use
   * @return copy of this builder with schema
   */
  def schema(s: Schema) : ValidatorBuilder[A] =
    ensure(SchemaValidator(s))

  /**
   * Add a comment to the issues of the validator
   * @param message message to show
   * @return a copy of this builder with the comment
   */
  def comment(message: String) : ValidatorBuilder[A] =
    ensure(ExplainValidator(Issue(Nil,message)))

  /**
   * Add a validator as a composite of this validator
   * @param v validator to compose
   * @return a copy of this builder with the validator
   */
  def ensure(v: Validator[A]) : ValidatorBuilder[A] = {
    if(v.isEmpty) {
      this
    } else {
      copy(validators = v :: validators)
    }
  }

  /**
   * Add a validator for a constraint
   * @param message text to explain what the constraint tests
   * @param f tests the constraint
   * @return a copy of this builder with the validator
   */
  def ensure(message: String)(f: A => Boolean) : ValidatorBuilder[A] =
    ensure(Validator.ensure(message)(f))

  /**
   * Add a validator for a field (of type B) that is member of A
   * @param field the name of the field
   * @param f a function to select the value of the field given an instance of A
   * @param g a function to modify the validator built for the field (e.g. add
   *          extra constraints)
   * @param bValidator the default validator for the field type
   * @param cb class tag for the field type
   * @tparam B the field type
   * @return a copy of this builder with a validator for the field
   */
  def field[B](
    field: String,
    f: A => B
  )(
    g: ValidatorBuilder[B] => ValidatorBuilder[B] = { b:ValidatorBuilder[B] => b }
  )(implicit
    bValidator: Validator[B] = Validator.empty[B],
    cb: ClassTag[B]
  ) : ValidatorBuilder[A] = {
    val finalbValidator = g(ValidatorBuilder[B]().ensure(bValidator)).build()

    ensure(
      FieldValidator(
        field = field,
        f = f,
        bValidator = finalbValidator
      )
    )
  }

  /**
   * Build a validator from the current settings
   * @return a validator
   */
  def build() : Validator[A] = {
    val validatorsWithSchema =
      validators ::: {
          // If one of the validators has a schema for this validator
          if(validators.exists(_.schema.exists(_.path.isEmpty))) {
            Nil
          } else {
            SchemaValidator[A](Schema(Nil,ca.toString(),(1,1))) :: Nil
          }
        }
    validatorsWithSchema.size match {
      case 0 => Validator.empty
      case 1 => validatorsWithSchema.head
      case _ => CompositeValidator(validatorsWithSchema)
    }
  }
}

object ValidatorBuilder {

  /**
   * A validator that is composed of zero or more validators
   * @param validators composed validators
   * @tparam A type validated
   */
  case class CompositeValidator[A](validators: List[Validator[A]]) extends Validator[A] {
    def apply(a: A) = validators.flatMap(_(a))
    val issues = validators.flatMap(_.issues)
    val schema = validators.flatMap(_.schema)
  }

  /**
   * A validator that tests a constraint
   * @param message text to explain what the constraint tests
   * @param f tests the constraint
   * @tparam A type validated
   */
  case class EnsureValidator[A](message: String, f: A => Boolean) extends Validator[A] {
    def apply(a: A) = if(f(a)) Nil else issues
    val issues = Issue(Nil,message) :: Nil
    val schema = Nil
  }

  /**
   * A validator that adds a message to issues
   * @param i issue to add
   * @tparam A type validated
   */
  case class ExplainValidator[A](i: Issue) extends Validator[A] {
    def apply(a: A) = Nil
    val issues = i  :: Nil
    val schema = Nil
  }

  /**
   * A validator that adds a schema
   * @param s schema to add
   * @tparam A type validated
   */
  case class SchemaValidator[A](s: Schema) extends Validator[A] {
    def apply(a: A) = Nil
    val issues = Nil
    val schema = s :: Nil
  }

 /**
   * Add a validator for a field (of type B) that is member of A
   * @param field the name of the field
   * @param f a function to select the value of the field given an instance of A
   * @param bValidator the validator for the field type
   * @param cb class tag for the field type
   * @tparam B the field type
   * @return a copy of this builder with a validator for the field
   */
   case class FieldValidator[A,B](
    field: String,
    f: A => B,
    bValidator: Validator[B]
  )(implicit
    cb: ClassTag[B]
  ) extends Validator[A] {
    def apply(a: A) =
      bValidator(f(a)).map(_.pushPath(field))
    def issues =
      bValidator.issues.map(_.pushPath(field))
    def schema =
      bValidator.schema.map(_.pushPath(field))
  }

  /**
   * A validator for an Option[A] that always passes if set to None
   * @param va the validator for A
   * @param ca class tag for A
   * @tparam A type validated
   */
  case class OptionValidator[A](
    va:Validator[A]
  )(implicit
    ca:ClassTag[A]
  ) extends Validator[Option[A]] {
    def apply(oa: Option[A]) = oa.fold(List.empty[Issue])(a => va(a))
    def issues = va.issues
    def schema = va.schema.map {
      case Schema(Nil,name,_) => Schema(Nil,name,(0,1))
      case s => s
    }
  }

  /**
   * A validator for a collection of A
   * @param va the validator for A
   * @param ca the class tag for A
   * @tparam M the collection type
   * @tparam A the type validated
   */
  case class TraversableValidator[
    M[AA] <: Traversable[AA],
    A
  ](
    va:Validator[A]
  )(implicit
    ca:ClassTag[A]
  ) extends Validator[M[A]] {
    def apply(ma: M[A]) =
      ma.toList.zipWithIndex
        .flatMap { case (a,i) =>
          va(a).map(_.pushPath(i.toString))
        }
    def issues = va.issues.map(_.pushPath("member"))
    def schema =
      Schema(Nil,ca.toString(),(0,Int.MaxValue)) ::
      va.schema.map(_.pushPath("member"))
  }

}
