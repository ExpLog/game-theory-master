package lp

import control.TPSolver
import data.{EdgeFlow, TransportationInstance}
import scala.collection.JavaConverters._

class SolverStub extends TPSolver {
  override def solve(problem: TransportationInstance): java.util.List[EdgeFlow] = {
    val solver = new LPSolver(problem)
    solver.flows
  }.asJava
}
