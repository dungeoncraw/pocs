package com.pekko.iot

import org.apache.pekko.actor.typed.{Behavior, PostStop, Signal}
import org.apache.pekko.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors}

object IotSupervisor {
  def apply(): Behavior[Nothing] = Behaviors.setup[Nothing](context => new IotSupervisor(context))
}
class IotSupervisor(context: ActorContext[Nothing]) extends AbstractBehavior[Nothing](context){
  context.log.info("IoT Application Started")

  override def onMessage(msg: Nothing): Behavior[Nothing] = {
    Behaviors.unhandled
  }

  override def onSignal: PartialFunction[Signal, Behavior[Nothing]] = {
    case PostStop =>
      context.log.info("IoT Application stopped")
      this
  }
}
