package com.unibo.s3.main_system.world.actors

import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.duration._
import com.badlogic.gdx.ai.steer.{Proximity, Steerable}
import com.badlogic.gdx.math.Vector2
import com.unibo.s3.main_system.characters.steer.collisions.gdx.Box2dSquareAABBProximity

import scala.concurrent.Await

/**
  * A 'proxy' aabb-based proximity detector, that works interacting with [[WorldActor]],
  * instead of directly referencing a [[com.badlogic.gdx.physics.box2d.World]].
  * @see [[Box2dSquareAABBProximity]]
  * @param owner the owner of this detector
  * @param worldActor a reference to the WorldActor
  * @param detectionRadius the detection radius of the queries
  *
  * @author mvenditto
  */
class Box2dSquareAABBProximityProxy (
  var owner: Steerable[Vector2],
  worldActor: ActorRef,
  detectionRadius: Float) extends Proximity[Vector2] {

  implicit val timeout = Timeout(5 seconds)

  override def getOwner: Steerable[Vector2] = owner

  override def setOwner(owner: Steerable[Vector2]): Unit = this.owner = owner

  override def findNeighbors(callback: Proximity.ProximityCallback[Vector2]): Int = {
    val future = worldActor ? ProximityQuery(owner, detectionRadius)
    val result = Await.result(future, timeout.duration).asInstanceOf[ProximityQueryResponse]
    result.neighbors.foreach(n => callback.reportNeighbor(n))
    result.neighbors.length
  }
}
