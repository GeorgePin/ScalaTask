package com.innowise.application.calculator

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import com.innowise.application.calculator.Main.Calculator.{Minus, Plus, Print}

import scala.util.Random

object Main extends App {
  private val system = ActorSystem("calculatorSystem")
  private val calculatorHelper = system.actorOf(Props[CalculatorHelper], name = "calculatorHelper")
  private val calculator = system.actorOf(Calculator.props(), name = "calculator")
  calculatorHelper ! "start"

  object Calculator {
    case class Plus(num: Int)

    case class Minus(num: Int)

    case class Print()

    def props(): Props = Props[Calculator]
  }

  class Calculator() extends Actor {
    private var value: Int = 0;

    def receive: Receive = {
      case Plus(num: Int) => {
        value += num
        sender() ! "start"
      }
      case Minus(num: Int)
      => {
        value -= num
        sender() ! "start"
      }
      case Print => {
        println(value)
        sender() ! "start"
      }
      case _ => {
        print("Default case.")
        context.stop(self)
      }
    }
  }


  class CalculatorHelper() extends Actor {
    var actionCounter: Int = 0

    def receive: Receive = {
      case "start" => {
        if (actionCounter >= 100) {
          self ! "stop"
        } else {
          val whichAction = Random.nextInt(3) + 1
          val value = Random.nextInt(100) + 1
          whichAction match {
            case 1 => {
              actionCounter += 1
              calculator ! Plus(value)
            }
            case 2 => {
              actionCounter += 1
              calculator ! Minus(value)
            }
            case 3 => {
              actionCounter += 1
              calculator ! Print
            }
            case _ => {
              print("Default case.")
              context.stop(self)
            }
          }
        }
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


