import lp.{TransportInstance, LPSolver}
import reader.Reader
import Reader._
import java.io.File
import reader.Reader

object Main {
  def main(args: Array[String]) {
    val path = args(0)
    val dir = new File(path)
    val fileStream = dir.listFiles.toStream

    lazy val instances:Stream[TransportInstance] =
      for(file <- fileStream) yield TransportInstance(file)

    for(i <- instances) {
      val lp = new LPSolver(i)
      println(lp)
      lp.dispose()
    }

    val r = new Reader(fileStream(0))
    println(r.name)
    println(r.source, r.sink)
  }
}
