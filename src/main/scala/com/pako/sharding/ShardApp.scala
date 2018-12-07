package com.pako.sharding

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.cluster.sharding.{ClusterSharding, ClusterShardingSettings, ShardRegion}


object ShardApp extends App {
  val system = ActorSystem("pb")

  ShardAppSetup.setup(system)

  val shard: ActorRef = ClusterSharding(system).shardRegion("Shard")

  if(System.getProperty("start") != null) {

    1 to 1000 foreach {
      counter => {
        shard ! MsgEnvelope(counter, Msg(s"Message to shard $counter"))
        Thread.sleep(1000)
      }
    }
  }

}

object ShardAppSetup {



  def setup(system: ActorSystem): ActorRef = {
    val counterRegion: ActorRef = ClusterSharding(system).start(
      typeName = "Shard",
      entityProps = ShardActor.props(),
      settings = ClusterShardingSettings(system),
      extractEntityId = extractEntityId,
      extractShardId = extractShardId)

    counterRegion
  }



  val extractEntityId: ShardRegion.ExtractEntityId = {
    case MsgEnvelope(id, payload) ⇒ (id.toString, payload)
  }

  val numberOfShards = 100

  val extractShardId: ShardRegion.ExtractShardId = {
    case MsgEnvelope(id, _) ⇒ (id % numberOfShards).toString
    case ShardRegion.StartEntity(id) ⇒
      // StartEntity is used by remembering entities feature
      (id.toLong % numberOfShards).toString
  }

}
