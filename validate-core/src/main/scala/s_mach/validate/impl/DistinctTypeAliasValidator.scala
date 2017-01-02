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

import s_mach.metadata.{Metadata, TypeMetadata}
import s_mach.validate._

case class DistinctTypeAliasValidator[V <: A,A](
  va: Validator[A]
) extends ValidatorImpl[V] {
  val thisRules = va.thisRules
  val rules = TypeMetadata.Val(thisRules)
  def validate(basePath: Metadata.Path)(v: V) =
    va.validate(basePath)(v)
  //  def apply(v: V) = va(v)

  override def and(other: Validator[V]) = {
    other match {
      case DistinctTypeAliasValidator(otherVa) =>
        DistinctTypeAliasValidator(va and otherVa.asInstanceOf[Validator[A]])
      case _ => super.and(other)
    }
  }

}
