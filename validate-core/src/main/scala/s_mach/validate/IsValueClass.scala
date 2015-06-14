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
package s_mach.validate

/**
 * A base trait for a user-defined value-class (UDVC) that is used to
 * constrain the value space of the underlying type . The UDVC attempts
 * to behave exactly as the underlying type in code. Methods
 * such as toString, hashCode and equals* pass-thru to the underlying
 * type. For other methods, implicit conversion from the UDVC to the
 * underling type is provided in the s_mach.validate package object.
 * Zero-runtime cost conversion to the underlying type is provided
 * automatically through Scala's value-class (see
 * http://docs.scala-lang.org/overviews/core/value-classes.html).
 *
 * Example value-class:
 * implicit class Name(underlying: String) extends AnyVal with IsValueType[String]
 *
 * Note: equals is a special case that still requires normalizing the right-hand
 * type to match the value class. Ex:
 *
 * Name("Hal") == "Hal" // always returns false + compiler warning
 * Name("Hal") == Name("Hal") // works correctly
 *
 * @tparam A type of underlying value class (Note: this parameter does not require
 *           inheritance from AnyVal since this would prevent using the trait with
 *           java.lang.String which does not inherit AnyVal)
 */
trait IsValueClass[A] extends Any {
  def underlying: A

  override def toString = underlying.toString
}