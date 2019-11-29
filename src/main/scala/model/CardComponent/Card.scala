package model.CardComponent

import model.CardTrait

import scala.util.Random


case class Card(symbol: String, task: String, color: String) {
  def getSymbol: String = symbol

  def getTask: String = task

  def getColor: String = color

  def myLogic(task: String): Unit ={
    cardLogic.logic
  }

  override def toString: String = {
    "Card(" + s"${
      getColor match {
        case "blue" => Console.BLUE;
        case "red" => Console.RED
      }
    }$getSymbol${Console.RESET})"
  }
}

object cardLogic {
  var logic = if (Random.nextInt() % 2 == 0) strategy1 else strategy2
  def strategy1 = println("I am strategy 1")
  def strategy2 = println("I am strategy 2")
}



object GenCardDeck {

  def apply(typ: String): CardTrait = typ match {
    case "special" =>
      SpecialCardsDeck()
    case "normal" =>
      NormalCardsDeck()
  }
}

object CardDeck {
  def apply(special: Integer, normal: Integer): List[Card] = {
    val specialList: List[Card] = (0 until special).map(GenCardDeck.apply("special").getCardDeck).toList
    val normalList: List[Card] = (0 until normal).map(GenCardDeck.apply("normal").getCardDeck).toList
    normalList ++ specialList
  }
}


case class SpecialCardsDeck() extends CardTrait {

  val specialCards: List[Card] = generateDeck

  override def generateDeck: List[Card] = {
    List(Card("1 11 start", "move;move;start", "red"),
      Card("4", "forwards;backwards", "red"),
      Card("7", "burn", "red"),
      Card("swap", "swap", "red"),
      Card("?", "joker", "red"),
      Card("13 play", "move;start", "red"))
  }

  override def getCardDeck: List[Card] = specialCards
}


case class NormalCardsDeck() extends CardTrait {

  val normalCards: List[Card] = generateDeck

  override def generateDeck: List[Card] = {
    List(Card("2", "move", "blue"),
      Card("3", "move", "blue"),
      Card("5", "move", "blue"),
      Card("6", "move", "blue"),
      Card("8", "move", "blue"),
      Card("9", "move", "blue"),
      Card("10", "move", "blue"),
      Card("12", "move", "blue"))
  }

  override def getCardDeck: List[Card] = normalCards
}

