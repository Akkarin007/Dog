package dog.model

import org.scalatest.{Matchers, WordSpec}

class PlayerSpec extends WordSpec with Matchers {
  "A player" when {
    "created" should {
      val player = Player(("PlayerSpec", 0), "yellow", Map(0 -> Piece(0), 1 -> Piece(0), 2 -> Piece(6), 3 -> Piece(0)), List(0, 1, 2, 3), null, Nil, 0)
      "have a name" in {
        player.nameAndIdx._1 should not be empty
      }
      "be printed" in {
        player.toString should be(player.nameAndIdx._1)
      }
      "have a color" in {
        player.consoleColor should be(player.consoleColor)
      }
      "have a map of pieces on the field" in {
        player.piece(2).pos should be(6)
      }
      "get the piece when giving a position" in {
        player.getPieceNum(0) should be(0)
        player.getPieceNum(6) should be(2)
        player.getPieceNum(1) should be(-1)
      }
    }
  }
}
