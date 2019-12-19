package controller.Component

import model.CardComponent.CardTrait
import model.{Board, Player}
import util.UndoManager

import scala.swing.Publisher

trait ControllerTrait extends Publisher {

  val undoManager: UndoManager
  var gameState: GameState
  var gameStateMaster: GameStateMasterTrait

  def doStep(): Unit

  def undoCommand(): Unit

  def redoCommand(): Unit

  //Board
  def createNewBoard(size: Int): Board

  def createRandomBoard(size: Int): Board

  /**
   * prints the board and houses
   *
   * @return the board and houses in a String
   */
  def toStringBoard: String

  /**
   * prints the houses of each player
   *
   * @return the houses in a String
   */
  def toStringHouse: String

  def getBoard: Board

  //Player
  //@TODO: extend method to dynamic playerADD with color algorithm, later... bitches
  def createPlayers(playerNames: List[String]): GameState

  /**
   * Manages the round
   *
   * @param otherPlayer is not -1 when e.g. swapping -> user has to know when to insert more or less commands
   * @param pieceNum    is a List of indexes for the pieces of each player for e.g. swapping, only first is used when its about a move
   * @param cardNum     is the index of the card in a CardList of the player that is played
   * @return a String that is returned to the TUI for more information
   */
  def manageRound(otherPlayer: Int, pieceNum: List[Int], cardNum: Int): String

  /**
   * uses the card and extracts its logic
   *
   * @param selectedPlayerList is the list of Players -> first one is the actual player =>
   *                           managed by manageRound but can also be set manually for testing purposes
   * @param pieceNum           is a List of indexes for the pieces of each player for e.g. swapping, only first is used when its about a move
   * @param cardNum            is the index of the card in a CardList of the player that is played
   * @return
   */
  def useCardLogic(selectedPlayerList: List[Int], pieceNum: List[Int], cardNum: Int): Int

  def createCardDeck(amounts: List[Int]): (Vector[CardTrait], Int)

  def toStringCardDeck: String

  def getSelectedCard(playerNum: Int, cardNum: Integer): CardTrait

  def drawCardFromDeck: CardTrait

  def drawFewCards(amount: Int): List[CardTrait]

  def initAndDistributeCardsToPlayer(amount: Int): Unit

  def toStringPlayerHands: String

  def testDistributeCardsToPlayer(playerNum: Int, cards: List[CardTrait]): Player
}
