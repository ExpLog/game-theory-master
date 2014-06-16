package telnet

import akka.actor.{ActorLogging, Actor}
import GameMaster._
import reader.StringExtractor._

class GameMaster extends Actor with ActorLogging {
  def receive = {
    case listBids: List[Bid] =>
  }
}

object GameMaster {
  case class Result(res: String)
  case class Bid(name: String, source: Int, sink: Int, cost: Double)

  def strToBid(name: String, str: String) = str match {
    case ext"$source, $sink, $cost" =>
      Bid(name, source.toInt, sink.toInt, cost.toDouble)
  }

  def bidFile() = ???
}
