package dog.controller.StateComponent

import dog.model.BoardComponent.BoardTrait
import dog.model.BoardComponent.boardBaseImpl.Board
import dog.model.CardComponent.CardTrait
import dog.model.CardComponent.cardBaseImpl.CardLogic.JokerState
import dog.model.CardComponent.cardBaseImpl.{Card, CardDeck}
import dog.model.{Player, PlayerBuilder}

trait GameStateMasterTrait {

  //player
  var colors: Array[String]
  var playerNames: Array[String]
  var playerVector: Vector[Player]
  var actualPlayerIdx: Int
  var pieceAmount: Int

  var roundAndCardsToDistribute: (Int, Int)
  var cardDeck: Vector[CardTrait]
  var lastPlayedCardOpt: Option[CardTrait]

  var cardPointer: Int
  var board: BoardTrait
  var boardSize: Int

  var message: Option[String]

  case class UpdateGame() {

    def withAmountDistributedCard(setAmount: Int): UpdateGame = {
      roundAndCardsToDistribute = (roundAndCardsToDistribute._1, setAmount)
      this
    }

    def withDistributedCard(): UpdateGame = {
      playerVector.foreach(x => x.setHandCards(Card.RandomCardsBuilder().withAmount(roundAndCardsToDistribute._2).buildRandomCardList))
      this
    }

    def withLastPlayedCard(setCard: CardTrait): UpdateGame = {
      lastPlayedCardOpt = Some(setCard)
      this
    }

    def withPlayers(setPlayers: Vector[Player]): UpdateGame = {
      playerVector = setPlayers
      this
    }

    def withActualPlayer(setActualPlayer: Int): UpdateGame = {
      actualPlayerIdx = setActualPlayer
      this
    }

    def withNextPlayer(): UpdateGame = {
      actualPlayerIdx = (actualPlayerIdx + 1) % playerVector.size
      this
    }


    def withNextRound(): UpdateGame = {
      if (roundAndCardsToDistribute._1 % 6 != 5)
      roundAndCardsToDistribute = (roundAndCardsToDistribute._1 + 1, roundAndCardsToDistribute._2 - 1)
      else
        roundAndCardsToDistribute = (roundAndCardsToDistribute._1 + 1, 6)
      this
    }

    def withCardDeck(setCardDeck: Vector[CardTrait]): UpdateGame = {
      cardDeck = setCardDeck
      this
    }

    def withCardPointer(setCardPointer: Int): UpdateGame = {
      cardPointer = setCardPointer
      this
    }

    def withBoard(setBoard: BoardTrait): UpdateGame = {
      board = setBoard
      this
    }

    def withLastMessage(setMessage: String): UpdateGame = {
      message = Some(setMessage)
      this
    }

    def withRemoveLastMessage: UpdateGame = {
      message = None
      this
    }

    def buildGame: GameState = {
      GameState((playerVector, actualPlayerIdx), (cardDeck, cardPointer), lastPlayedCardOpt, board, message)
    }

    def resetGame: GameState = {
      //Board
      boardSize = 96 // hast to be dividable by 4
      board = new Board(boardSize)

      // Player
      playerNames = Array("Player 1", "Player 2", "Player 3", "Player 4")
      colors = Array("yellow", "white", "green", "red")
      pieceAmount = 4
      playerVector = playerNames.indices.map(i => new PlayerBuilder().Builder()
        .withColor(colors(i))
        .withName((playerNames(i), i))
        .withPiece(pieceAmount, (boardSize / playerNames.length) * i)
        .withGarage(i)
        .withGeneratedCards(roundAndCardsToDistribute._2).build()).toVector
      actualPlayerIdx = 0

      // Card
      cardDeck = CardDeck.CardDeckBuilder().withAmount(List(10, 10)).withShuffle.buildCardVector
      cardPointer = cardDeck.length
      roundAndCardsToDistribute = (0, 6)
      lastPlayedCardOpt = Some(Card("pseudo", "pseudo", "pseudo"))

      message = None

      GameState((playerVector, actualPlayerIdx), (cardDeck, cardPointer), lastPlayedCardOpt, board, None)
    }

    def loadGame(gameState: GameState): UpdateGame = {
      JokerState.reset
      cardDeck = gameState.cardDeck._1
      cardPointer = gameState.cardDeck._2
      board = gameState.board
      playerVector = gameState.players._1
      actualPlayerIdx = gameState.players._2
      lastPlayedCardOpt = gameState.lastPlayedCardOpt
      this
    }
  }
}
