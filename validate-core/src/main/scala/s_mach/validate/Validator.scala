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
         .t1i .,::;;; ;1tt        Copyright (c) 2016 S-Mach, Inc.
         Lft11ii;::;ii1tfL:       Author: lance.gatlin@gmail.com
          .L1 1tt1ttt,,Li
            ...1LLLL...
*/
package s_mach.validate

import scala.language.experimental.macros
import scala.language.higherKinds
import scala.reflect.macros.blackbox
import s_mach.codetools.IsValueClass
import s_mach.metadata._
import s_mach.validate.impl._

/**
 * Base trait for a type-class validator that can check an instance of data
 * against a set of rules
 * @tparam A type of data
 */
trait Validator[A] {
  /** @return list of rules tested for only the metadata node for this type
    *         (excluding child fields and members)  */
  def thisRules : List[Rule]
  /** @return a list of rules tested for each metadata node in this type */
  def rules : TypeMetadata[List[Rule]]
  /** @return test all rules against an instance returning a list of failures or
    *         if valid Nil for each metadata node */
  def apply(a: A) : Metadata[List[Rule]]

  /** @return a new Validator that tests all rules in this validator and other */
  def and(other: Validator[A]) : Validator[A]
}

object Validator {
  /** @return a Validator for a data value */
  def forVal[A](checks: Check[A]*) = ValidatorOps.forVal[A](checks:_*)

  /**
   * Create an empty Validator for a product type that can
   * be used to manually build a validator that uses no macro
   * generated code.
   * Note: not generally needed. Use forProductType macro.
   * @tparam A the product type
   * @return
   */
  def builder[A] : ProductBuilder[Validator,A] =
    ValidatorForProduct[A]()

  /**
   * Generate a Validator implementation for a product type
   * @tparam A the product type
   * @return the Validator implementation
   */
  def forProductType[A <: Product] : Validator[A] =
    macro macroForProductType[A]

  // Note: Scala requires this to be public
  def macroForProductType[A:c.WeakTypeTag](
    c: blackbox.Context
  ) : c.Expr[Validator[A]] = {
    val builder = new ValidatorMacroBuilder(c)
    builder.build[A]().asInstanceOf[c.Expr[Validator[A]]]
  }

  /**
   * Generate a validator for a user-defined value class that constrains
   * the value space of the underlying type
   * @return
   */
  def forValueClass[V <: IsValueClass[A],A](
    f: Validator[A] => Validator[A]
  )(implicit va: Validator[A]) : Validator[V] =
    ValidatorOps.forValueClass[V,A](f)

  /**
   * Generate a validator for a user-defined value class that constrains
   * the value space of the underlying type
   * @return
   */
  def forDistinctTypeAlias[V <: A,A](
    f: Validator[A] => Validator[A]
  )(implicit va: Validator[A]) : Validator[V] =
    ValidatorOps.forDistinctTypeAlias[V,A](f)

  /**
   * A validator that tests a constraint
   * @param rule rule description
   * @param check tests the constraint
   * @tparam A type validated
   */
  def ensure[A](
    rule: Rule
  )(
    check: A => Boolean
  ) : Validator[A] = ValidatorOps.ensure(rule)(check)

  def checks[A](checks: Check[A]*) : Validator[A] =
    CheckValidator(checks.toList)

//  /**
//   * A validator that tests a constraint
//   * @param message rule description
//   * @param check tests the constraint
//   * @tparam A type validated
//   */
//  def ensure[A](
//    message: String
//  )(
//    check: A => Boolean
//  ) : Validator[A] = ValidatorOps.ensure(Rule(message))(check)

  /**
   * A validator that adds an unchecked rule that is only displayed
   * @param rule rule description
   * @tparam A type validated
   */
  def comment[A](rule: Rule) : Validator[A] =
    ValidatorOps.comment(rule)

  /**
   * A validator for an Option[A] that always passes if set to None
   * @param va the validator for A
   * @tparam A type validated
   */
  def forOption[A](implicit
    va: Validator[A]
  ) : Validator[Option[A]] = ValidatorOps.forOption[A](va)

  /**
   * A validator for a collection of A
   * @param va the validator for A
   * @tparam M the collection type
   * @tparam A the type validated
   */
  def forTraversable[
    M[AA] <: Traversable[AA],
    A
  ](implicit va: Validator[A]) : Validator[M[A]] = ValidatorOps.forTraversable(va)
}
