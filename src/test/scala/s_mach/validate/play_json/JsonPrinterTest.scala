package s_mach.validate.play_json

import org.scalatest.{FlatSpec, Matchers}
import s_mach.validate.ExampleUsage._
import s_mach.validate.{Text, Validator}

class JsonPrinterTest extends FlatSpec with Matchers {
  "JsonPrinter.print" should "correctly print JSON from Seq[Explain] for a single validator" in {
    implicitly[Validator[Name]].explain.prettyPrintJson should equal(
"""[ "must be string", "must not be empty", "must not be longer than 64 characters", "must contain only letters or spaces" ]"""
    )
  }

  "JsonPrinter.print" should "correctly print JSON from Seq[Explain] for a single case class validator" in {
    implicitly[Validator[Person]].explain.prettyPrintJson should equal(
"""{
  "this" : "age plus id must be less than 1000",
  "id" : [ "must be integer" ],
  "name" : [ "must be string", "must not be empty", "must not be longer than 64 characters", "must contain only letters or spaces" ],
  "age" : [ "must be integer", "must be between (0,150)" ]
}"""
    )
  }

  "JsonPrinter.print" should "correctly print JSON from Seq[Explain] for a nested case class validator" in {
    implicitly[Validator[Family]].explain.prettyPrintJson should equal(
"""{
  "this" : [ "father must be older than children", "mother must be older than children" ],
  "father" : {
    "this" : "age plus id must be less than 1000",
    "id" : [ "must be integer" ],
    "name" : [ "must be string", "must not be empty", "must not be longer than 64 characters", "must contain only letters or spaces" ],
    "age" : [ "must be integer", "must be between (0,150)" ]
  },
  "mother" : {
    "this" : "age plus id must be less than 1000",
    "id" : [ "must be integer" ],
    "name" : [ "must be string", "must not be empty", "must not be longer than 64 characters", "must contain only letters or spaces" ],
    "age" : [ "must be integer", "must be between (0,150)" ]
  },
  "children" : {
    "this" : "must be array of zero or more members",
    "member" : {
      "this" : "age plus id must be less than 1000",
      "id" : [ "must be integer" ],
      "name" : [ "must be string", "must not be empty", "must not be longer than 64 characters", "must contain only letters or spaces" ],
      "age" : [ "must be integer", "must be between (0,150)" ]
    }
  },
  "grandMother" : {
    "this" : [ "optional", "age plus id must be less than 1000" ],
    "id" : [ "must be integer" ],
    "name" : [ "must be string", "must not be empty", "must not be longer than 64 characters", "must contain only letters or spaces" ],
    "age" : [ "must be integer", "must be between (0,150)" ]
  },
  "grandFather" : {
    "this" : [ "optional", "age plus id must be less than 1000" ],
    "id" : [ "must be integer" ],
    "name" : [ "must be string", "must not be empty", "must not be longer than 64 characters", "must contain only letters or spaces" ],
    "age" : [ "must be integer", "must be between (0,150)" ]
  }
}"""
    )
  }


}
