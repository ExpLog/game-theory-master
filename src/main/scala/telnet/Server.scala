package telnet

import akka.actor.{Actor, ActorRef, IO, IOManager, ActorLogging, Props}
import scala.collection.mutable.Map
import akka.util._
import scala.concurrent.duration._
import reader.StringExtractor._

import telnet.GameMaster.{Bid, Result}

class TelnetServer(gameMaster: ActorRef) extends Actor with ActorLogging {
  import TelnetServer._

  val subServers = Map.empty[IO.Handle, ActorRef]
  val serverSocket = IOManager(context.system).listen("0.0.0.0", 31733)

  def receive = {
    case IO.Listening(server, address) => log.info("Telnet Server listeninig on port {}", address)
    case IO.NewClient(server) =>
      log.info("New incoming client connection on server")
      val socket = server.accept()
      socket.write(ByteString("Name?"))
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
  
  class SubServer(socket: IO.SocketHandle,
                  gameMaster: ActorRef) extends Actor with ActorLogging {
    var name = ""

    def receive = {
      case NewMessage(msg) =>
        msg match {
          case ext"name $myName" => 
            name = myName
            log.info(s"Name $name received")
          case m if m.startsWith("bid") =>
          //gameMaster ! Bid(name, m)
          case m =>
            socket.write(ByteString("Unknown message receied."))
            log.warning(s"Received unknown message from $name")
        }
      case Result(res) =>
        socket.write(ByteString(res))
    }
  }
}