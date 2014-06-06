package graph

import scala.language.implicitConversions

class Edge(val source: Int, val sink: Int,
           val varCost: Double, val fixedCost: Double) {
  override def toString: String =
    s"$source, $sink, $varCost, $fixedCost"

  def name: String = s"($source,$sink)"
}

object Edge {
  //implicit def edgeToPair(e: graph.Edge) = (e.source, e.sink)
}