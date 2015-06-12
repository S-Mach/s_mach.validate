package s_mach.validate

import org.scalatest.{Matchers, FlatSpec}
import ExampleUsage._
import play.api.libs.json.Json
import s_mach.validate.play_json.JsonPrinter

class JsonPrinterTest extends FlatSpec with Matchers {
  "JsonPrinter.print" should "correctly print JSON from Seq[Explain] for a single validator" in {
    Json.prettyPrint(JsonPrinter.print(isName.explain)) should equal(
"""[ "must contain only letters or spaces", "must not be empty", "must not be longer than 64 characters" ]"""
    )
  }
  
  "JsonPrinter.print" should "correctly print JSON from Seq[Explain] for a single case class validator" in {
    Json.prettyPrint(JsonPrinter.print(implicitly[Validator[Person]].explain)) should equal(
"""{
  "this" : "age plus id must be less than 1000",
  "id" : [ "must be integer" ],
  "name" : [ "must be string", "must contain only letters or spaces", "must not be empty", "must not be longer than 64 characters" ],
  "age" : [ "must be integer", "must be between (0,150)" ]
}"""
    )
  }

  "JsonPrinter.print" should "correctly print JSON from Seq[Explain] for a nested case class validator" in {
    Json.prettyPrint(JsonPrinter.print(implicitly[Validator[Family]].explain)) should equal(
"""{
  "this" : [ "father must be older than children", "mother must be older than children" ],
  "father" : {
    "this" : "age plus id must be less than 1000",
    "id" : [ "must be integer" ],
    "name" : [ "must be string", "must contain only letters or spaces", "must not be empty", "must not be longer than 64 characters" ],
    "age" : [ "must be integer", "must be between (0,150)" ]
  },
  "mother" : {
    "this" : "age plus id must be less than 1000",
    "id" : [ "must be integer" ],
    "name" : [ "must be string", "must contain only letters or spaces", "must not be empty", "must not be longer than 64 characters" ],
    "age" : [ "must be integer", "must be between (0,150)" ]
  },
  "children" : {
    "this" : "must be array of zero or more members",
    "member" : {
      "this" : "age plus id must be less than 1000",
      "id" : [ "must be integer" ],
      "name" : [ "must be string", "must contain only letters or spaces", "must not be empty", "must not be longer than 64 characters" ],
      "age" : [ "must be integer", "must be between (0,150)" ]
    }
  },
  "grandMother" : {
    "this" : [ "optional", "age plus id must be less than 1000" ],
    "id" : [ "must be integer" ],
    "name" : [ "must be string", "must contain only letters or spaces", "must not be empty", "must not be longer than 64 characters" ],
    "age" : [ "must be integer", "must be between (0,150)" ]
  },
  "grandFather" : {
    "this" : [ "optional", "age plus id must be less than 1000" ],
    "id" : [ "must be integer" ],
    "name" : [ "must be string", "must contain only letters or spaces", "must not be empty", "must not be longer than 64 characters" ],
    "age" : [ "must be integer", "must be between (0,150)" ]
  }
}"""
    )
  }


}
