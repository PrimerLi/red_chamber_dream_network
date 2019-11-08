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

class Edge
{
    var left: String = "null"
    var right: String = "null"
    var weight: Double = 0.0
    def this(left: String, right: String, weight: Double) = 
    {
        this()
        this.left = left
        this.right = right
        this.weight = weight
    }
    override def toString: String = 
    {
        return Array(this.left, this.right, this.weight.toString).mkString(";")
    }
}

object Parser
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
   
    def getEdges(names: Array[String], line: String): ArrayBuffer[Edge] = 
    {
        val nameStats: HashMap[String, Int] = new HashMap[String, Int]
        for (name <- names)
        {
            if (line.contains(name))
            {
                if (nameStats.contains(name))
                {
                    nameStats(name) += 1
                }
                else
                {
                    nameStats(name) = 1
                }
            }
        }
        val keys: Array[String] = nameStats.keys.toArray
        val edges: ArrayBuffer[Edge] = new ArrayBuffer[Edge]
        for (i <- keys.indices)
        {
            for (j <- i+1 until keys.size)
            {
                var left: String = "null"
                var right: String = "null"
                if (keys(i) < keys(j))
                {
                    left = keys(i)
                    right = keys(j)
                }
                else
                {
                    left = keys(j)
                    right = keys(i)
                }
                val weight: Double = min(nameStats(left), nameStats(right))
                edges.append(new Edge(left, right, weight))
            }
        }
        return edges
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
        
        val line: String = Source.fromFile("a.txt").getLines.toArray.head
        val edges: ArrayBuffer[Edge] = getEdges(names, line)
        val writer1 = new PrintWriter(new File("edges.csv"))
        for (edge <- edges)
        {
            writer1.write(edge + "\n")
        }
        writer1.close()
    }
}
