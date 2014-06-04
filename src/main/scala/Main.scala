import MyS.MySContext
import java.io.File

object Main {
  def main(args: Array[String]) {
    val path = args(0)
    val dir = new File(path)
    val fileStream = dir.listFiles.toStream

    lazy val instances:Stream[TransportInstance] =
      for(file <- fileStream) yield TransportInstance(file.getAbsolutePath)

    for(i <- instances) {
      val lp = new LPSolver(i)
      lp.dispose()
    }

    LPSolver.endGame()

    val r = new Reader(fileStream(0).getAbsolutePath)
    println(r.name)
  }
}
