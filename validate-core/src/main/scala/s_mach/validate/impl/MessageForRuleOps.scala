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

object MessageForRuleOps {
  def maybeExplainStringPattern(pattern: String) : Option[String] = {
    pattern match {
      case CharGroupPattern(groups@_*) =>
        import CharGroup._
        val printGroups =
          groups
            .map {
              case UnicodeLetter => (0,"unicode letters")
              case UppercaseLetter => (1, "uppercase letters")
              case LowercaseLetter => (2, "lowercase letters")
              case Letter => (3,"letters")
              case WordLetter => (4,"word letters")
              case Digit => (5,"digits")
              case Underscore => (6,"underscores")
              case Hyphen => (7,"hyphens")
              case Space => (8,"spaces")
              case Whitespace => (9,"whitespace")
            }
            .sortBy(_._1)
            .map(_._2)

        val csmiddle = printGroups.init.mkString(", ")
        val last = if(printGroups.size > 1) s" or ${printGroups.last}" else printGroups.last
        Some(s"must contain only $csmiddle$last")
      case _ => None
    }
  }


  val pfDefaultMessageFor : PartialFunction[Rule,String] = {
    case Rule(Validators.stringLengthMin.key,params) =>
      params.head match {
        case "1" =>
          s"must not be empty"
        case n =>
          s"must have at least $n characters"
      }
    case Rule(Validators.stringLengthMax.key,params) =>
      s"must not be longer than ${params.head} characters"
    case Rule(Validators.stringPattern.key,params) =>
      maybeExplainStringPattern(params.head).getOrElse {
        s"must match regex pattern '${params.head}'"
      }
    case Rule(Validators.numberMinInclusive.key,params) =>
      s"must be greater than or equal to ${params.head}"
    case Rule(Validators.numberMinExclusive.key,params) =>
      s"must be greater than ${params.head}"
    case Rule(Validators.numberMaxInclusive.key,params) =>
      s"must be less than or equal to ${params.head}"
    case Rule(Validators.numberMaxExclusive.key,params) =>
      s"must be less than ${params.head}"
  }

  def messageForUnhandledRule(rule: Rule) : String = {
    import rule._
    val args = if(params.nonEmpty) {
      s": ${params.mkString(",")}"
    } else {
      ""
    }
    s"${key.name}$args"

  }

  def defaultMessageFor(rule: Rule) : String =
    pfDefaultMessageFor.applyOrElse(rule,messageForUnhandledRule)
}
