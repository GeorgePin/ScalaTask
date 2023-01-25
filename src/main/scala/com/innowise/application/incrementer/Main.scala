package com.innowise.application.incrementer

import akka.actor.{Actor, ActorSystem, PoisonPill, Props}
import com.innowise.application.incrementer.Main.Incrementer.{Decrement, Exit, Increment, Print}

import scala.util.Random


object Main extends App {
  private val system = ActorSystem("incrementSystem")
  private val incrementerHelper = system.actorOf(Props[IncrementerHelper], name = "incrementerHelper")
  private val incrementer = system.actorOf(Props[Incrementer], name = "incrementer")
  incrementerHelper ! "newStart"


  object Incrementer {
    case object Increment

    case object Decrement

    case object Print

    case object Exit


  }

  class Incrementer extends Actor {
    override def postStop: Unit = {
      println("Incrementer is going to die soon")
    }

    def receive: Receive = handleReceive(0)


    def handleReceive(value: Int): Receive = {
      case Increment => {
        val newValue = value + 1
        context.become(handleReceive(newValue))
        sender ! newValue
      }
      case Decrement => {
        val newValue = value - 1
        context.become(handleReceive(value - 1))
        sender ! newValue
      }
      case Print => {
        println(value)
        sender ! value
      }
      case _ => {
        //                incrementerHelper ! PoisonPill
        //                self ! PoisonPill
        context.system.terminate()
      }
    }
  }

  object IncrementerHelper {

    val actionsList = List(Increment, Decrement, Print, Exit)
  }

  class IncrementerHelper extends Actor {

    import IncrementerHelper._

    override def postStop: Unit = {
      println("IncrementerHelper is going to die soon")
    }

    def receive: Receive = {
      case value: Int => {
        val whichAction = if (-1000 >= value || value >= 1000) actionsList(3) else actionsList(Random.nextInt(2) + 1)
        whichAction match {
          case Increment => {
            incrementer ! Increment
          }
          case Decrement => {
            incrementer ! Decrement
          }
          case Print => {
            incrementer ! Print
          }
          case Exit => {
            //            incrementer ! PoisonPill
            //            self ! PoisonPill
            context.system.terminate()
          }
        }
      }
      case "newStart" => {
        incrementer ! Increment
      }
      case _ => {
        //        incrementer ! PoisonPill
        //        self ! PoisonPill
        context.system.terminate()
      }
    }
  }
}