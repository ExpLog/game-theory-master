import java.io.File

object Main {
  def main(args: Array[String]) {
    val path = "C:\\Users\\DomHellsing\\IdeaProjects\\game-theory-master\\src\\main\\resources"
    val dir = new File(path)
    val fileStream = dir.listFiles.toStream

    lazy val instances:Stream[TransportInstance] =
      for(file <- fileStream) yield TransportInstance(file.getAbsolutePath)

    for(i <- instances) {
      val lp = new LPSolver(i)
    }
  }
}
