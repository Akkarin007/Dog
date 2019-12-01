package controller

import controller.GameState._
import model.CardComponent.{Card, CardDeck, CardLogic}
import model._
import util.Observable

import scala.util.Random

class Controller() extends Observable {

  var gameState: GameState = IDLE
  var board: Board = setNewBoard(30)
  var player: Array[Player] = createSetPlayer(List("p1", "p2", "p3", "p4"))
  var cardDeck: (Array[Card], Int) = createCardDeck //card deck and int pointer


  //Board

  def setNewBoard(size: Int): Board = {
    board = new Board(size)
    gameState = CREATEBOARD
    notifyObservers
    board
  }

  def createRandomBoard(size: Int): Board = {
    board = new BoardCreateStrategyRandom().createNewBoard(size)
    gameState = CREATEBOARD
    board
  }

  def toStringBoard: String = toStringHouse + board.toString()

  def toStringHouse: String = {
    val title = s"${Console.UNDERLINED}Houses${Console.RESET}"
    val up = "‾" * player.length * 3
    val down = "_" * player.length * 3
    var house = ""
    player.indices.foreach(i => house = house + s" ${
      player(i).color match {
        case "gelb" => Console.YELLOW;
        case "blau" => Console.BLUE;
        case "grün" => Console.GREEN;
        case "rot" => Console.RED
      }
    }${player(i).inHouse}${Console.RESET} ")
    "\n" + down + "\n" + house + "\t" + title + "\n" + up + "\n"
  }

  def getBoard: Board = board

  //Player

  def createSetPlayer(playerNames: List[String]): Array[Player] = {
    val colors = Array("gelb", "blau", "grün", "rot")
    player = playerNames.indices.map(i => new Player(playerNames(i), colors(i), 4)).toArray
    gameState = CREATEPLAYER
    notifyObservers
    player
  }

  def useCardLogic(playerNum: List[Int], pieceNum: Int, cardNum: Int): Player = {
    if (player(playerNum.head).cardList.nonEmpty) {
      val selectedCard: Card = playCard(playerNum.head, cardNum)
      val taskMode = CardLogic.getLogic(selectedCard.getTask)
      val taskToInt = if (selectedCard.getTask == "move") selectedCard.getSymbol.toInt else 0
      val updateGame: (Board, Array[Player]) = CardLogic.setStrategy(taskMode, player, board, playerNum, pieceNum, taskToInt)
      board = updateGame._1
      player = updateGame._2
    }
    notifyObservers
    player(playerNum.head)
  }

  //Cards

  def createCardDeck: (Array[Card], Int) = {
    val array = Random.shuffle(CardDeck.apply()).toArray
    (array, array.length)
  }

  def toStringCardDeck: String = {
    var cardString: String = "________DECK________\n"
    cardDeck._1.indices.foreach(i => cardString += s"$i: ${cardDeck._1(i)}\n") + "\n"
  }

  def drawFewCards(amount: Int): List[Card] = {
    var hand: List[Card] = List(drawCardFromDeck)
    for (i <- 0 until amount - 1) {
      hand = drawCardFromDeck :: hand
    }
    hand
  }

  def drawCardFromDeck: Card = {
    if (cardDeck._2 != 0) cardDeck = (cardDeck._1, cardDeck._2 - 1)
    cardDeck._1(cardDeck._2)
  }

  def playCard(playerNum: Int, cardNum: Integer): Card = {
    val oldCard = player(playerNum).getCard(cardNum)
    player(playerNum) = player(playerNum).copy(cardList = player(playerNum).removeCard(player(playerNum).getCard(cardNum)))
    println(s"$oldCard with ${oldCard.getTask}")
    oldCard
  }

  def toStringPlayerHands: Unit = {
    player.indices.foreach(i => println(s"${
      player(i).color match {
        case "gelb" => Console.YELLOW
        case "blau" => Console.BLUE
        case "grün" => Console.GREEN
        case "rot" => Console.RED
      }
    }player: " + i + s"${Console.RESET} --> myHand: " + player(i).cardList))
  }

  def setHandCards(playerNum: Int, cards: List[Card]): Player = {
    player(playerNum) = player(playerNum).setHandCards(cards)
    player(playerNum)
  }

  def initPlayerHandCards(amount: Int): Unit = {
    player.indices.foreach(pNr => player(pNr) = player(pNr).setHandCards(drawFewCards(amount)))
  }


}
