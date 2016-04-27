package s_mach.validate.example

import java.util.Locale

import s_mach.i18n._

object ExampleI18N {
  implicit val i18ncfg = I18NConfig(UTF8Messages(Locale.US))

  val m_mother_must_be_older_than_children = 'm_mother_must_be_older_than_children.m0
  val m_father_must_be_older_than_children = 'm_father_must_be_older_than_children.m0
}
