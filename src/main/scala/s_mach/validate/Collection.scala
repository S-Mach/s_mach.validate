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

/**
 * Helper functions for validating collections
 */
object Collection {
  /**
   * A validator for a collection of A
   * @tparam A the type validated
   */
  def nonEmpty[A] =
    Validator.ensure[Traversable[A]](
      "must not be empty"
    )(_.nonEmpty)

  /**
   * A validator for a collection of A
   * @tparam A the type validated
   */
  def maxSize[A](max: Int) =
    Validator.ensure[Traversable[A]](
      s"must not have size greater than $max"
    )(_.size < max)

}
