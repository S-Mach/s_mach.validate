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
package s_mach.validate.example

import com.fasterxml.jackson.databind.{JsonNode, ObjectMapper}
import com.github.fge.jsonschema.cfg._
import com.github.fge.jsonschema.core.report.{LogLevel, ProcessingMessage}
import com.github.fge.jsonschema.main.{JsonSchema => FgeJsonSchema, JsonSchemaFactory => FgeJsonSchemaFactory}
import com.github.fge.jsonschema.processors.syntax.SyntaxValidator
import org.scalatest.{FlatSpec, Matchers}
import play.api.libs.json.{JsValue, Json}
import s_mach.codetools.play_json._
import s_mach.explain_json._
import s_mach.explain_play_json._
import s_mach.validate.play_json._
import ExampleUsage._
import ExampleUsage2._
import ExampleI18N._

import scala.collection.JavaConverters._

class PrintJsonSchemaTest extends FlatSpec with Matchers {

  // todo: missing comments
//  "ExplainJsonSchema[Name]" should "correctly print JSON for a string validator" in {
//    explainPlayJson[Name].printJsonSchema("http://test.org").pretty should equal(
//"""{
//  "$schema" : "http://json-schema.org/draft-04/schema#",
//  "id" : "http://test.org",
//  "type" : "string",
//  "minLength" : 1,
//  "maxLength" : 64,
//  "pattern" : "^[A-Za-z ]*$",
//  "comments" : [ "must contain only letters or spaces" ]
//}"""
//    )
//  }

  "ExplainJsonSchema[Name]" should "correctly print JSON for a number validator" in {
    explainPlayJson[WeightLb].printJsonSchema[JsValue]("http://test.org").pretty should equal(
"""{
  "$schema" : "http://json-schema.org/draft-04/schema#",
  "id" : "http://test.org",
  "type" : "number",
  "minimum" : 0.0,
  "exclusiveMinimum" : true,
  "maximum" : 1000.0,
  "exclusiveMaximum" : true
}"""
    )
  }

  // todo: fix above missing comments
//  "ExplainJsonSchema[Person]" should "correctly print JSON for a single case class validator" in {
//    explainPlayJson[Person].printJsonSchema("http://test.org").pretty should equal(
//"""{
//  "$schema" : "http://json-schema.org/draft-04/schema#",
//  "id" : "http://test.org",
//  "type" : "object",
//  "properties" : {
//    "id" : {
//      "id" : "http://test.org/id",
//      "type" : "integer"
//    },
//    "name" : {
//      "id" : "http://test.org/name",
//      "type" : "string",
//      "minLength" : 1,
//      "maxLength" : 64,
//      "pattern" : "^[A-Za-z] *$",
//      "comments" : [ "must contain only letters or spaces" ]
//    },
//    "age" : {
//      "id" : "http://test.org/age",
//      "type" : "integer",
//      "minimum" : 0,
//      "maximum" : 150
//    }
//  },
//  "additionalProperties" : true,
//  "required" : [ "id", "name", "age" ],
//  "additionalRules" : [ "age plus id must be less than 1000" ]
//}"""
//    )
//  }

  // todo: ordering issue
//  "ExplainJsonSchema[Family]" should "correctly print JSON for a nested case class validator" in {
//    explainPlayJson[Family].printJsonSchema("http://test.org").pretty should equal(
//"""{
//  "$schema" : "http://json-schema.org/draft-04/schema#",
//  "id" : "http://test.org",
//  "type" : "object",
//  "properties" : {
//    "father" : {
//      "id" : "http://test.org/father",
//      "type" : "object",
//      "properties" : {
//        "id" : {
//          "id" : "http://test.org/father/id",
//          "type" : "integer"
//        },
//        "name" : {
//          "id" : "http://test.org/father/name",
//          "type" : "string",
//          "minLength" : 1,
//          "maxLength" : 64,
//          "pattern" : "^[ A-Za-z]*$",
//          "comments" : [ "must contain only letters or spaces" ]
//        },
//        "age" : {
//          "id" : "http://test.org/father/age",
//          "type" : "integer",
//          "minimum" : 0,
//          "maximum" : 150
//        }
//      },
//      "additionalProperties" : true,
//      "required" : [ "id", "name", "age" ],
//      "additionalRules" : [ "age plus id must be less than 1000" ]
//    },
//    "mother" : {
//      "id" : "http://test.org/mother",
//      "type" : "object",
//      "properties" : {
//        "id" : {
//          "id" : "http://test.org/mother/id",
//          "type" : "integer"
//        },
//        "name" : {
//          "id" : "http://test.org/mother/name",
//          "type" : "string",
//          "minLength" : 1,
//          "maxLength" : 64,
//          "pattern" : "^[ A-Za-z]*$",
//          "comments" : [ "must contain only letters or spaces" ]
//        },
//        "age" : {
//          "id" : "http://test.org/mother/age",
//          "type" : "integer",
//          "minimum" : 0,
//          "maximum" : 150
//        }
//      },
//      "additionalProperties" : true,
//      "required" : [ "id", "name", "age" ],
//      "additionalRules" : [ "age plus id must be less than 1000" ]
//    },
//    "children" : {
//      "type" : "array",
//      "minItems" : 0,
//      "uniqueItems" : false,
//      "additionalItems" : false,
//      "items" : {
//        "id" : "http://test.org/children/1",
//        "type" : "object",
//        "properties" : {
//          "id" : {
//            "id" : "http://test.org/children/1/id",
//            "type" : "integer"
//          },
//          "name" : {
//            "id" : "http://test.org/children/1/name",
//            "type" : "string",
//            "minLength" : 1,
//            "maxLength" : 64,
//            "pattern" : "^[ A-Za-z]*$",
//            "comments" : [ "must contain only letters or spaces" ]
//          },
//          "age" : {
//            "id" : "http://test.org/children/1/age",
//            "type" : "integer",
//            "minimum" : 0,
//            "maximum" : 150
//          }
//        },
//        "additionalProperties" : true,
//        "required" : [ "id", "name", "age" ],
//        "additionalRules" : [ "age plus id must be less than 1000" ]
//      }
//    },
//    "grandMother" : {
//      "id" : "http://test.org/grandMother",
//      "type" : "object",
//      "properties" : {
//        "id" : {
//          "id" : "http://test.org/grandMother/id",
//          "type" : "integer"
//        },
//        "name" : {
//          "id" : "http://test.org/grandMother/name",
//          "type" : "string",
//          "minLength" : 1,
//          "maxLength" : 64,
//          "pattern" : "^[ A-Za-z]*$",
//          "comments" : [ "must contain only letters or spaces" ]
//        },
//        "age" : {
//          "id" : "http://test.org/grandMother/age",
//          "type" : "integer",
//          "minimum" : 0,
//          "maximum" : 150
//        }
//      },
//      "additionalProperties" : true,
//      "required" : [ "id", "name", "age" ],
//      "additionalRules" : [ "age plus id must be less than 1000" ]
//    },
//    "grandFather" : {
//      "id" : "http://test.org/grandFather",
//      "type" : "object",
//      "properties" : {
//        "id" : {
//          "id" : "http://test.org/grandFather/id",
//          "type" : "integer"
//        },
//        "name" : {
//          "id" : "http://test.org/grandFather/name",
//          "type" : "string",
//          "minLength" : 1,
//          "maxLength" : 64,
//          "pattern" : "^[ A-Za-z]*$",
//          "comments" : [ "must contain only letters or spaces" ]
//        },
//        "age" : {
//          "id" : "http://test.org/grandFather/age",
//          "type" : "integer",
//          "minimum" : 0,
//          "maximum" : 150
//        }
//      },
//      "additionalProperties" : true,
//      "required" : [ "id", "name", "age" ],
//      "additionalRules" : [ "age plus id must be less than 1000" ]
//    }
//  },
//  "additionalProperties" : true,
//  "required" : [ "father", "mother", "children" ],
//  "additionalRules" : [ "father must be older than children", "mother must be older than children" ]
//}"""
//    )
//  }

  def doValidateSchema(schemaJson: JsonNode) : List[ProcessingMessage] = {
    val cfgBuilder  = ValidationConfiguration.newBuilder()
    val v = new SyntaxValidator(cfgBuilder.freeze())
    v.validateSchema(schemaJson).asScala.filter(_.getLogLevel == LogLevel.ERROR).toList
  }

  def doValidate(schema: FgeJsonSchema, json:JsonNode) : List[ProcessingMessage] = {
    schema.validate(json).asScala.filter(_.getLogLevel == LogLevel.ERROR).toList
  }

  "ExplainJsonSchema[Person]" should "validate as a JsonSchema and validate good and bad json" in {
    val jsonString = explainPlayJson[Person].printJsonSchema[JsValue]("http://test.org").pretty
    val mapper = new ObjectMapper()
    val schemaJsonNode = mapper.readTree(jsonString)

    doValidateSchema(schemaJsonNode) should equal(Nil)

    val factory = FgeJsonSchemaFactory.newBuilder().freeze()
    val schema = factory.getJsonSchema(schemaJsonNode)
    val goodJson = Json.toJson(Person(1,"adsf",100)).pretty
    val goodJsonNode = mapper.readTree(goodJson)

    doValidate(schema,goodJsonNode) should equal(Nil)

    val badJson = """{ "id": 1 }"""
    val badJsonNode = mapper.readTree(badJson)

    doValidate(schema,badJsonNode).mkString("\n") should equal(
    """error: object has missing required properties (["age","name"])
    level: "error"
    schema: {"loadingURI":"#","pointer":""}
    instance: {"pointer":""}
    domain: "validation"
    keyword: "required"
    required: ["age","id","name"]
    missing: ["age","name"]
"""
    )
  }

  "ExplainJsonSchema[Family]" should "validate as a JsonSchema and validate good and bad json" in {
    val jsonString = explainPlayJson[Family].printJsonSchema[JsValue]("http://test.org").pretty
    val mapper = new ObjectMapper()
    val schemaJsonNode = mapper.readTree(jsonString)

    doValidateSchema(schemaJsonNode) should equal(Nil)

    val factory = FgeJsonSchemaFactory.newBuilder().freeze()
    val schema = factory.getJsonSchema(schemaJsonNode)
    val p = Person(1,"asdf",100)
    val goodJson = Json.toJson(Family(p,p,Seq(p,p),Some(p),None)).pretty
    val goodJsonNode = mapper.readTree(goodJson)

    doValidate(schema,goodJsonNode) should equal(Nil)

    val badJson = """{ "id": 1 }"""
    val badJsonNode = mapper.readTree(badJson)

    doValidate(schema,badJsonNode).map(_.toString) should equal(List(
    """error: object has missing required properties (["children","father","mother"])
    level: "error"
    schema: {"loadingURI":"#","pointer":""}
    instance: {"pointer":""}
    domain: "validation"
    keyword: "required"
    required: ["children","father","mother"]
    missing: ["children","father","mother"]
"""
    ))
  }

}
