package com.innowise.application.incrementor

import akka.actor.{Actor, ActorSystem, Props}
import com.innowise.application.incrementor.Main.Incrementer.ValueHolder

import scala.util.Random


object Main extends App {
  private val system = ActorSystem("incrementSystem")
  private val incrementerHelper = system.actorOf(Props[IncrementerHelper], name = "incrementerHelper")
  private val incrementer = system.actorOf(Incrementer.props(), name = "incrementer")
  incrementerHelper ! "newStart"


  object Incrementer {

    case class ValueHolder(value: Int, action: String)


    def props(): Props = Props[Incrementer]
  }

  class Incrementer() extends Actor {

    def receive: Receive = {
      case ValueHolder(value: Int, "increment") => {
        sender() ! ValueHolder(value + 1, "default")
      }
      case ValueHolder(value: Int, "decrement") => {
        sender() ! ValueHolder(value - 1, "default")
      }
      case ValueHolder(value: Int, "print") => {
        println(value)
        sender() ! ValueHolder(value, "default")
      }
      case _ => {
        print("Default case.")
        context.stop(self)
      }
    }
  }


  class IncrementerHelper() extends Actor {
    var actionCounter: Int = 0

    def receive: Receive = {
      case ValueHolder(value: Int, _) => {
        if (actionCounter >= 100) {
          self ! "stop"
        } else {
          val whichAction = Random.nextInt(3) + 1
          whichAction match {
            case 1 => {
              actionCounter += 1
              incrementer ! ValueHolder(value, "increment")
            }
            case 2 => {
              actionCounter += 1
              incrementer ! ValueHolder(value, "decrement")
            }
            case 3 => {
              actionCounter += 1
              incrementer ! ValueHolder(value, "print")
            }
            case _ => {
              print("Default case.")
              context.stop(self)
            }
          }
        }
      }
      case "newStart" => {
        incrementer ! ValueHolder(0, "increment")
      }
      case "stop" => {
        println("exit.")
      }
      case _ => {
        print("Default case.")
        context.stop(self)
      }

    }
  }
}