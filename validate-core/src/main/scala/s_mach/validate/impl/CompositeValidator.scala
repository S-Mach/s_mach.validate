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

import s_mach.validate._

case class CompositeValidator[A](
  validators: List[Validator[A]]
) extends ValidatorImpl[A] {
  require(validators.nonEmpty, "validators must be non empty")
  val thisRules = validators.flatMap(_.thisRules)
  val rules =
    validators
      .map(_.rules)
      .reduce { (tm1,tm2) =>
        tm1.merge(tm2)(_ ::: _)
      }
  def apply(a: A) =
      validators
        .map(_(a))
        .reduce { (m1,m2) =>
          m1.merge(m2)(_ ::: _)
        }

  override def and(other: Validator[A]) = {
    other match {
      case CompositeValidator(otherValidators) =>
        // Note: not using distinct here to preserve insert order
        val builder = List.newBuilder[Validator[A]]
        builder ++= validators
        otherValidators.foreach { v =>
          if(validators.exists(_ eq v) == false) {
            builder += v
          }
        }
        CompositeValidator(builder.result())
      case _ => super.and(other)
    }
  }
}
