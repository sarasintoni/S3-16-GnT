package com.unibo.s3.main_system.communication

import akka.actor.{Props, UntypedAbstractActor}
import com.unibo.s3.main_system.communication.Messages.{responseNeighbourMsg, sendCopInfoMsg}

class CharacterActor extends UntypedAbstractActor {

  //grafo in qualche struttura

  //BaseCharacter incapsulato

  override def onReceive(message: Any): Unit = message match {
    case msg: responseNeighbourMsg =>
      msg.neighbours.foreach(neighbour => neighbour.tell(sendCopInfoMsg(), getSelf()))
      //dico sotto i vicini
    case msg: sendCopInfoMsg =>
      println("cop: " + getSelf() + "| info from: " + getSender())
      //qui ho le info dell'altro poliziotto quindi poi posso fare quello che devo

    //case ricevo grafo iniziale => salvo grafo iniziale
    //case
    case _ => println("(characterActor) message unknown:" + message)
  }
}

object CharacterActor {
  def props(): Props = Props(new CharacterActor())
}