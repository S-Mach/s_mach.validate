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
package s_mach.validate.play_json

/**
 * Enumeration for json types
 */
//sealed trait JsonType
//object JsonType {
//  case object JsonBoolean extends JsonType
//  case object JsonString extends JsonType
//  case object JsonNumber extends JsonType
//  case object JsonInteger extends JsonType
//  case object JsonObject extends JsonType
//  case object JsonArray extends JsonType
//}

/**
 * Base trait for JsonSchema rules
 **/
sealed trait JsonSchemaRule
object JsonSchemaRule {
  case class Maximum(value: BigDecimal, exclusive: Boolean) extends JsonSchemaRule
  case class Minimum(value: BigDecimal, exclusive: Boolean) extends JsonSchemaRule
  case class StringMaxLength(value: Int) extends JsonSchemaRule
  case class StringMinLength(value: Int) extends JsonSchemaRule
  case class StringPattern(value: String) extends JsonSchemaRule
  // Note: these are handled through cardinality
//  case class MaxItems(value: Int) extends JsonSchemaRule
//  case class MinItems(value: Int) extends JsonSchemaRule
}

sealed trait JsonSchemaInfo

object JsonSchemaInfo {
  sealed trait JsonType extends JsonSchemaInfo {
    def isOptional: Boolean
    def isOptional(value: Boolean) : JsonSchemaInfo
    def additionalRules: List[String]
    def comments: List[String]
  }
  sealed trait JsonVal extends JsonType {
    def rules: List[JsonSchemaRule]
  }

  case class JsonBoolean(
    isOptional: Boolean = false,
    additionalRules: List[String] = Nil,
    comments: List[String] = Nil
  ) extends JsonVal {
    def rules = Nil
    override def isOptional(value: Boolean) = copy(isOptional = value)
  }

  case class JsonString(
    isOptional: Boolean = false,
    rules: List[JsonSchemaRule] = Nil,
    additionalRules: List[String] = Nil,
    comments: List[String] = Nil
  ) extends JsonVal {
    override def isOptional(value: Boolean) = copy(isOptional = value)
  }

  case class JsonNumber(
    isOptional: Boolean = false,
    rules: List[JsonSchemaRule] = Nil,
    additionalRules: List[String] = Nil,
    comments: List[String] = Nil
  ) extends JsonVal {
    override def isOptional(value: Boolean) = copy(isOptional = value)
  }

  case class JsonInteger(
    isOptional: Boolean = false,
    rules: List[JsonSchemaRule] = Nil,
    additionalRules: List[String] = Nil,
    comments: List[String] = Nil
  ) extends JsonVal {
    override def isOptional(value: Boolean) = copy(isOptional = value)
  }

  case class JsonArray(
    isOptional: Boolean = false,
    additionalRules: List[String] = Nil,
    comments: List[String] = Nil
  ) extends JsonType {
    override def isOptional(value: Boolean) = copy(isOptional = value)
  }

  case class JsonObject(
    isOptional: Boolean = false,
    additionalRules: List[String] = Nil,
    comments: List[String] = Nil
  ) extends JsonType {
    override def isOptional(value: Boolean) = copy(isOptional = value)
  }

  case object OptMarker extends JsonSchemaInfo
}
