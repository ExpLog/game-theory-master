import java.io.File

object Main {
  def main(args: Array[String]) {
    val path = "C:\\Users\\DomHellsing\\IdeaProjects\\game-theory-master\\src\\main\\resources"
    val dir = new File(path)

    val instances = for(file <- dir.listFiles) yield TransportInstance(file.getAbsolutePath)

    for(i <- instances) {
      val lp = new LPSolver(i)
      lp.optimize()
    }
  }
}
