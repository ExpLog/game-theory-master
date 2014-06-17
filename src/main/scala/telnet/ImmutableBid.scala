package telnet

import data.Bid
import scala.language.implicitConversions

case class ImmutableBid(owner: String, source: Int,
          sink: Int, bid: Double)

object ImmutableBid {
  implicit def bidToImmutableBid(bid: Bid): ImmutableBid =
    new ImmutableBid(bid.getOwner, bid.getSource, bid.getSink, bid.getBid)

  implicit def immutableBidToBid(bid: ImmutableBid): Bid =
    new Bid(bid.owner, bid.source, bid.sink, bid.bid)
}