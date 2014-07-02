package reader

import scala.io.Source.fromFile
import java.io.File
import StringExtractor.StringExtractorContext
import scala.language.implicitConversions

class Reader(file: File) {
  val linesBuffer = fromFile(file).getLines()

  val name: String = linesBuffer.next() match {
    case ext"BEGIN FCTP PROBLEM.    $name" => name
  }

  val (source,sink): (Int, Int) = linesBuffer.next() match {
    case ext" $name           SOURCES=   $src , SINKS=   $snk & $trash" =>
      (src.toInt, snk.toInt)
  }
}

object Reader {
  implicit def fileToPath(file: File) = file.getAbsolutePath

  implicit def pathToFile(path: String) = new File(path)
}
