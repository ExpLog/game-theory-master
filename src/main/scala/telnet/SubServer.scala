package telnet

import akka.actor.{ActorLogging, Actor, ActorRef, IO}
import akka.util.ByteString
import reader.BidReader._
import reader.StringExtractor._
import telnet.GameMaster.{BidList, Broadcast}
import telnet.TelnetServer.{PlayerName, NewMessage}


class SubServer(socket: IO.SocketHandle,
                gameMaster: ActorRef) extends Actor with ActorLogging {
  var name = "__DEFAULT__"

  def receive = {
    case Broadcast(msg) =>
      socket.write(ByteString(msg))

    case NewMessage(msg) =>
      msg match {
        case ext"name $myName" =>
          name = myName
          sender ! PlayerName(name)
          log.info(s"Name $name registered with the server.")

        case m if m.startsWith("bid\n") =>
          val bids = readBids(name, m)
          gameMaster ! BidList(bids)

        case m =>
          socket.write(ByteString("Unknown message received."))

          log.error(s"Received unknown message from $name:\n"+m)
      }
  }
}
