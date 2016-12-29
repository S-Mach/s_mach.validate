package s_mach.validate

/**
 * A wrapper to indicate that a value hasn't been validated yet.
 * Deserializers can be wrapped to return Unvalidated[A] instead of just A
 * to use the type system to prevent accidentally using unvalidated data.
 * @param unsafe value
 * @tparam A type of value
 */
case class Unvalidated[A](unsafe: A)
