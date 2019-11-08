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

    def updateNameStats(name: String, nameStats: HashMap[String, Int]): HashMap[String, Int] = 
    {
        if (nameStats.contains(name))
        {
            nameStats(name) += 1
        }
        else
        {
            nameStats(name) = 1
        }
        return nameStats
    }

    def createEdgesFromNameStats(nameStats: HashMap[String, Int]): ArrayBuffer[Edge] = 
    {
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
   
    def getEdges(names: Array[String], line: String): ArrayBuffer[Edge] = 
    {
        var nameStats: HashMap[String, Int] = new HashMap[String, Int]
        for (name <- names)
        {
            if (line.contains(name))
            {
                nameStats = updateNameStats(name, nameStats)
            }
        }
        
        val edges: ArrayBuffer[Edge] = createEdgesFromNameStats(nameStats)
        return edges
    }

    def getEdges(names: Array[String], aliases: HashMap[String, Array[String]], line: String): ArrayBuffer[Edge] = 
    {
        var nameStats: HashMap[String, Int] = new HashMap[String, Int]
        for (name <- names)
        {
            if (aliases.contains(name))
            {
                val alternateNames: Array[String] = aliases(name)
                for (alternateName <- alternateNames)
                {
                    if (line.contains(alternateName))
                    {
                        nameStats = updateNameStats(name, nameStats) 
                    }
                }
            }
            else
            {
                if (line.contains(name))
                {
                    nameStats = updateNameStats(name, nameStats) 
                }
            }
        }
        val edges: ArrayBuffer[Edge] = createEdgesFromNameStats(nameStats)
        return edges
    }

    def readAliases(aliasesFileName: String): HashMap[String, Array[String]] = 
    {
        assert(Files.exists(Paths.get(aliasesFileName)))
        val aliases: HashMap[String, Array[String]] = new HashMap[String, Array[String]]
        for (line <- Source.fromFile(aliasesFileName).getLines)
        {
            val a: Array[String] = line.split(":")
            val name: String = a.head
            val alternateNames: ArrayBuffer[String] = a.last.split(",").to[ArrayBuffer]
            alternateNames.append(name)
            aliases(name) = alternateNames.toArray
        }
        return aliases
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

        val aliases: HashMap[String, Array[String]] = readAliases("aliases.txt")
        
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
