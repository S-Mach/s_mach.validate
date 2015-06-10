package s_mach.validate.play_json

import play.api.libs.json._
import s_mach.validate._

import scala.collection.mutable

object JsonPrinter {
  def print(explains: Seq[Explain]) : JsValue = {
    if(explains.isEmpty) {
      JsNull
    } else {
      val ordered = explains.sortBy(i => -i.path.size)
      val root = new ExplainNode()
      ordered.foreach(root.append)
      root.toJson
    }
  }

  private class ExplainNode(
    fields: mutable.Map[String,ExplainNode] = mutable.Map.empty,
    messages: mutable.Buffer[String] = mutable.Buffer.empty
  ) {
    def append(e: Explain) : Unit = {
      e.path match {
        case Nil =>
          e match {
            case Rule(path,message) =>
              messages += message
            case Schema(path,name,cardinality) =>
              mkSchemaNameMessage(name).foreach(messages.append(_))
              mkCardinalityMessage(cardinality).foreach(messages.append(_))
          }
        case _ =>
          val (head,recurse) = e.popPath()
          fields.get(head) match {
            case Some(node) =>
              node.append(recurse)
            case None =>
              val node = new ExplainNode()
              node.append(recurse)
              fields.put(head,node)
          }

      }
    }

    def toJson : JsValue = {
      fields.size match {
        case 0 => JsArray(messages.reverseMap(JsString.apply))
        case _ =>
          val optThis = messages.size match {
            case 0 => None
            case 1 => Some("this" -> JsString(messages.head))
            case _ => Some("this" -> JsArray(messages.reverseMap(JsString.apply)))
          }
          val fieldsWithThis =
            fields.toSeq.map { case (field,node) =>
              (field,node.toJson)
            } ++ optThis
          JsObject(fieldsWithThis)
      }
    }
  }

  private def mkCardinalityMessage(cardinality: (Int,Int)) : Option[String] = {
    cardinality match {
      case (1,1) => None
      case (0,1) => Some(s"optional")
      case (0,Int.MaxValue) => Some(s"must be array of zero or more members")
      case (min,max) => Some(s"must be array of $min to $max members")
    }
  }

  private def mkSchemaNameMessage(name: String) : Option[String] = {
    {
      name match {
        case "java.lang.String" => Some("string")
        case "Char" => Some("character")
        case "Boolean" => Some("boolean")
        case "Byte" => Some(s"integer")
        case "Int" => Some(s"integer")
        case "Short" => Some(s"integer")
        case "Long" => Some(s"integer")
        case "Float" => Some(s"number")
        case "Double" => Some(s"number")
        case "scala.math.BigInt" => Some("number")
        case "scala.math.BigDecimal" => Some("number")
        case _ => None
      }
    }.map(m => s"must be $m")
  }
}
