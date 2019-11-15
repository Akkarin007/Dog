package model.playerComponent

import model.{Piece, Player}
import org.scalatest.{Matchers, WordSpec}

class PlayerSpec extends WordSpec with Matchers {
  "A player" when {
    "created" should {
      //      val cards: Array[CardTrait] = Array(SevenCard(), JokerCard(), ChangeCard())
      val player = Player("PlayerSpec", "gelb", Map(0 -> new Piece(0), 1 -> new Piece(0), 2 -> new Piece(0), 3 -> new Piece(0)))
      "have a name" in {
        player.name should not be empty
      }
      "be printed" in {
        player.toString should be(player.name)
      }
      "have a color" in {
        player.getColor should be(player.color)
      }
      "have a map of pieces on the field" in {
        player.getPiece(2).getPosition should be(0)
      }
    }
  }
}
