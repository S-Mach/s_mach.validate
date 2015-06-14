/*
                    ,i::,
               :;;;;;;;
              ;:,,::;.
            1ft1;::;1tL
              t1;::;1,
               :;::;               _____       __  ___              __
          fCLff ;:: tfLLC         / ___/      /  |/  /____ _ _____ / /_
         CLft11 :,, i1tffLi       \__ \ ____ / /|_/ // __ `// ___// __ \
         1t1i   .;;   .1tf       ___/ //___// /  / // /_/ // /__ / / / /
       CLt1i    :,:    .1tfL.   /____/     /_/  /_/ \__,_/ \___//_/ /_/
       Lft1,:;:       , 1tfL:
       ;it1i ,,,:::;;;::1tti      s_mach.validate
         .t1i .,::;;; ;1tt        Copyright (c) 2014 S-Mach, Inc.
         Lft11ii;::;ii1tfL:       Author: lance.gatlin@gmail.com
          .L1 1tt1ttt,,Li
            ...1LLLL...
*/
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

  /** @return schema for type A */
  def schema: Schema

  /** @return list of schema for child fields and their descendants */
  def descendantSchema: List[Schema]

  /** @return list of rules and all schema for type A */
  final def explain: List[Explain] =
    // Order here is important
    schema :: (descendantSchema ::: rules)

  /**
   * Compose two validators
   * @param other validtor to compose with this
   * @return a new validator composed of this and other
   */
  def and(other: Validator[A]) : Validator[A]
}

object Validator {
  // Note: have
  /**
   * @return a Validator that has no rules or descendant schema
   * with a default schema for the type
   */
  def empty[A](implicit ca:ClassTag[A]) = new Validator[A] {
    def apply(a: A) = Nil
    def and(other: Validator[A]) = other
    def rules = Nil
    def descendantSchema = Nil
    val schema = Schema(Nil,ca.toString(),(1,1))
  }

  /**
   * Alias for empty to support builder syntax
   * @return an empty Validator
   * */
  @inline def builder[A](implicit ca:ClassTag[A]) : Validator[A] =
    empty[A]

  /**
   * Generate a DataDiff implementation for a product type
   * @tparam A the product type
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
   * A validator for a user-defined value class that constrains
   * the value space of the underlying type
   * @param other validators that constrain the value space of the
   *              underlying type
   * @return
   */
  def forValueClass[V <: IsValueClass[A],A](other: Validator[A])(implicit
    va:Validator[A],
    ca: ClassTag[A],
    cv: ClassTag[V]
  ) : Validator[V] = ValueClassValidator[V,A](
    va and other
  )

  /**
   * A validator that is composed of zero or more validators
   * @param validators composed validators
   * @tparam A type validated
   */
  def apply[A](
    validators: Validator[A]*
  )(implicit
    ca:ClassTag[A]
  ) : Validator[A] =
    CompositeValidator[A](validators.toList)

  /**
   * A validator that tests a constraint
   * @param message text to explain what the constraint tests
   * @param f tests the constraint
   * @tparam A type validated
   */
  def ensure[A](
    message: String
  )(
    f: A => Boolean
  )(implicit
    ca:ClassTag[A]
  ) : Validator[A] =
    EnsureValidator[A](message,f)

  /**
   * A validator that adds a comment to rules
   * @param message comment
   * @tparam A type validated
   */
  def comment[A](message: String)(implicit ca:ClassTag[A]) : Validator[A] =
    ExplainValidator[A](Rule(Nil,message))

  def field[A,B](
    fieldName: String,
    unapply: A => B
  )(
    vb: Validator[B]
  )(implicit
    ca: ClassTag[A]
  ) : Validator[A] =
    FieldValidator(fieldName,unapply,vb)

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
  ) : Validator[Option[A]] = OptionValidator[A](va)

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
  ) : Validator[M[A]] = CollectionValidator[M,A](va)
}
