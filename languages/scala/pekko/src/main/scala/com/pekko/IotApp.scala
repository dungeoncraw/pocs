package com.pekko

import com.pekko.iot.IotSupervisor
import org.apache.pekko.actor.typed.ActorSystem

object IotApp {
  def main(args: Array[String]): Unit = {
    // create the iot actor system
    ActorSystem[Nothing](IotSupervisor(), "iot-system")
  }
}