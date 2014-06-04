import gurobi._
import LPSolver._
import math.min

class LPSolver(transport: TransportInstance) {
  val model = new GRBModel(env)

  /**
   * Maps edge = (source, sink) onto their respective GRBVar and
   * sets their coefficients in the objective function.
   */
  val edges: Map[(Int, Int), GRBVar] = {
    for {
      e <- transport.edges
    } yield {
      val m = min(transport.sources(e.source).amount, transport.sinks(e.sink).amount)
      ((e.source, e.sink), model.addVar(0.0, m, e.varCost, GRB.CONTINUOUS, e.name))
    }
  }.toMap

  model.update()

  /**
   * Sets the objective to be minimized.
   */
  model.setObjective(model.getObjective, GRB.MINIMIZE)

  /**
   * Creates constraints associated with sources' supplies.
   */
  for (s <- transport.sources) s match {
    case (idx, node) =>
      val edgeSet = edges.filterKeys {
        case edgePair => edgePair._1 == idx
      }

      val expr = new GRBLinExpr()
      edgeSet foreach {
        case (edgePair, v) => expr.addTerm(1.0, v)
      }
      model.addConstr(expr, GRB.EQUAL, node.amount, node.name("source-"))
  }

  /**
   * Creates constraints associated with sinks' demands.
   */
  for (s <- transport.sinks) s match {
    case (idx, node) =>
      val edgeSet = edges.filterKeys {
        case edgePair => edgePair._2 == idx
      }

      val expr = new GRBLinExpr()
      edgeSet foreach {
        case (edgePair, v) => expr.addTerm(1.0, v)
      }

      model.addConstr(expr, GRB.EQUAL, node.amount, node.name("sink-"))
  }

  model.optimize()

  override def toString = {
    edges.foldLeft("") {
      case (r, ((source, sink), v)) =>
        val str = s"$source, $sink, ${v.get(GRB.DoubleAttr.X)}\n"
        r + str
    }
  }

  def dispose() = model.dispose()
}

object LPSolver {
  val env = new GRBEnv("game.log")

  def endGame() = env.dispose()
}
