import akka.actor.{Props, ActorSystem}
import java.io.File
import telnet.{GameMaster, TelnetServer}

object SeverMain {
  val system = ActorSystem.create("GameServer")

  def main(args: Array[String]) {
    args.toList match{
      case nPlayers::nRounds::dir::_ =>
        startServer(nPlayers.toInt, nRounds.toInt, dir)
    }
  }

  def startServer(nPlayers: Int, nRounds: Int, dir: String) {
    val serverName = "Telnet"
    val files = new File(dir)

    val gm = system.actorOf(Props(new GameMaster(files, nRounds)))
    val server = system.actorOf(
      Props(
        new TelnetServer(nPlayers,gm, serverName)), serverName)
  }
}
