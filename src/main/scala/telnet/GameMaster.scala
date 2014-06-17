package telnet

import akka.actor.{ActorRef, ActorLogging, Actor}
import GameMaster._
import reader.StringExtractor._
import java.io.File
import control.InstanceHandlerImpl
import utils.InstanceConverter
import scala.collection.JavaConverters._
import data.TransportationInstance

class GameMaster(dir: File, nRounds: Int) extends Actor with ActorLogging {
  private var nPlayers = 0
  private var players: List[String] = Nil
  private var serverName = ""

  lazy val files = dir.listFiles.toIterator
  val instHandler = new InstanceHandlerImpl
  private var currentRound = 0

  def sendToPlayers(server: String, msg: String) = {
    context.system.actorSelection("/user/" + server) ! Broadcast(msg)
  }

  def receive = {
    case StartGame(listPlayers, server) =>
      players = listPlayers
      nPlayers = players.size
      serverName = server

      if(files.hasNext) {
        val instance = InstanceConverter.convert(files.next().getAbsolutePath)
        instHandler.init(players.asJava, instance)

        val instName = instHandler.getInstance().getName
        val edges = edgesPerRound(instHandler.getInstance, nPlayers, nRounds)

        sendToPlayers(serverName, instanceMessage(instName, edges))
      }

    case (name: String, bids: List[ImmutableBid]) =>
  }
}

object GameMaster {
  case class StartGame(nPlayers: List[String], server: String)
  case class Broadcast(msg: String)

  def instanceMessage(instName: String, edges: Int) = s"instance $instName $edges\n"

  def edgesPerRound(instance: TransportationInstance, nplayers: Int, rounds: Int): Int = {
    val ne = ((instance.getEdges.size * 0.4) / (nplayers * rounds)).asInstanceOf[Int]
    List(ne,1).max
  }
}
