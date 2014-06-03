class TransportInstance(src: List[Node], snk: List[Node],
                val edges: List[Edge], val name: String) {
  val sources: Map[Int,Node] = (for(s <- src) yield (s.idx, s)).toMap
  val sinks: Map[Int,Node] = (for(s <- snk) yield (s.idx, s)).toMap

  println(name)

  override def toString = {
    val strSources = sources.foldLeft(""){
      case (r, (idx, node)) => r + node.idx + ", " + node.amount + "\n"
    }

    val strSinks = sinks.foldLeft(""){
      case (r, (idx, node)) => r + node.idx + ", " + node.amount + "\n"
    }

    val strEdges = edges.map(e => e.toString + "\n").foldLeft("")(_+_)

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
