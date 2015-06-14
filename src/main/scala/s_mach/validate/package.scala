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
package s_mach

import scala.language.higherKinds
import scala.language.implicitConversions
import s_mach.validate.impl._
import scala.reflect.ClassTag

package object validate extends
  TupleValidatorImplicits with
  DataTypeValidatorImplicits with
  CollectionValidatorImplicits
{

  @inline implicit def valueClassToA[A](v: IsValueClass[A]) : A =
    v.underlying

  /** @return the Validator for the type */
  def validator[A](implicit v:Validator[A]) = v

  implicit class Net_SMach_PimpEverything[A](val self: A) extends AnyVal {
    /** @return list of rules that did not pass OR Nil if valid */
    def validate(implicit v:Validator[A]) : List[Rule] = v(self)
  }

  implicit class Net_SMach_PimpMyValidator[A](val self: Validator[A]) extends AnyVal {
    /** @return an optional validator wrapper of self */
    def optional(implicit ca:ClassTag[A]) = OptionValidator(self)
    /** @return a collection validator wrapper of self */
    def zeroOrMore(implicit ca:ClassTag[A]) = CollectionValidator(self)
  }
}
