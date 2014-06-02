/**
 * Created by Guest on 02/06/14.
 */



class TransportInstance(src: List[Node], snk: List[Node],
                val edges: List[Edge], val name: String) {
  val sources: Map[Int,Node] = (for(s <- src) yield (s.idx, s)).toMap
  val sinks: Map[Int,Node] = (for(s <- snk) yield (s.idx, s)).toMap

  override def toString =
    src.mkString("\n") + snk.mkString("\n") + edges.mkString("\n")
}

object TransportInstance {
  val convert = new ConvertInstance

  def apply(str: String) = {
    convert.convert(str, str+"simple")
    new TransportInstance(convert.getSources.toList, convert.getSinks.toList,
      convert.getEdges.toList, convert.getName)
  }
}
