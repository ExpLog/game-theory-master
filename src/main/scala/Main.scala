import java.io.File

import akka.actor.{ActorSystem, Props}
import telnet.{GameMaster, TelnetServer}
import utils.InstanceConverter

object Main extends App {
  def startServer(nPlayers: Int, nRounds: Int, dir: String) {
    val system = ActorSystem.create("GameServer")

    val serverName = "Telnet"
    val files = new File(dir)

    val gm = system.actorOf(Props(new GameMaster(files, nRounds)))
    val server = system.actorOf(
      Props(
        new TelnetServer(nPlayers,gm, serverName)), serverName)
  }

  args.toList match{
    case nPlayers::nRounds::dir::_ =>
      startServer(nPlayers.toInt, nRounds.toInt, dir)
    case _ => {
      println("Usage is <number of players> <number of rounds> <directory with instances>")
    }
  }
}
