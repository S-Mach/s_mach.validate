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

import s_mach.metadata.Metadata.Path
import s_mach.metadata._
import s_mach.validate._

case class OptionValidator[A](
  va: Validator[A]
) extends ValidatorImpl[Option[A]] {
  val thisRules = va.thisRules
  val rules = TypeMetadata.Arr(Nil,Cardinality.ZeroOrOne,va.rules)
  def validate(basePath: Path)(oa: Option[A]) = {
    oa match {
      case None => Stream.empty
      case Some(a) =>
        va.validate(Metadata.PathNode.SelectMember(Cardinality.ZeroOrOne,0) :: basePath)(a)
//        Metadata.Arr(Nil,Cardinality.ZeroOrOne,Seq(va(a)))
    }
  }

  //  def apply(oa: Option[A]) = {
//    oa match {
//      case None => Metadata.Arr(Nil,Cardinality.ZeroOrOne,Seq.empty)
//      case Some(a) => Metadata.Arr(Nil,Cardinality.ZeroOrOne,Seq(va(a)))
//    }
//  }
  override def and(other: Validator[Option[A]]) = {
    other match {
      case OptionValidator(otherVa) =>
        OptionValidator(va and otherVa)
      case _ => super.and(other)
    }
  }
}