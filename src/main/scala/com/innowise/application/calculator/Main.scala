package com.innowise.application.calculator

import akka.actor.{Actor, ActorSystem, Props}
import com.innowise.application.calculator.Main.Calculator.{Minus, Plus, Print}

import scala.util.Random

object Main extends App {
  private val system = ActorSystem("calculatorSystem")
  private val calculatorHelper = system.actorOf(Props[CalculatorHelper], name = "calculatorHelper")
  private val calculator = system.actorOf(Props[Calculator], name = "calculator")
  calculatorHelper ! "start"

  object Calculator {
    case class Plus(num: Int)

    case class Minus(num: Int)

    case object Print

  }

  class Calculator extends Actor {
    private var value: Int = 0

    override def postStop: Unit = {
      println("Calculator is going to die soon")
    }

    def receive: Receive = {
      case Plus(num: Int) => {
        value += num
        sender ! "start"
      }
      case Minus(num: Int)
      => {
        value -= num
        sender ! "start"
      }
      case Print => {
        println(value)
        sender ! "start"
      }
      case _ => {
        context.system.terminate
      }
    }
  }


  class CalculatorHelper extends Actor {
    private var actionCounter: Int = 0

    override def postStop: Unit = {
      println("CalculatorHelper is going to die soon")
    }

    def receive: Receive = {
      case "start" => {
        if (actionCounter >= 100) {
          context.system.terminate
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
              context.system.terminate
            }
          }
        }
      }
      case _ => {
        context.system.terminate
      }
    }
  }
}


