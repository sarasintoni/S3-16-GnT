package com.unibo.s3.main_system.communication

import scala.collection.JavaConversions.asScalaBuffer
import akka.actor.{Props, UntypedAbstractActor}
import com.unibo.s3.main_system.communication.Messages.{MapMsg, StartMsg}
import com.unibo.s3.main_system.map.{MapGenerator, MazeMapGenerator}



class MapActor extends UntypedAbstractActor {

   // val FILEPATH = "maps/mapFile.txt" uncomment if file is required
  val mapGenerator: MapGenerator = new MazeMapGenerator

  override def onReceive(message: Any): Unit = message match {
    case _: StartMsg =>
      mapGenerator.generate(8, 20, 20, 0, 0) //valori da decidere una volta decise le dimensioni possibili per la mappa
      mapGenerator.getMap.foreach(line => SystemManager.getInstance().getLocalActor("graphActor").tell(MapMsg(line), getSelf()))
      mapGenerator.getMap.foreach(line => SystemManager.getInstance().getLocalActor("worldActor").tell(MapMsg(line), getSelf()))
    // val file = Gdx.files.local(FILEPATH)
     // file.readString().split("\\n").foreach(line => SystemManager.getInstance().getLocalActor("graphActor").tell(MapMsg(line), getSelf()))
    case _ => println("(mapActor) message unknown:" + message)
  }
}

object MapActor {
  def props(): Props = Props(new MapActor())
}