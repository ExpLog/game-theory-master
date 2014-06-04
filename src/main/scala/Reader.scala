import scala.io.Source._
import MyS.MySContext

class Reader(srcPath: String) {
  val lines = fromFile(srcPath).getLines()

  val name: String = lines.next() match {
    case mys"BEGIN FCTP PROBLEM.    $name" => name
  }
}
