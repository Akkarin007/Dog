package dog.controller

import dog.model.CardComponent.CardTrait

trait Event {
  def changeState(): Event
}


object StateContext2 {

  val stateOn = new onState
  val stateOff = new offState
  var state: Event = stateOn

  def handle(): Event = {
    state.changeState()
    state
  }

  class onState() extends Event {
    override def changeState(): Event = {
      state = stateOff
      println("I am on")
      state
    }
  }

  class offState() extends Event {
    override def changeState(): Event = {
      state = stateOn
      println("I am off")
      state
    }
  }

}

case class InputCard(actualPlayer: Int, otherPlayer: Int, selPieceList: List[Int], cardNum: (Int, Int), selectedCard: CardTrait, moveBy: Int)

//--------------------------------------------------------------------------------------

object InputCardMaster {

  var otherPlayer: Int = -1
  var selPieceList: List[Int] = List(0)
  var cardNum: (Int, Int) = (0, 0)
  var actualPlayer = 0
  var moveBy: Int = 0
  var selCard: CardTrait = _

  //  var modeOfCard :

  case class UpdateCardInput() {

    def withOtherPlayer(otherP: Int): UpdateCardInput = {
      otherPlayer = otherP
      this
    }

    def withActualPlayer(actPlayer: Int): UpdateCardInput = {
      actualPlayer = actPlayer
      this
    }

    def withPieceNum(pieceN: List[Int]): UpdateCardInput = {
      selPieceList = pieceN
      this
    }

    def withCardNum(cardN: (Int, Int)): UpdateCardInput = {
      cardNum = cardN
      this
    }

    def withSelectedCard(selectedC: CardTrait): UpdateCardInput = {
      selCard = selectedC
      moveBy = if (selCard.task.equals("move")) selCard.symbol.toInt else 0
      this
    }

    def withMoveBy(move: Int): UpdateCardInput = {
      moveBy = move
      this
    }

    def buildCardInput(): InputCard = {
      InputCard(actualPlayer, otherPlayer, selPieceList, cardNum, selCard, moveBy)
    }
  }
}