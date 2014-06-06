package telnet

import akka.actor.ActorSystem

object ServerMain {
  def main(args: Array[String]) {
    val system = ActorSystem("Server")
    val server = new Server

  }
}
