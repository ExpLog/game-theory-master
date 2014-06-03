import scala.language.implicitConversions

class Edge(val source: Int, val sink: Int,
           val varCost: Double, val fixedCost: Double) {
  override def toString: String =
    source + ", " + sink + ", " + varCost + ", " + fixedCost

  def name: String = List(source,sink).mkString("(", ",", ")")
}

object Edge {
  implicit def edgeToPair(e: Edge) = (e.source, e.sink)
}