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
  def schema(s: Schema) : ValidatorBuilder[A] = {
    copy(
      SchemaValidator[A](s) ::
      // Ensure there is only ever one root schema
      validators.filter {
        case SchemaValidator(Schema(Nil,_,_)) => false
        case _ => true
      }
    )
  }


  /**
   * Add a comment to the rules of the validator (that
   * does not test anything)
   * @param message message to show
   * @return a copy of this builder with the comment
   */
  def comment(message: String) : ValidatorBuilder[A] =
    ensure(ExplainValidator(Rule(Nil,message)))

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
    ensure(EnsureValidator(message, f))

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
    val finalbValidator =
      g(ValidatorBuilder[B](bValidator :: Nil)).build()

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
       {
          // If one of the validators already has a schema for this
          // validator
          if(validators.exists(_.schema.exists(_.path.isEmpty))) {
            Nil
          } else {
            SchemaValidator[A](Schema(Nil,ca.toString(),(1,1))) :: Nil
          }
        } ::: validators.reverse
    CompositeValidator(validatorsWithSchema)
// This doesn't really help much - with even one added validator there
// will always be two (since Schema is added if missing)
// Just schema will be pretty rare
//    validatorsWithSchema.size match {
//      case 0 => ??? // unreachable
//      case 1 => validatorsWithSchema.head
//      case _ => CompositeValidator(validatorsWithSchema)
//    }
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
    val rules = validators.flatMap(_.rules)
    val schema = validators.flatMap(_.schema)
    val explain = validators.flatMap(_.explain)
    def and(other: Validator[A]) : CompositeValidator[A] =
      other match {
        case CompositeValidator(more) => copy(more ::: validators)
        case _ => copy(other :: validators)
      }
  }

  /**
   * A validator that tests a constraint
   * @param message text to explain what the constraint tests
   * @param f tests the constraint
   * @tparam A type validated
   */
  case class EnsureValidator[A](message: String, f: A => Boolean) extends Validator[A] {
    def apply(a: A) = if(f(a)) Nil else rules
    val rules = Rule(Nil,message) :: Nil
    val schema = Nil
    val explain = rules
  }

  /**
   * A validator that adds a rule for display but not check
   * @param r rule to display but not check
   * @tparam A type validated
   */
  case class ExplainValidator[A](r: Rule) extends Validator[A] {
    def apply(a: A) = Nil
    val rules = r  :: Nil
    val schema = Nil
    val explain = rules
  }

  /**
   * A validator that adds a schema
   * @param s schema to add
   * @tparam A type validated
   */
  case class SchemaValidator[A](s: Schema) extends Validator[A] {
    def apply(a: A) = Nil
    val rules = Nil
    val schema = s :: Nil
    val explain = schema
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
    val rules =
      bValidator.rules.map(_.pushPath(field))
    val schema =
      bValidator.schema.map(_.pushPath(field))
    val explain =
      bValidator.explain.map(_.pushPath(field))
  }

  private def modSchemaCardinality(cardinality: (Int,Int)) : Explain => Explain = {
    case s@Schema(Nil,name,_) =>
      s.copy(cardinality = cardinality)
    case s => s
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
    def apply(oa: Option[A]) = oa.fold(List.empty[Rule])(a => va(a))
    val rules = va.rules
    val schema = va.schema.map {
      case s@Schema(Nil,_,_) => s.copy(cardinality = (0,1))
      case other => other
    }
    val explain = va.explain.map {
      case s@Schema(Nil,_,_) => s.copy(cardinality = (0,1))
      case other => other
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
    ca:ClassTag[A],
    cm:ClassTag[M[A]]
  ) extends Validator[M[A]] {
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

}
