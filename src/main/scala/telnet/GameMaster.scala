package telnet


import akka.actor.{ActorLogging, Actor}
import java.io.File
import scala.collection.JavaConverters._

import utils.{BidFilter, InstanceConverter}
import control.InstanceHandlerImpl
import data._
import GameMaster._
import ImmutableBid._

class GameMaster(dir: File, nRounds: Int) extends Actor with ActorLogging {
  private var nPlayers = 0
  private var players: List[String] = Nil
  private val totalPayoff = collection.mutable.Map[String,Double]()
  private var serverName = ""

  lazy val files = dir.listFiles.toIterator
  val instHandler = new InstanceHandlerImpl
  private var currentInst = dummyTransportationInstance
  private var nEdges = 0
  private var currentRound = 0
  private var bids: List[ImmutableBid] = Nil
  private var receivedBids: Int = 0

  def sendToPlayers(msg: String) = {
    context.system.actorSelection("/user/" + serverName + "/*") ! Broadcast(msg)
  }

  def closeAll() = {
    val finalResult = endMessage(totalPayoff.toMap)
    log.info("Printing total payoffs for each player.")
    print(finalResult)
    sendToPlayers(finalResult)
    context.system.actorSelection("user/" + serverName) ! "terminate"
  }

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
    if(currentRound == 0 && files.hasNext){
      val instance = InstanceConverter.convert(files.next().getAbsolutePath)
      instance.augment(20)
      instHandler.init(players.asJava, instance)

      currentInst = instHandler.getInstance()
      nEdges = edgesPerRound(instHandler.getInstance, nPlayers, nRounds)

      sendToPlayers(instanceMessage(currentInst.getName, nEdges))
      log.info(s"Initiating game instance ${currentInst.getName}.")
      log.info(s"Requesting bids.")
    } else if(currentRound > 0 && currentRound < nRounds){
      sendToPlayers(instanceMessage(currentInst.getName, nEdges))
      log.info(s"Requesting bids.")
    } else if(currentRound >= nRounds){
      val instancePayoff = instHandler.getPayoffMap.asScala

      for( (player, payoff) <- totalPayoff) {
        totalPayoff += player -> {payoff + instancePayoff(player)}
      }
      log.info(s"Ending game instance ${currentInst.getName}")
      resetRound()
      nextMatch()
    } else if(!files.hasNext){
      closeAll()
    }
  }

  def playRound() {
    val javaBids =
        BidFilter.filter(players.asJava, bids.map(immutableBidToBid).asJava, nEdges)
    log.info(s"Received a total of ${javaBids.size} valid bids.")
    val results = instHandler.solve(javaBids).asScala

    val csv: List[String] =
      results.map{case (e, eInfo) => s"${e.getSourceId} ${e.getSinkId}"+ " " + eInfo.csv}.toList
    val msg = s"result ${csv.length}\n" + csv.mkString("")
    println(msg)
    sendToPlayers(msg)

    log.info(s"Sending results of round $currentRound of instance ${currentInst.getName} to all players.")
    currentRound = currentRound + 1
  }

  def receive = {
    case StartGame(listPlayers, server) =>
      players = listPlayers
      nPlayers = players.size
      serverName = server
      for(p <- players) totalPayoff += p -> 0

      nextMatch()

    case BidList(incBids) =>
      updateBids(incBids)
      log.info(s"Received ${incBids.length} bids from player ${incBids(0).owner}.")
      if(receivedBids == nPlayers){
        log.info(s"Playing round $currentRound of ${currentInst.getName}.")
        playRound()

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

  def endMessage(totalPayoff: Map[String, Double]): String = {
    val payoffs: List[(String,Double)] =
      totalPayoff.toList.sortBy{case (s,d) => -d}
    val lines: List[String] = payoffs.map{
      case (player, payoff) => s"$player $payoff\n"
    }
    s"end ${totalPayoff.size}\n"+lines.mkString
  }

  def edgesPerRound(instance: TransportationInstance, nplayers: Int, rounds: Int): Int = {
    val ne = ((instance.getEdges.size * 0.4) / (nplayers * rounds)).asInstanceOf[Int]
    List(List(ne,1).max,500).min
  }

  val dummyListNode = List[Node]().asJava
  val dummyListEdge = List[Edge]().asJava
  val dummyTransportationInstance =
    new TransportationInstance("", dummyListNode, dummyListNode, dummyListEdge)
}