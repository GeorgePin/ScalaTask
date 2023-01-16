package com.innowise.application.incrementer

import akka.actor.{Actor, ActorSystem, PoisonPill, Props}
import com.innowise.application.incrementer.Main.Incrementer.{Decrement, Increment, Print}

import scala.util.Random


object Main extends App {
  private val system = ActorSystem("incrementSystem")
  private val incrementerHelper = system.actorOf(Props[IncrementerHelper], name = "incrementerHelper")
  private val incrementer = system.actorOf(Props[Incrementer], name = "incrementer")
  incrementerHelper ! "newStart"


  object Incrementer {
    case class Increment(value: Int)

    case class Decrement(value: Int)

    case class Print(value: Int)


  }

  class Incrementer extends Actor {
    override def postStop: Unit = {
      println("Incrementer is going to die soon")
    }


    def receive: Receive = {
      case Increment(value: Int) => {
        sender ! value + 1
      }
      case Decrement(value: Int) => {
        sender ! value - 1
      }
      case Print(value: Int) => {
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


  class IncrementerHelper extends Actor {
    override def postStop: Unit = {
      println("IncrementerHelper is going to die soon")
    }

    def receive: Receive = {
      case value: Int => {
        val whichAction = if (-1000 > value || value > 1000) 4 else Random.nextInt(3) + 1
        whichAction match {
          case 1 => {
            incrementer ! Increment(value)
          }
          case 2 => {
            incrementer ! Decrement(value)
          }
          case 3 => {
            incrementer ! Print(value)
          }
          case 4 => {
            //            incrementer ! PoisonPill
            //            self ! PoisonPill
            context.system.terminate()
          }
        }
      }
      case "newStart" => {
        incrementer ! Increment(0)
      }
      case _ => {
        //        incrementer ! PoisonPill
        //        self ! PoisonPill
        context.system.terminate()
      }
    }
  }
}