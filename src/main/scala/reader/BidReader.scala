package reader

import StringExtractor._
import telnet.ImmutableBid

object BidReader {
  /**
   * A string following the message protocol previously defined.
   * The parameter str must be composed of the word ´bid´ in the first line,
   * followed by several bids, each on its own line, consisting of
   * "source, sink, priceBid"
   * @param str
   * @return
   */
  def readBids(name: String, str: String): List[ImmutableBid] = {
    val lines: List[String] = str.split("\n").toList.drop(1)
    val bids: List[ImmutableBid] = lines.filter{
      case ext"$source $sink $priceBid" => true
    }.map{
      case ext"$source $sink $priceBid" =>
        new ImmutableBid(name, source.toInt, sink.toInt, priceBid.toDouble)
    }
    bids
  }
}
