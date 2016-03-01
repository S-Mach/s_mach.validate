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
*/package s_mach.validate

import s_mach.validate.impl.MessageForRuleOps

/**
 * Type-class for building a human readable message for a rule. A default
 * English only implementation is provided. Implementors are free to replace the
 * default with an implementation that utilizes the localization framework
 * of their choice.
 */
trait MessageForRule {
  /** @return human readable message for rule */
  def messageFor(rule: Rule) : String
}


object MessageForRule {
  val default = new MessageForRule {
    def messageFor(rule: Rule) =
      MessageForRuleOps.defaultMessageFor(rule)
  }

  object Implicits {
    implicit val defaultMessageForRule = MessageForRule.default
  }
}