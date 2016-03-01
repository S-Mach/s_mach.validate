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
package s_mach.validate.impl

import s_mach.metadata._
import s_mach.validate._

object ValidatorForProduct {
  case class FieldValidator[A,B](
    unapply: A => B,
    vb: Validator[B]
  )  {
    def apply(a: A) = vb(unapply(a))
  }
}

case class ValidatorForProduct[A](
  fields: Map[MetaField,ValidatorForProduct.FieldValidator[A,_]]
) extends ValidatorImpl[A] with ProductBuilder[Validator,A] {
  import ValidatorForProduct._

  val thisRules = Nil
  val rules = TypeMetadata.Rec(
    Nil,
    fields.map { case (f,fieldValidator) =>
      (f,fieldValidator.vb.rules)
    }
  )

  def apply(a: A) = Metadata.Rec(
    Nil,
    fields.map { case (f,fv) =>
      (f,fv(a))
    }
  )

  def field[B](
    name: String, 
    unapply: A => B
  )(
    f: Validator[B] => Validator[B]
  )(implicit baseVb: Validator[B]) = {
    val vb = f(baseVb)
    val index = if(fields.isEmpty) 0 else fields.maxBy(_._1.index)._1.index + 1
    val fv = FieldValidator(unapply,vb)
    copy(fields =
      fields.+((MetaField(name, index), fv))
    )
  }

  /*
   Note: could override and here for efficiently combining product validators
   with other product validators. However, most product validators will be
   macro generated and can't be combined like this. So implementing this would
   be a wasted effort.
    */

  def build() = this
}