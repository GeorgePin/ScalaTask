package com.innowise.application.calculator

import akka.actor.{Actor, ActorRef, ActorSystem, Props}

import scala.util.Random

case class Plus(num: Int)

case class Minus(num: Int)

case object Print

object StartCalculator

class Calculator(calculatorHelper: ActorRef) extends Actor {
  private val value: Int = 0;

  def receive: Receive = onMessage(value)

  private def onMessage(value: Int): Receive = {
    case Plus(num: Int) => {
      context.become(onMessage(value + num))
      calculatorHelper ! StartCalculator
    }
    case Minus(num: Int) => {
      context.become(onMessage(value - num))
      calculatorHelper ! StartCalculator
    }
    case Print => {
      println(value)
      calculatorHelper ! StartCalculator
    }
    case _ => {
      print("Default case.")
      context.stop(self)
    }
  }
}

object Calculator {

}

class CalculatorHelper extends Actor {
  def receive: Receive = {
    case StartCalculator => {
      val whichAction = Random.nextInt(3) + 1
      val value = Random.nextInt(100) + 1
      whichAction match {
        case 1 => sender ! Plus(value)
        case 2 => sender ! Minus(value)
        case 3 => sender ! Print
        case _ => {
          print("Default case.")
          context.stop(self)
        }
      }
    }
    case _ => {
      print("Default case.")
      context.stop(self)
    }

  }
}

object CalculatorTest extends App {
  private val system = ActorSystem("CalculatorSystem")
  private val calculatorHelper = system.actorOf(Props[CalculatorHelper], name = "calculatorHelper")
  private val calculator = system.actorOf(Props(new Calculator(calculatorHelper)), name = "calculator")
  calculator ! Print
}
