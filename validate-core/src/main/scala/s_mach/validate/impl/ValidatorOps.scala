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
         .t1i .,::;;; ;1tt        Copyright (c) 2015 S-Mach, Inc.
         Lft11ii;::;ii1tfL:       Author: lance.gatlin@gmail.com
          .L1 1tt1ttt,,Li
            ...1LLLL...
*/
package s_mach.validate.impl

import scala.language.higherKinds
import s_mach.codetools.IsValueClass
import s_mach.metadata._
import s_mach.validate._

object ValidatorOps {
  private val _empty = new Validator[Any] {
    val thisRules = Nil
    val rules = TypeMetadata.Val[List[Rule]](Nil)
    def apply(a: Any) = Metadata.Val(Nil)
    def and(other: Validator[Any]) = other
  }
  def empty[A] = _empty.asInstanceOf[Validator[A]]

  def ensure[A](rule: Rule)(check: A => Boolean) : Validator[A] =
    CheckValidator(Check(rule)(check) :: Nil)

  def comment[A](rule: Rule) : Validator[A] =
    CommentValidator(rule)

  def forValueClass[V <: IsValueClass[A],A](
    f: Validator[A] => Validator[A]
  )(implicit va: Validator[A]) : Validator[V] =
    ValueClassValidator[V,A](f(va))

  def forDistinctTypeAlias[V <: A,A](
    f: Validator[A] => Validator[A]
  )(implicit va: Validator[A]) : Validator[V] =
    DistinctTypeAliasValidator[V,A](f(va))

  def forOption[A](va: Validator[A]) : Validator[Option[A]] =
    OptionValidator(va)

  def forTraversable[
    M[AA] <: Traversable[AA],
    A
  ](va: Validator[A]) : Validator[M[A]] = TraversableValidator(va)

}
