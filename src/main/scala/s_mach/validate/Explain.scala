package s_mach.validate

sealed trait Explain {
  def path: Seq[String]
  def pushPath(field: String) : Explain
  def popPath() : (String,Explain)
}

case class Issue(path: List[String], message: String) extends Explain {
  override def toString = s"${
    path match {
      case Nil => ""
      case _ => path.mkString(".") + ": "
    }
  }$message"

  override def pushPath(field: String) : Issue =
    copy(path = field :: path)
  override def popPath() : (String,Issue) =
    (path.head, copy(path = path.tail))
}

case class Schema(path: List[String], name: String, cardinality: (Int,Int)) extends Explain {
  override def pushPath(field: String) : Schema =
    copy(path = field :: path)
  override def popPath() : (String, Schema) =
    (path.head, copy(path = path.tail))
}