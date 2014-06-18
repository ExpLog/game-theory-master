package telnet

import akka.actor._
import scala.collection.mutable.Map
import akka.util._
import telnet.GameMaster.StartGame

class TelnetServer(nPlayers: Int,
                   gameMaster: ActorRef,
                   serverName: String) extends Actor with ActorLogging {

  require(nPlayers > 0)
  import TelnetServer._

  val subServers = Map.empty[IO.Handle, ActorRef]
  val serverSocket = IOManager(context.system).listen("0.0.0.0", 8080)
  var players: List[String] = Nil

  def receive = {
    case PlayerName(name) =>
      players = name::players
      if(players.size == nPlayers){
        gameMaster ! StartGame(players, serverName)
        log.info("All players accounted for. Starting game.")
      }

    case IO.Listening(server, address) =>
      log.info("Hello, world!")
      log.info("Telnet Server listening on port {}.", address)

    case IO.NewClient(server) =>
      val socket = server.accept()
      socket.write(ByteString("name\n")) //asks for a name
      subServers += (socket ->
        context.actorOf(Props(new SubServer(socket, gameMaster))))
      log.info("New incoming client connection on server.")

    case IO.Read(socket, bytes) =>
      val cmd = ascii(bytes)
      subServers(socket) ! NewMessage(cmd)

    case IO.Closed(socket, cause) =>
      context.stop(subServers(socket))
      subServers -= socket
      log.info("A connection was closed.")

    case "terminate" =>
      subServers.foreach{
        case (socket, actor) =>
          socket.close()
      }
      log.info("Terminating all connections.")
      log.info("Goodbye.")
      context.system.shutdown()
  }
}

object TelnetServer {
  def ascii(bytes: ByteString): String = {
    bytes.decodeString("UTF-8").trim
  }

  case class NewMessage(msg: String)
  case class PlayerName(name: String)
}