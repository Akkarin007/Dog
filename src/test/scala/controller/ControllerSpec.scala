package controller


import model._
import org.scalatest.{Matchers, WordSpec}

class ControllerSpec extends WordSpec with Matchers {

  "A Controller" when {
    "initialized" should {
      val controller: Controller = new Controller()
      "create a board " in {
        controller.createBoard(10) should be(controller.getBoard)
      }
      "print board" in {
        controller.toStringBoard should be(controller.toStringHouse + controller.board.toString())
      }
      "create a player" in {
        val players: Array[Player] = controller.createPlayer(List("Player1", "Player2", "Player3", "Player4"))
        players(0).toString should be("Player1")
        players(1).toString should be("Player2")
        players(2).toString should be("Player3")
        players(3).toString should be("Player4")
      }
      "set players" in {
        val players: Array[Player] = controller.setPlayer(List("Player1", "Player2", "Player3", "Player4"))
        players(0).toString should be("Player1")
        players(1).toString should be("Player2")
        players(2).toString should be("Player3")
        players(3).toString should be("Player4")
      }
      "move a player by 4" in {
        controller.setPlayer(List("Player1", "Player2", "Player3", "Player4"))
        controller.movePlayer(3, 0, 4).getPosition(0) should be(4)
      }
      "move a player by 0" in {
        controller.setPlayer(List("Player1", "Player2", "Player3", "Player4"))
        controller.movePlayer(3, 0, 0).getPosition(0) should be(0)
      }
    }
  }
}
