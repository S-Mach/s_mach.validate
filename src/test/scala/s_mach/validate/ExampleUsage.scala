package s_mach.validate

object ExampleUsage {

import s_mach.validate._
import play.api.libs.json._
import s_mach.validate.play_json._

implicit class Name(
  val underlying: String
) extends AnyVal with IsValueType[String]
object Name {
  implicit val valueType_Name = ValueType[Name, String](
    new Name(_),
    _.underlying
  )
  implicit val validator_Name =
    Validator.forValueType[Name, String] {
      import Text._
      nonEmpty and maxLength(64) and allLettersOrSpaces
    }
}

implicit class Age(
  val underlying: Int
) extends AnyVal with IsValueType[Int]
object Age {
  implicit val valueType_Age = ValueType[Age,Int](
    new Age(_),
    _.underlying
  )
  implicit val validator_Age = {
    import Validator._
    forValueType[Age,Int](
      ensure(s"must be between (0,150)") { age =>
        0 <= age && age <= 150
      }
    )
  }
}

case class Person(id: Int, name: Name, age: Age)

object Person {
  implicit val validator_Person = {
    import Validator._

    forProductType[Person] and
    ensure(
      "age plus id must be less than 1000"
    )(p => p.id + p.age < 1000)
  }

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
  implicit val validator_Family = {
    import Validator._

    forProductType[Family] and
    ensure("father must be older than children") { family =>
      family.children.forall(_.age < family.father.age)
    } and
    ensure("mother must be older than children") { family =>
      family.children.forall(_.age < family.mother.age)
    }
  }

  implicit val format_Family = Json.format[Family].withValidator
}

}