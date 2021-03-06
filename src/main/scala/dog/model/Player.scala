package dog.model

import dog.model.BoardComponent.BoardTrait
import dog.model.BoardComponent.boardBaseImpl.Board
import dog.model.CardComponent.CardTrait
import dog.model.CardComponent.cardBaseImpl.Card

import scala.util.{Failure, Success, Try}


case class Player(nameAndIdx: (String, Int),
                  color: String, piece: Map[Int, Piece],
                  inHouse: List[Int], garage: BoardTrait,
                  cardList: List[CardTrait],
                  homePosition: Int) {

  def nextPiece(): Int = {
    Try(inHouse.head) match {
      case Success(iH) => iH
      case _ => -1
    }
  }

  val consoleColor: String = {
    color match {
      case "green" => Console.GREEN
      case "white" => Console.WHITE
      case "red" => Console.RED
      case "yellow" => Console.YELLOW
      case _ => ""
    }
  }

  def getPieceNum(position: Int): Int = {
    piece.foreach(x => if (x._2.pos == position) return x._1)
    -1
  }

  def piecePosition(pieceNum: Int): Int = piece(pieceNum).pos

  def overridePlayer(pieceNum: Int): Player = {
    copy(piece = piece.updated(pieceNum, piece(pieceNum).copy(pos = homePosition)), inHouse = pieceNum :: inHouse)
  }

  def setPosition(pieceIdx: Int, newPos: Int): Player = {
    copy(piece = piece.updated(pieceIdx, piece(pieceIdx).copy(pos = newPos)))
  }

  def updateLastPieceInHouse(pieceIdx: Int):Player ={
    copy(inHouse = inHouse.filter(_ != pieceIdx))
  }

  def setNewGaragePosition(pieceIdx: Int, newPos: Int): Player = {
    copy(piece = piece.updated(pieceIdx, piece(pieceIdx).copy(pos = newPos)),
      garage = garage.fill(garage.cell(newPos).addPlayerToCell(this), newPos))
  }

  def swapPiece(pieceIdx: Int, newPos: Int): Player = {
    copy(piece = piece.updated(pieceIdx, piece(pieceIdx).copy(pos = newPos)), inHouse = {
      if (newPos == homePosition)
        pieceIdx :: inHouse
      else if (piece(pieceIdx).pos == 0 && piece(pieceIdx).pos < newPos)
        inHouse.filter(_ != pieceIdx)
      else
        inHouse
    })
  }

  def setHandCards(myCards: List[CardTrait]): Player = {
    copy(cardList = myCards)
  }

  def removeCard(card: CardTrait): Player = {
    tryRemoveCard(card) match {
      case Some(list) => copy(cardList = list)
      case None => this
    }
  }

  def tryRemoveCard(card: CardTrait): Option[List[CardTrait]] = {
    Try(cardList diff List(card)) match {
      case Success(list) => Some(list)
      case Failure(_) =>
        None
    }
  }

  def getCard(cardIdx: Int): CardTrait = {
    tryGetCard(cardIdx) match {
      case Some(value) => value
      case None => throw new Exception("Es konnte keine Karte ausgewählt werden!\n")
    }
  }

  def tryGetCard(cardIdx: Int): Option[CardTrait] = {
    Try(cardList(cardIdx)) match {
      case Success(value) => Some(value)
      case Failure(_) => None
    }
  }

  override def toString: String = nameAndIdx._1

  def toStringColor: String = s"$consoleColor$toString${Console.RESET}"
}


case class Piece(pos: Int) {

  def movePiece(moveBy: Int): Piece = copy(pos = pos + moveBy)
}

class PlayerBuilder {
  var pieceAmount: Int = 4
  var color: String = "blue"
  var nameAndIndex: (String, Int) = ("Bobby", 0)
  var cardsDeck: List[CardTrait] = Card.RandomCardsBuilder().withAmount(6).buildRandomCardList
  var homePosition = 0
  var pieces: Map[Int, Piece] = (0 until pieceAmount).map(i => (i, Piece(homePosition))).toMap
  var inHouse: List[Int] = List(0, 1, 2, 3)
  var garage: BoardTrait = new Board(5, 0)


  case class Builder() {
    def withColor(c: String): Builder = {
      color = c
      this
    }

    def withName(setNameAndIdx: (String, Int)): Builder = {
      nameAndIndex = setNameAndIdx
      this
    }

    def withPiece(piecesAmount: Int, homePos: Int): Builder = {
      pieceAmount = piecesAmount
      homePosition = homePos
      pieces = (0 until piecesAmount).map(i => (i, Piece(homePos))).toMap
      inHouse = (0 until piecesAmount).toList
      this
    }


    def withPieces(setPieces: Map[Int, Piece], setHomePosition: Int): Builder = {
      pieces = setPieces
      pieceAmount = pieces.size
      homePosition = setHomePosition
      inHouse = (0 until pieceAmount).toList
      this
    }

    def withPieces(setPieces: Map[Int, Piece]): Builder = {
      pieces = setPieces
      pieceAmount = pieces.size
      this
    }

    def withCards(cards: List[CardTrait]): Builder = {
      cardsDeck = cards
      this
    }

    def withGeneratedCards(setAmount: Int): Builder = {
      cardsDeck = Card.RandomCardsBuilder().withAmount(setAmount).buildRandomCardList
      this
    }

    // can be used for testing or a player with explicit garage
    def withGarage(id: Int): Builder = {
      garage = new Board(id, inHouse.size)
      this
    }

    def build(): Player = {
      Player(nameAndIndex, color, pieces, inHouse, garage, cardsDeck, homePosition)
    }
  }

}
