object MyS {

  implicit class MySContext (val sc : StringContext) {

    object mys {

      def apply (args : Any*) : String =
        sc.s (args : _*)

      def unapplySeq (s : String) : Option[Seq[String]] = {
        val regexp = sc.parts.mkString ("(.+)").r
        regexp.unapplySeq (s)
      }

    }

  }

}