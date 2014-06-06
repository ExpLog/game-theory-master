package lp

import graph._
import converter.ConvertInstance

class TransportInstance(src: List[Node], snk: List[Node],
                val edges: List[Edge], val name: String) {
  val sources: Map[Int,Node] = (for(s <- src) yield (s.idx, s)).toMap
  val sinks: Map[Int,Node] = (for(s <- snk) yield (s.idx, s)).toMap

  override def toString = {
    val strSources = sources.map{case (idx, node) => node + "\n"}.foldLeft("")(_+_)
    val strSinks = sinks.map{case (idx, node) => node + "\n"}.foldLeft("")(_+_)
    val strEdges = edges.map(e => e + "\n").foldLeft("")(_+_)

    val nSource = sources.size
    val nSink = sinks.size
    val nEdges = edges.length

    name + "\n" + s"$nSource, $nSink, $nEdges\n" + strSources + strSinks + strEdges
  }
}

object TransportInstance {
  val convert = new ConvertInstance

  def apply(str: String) = {
    convert.convert(str)
    new TransportInstance(convert.getSources.toList, convert.getSinks.toList,
      convert.getEdges.toList, convert.getName)
  }


}
