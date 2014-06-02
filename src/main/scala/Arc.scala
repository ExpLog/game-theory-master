/**
 * Created by Guest on 02/06/14.
 */
class Arc(i: Int, j: Int, c: Double, f: Double) {

}

object Arc {
  def stringToArc(str: Array[String]): Option[(Int, Int, Double, Double)] = {
    if (str.length == 4)
      Some((str(0).toInt, str(1).toInt, str(2).toDouble, str(3).toDouble))
    else
      None
  }

  def apply(i: Int, j: Int, c: Double, f: Double): Arc =
    new Arc(i, j, c, f)

  def unapply(str: String): Option[Arc] = stringToArc(str.split(",")) match {
    case Some((i, j, c, f)) => Some(new Arc(i,j,c,f))
    case None => None
  }
}