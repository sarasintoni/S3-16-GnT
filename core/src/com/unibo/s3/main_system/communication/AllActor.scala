package com.unibo.s3.main_system.communication

import akka.actor.Props
import com.unibo.s3.main_system.communication.Messages.FileMsg
import org.jgrapht.UndirectedGraph
import org.jgrapht.graph.DefaultEdge

class AllActor(override val name: String) extends NamedActor {

  override def onReceive(message: Any): Unit = message match {
    case msg: UndirectedGraph[String, DefaultEdge] =>
      println("name: " + name + "| graph: " + msg.toString + " from: " + sender())
    case msg: FileMsg =>
      println("name: " + name + "| file line: " + msg.line + " from: " + sender())
    case _ => println("message unknown")
  }
}

object AllActor {
  def props(name: String): Props = Props(new AllActor(name))
}
