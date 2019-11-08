package dream.chamber.red

import scala.io.Source
import scala.collection.mutable.{ArrayBuffer, HashMap, Stack, Queue}
import java.nio.file.{Files, Paths}
import java.io.{PrintWriter, File}
import scala.math._
import util.control.Breaks._
import java.util.Calendar
import scala.util.Random
import sys.process._

object Processor
{
    def readNames(inputFileName: String): Array[String] = 
    {
        assert(Files.exists(Paths.get(inputFileName)))
        val names: Array[String] = Source.fromFile(inputFileName).getLines.toArray.map(line => {
            val a: Array[String] = line.split("\\t")
            a.head
        })
        return names
    }
    def main(args: Array[String]): Unit = 
    {
        val inputFileName: String = "../data/characters.txt"
        val names: Array[String] = readNames(inputFileName)
        val writer = new PrintWriter(new File("names.txt"))
        for (name <- names)
        {
            writer.write(name + "\n")
        }
        writer.close()
    }
}
