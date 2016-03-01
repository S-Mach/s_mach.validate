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

import scala.language.higherKinds

case class TraversableValidator[M[AA] <: Traversable[AA],A](
  va: Validator[A]
) extends ValidatorImpl[M[A]] {
  val thisRules = va.thisRules
  val rules = TypeMetadata.Arr(Nil,Cardinality.ZeroOrMore,va.rules)
  def apply(ma: M[A]) = {
    // Note: no zipWithIndex available on Traversable so using
    // imperative style
    val builder = Map.newBuilder[Int,Metadata[List[Rule]]]
    var i = 0
    ma.foreach { a =>
      builder += ((i,va(a)))
      i = i + 1
    }
    Metadata.Arr(Nil,Cardinality.ZeroOrMore,builder.result())
  }

  override def and(other: Validator[M[A]]) = {
    other match {
      case TraversableValidator(otherVa) =>
        TraversableValidator(va and otherVa)
      case _ => super.and(other)
    }
  }
}
