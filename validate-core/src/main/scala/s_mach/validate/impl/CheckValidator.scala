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

import s_mach.metadata._
import s_mach.validate._

case class CheckValidator[A](
  checks : List[Check[A]]
) extends ValidatorImpl[A] {
  val thisRules = checks.map(_.rule)
  val rules = TypeMetadata.Val(thisRules)
  def apply(a: A) = Metadata.Val(checks.flatMap(_(a)))

  override def and(other: Validator[A]) = {
    other match {
      case CheckValidator(otherChecks) =>
        // Note: not using Set directly to preserve insert order
        val builder = List.newBuilder[Check[A]]
        builder ++= checks
        // Using LinkedHashSet here to dedup Checks and preserve insert order
        val checkSet = checks.toSet

        otherChecks.foreach { c =>
          if(checkSet.contains(c) == false) {
            builder += c
          }
        }
        CheckValidator(builder.result())
      case _ => super.and(other)
    }
  }
}