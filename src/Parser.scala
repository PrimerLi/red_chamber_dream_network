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

    override def equals(other: Any) = other match 
    {
        case that: Edge => this.left == that.left && this.right == that.right
        case _ => false
    }

    override def hashCode: Int = 
    {
        var result: Int = this.left.hashCode
        result += 31 * result + this.right.hashCode
        return result
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
        
        val writer = new PrintWriter(new File("names.txt"))
        for (name <- names)
        {
            writer.write(name + "\n")
        }
        writer.close()

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
        val aliases: HashMap[String, Array[String]] = readAliases("aliases.txt")
        val corpusFileName: String = "../data/red_chamber_dream.txt"
        var counter: Int = 0
        val interval: Int = 100
        val allEdges: HashMap[(String, String), Double] = new HashMap[(String, String), Double]
        breakable
        {
            for (line <- Source.fromFile(corpusFileName).getLines)
            {
                counter += 1
                if (counter % interval == 0)
                {
                    println("Number of lines processed = " + counter)
                }
                val edges: ArrayBuffer[Edge] = getEdges(names, aliases, line)
                for (edge <- edges)
                {
                    if (allEdges.contains((edge.left, edge.right)))
                    {
                        allEdges((edge.left, edge.right)) += edge.weight
                    }
                    else
                    {
                        allEdges((edge.left, edge.right)) = edge.weight
                    }
                }
                if (false && counter > 1000)
                {
                    break
                }
            }
        }
        val writer = new PrintWriter(new File("edges.csv"))
        val allEdgesSorted: Array[((String, String), Double)] = allEdges.to[Array].sortBy(ele => ele._2).reverse
        for (((left, right), weight) <- allEdgesSorted)
        {
            writer.write(left + ";" + right + ";" + weight.toString + "\n")
        }
        writer.close()
    }
}
