package graph

class Node(val idx: Int, val amount: Double) {
  override def toString = s"$idx, $amount"

  /**
   * Creates a name for the node.
   * @param str Suffix to be added
   * @return
   */
  def name(str: String) = str+idx
}