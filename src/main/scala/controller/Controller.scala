package controller

import model.CardComponent.{Card, CardDeck, CardLogic}
import model._
import util.Observable

import scala.util.Random

class Controller(var board: Board) extends Observable {

  var gameState: GameState = UpdateGame(this).buildGame
  initAndDistributeCardsToPlayer(6)

  //Board
  def createNewBoard(size: Int): Board = {
    board = new Board(size)
    notifyObservers
    board
  }

  def createRandomBoard(size: Int): Board = {
    board = new BoardCreateStrategyRandom().createNewBoard(size)
    board
  }

  def toStringBoard: String = toStringHouse + board.toString()

  def toStringHouse: String = {
    val players: Array[Player] = gameState.players._1
    val title: String = s"${Console.UNDERLINED}Houses${Console.RESET}"
    val up: String = "‾" * players.length * 3
    val down: String = "_" * players.length * 3
    var house: String = ""
    players.indices.foreach(i => house = house + s" ${players(i).color}${players(i).inHouse}${Console.RESET} ")
    "\n" + down + "\n" + house + "\t" + title + "\n" + up + "\n"
  }

  def getBoard: Board = board

  //Player
  //@TODO: extend method to dynamic playerADD with color algorithm, later... bitches
  def createPlayers(playerNames: List[String]): GameState = {
    val colors = Array("gelb", "blau", "grün", "rot")
    val players: Array[Player] = playerNames.indices.map(i => Player.PlayerBuilder().withColor(colors(i)).withName(playerNames(i)).build()).toArray
    gameState = UpdateGame(this).withPlayers(players).buildGame
    gameState
  }

  def useCardLogic(selectedPlayerIndices: List[Int], pieceNum: List[Int], cardNum: Int): Int = {
    val players: Array[Player] = gameState.players._1
    if (selectedPlayerIndices != Nil && players(selectedPlayerIndices.head).cardList.nonEmpty) {

      val selectedCard: Card = playChosenCard(selectedPlayerIndices.head, cardNum)
      val task = selectedCard.getTask

      if (task == "swap" || task == "move") { // will be changed later as well since other logic's aren't implemented yet
        val taskMode = CardLogic.getLogic(task)
        val moveInInt = if (selectedCard.getTask == "move") selectedCard.getSymbol.toInt else 0
        val updateGame: (Board, Array[Player], Int) = CardLogic.setStrategy(taskMode, players, board, selectedPlayerIndices, pieceNum, moveInInt)

        gameState = UpdateGame(this).withPlayers(updateGame._2).withBoard(updateGame._1).buildGame
        notifyObservers
        return updateGame._3
      }
    }
    -1
  }

  //Cards

  def createCardDeck: (Array[Card], Int) = {
    val array = Random.shuffle(CardDeck.apply()).toArray
    (array, array.length)
  }

  def toStringCardDeck: String = {
    var cardString: String = "________DECK________\n"
    val cardDeck: (Array[Card], Int) = gameState.cardDeck
    cardDeck._1.indices.foreach(i => if (i < cardDeck._2) cardString += s"$i: ${cardDeck._1(i)}\n") + "\n"
    cardString
  }

  def drawFewCards(amount: Int): List[Card] = {
    var hand: List[Card] = Nil
    for (i <- 0 until amount) {
      hand = drawCardFromDeck :: hand
    }
    hand
  }

  def drawCardFromDeck: Card = {
    var cardDeck: (Array[Card], Int) = gameState.cardDeck
    if (cardDeck._2 != 0) cardDeck = (cardDeck._1, cardDeck._2 - 1)
    gameState = UpdateGame(this).withCardDeck(cardDeck._1).withCardPointer(cardDeck._2).buildGame
    cardDeck._1(cardDeck._2)
  }

  def playChosenCard(playerNum: Int, cardNum: Integer): Card = {
    val oldCard = player(playerNum).getCard(cardNum)
    player(playerNum) = player(playerNum).copy(cardList = player(playerNum).removeCard(player(playerNum).getCard(cardNum)))
    println(s"$oldCard with ${oldCard.getTask}")
    oldCard
  }

  def toStringPlayerHands: Unit = {
    player.indices.foreach(i => println(s"${player(i).color}player: " + i + s"${Console.RESET} --> myHand: " + player(i).cardList))
  }

  def distributeCardsToPlayer(playerNum: Int, cards: List[Card]): Player = {
    player(playerNum) = player(playerNum).setHandCards(cards)
    player(playerNum)
  }

  def initAndDistributeCardsToPlayer(amount: Int): Unit = {
    player.indices.foreach(pNr => player(pNr) = player(pNr).setHandCards(drawFewCards(amount)))
  }


}
