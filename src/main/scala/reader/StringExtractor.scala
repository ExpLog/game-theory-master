package reader

object StringExtractor {
  implicit class StringExtractorContext (val sc : StringContext) {
    object ext {
      def apply (args : Any*) : String =
        sc.s (args : _*)

      def unapplySeq (s : String) : Option[Seq[String]] = {
        val regexp = sc.parts.mkString("(.+)").r
        regexp.unapplySeq (s)
      }
    }
  }
}