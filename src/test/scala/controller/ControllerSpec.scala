package controller


import model.CardComponent.Card
import model._
import org.scalatest.{Matchers, WordSpec}

class ControllerSpec extends WordSpec with Matchers {

  "A Controller" when {
    "initialized" should {
      val controller: Controller = new Controller()
      "create a board " in {
        controller.createNewBoard(16) should be(controller.getBoard)
      }
      "create a random Board" in {
        controller.createRandomBoard(10)
        val board: Board = controller.gameState.board
        controller.toStringBoard should be(controller.toStringHouse + board.toString())
      }
      "print board" in {
        val board: Board = controller.gameState.board
        controller.toStringBoard should be(controller.toStringHouse + board.toString)
      }
      "create a player" in {
        val players: Vector[Player] = controller.createPlayers(List("Player1", "Player2", "Player3", "Player4")).players._1
        players(0).toString should be("Player1")
        players(1).toString should be("Player2")
        players(2).toString should be("Player3")
        players(3).toString should be("Player4")
      }
      "set players" in {
        val players: Vector[Player] = controller.createPlayers(List("Player1", "Player2", "Player3", "Player4")).players._1
        players(0).toString should be("Player1")
        players(1).toString should be("Player2")
        players(2).toString should be("Player3")
        players(3).toString should be("Player4")
      }
      "manage the round" in {
        controller.createPlayers(List("Player1", "Player2", "Player3", "Player4"))
        controller.createNewBoard(20)
        val cardList: List[Card] = Card("3", "move", "blue") :: Card("5", "move", "blue") :: Nil
        controller.testDistributeCardsToPlayer(playerNum = 0, cardList).cardList should be(cardList)
        controller.manageRound(0, List(0), 0) should be(s"Player ${controller.gameState.players._1(controller.gameState.players._2).color}${controller.gameState.players._1(controller.gameState.players._2).name}${Console.RESET}'s turn\n")
      }
      "move a player by 5" in {
        controller.createPlayers(List("Player1", "Player2", "Player3", "Player4"))
        controller.createNewBoard(20)
        val cardList: List[Card] = Card("3", "move", "blue") :: Card("5", "move", "blue") :: Nil
        controller.testDistributeCardsToPlayer(playerNum = 3, cardList).cardList should be(cardList)

        controller.useCardLogic(selectedPlayerList = List(3), pieceNum = List(0), cardNum = 0) should be(0)
        controller.gameState.players._1(3).piece(0).position should be(3)

        controller.useCardLogic(selectedPlayerList = List(3), pieceNum = List(1), cardNum = 0) should be(0)
        controller.gameState.players._1(3).piece(1).position should be(5)

      }
      "move a player by 0" in {
        controller.createPlayers(List("Player1", "Player2", "Player3", "Player4"))
        controller.createNewBoard(20)
        val cardList: List[Card] = Card("0", "move", "blue") :: Nil
        controller.testDistributeCardsToPlayer(playerNum = 3, cardList).getCard(0) should be(cardList(0))

        controller.useCardLogic(selectedPlayerList = List(3), pieceNum = List(0), cardNum = 0)
        controller.gameState.players._1(3).piece(0).position should be(0)
      }
      "override a player" in {
        controller.createPlayers(List("Player1", "Player2", "Player3", "Player4"))
        controller.createNewBoard(20)
        val cardList: List[Card] = Card("5", "move", "blue") :: Card("5", "move", "blue") :: Nil
        var p: Player = controller.gameState.players._1(3)
        controller.testDistributeCardsToPlayer(playerNum = 3, cardList)

        controller.useCardLogic(selectedPlayerList = List(3), pieceNum = List(0), cardNum = 0) should be(0)
        controller.useCardLogic(selectedPlayerList = List(3), pieceNum = List(1), cardNum = 0) should be(0)
        p = controller.gameState.players._1(3)

        p.getPosition(0) should be(5)
        p.getPosition(1) should be(5)

      }
      "swap two players" in {
        controller.createPlayers(List("Player1", "Player2", "Player3", "Player4"))
        controller.createNewBoard(20)
        val cardListP2: List[Card] = Card("swap", "swap", "red") :: Nil
        val cardListP3: List[Card] = Card("5", "move", "blue") :: Card("3", "move", "blue") :: Card("9", "move", "blue") :: Nil

        //set cards
        controller.testDistributeCardsToPlayer(playerNum = 2, cardListP2).cardList should be(cardListP2)
        controller.testDistributeCardsToPlayer(playerNum = 3, cardListP3).cardList should be(cardListP3)

        //use CardLogic
        controller.useCardLogic(selectedPlayerList = List(3), pieceNum = List(0), cardNum = 0) should be(0)
        controller.useCardLogic(selectedPlayerList = List(3), pieceNum = List(1), cardNum = 0) should be(0)
        controller.useCardLogic(selectedPlayerList = List(3), pieceNum = List(2), cardNum = 0) should be(0)

        controller.gameState.players._1(3).getPosition(2) should be(9)

        controller.useCardLogic(selectedPlayerList = List(2, 3), pieceNum = List(2, 2), cardNum = 0) should be(0)

        //check position
        controller.gameState.players._1(3).getPosition(2) should be(0)
        controller.gameState.players._1(2).getPosition(2) should be(9)
      }
      "swap two players when no player is on the field" in {
        controller.createPlayers(List("Player1", "Player2", "Player3", "Player4"))
        controller.createNewBoard(20)
        val cardListP1: List[Card] = Card("swap", "swap", "red") :: Nil

        //set cards
        controller.testDistributeCardsToPlayer(playerNum = 1, cardListP1).cardList should be(cardListP1)

        //use CardLogic
        controller.useCardLogic(selectedPlayerList = List(1, 2), pieceNum = List(2, 3), cardNum = 0) should be(-1)

        //check if player stays the same
        controller.gameState.players._1(1).getPosition(2) should be(0)
      }
      "play a Card" in {
        controller.createPlayers(List("Player1", "Player2", "Player3", "Player4"))
        controller.testDistributeCardsToPlayer(playerNum = 0, List(Card("5", "move", "blue")))
        val handCards: List[Card] = controller.gameState.players._1(0).cardList
        controller.gameState.players._1(0).cardList should not be empty

        controller.getSelectedCard(0, 0) should be(handCards.head)

        controller.gameState.players._1(0).cardList should be(empty)
      }
      "create a Card Deck" in {
        val cardDeck: (Vector[Card], Int) = controller.createCardDeck(List(0, 0))
        cardDeck._1.length should be(cardDeck._2)
      }
      "draw Cards" in {
        controller.drawFewCards(10).foreach(x => be(x.isInstanceOf[Card]))
      }
      "draw Card from Deck" in {
        controller.drawCardFromDeck.isInstanceOf[Card] should be(true)
      }
    }
  }
}
