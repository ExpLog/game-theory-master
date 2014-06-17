package telnet

import akka.actor.{Actor, ActorRef, IO, IOManager, ActorLogging, Props}
import scala.collection.mutable.Map
import akka.util._
import scala.concurrent.duration._
import reader.StringExtractor._

import reader.BidReader._
import telnet.GameMaster.{Broadcast, StartGame}

class TelnetServer(nPlayers: Int,
                   gameMaster: ActorRef,
                   serverName: String) extends Actor with ActorLogging {

  require(nPlayers > 0)
  import TelnetServer._

  val subServers = Map.empty[IO.Handle, ActorRef]
  val serverSocket = IOManager(context.system).listen("localhost", 8080)
  var players: List[String] = Nil

  def receive = {
    case PlayerName(name) =>
      players = name::players
      if(players.size == nPlayers)
        gameMaster ! StartGame(players, serverName)

    case Broadcast(msg) =>
      subServers.foreach{
        case (s, a) => a ! Broadcast(msg)
      }

    case IO.Listening(server, address) =>
      log.info("Telnet Server listeninig on port {}.", address)

    case IO.NewClient(server) =>
      log.info("New incoming client connection on server.")
      val socket = server.accept()

      socket.write(ByteString("name\n")) //asks for a name
      subServers += (socket ->
        context.actorOf(Props(new SubServer(socket, gameMaster))))

    case IO.Read(socket, bytes) =>
      val cmd = ascii(bytes)
      subServers(socket) ! NewMessage(cmd)

    case IO.Closed(socket, cause) =>
      context.stop(subServers(socket))
      subServers -= socket
  }
}

object TelnetServer {
  implicit val askTimeout = Timeout(5 seconds)

  def ascii(bytes: ByteString): String = {
    bytes.decodeString("UTF-8").trim
  }

  case class NewMessage(msg: String)
  case class PlayerName(name: String)
  
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
            log.info(s"Name $name registered with the server.")
            sender ! PlayerName(name)

          case m if m.startsWith("bid") =>
            val bids = readBids(name, m)
            gameMaster ! (name, bids)

          case m =>
            socket.write(ByteString("Unknown message received."))
            log.warning(s"Received unknown message from $name.")
        }
    }
  }
}