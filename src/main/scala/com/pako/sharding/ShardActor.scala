package com.pako.sharding

import akka.actor.{Actor, Props}
import akka.cluster.sharding.ShardRegion.Passivate

object ShardActor {
  def props() = Props[ShardActor]
}

final case class MsgEnvelope(id: Long, payload: Any)
case class Msg(msg: String)

class ShardActor extends Actor {

  println("I am alive")

  override def receive: Receive = {
   case Msg(msg) => println(s"Msg in node $msg - $self()")
   case um => println(s"Unknow message $um")
  }
}
