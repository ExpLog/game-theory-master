

class Node(val idx: Int, val amount: Double) {
  override def toString: String = idx + "," + amount

  def name(str: String) = str+idx
}