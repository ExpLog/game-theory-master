package lp

import gurobi._
import LPSolver._
import math.max
import data._
import scala.collection.JavaConverters._
import scala.collection.JavaConversions._

class LPSolver(transport: TransportationInstance) {
  val model = new GRBModel(env)

  val maxSource = transport.getSources.asScala.toList.foldLeft(0.0){
    case (r,n) => max(r, n.getAmount)
  }

  val maxSink = transport.getSources.asScala.toList.foldLeft(0.0){
    case (r,n) => max(r, n.getAmount)
  }

  val maxAmount = max(maxSource, maxSink)

  val edges: Map[(Int, Int), GRBVar] = {
    for {
      e: Edge <- transport.getEdges
    } yield {
      ((e.getSourceId, e.getSinkId), model.addVar(0.0, maxAmount, e.getVarCost, GRB.CONTINUOUS, e.getName))
    }
  }.toMap

  model.update()

  model.setObjective(model.getObjective, GRB.MINIMIZE)

  transport.getSources.foreach {
    case node =>
      val edgeSet: List[Edge] = transport.getEdgesFrom(node.getID).asScala.toList

      val expr = new GRBLinExpr()
      edgeSet.foreach {
        case e =>
          val src = e.getSourceId
          val snk = e.getSinkId
          val variable = edges(src,snk)
          expr.addTerm(1.0, variable)
      }

      model.addConstr(expr, GRB.EQUAL, node.getAmount, node.getLabel("source"))
  }

  transport.getSinks.foreach {
    case node =>
      val edgeSet: List[Edge] = transport.getEdgesTo(node.getID).asScala.toList

      val expr = new GRBLinExpr()
      edgeSet.foreach {
        case e =>
          val src = e.getSourceId
          val snk = e.getSinkId
          val variable = edges(src,snk)
          expr.addTerm(1.0, variable)
      }

      model.addConstr(expr, GRB.EQUAL, node.getAmount, node.getLabel("sink"))
  }

  model.optimize()

  override def toString = {
    edges.foldLeft(""){
      case (r, ((source, sink), v)) =>
        val value = v.get(GRB.DoubleAttr.X)
        val str = s"$source, $sink, $value\n"
        r+str
    }
  }

  def flows: List[EdgeFlow] = {
    for( e <- edges ) yield {
      val flow = e._2.get(GRB.DoubleAttr.X)
      new EdgeFlow(e._1._1, e._1._2, flow)
    }
  }.toList

  def dispose() = model.dispose()
}

object LPSolver {
  val env = new GRBEnv("game.log")
  env.set(GRB.IntParam.LogToConsole, 0)
}
