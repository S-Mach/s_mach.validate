package s_mach.validate

/**
 * A base trait for an entity that provides human-readable information
 * about a field (or no field) within a case class. A field path is used
 * to point at which field within the "context" the information is
 * associated with.
 * Ex:
 * case class A(i: Int)
 * case class B(a: A)
 *
 * Field path for the i field of A from within B:
 * "a" :: "i" :: Nil
 *
 * Field path of i from within context of A:
 * "i" :: Nil
 *
 * Field path used to "this" (for A or B):
 * Nil
 */
sealed trait Explain {
  /** @return field path */
  def path: List[String]
  /** @return a copy of this with a field appended to the head of the path */
  def pushPath(field: String) : Explain
  /** @return a tuple of the head of the path and copy of this with the
    *         head of the path removed */
  def popPath() : (String,Explain)
}

/**
 * A description of a constraint on the value of a field
 * @param path field path
 * @param desc human readable description e.g. "must be between (0,150)"
 */
case class Rule(path: List[String], desc: String) extends Explain {
  override def toString = s"${
    path match {
      case Nil => ""
      case _ => path.mkString(".") + ": "
    }
  }$desc"

  override def pushPath(field: String) : Rule =
    copy(path = field :: path)
  override def popPath() : (String,Rule) =
    (path.head, copy(path = path.tail))
}

/**
 * A schema that describes the type and cardinality of a field
 * @param path field path
 * @param typeName class tag name e.g. "java.lang.String" or "Int"
 * @param cardinality a tuple of the minimum and maximum number of elements allowed 
 *                    for the field. Common cardinalities:
 *                    (1,1) => single value
 *                    (0,1) => optional value
 *                    (0,Int.MaxValue) => collection
 */
case class Schema(path: List[String], typeName: String, cardinality: (Int,Int)) extends Explain {
  override def pushPath(field: String) : Schema =
    copy(path = field :: path)
  override def popPath() : (String, Schema) =
    (path.head, copy(path = path.tail))
}