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
package s_mach.validate

import scala.language.higherKinds
import scala.reflect.ClassTag
import s_mach.validate.impl._

object CollectionValidatorImplicits extends CollectionValidatorImplicits
trait CollectionValidatorImplicits {
  /** @return an optional validator wrapper for any type that implicitly defines
    *         a validator */
  implicit def validator_Option[A](implicit
    va:Validator[A],
    ca:ClassTag[A]
  ) = OptionValidator[A](va)

  /** @return a collection validator wrapper for any type that implicitly defines
    *         a validator */
  implicit def validator_Traversable[
    M[AA] <: Traversable[AA],
    A
  ](implicit
    va:Validator[A],
    ca:ClassTag[A],
    cm:ClassTag[M[A]]
  ) = CollectionValidator[M,A](va)
}
