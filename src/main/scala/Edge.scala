/**
 * Created by Guest on 02/06/14.
 */
class Edge(val source: Int, val sink: Int,
           val varCost: Double, val fixedCost: Double) {
  override def toString: String =
    source + "," + sink + "," + varCost + "," + fixedCost
  def name: String = List(source,sink).mkString("(", ",", ")")
}