package s_mach.validate

object ExampleUsage {

import s_mach.validate._
import play.api.libs.json._
import s_mach.validate.play_json._

val isName = {
  import Text._
  Validator.builder[String]
   .ensure(nonEmpty)
   .ensure(maxLength(64))
   .ensure(allLettersOrWhitespace)
   .build()
}

val isAge =
  Validator.builder[Int]
   .ensure(s"must be between (0,150)") { age =>
     0 <= age && age <= 150
   }
   .build()

case class Person(id: Int, name: String, age: Int)

object Person {
  implicit val validator_Person =
    Validator.builder[Person]
      .field("id",_.id)()
      .field("name",_.name)(
        _.ensure(isName)
      )
      .field("age",_.age)(
        _.ensure(isAge)
      )
     .ensure("age plus id must be less than 1000")(p => p.id + p.age < 1000)
     .build()

  implicit val format_Person = Json.format[Person].withValidator
}

case class Family(
  father: Person,
  mother: Person,
  children: Seq[Person],
  grandMother: Option[Person],
  grandFather: Option[Person]
)

object Family {
  implicit val validator_Family =
    Validator.builder[Family]
      .field("father",_.father)()
      .field("mother",_.mother)()
      .field("children",_.children)()
      .field("grandMother",_.grandMother)()
      .field("grandFather",_.grandFather)()
      .ensure("father must be older than children") { family =>
        family.children.forall(_.age < family.father.age)
      }
      .ensure("mother must be older than children") { family =>
        family.children.forall(_.age < family.mother.age)
      }
      .build()
  implicit val format_Family = Json.format[Family].withValidator
}

}