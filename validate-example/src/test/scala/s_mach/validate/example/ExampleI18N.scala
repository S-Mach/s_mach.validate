package s_mach.validate.example

import java.util.Locale

import s_mach.i18n._
import s_mach.i18n.messages._

object ExampleI18N {
  implicit val i18ncfg = I18NConfig(Locale.US)

  val m_mother_must_be_older_than_children = 'm_mother_must_be_older_than_children.m0
  val m_father_must_be_older_than_children = 'm_father_must_be_older_than_children.m0
  val m_age_plus_id_must_be_less_than_$n = 'age_plus_id_must_be_less_than_$n.m[Int]
}
