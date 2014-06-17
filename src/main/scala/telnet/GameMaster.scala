package telnet

import akka.actor.{ActorLogging, Actor}
import java.io.File
import scala.collection.JavaConverters._

import utils.InstanceConverter
import control.InstanceHandlerImpl
import data.{Bid, TransportationInstance, Edge, EdgeInfo, Node}
import GameMaster._
import ImmutableBid._

class GameMaster(dir: File, nRounds: Int) extends Actor with ActorLogging {
  private var nPlayers = 0
  private var players: List[String] = Nil
  private var serverName = ""

  lazy val files = dir.listFiles.toIterator
  val instHandler = new InstanceHandlerImpl
  private var currentInst = dummyTransportationInstance
  private var currentRound = 0
  private var bids: List[ImmutableBid] = Nil
  private var receivedBids: Int = 0

  def sendToPlayers(msg: String) = {
    context.system.actorSelection("/user/" + serverName + "/*") ! Broadcast(msg)
  }

  def closeAll() = context.system.actorSelection("user/" + serverName) ! "terminate"

  def resetBids()  {
    bids = Nil
    receivedBids = 0
  }

  def updateBids(newBids: List[ImmutableBid]) {
    bids = newBids ++ bids
    receivedBids = receivedBids + 1
  }

  def resetRound() { currentRound = 0 }

  def nextMatch()  {
    //TODO: send results in all cases
    if(!files.hasNext){
      closeAll()
    }

    if(currentRound == 0 && files.hasNext){
      val instance = InstanceConverter.convert(files.next().getAbsolutePath)
      instHandler.init(players.asJava, instance)

      currentInst = instHandler.getInstance()
      val nEdges = edgesPerRound(instHandler.getInstance, nPlayers, nRounds)

      sendToPlayers(instanceMessage(currentInst.getName, nEdges))
      log.info(s"Initiating game instance ${currentInst.getName}.")
    }

    if(currentRound > 0 && currentRound < nRounds){
      instHandler.init(players.asJava, currentInst)

      val nEdges = edgesPerRound(instHandler.getInstance, nPlayers, nRounds)

      sendToPlayers(instanceMessage(currentInst.getName, nEdges))
    }

    if(currentRound >= nRounds){
      resetRound()
      nextMatch()
    }
  }

  def playRound() {
    val javaBids: List[Bid] = bids.map(b => immutableBidToBid(b))
    val results = instHandler.solve(javaBids.asJava).asScala

    val csv: List[String] = results.map{case (e, eInfo) => eInfo.csv}.toList
    val msg = s"result ${csv.length}\n" + csv.foldLeft("")(_+_)
    log.info("Sending results to all players.")
    sendToPlayers(msg)
  }

  def receive = {
    case StartGame(listPlayers, server) =>
      players = listPlayers
      nPlayers = players.size
      serverName = server
      nextMatch()

    case BidList(incBids) =>
      updateBids(incBids)
      if(receivedBids == nPlayers){
        playRound()
        log.info(s"Playing round $currentRound of ${currentInst.getName}.")

        resetBids()
        nextMatch()
      }
  }
}

object GameMaster {
  case class StartGame(nPlayers: List[String], server: String)
  case class Broadcast(msg: String)
  case class BidList(bids: List[ImmutableBid])

  def instanceMessage(instName: String, edges: Int) = s"instance $instName $edges\n"

  def edgesPerRound(instance: TransportationInstance, nplayers: Int, rounds: Int): Int = {
    val ne = ((instance.getEdges.size * 0.4) / (nplayers * rounds)).asInstanceOf[Int]
    List(ne,1).max
  }

  val dummyListNode = List[Node]().asJava
  val dummyListEdge = List[Edge]().asJava
  val dummyTransportationInstance =
    new TransportationInstance("", dummyListNode, dummyListNode, dummyListEdge)
}
