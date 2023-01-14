package com.innowise.application.bankAccount

import akka.actor.{Actor, ActorSystem, Props}
import com.innowise.application.bankAccount.Main.Account.ActionHelper
import com.innowise.application.bankAccount.Main.Bank.{BlockAccount, Deposit, Statement, UnblockAccount, Withdraw}

object Main extends App {
  val system = ActorSystem("backSystem")
  val bank = system.actorOf(Props[Bank], "bank")
  val accountA = system.actorOf(Props[Account], "accountA")
  val accountB = system.actorOf(Props[Account], "accountB")

  test


  class Bank extends Actor {
    override def receive: Receive = {
      case Deposit(account: Account, amount: BigDecimal) => {
        if (account.getIsBlocked) {
          println("Account is blocked!")
        } else {
          val balance: BigDecimal = account.getBalance
          account.setBalance(balance + amount)
        }
      }
      case Withdraw(account: Account, amount: BigDecimal) => {
        if (account.getIsBlocked) {
          println("Account is blocked!")
        } else if (account.getBalance.compareTo(amount) < 0) {
          println("Insufficient funds")
        }
        else {
          val balance: BigDecimal = account.getBalance
          account.setBalance(balance - amount)
        }
      }
      case Statement(account: Account) => {
        if (account.getIsBlocked) {
          println("Account is blocked!")
        } else {
          println("You have $".concat {
            s"${account.getBalance} left on your account"
          })
        }
      }
      case BlockAccount(account: Account) => {
        if (account.getIsBlocked) {
          println("Account is already blocked")
        } else {
          account.setIsBlocked(true)
          println("Your account was blocked successfully")
        }
      }
      case UnblockAccount(account: Account) => {
        if (!account.getIsBlocked) {
          println("Account is not blocked")
        } else {
          account.setIsBlocked(false)
          println("Your account was unblocked successfully")
        }
      }
      case _ => {
        println("Unknown command")
      }
    }
  }

  object Bank {
    case class Deposit(account: Account, amount: BigDecimal)

    case class Withdraw(account: Account, amount: BigDecimal)

    case class Statement(account: Account)

    case class BlockAccount(account: Account)

    case class UnblockAccount(account: Account)


  }


  class Account extends Actor {
    private var balance: BigDecimal = 0
    private var isBlocked: Boolean = false

    override def receive: Receive = {
      case ActionHelper("deposit", amount: BigDecimal) => {
        bank ! Deposit(this, amount)
      }
      case ActionHelper("withdraw", amount: BigDecimal) => {
        bank ! Withdraw(this, amount)
      }
      case ActionHelper("statement", _) => {
        bank ! Statement(this)
      }
      case ActionHelper("unblockAccount", _) => {
        bank ! UnblockAccount(this)
      }
      case ActionHelper("blockAccount", _) => {
        bank ! BlockAccount(this)
      }
      case _ => {
        println("Unknown command")
      }

    }

    def getBalance: BigDecimal = balance

    def setBalance(balance: BigDecimal): Unit = this.balance = balance

    def getIsBlocked: Boolean = isBlocked

    def setIsBlocked(isBlocked: Boolean): Unit = this.isBlocked = isBlocked

  }

  object Account {
    case class ActionHelper(action: String, amount: BigDecimal = 0)
  }

  def test: Unit = {
    accountA ! ActionHelper("withdraw", 50.001)
    accountA ! ActionHelper("deposit", 50.001)
    accountA ! ActionHelper("statement")
    accountA ! ActionHelper("unblockAccount")
    accountA ! ActionHelper("blockAccount")

    accountB ! ActionHelper("unblockAccount")
    accountB ! ActionHelper("blockAccount")
    accountB ! ActionHelper("statement")

    accountA ! ActionHelper("withdraw", 50.001)
    accountB ! ActionHelper("someRandom4242String")
    accountA ! ActionHelper("deposit", 50.001)
    accountA ! ActionHelper("unblockAccount", 50.001)

    accountB ! ActionHelper("someRandomString")
    accountB ! ActionHelper("deposit", 42.691)
    accountB ! ActionHelper("unblockAccount")
    accountB ! ActionHelper("statement")
    accountB ! ActionHelper("deposit", 42.691)
    accountB ! ActionHelper("statement")
  }

}
