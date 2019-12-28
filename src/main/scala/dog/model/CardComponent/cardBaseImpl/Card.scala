package dog.model.CardComponent.cardBaseImpl

import dog.controller.{GameState, InputCard, InputCardObject}
import dog.model.BoardComponent.BoardTrait
import dog.model.CardComponent.{CardDeckTrait, CardTrait}
import dog.model.Player

import scala.collection.mutable.ListBuffer
import scala.util.{Random, Success, Try}


case class Card(symbol: String, task: String, color: String) extends CardTrait {

  override def toString: String = {
    "Card(" + s"${
      color match {
        case "blue" => Console.BLUE;
        case "red" => Console.RED
        case _ => ""
      }
    }$symbol${Console.RESET})"
  }

  override def parseToList: List[String] = symbol :: task :: color :: Nil

  override def parse(select: Int): Card = {
    val newSymbol: Try[String] = Try(symbol.split("\\s+")(select))
    val newTask: Try[String] = Try(task.split("\\s")(select))

    copy(symbol = newSymbol match {
      case Success(symbol) => symbol
      case _ => symbol.split("\\s+")(0)
    }, task = newTask match {
      case Success(task) => task
      case _ => task.split("\\s+")(0)
    })
  }
}


object CardLogic {

  val move: (GameState, InputCard) => (BoardTrait, Vector[Player], Int) = (gameState: GameState, inputC: InputCard) => {

    var isValid: Int = 0
    //move piece of specific player by returning a copy of the piece to the copy constructor player and returning a copy of the player
    val players: Vector[Player] = gameState.players._1
    var finalPlayer: Vector[Player] = Vector.empty[Player]
    val p: Player = players(inputC.selPlayerList.head)
    val board: BoardTrait = gameState.board
    val newPos: Int = Math.floorMod(inputC.selectedCard.parseToList.head.toInt + p.getPosition(inputC.pieceNum.head), board.size)

    //overriding player
    if (board.checkOverrideOtherPlayer(p, inputC.pieceNum.head, newPos)) {
      //get indexes and pieces
      val oPlayerIdx: Int = players.indexWhere(x => x.color == board.cell(newPos).getColor)
      val oPlayerPieceNum: Int = players(oPlayerIdx).getPieceNum(newPos) //get piece of other Player

      //check whether move valid or not
      if (oPlayerPieceNum == -1) isValid = -1

      //update Vector when overridden
      val overriddenPlayers: Vector[Player] = players.updated(oPlayerIdx, players(oPlayerIdx).overridePlayer(oPlayerPieceNum))
      finalPlayer = overriddenPlayers.updated(inputC.selPlayerList.head, players(inputC.selPlayerList.head).setPosition(inputC.pieceNum.head, newPos))
    } else {

      //update Vector when not overridden
      finalPlayer = players.updated(inputC.selPlayerList.head, players(inputC.selPlayerList.head).setPosition(inputC.pieceNum.head, newPos))
    }

    (board.updateMovePlayer(p, inputC.pieceNum.head, newPos), finalPlayer, isValid)
  }

  val swap: (GameState, InputCard) => (BoardTrait, Vector[Player], Int) = (gameState: GameState, inputCard: InputCard) => {

    var isValid = 0
    //swap a piece of the player that uses the card with the furthest piece of another player
    val p: Player = gameState.players._1(inputCard.selPlayerList.head)
    val swapPlayer: Player = gameState.players._1(inputCard.selPlayerList(1))
    val swapPos: (Int, Int) = (p.getPosition(inputCard.pieceNum.head), swapPlayer.getPosition(inputCard.pieceNum(1)))

    if (swapPos._2 == 0) isValid = -1 //Second Player is not on the field

    var players: Vector[Player] = gameState.players._1.updated(inputCard.selPlayerList.head, p.swapPiece(inputCard.pieceNum.head, swapPos._2)) //swap with second player

    players = players.updated(inputCard.selPlayerList(1), swapPlayer.swapPiece(inputCard.pieceNum(1), swapPos._1)) //swap with first player

    val nBoard: BoardTrait = gameState.board.updateSwapPlayers(players, inputCard.selPlayerList, inputCard.pieceNum)

    (nBoard, players, isValid)
  }

  val four: (GameState, InputCard) => (BoardTrait, Vector[Player], Int) = (gameState: GameState, inputCard: InputCard) => {
    val cardAttribute = inputCard.selectedCard.parseToList.head
    val cardOption = inputCard.selectedCard.task.split("\\s+")
    cardOption(inputCard.cardNum._2) match {
      case "forward" => move(gameState, InputCardObject.UpdateCardInput().withCardAttributes("move" :: cardAttribute.toString :: Nil).buildCardInput())
      case "backward" => move(gameState, InputCardObject.UpdateCardInput().withCardAttributes("move" :: "-" + cardAttribute :: Nil).buildCardInput())
      case _ => (gameState.board, gameState.players._1, -1)
    }
  }

  def setStrategy(callback: (GameState, InputCard) => (BoardTrait, Vector[Player], Int), gameState: GameState, inputCard: InputCard): (BoardTrait, Vector[Player], Int) = {
    callback(gameState, inputCard)
  }

  def getLogic(mode: String): (GameState, InputCard) => (BoardTrait, Vector[Player], Int) = {
    mode match {
      case "move" => move
      case "swap" => swap
      //      case "backward" => four
      //      case "forward" => four
      //      case "start" => Nil
      case _ => move
    }
  }
}


object Card {
  var amount = 6

  case class RandomCardsBuilder() {

    def withAmount(setAmount: Int): RandomCardsBuilder = {
      amount = setAmount
      this
    }

    def buildRandomCardList: List[CardTrait] = {
      val shuffledCardList: List[CardTrait] = Random.shuffle(CardDeck.CardDeckBuilder().withAmount(List(amount * 2, amount * 2)).buildCardList)
      shuffledCardList.take(amount)
    }
  }

}

object GenCardDeck {
  def apply(typ: String): CardDeckTrait = typ match {
    case "special" => SpecialCardsDeck()
    case "normal" => NormalCardsDeck()
  }
}

object CardDeck {
  var cardDeck: Vector[CardTrait] = GenCardDeck.apply("special").cardDeck.toVector
  var lengthOfCardDeck: Int = cardDeck.length
  var amount: List[Int] = List(2, 2)

  def toStringCardDeck(cardDeck: (Vector[CardTrait], Int)): String = {
    var cardString: String = "________DECK________\n"
    cardDeck._1.indices.foreach(i => if (i < cardDeck._2) cardString += s"$i: ${cardDeck._1(i)}\n") + "\n"
    cardString
  }

  case class CardDeckBuilder() {

    def withAmount(setAmount: List[Int]): CardDeckBuilder = {
      amount = setAmount
      withCardDeck
      this
    }

    def withCardDeck: CardDeckBuilder = {
      val list = new ListBuffer[CardTrait]()
      amount.indices.foreach(f = i => for (_ <- 0 until amount(i)) {
        i match {
          case 0 => list.++=(GenCardDeck.apply("special").cardDeck)
          case 1 => list.++=(GenCardDeck.apply("normal").cardDeck)
        }
      })
      cardDeck = list.toVector
      lengthOfCardDeck = cardDeck.length
      this
    }

    def withShuffle: CardDeckBuilder = {
      val shuffledCardDeck: Vector[CardTrait] = Random.shuffle(cardDeck)
      cardDeck = shuffledCardDeck
      this
    }

    def buildCardVectorWithLength: (Vector[CardTrait], Int) = (cardDeck, lengthOfCardDeck)

    def buildCardVector: Vector[CardTrait] = cardDeck

    def buildCardListWithLength: (List[CardTrait], Int) = (cardDeck.toList, lengthOfCardDeck)

    def buildCardList: List[CardTrait] = cardDeck.toList

  }

}


case class SpecialCardsDeck() extends CardDeckTrait {

  override val cardDeck: List[CardTrait] = generateDeck

  override def generateDeck: List[CardTrait] = {
    List(Card("1 11 play", "move move play", "red"),
      Card("4", "backward forward", "red"),
      Card("7", "burn", "red"),
      Card("swapCard", "swap", "red"),
      Card("questionmark", "joker", "red"),
      Card("13 play", "move play", "red"))
  }
}


case class NormalCardsDeck() extends CardDeckTrait {

  override val cardDeck: List[CardTrait] = generateDeck

  override def generateDeck: List[CardTrait] = {
    List(Card("2", "move", "blue"),
      Card("3", "move", "blue"),
      Card("5", "move", "blue"),
      Card("6", "move", "blue"),
      Card("8", "move", "blue"),
      Card("9", "move", "blue"),
      Card("10", "move", "blue"),
      Card("12", "move", "blue"))
  }
}
