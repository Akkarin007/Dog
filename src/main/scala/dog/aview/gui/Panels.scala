package dog.aview.gui

import dog.controller.ControllerComponent.ControllerTrait
import dog.controller.StateComponent.InputCardMaster
import dog.model.BoardComponent.BoardTrait
import dog.model.CardComponent.CardTrait
import dog.model.Player
import dog.util.SelectedState
import javafx.scene.layout.GridPane
import scalafx.Includes.when
import scalafx.geometry.Insets
import scalafx.geometry.Pos.Center
import scalafx.scene.control.{Button, Label}
import scalafx.scene.image.ImageView
import scalafx.scene.layout.{BorderPane, HBox, StackPane, VBox}
import scalafx.scene.paint.Color._

object CardPanel {

  val stdPath = "file:src/main/scala/resources/"
  val bgColor: String = "-fx-background-color:#383838;"

  //generates new Cards and puts it into Seq
  def newIcons(controller: ControllerTrait, iconAmount: Int, currentCard: CardTrait, cardIdx: Int): Seq[Button] = {
    var idx = 0
    val task = currentCard.task.split("\\s+") //GenImages.genIcon()
    val symbol: Array[String] = currentCard.symbol.split("\\s+")
    val symbolLength: Int = symbol.length
    Seq.fill(iconAmount)(new Button(
      if (symbol(0).equals("4")) (if (idx == 0) "-" else "+") + symbol(0)
      else if (symbolLength > 1 && idx != symbolLength - 1) "+" + symbol(idx)
      else "",
      GenImages.genIconImage(task(idx))) {
      id = idx.toString
      //style for circle Button
      idx = idx + 1
      val styleFirst: String = "-fx-background-radius: 5em; " +
        "-fx-min-width: 30px; " +
        "-fx-min-height: 30px; " +
        "-fx-max-width: 100px; " +
        "-fx-max-height: 50px;" +
        "-fx-padding:5;"
      style <== when(hover) choose styleFirst + "-fx-background-color:#d3d3d3;" otherwise styleFirst

      //PlayButton ActionListener
      onAction = _ => {
        println("----------------------------------------- Clicked IconID : " + getId.toInt)
        val actPlayer = controller.gameState.actualPlayer
        val inputCard = InputCardMaster.UpdateCardInput()
          .withActualPlayer(controller.gameStateMaster.actualPlayerIdx)
          .withCardNum((cardIdx, getId.toInt))
          .withSelectedCard(actPlayer.getCard(cardIdx))
          .buildCardInput()
        if (inputCard.selPieceList.head != -1) controller.manageRound(inputCard)
      }

    })
  }

  //generates new Cards and puts it into Seq
  def newCards(gridSeq: Seq[GridPane], amount: Int, cardList: List[CardTrait]): Seq[Button] = {
    var idx = 0
    Seq.fill(amount)(new Button("", GenImages.genCardImage(cardList(idx).symbol)) {
      style = bgColor
      idx = idx + 1
    })
  }

}

object GenImages {

  val stdPath = "file:src/main/scala/resources/"

  def genCardImage(typ: String): ImageView = new ImageView(stdPath + typ + ".png") {
    fitHeight = 200
    fitWidth = 125
  }

  def genIconImage(typ: String): ImageView = new ImageView(stdPath + typ + ".png") {
    //playButton size
    fitHeight = 20
    fitWidth = 20
  }
}

object CardDeckPanel {
  val bgColor: String = "-fx-background-color:#383838;"

  val stdPath = "file:src/main/scala/resources/"

  def newCardDeck(c: ControllerTrait): StackPane = {

    val amountLabel = new Label(c.gameState.cardDeck._2.toString)
    val stackPane = new StackPane() {
      padding = Insets(30, 30, 30, 30)
    }
    val height = if (c.gameState.board.size < 28) 200 else 100
    val width = if (c.gameState.board.size < 28) 125 else 60
    new GridPane {

      val cardDeckIcon: ImageView = new ImageView(stdPath + "green_back.png") {
        fitHeight = height
        fitWidth = width
      }


      stackPane.getChildren.addAll(cardDeckIcon, amountLabel)
      add(stackPane, 0, 0)
    }
    stackPane
  }
}

object PlayerStatusPanel {

  def newStatusPane(controller: ControllerTrait): HBox = new HBox {
    alignment = Center
    children.addAll(newStatusDisplay(controller), newPlacedCard(controller), CardDeckPanel.newCardDeck(controller))

    //    top = newStatusDisplay(controller)
    //    center = newPlacedCard(controller)
  }

  def newPlacedCard(c: ControllerTrait): Button = {
    val lastCard = if (c.gameStateMaster.lastPlayedCardOpt.nonEmpty) c.gameStateMaster.lastPlayedCardOpt.get.symbol else "laidcarddeck"
    val height = if (c.gameState.board.size < 28) 200 else 100
    val width = if (c.gameState.board.size < 28) 125 else 60
    new Button("", new ImageView(stdPath + lastCard + ".png") {
      fitHeight = height
      fitWidth = width

    }) {
      style = "-fx-background-color: transparent;" +
        "-fx-background-radius: 5em; " +
        "-fx-min-width: 30; " +
        "-fx-min-height: 30; " +
        "-fx-padding:5;"
    }
  }

  val bgColor: String = "-fx-background-color:#383838;"
  val stdPath = "file:src/main/scala/resources/"

  def newStatusDisplay(c: ControllerTrait): VBox = {
    val player: Player = c.gameState.players._1(c.gameState.players._2)
    val playerStateLabel = new Label(player.toString) {

      style = "-fxf-font-size: 20pt"
      textFill = player.color match {
        case "green" => Green;
        case "white" => White;
        case "yellow" => Yellow;
        case "red" => Red;
        case _ => Black
      }
    }
    var idx = 0
    val inHouse = Seq.fill(player.piece.size)(new Button("") {
      val color: String = if (player.inHouse.size <= idx) "" else player.color
      idx = idx + 1
      val colorHouses: String = color match {
        case "green" => "-fx-background-color:#008000;";
        case "white" => "-fx-background-color:#FFFFFF;"
        case "yellow" => "-fx-background-color:#FFFF00;"
        case "red" => "-fx-background-color:#FF0000;"
        case _ => "-fx-background-color:#979797;"
      }
      style = "-fx-padding:10;-fx-background-radius: 5em; " +
        "-fx-min-width: 30px; " +
        "-fx-min-height: 30px; " +
        "-fx-max-width: 30px; " +
        "-fx-max-height: 30px;" + colorHouses

    })

    val gridHouse = new GridPane {
      setPadding(Insets(10, 20, 20, 20))
      inHouse.indices.foreach(i => add(inHouse(i), i, 0))
    }

    new VBox() {
      alignment = Center

      children.add(playerStateLabel)
      children.add(gridHouse)
    }
  }
}

object BoardPanel {

  val stdPath = "file:src/main/scala/resources/"
  val bgColor: String = "-fx-background-color:#383838;"

  def newBoardPane(controller: ControllerTrait): BorderPane = {
    new BorderPane() {

      val board: BoardTrait = controller.gameState.board
      val amount: Int = board.size

      val fieldIconSeq: Seq[Button] = generateFieldIconSeq(amount, board, controller)
      var idx = 0
      val playerSize: Int = controller.gameState.players._1.size
      var garageFieldIconSeq: Seq[Seq[Button]] = Seq.empty
      (0 until playerSize)
        .foreach(x => garageFieldIconSeq = garageFieldIconSeq :+ generateFieldIconSeq(controller.gameState.players._1(x).garage.size, controller.gameState.players._1(x).garage, controller)
        )

      val stackPane: StackPane = new StackPane

      //      val scrollPane: ScrollPane = new ScrollPane() {
      //        content() = newBoardGrid(amount, fieldIconSeq)
      //      }

      stackPane.getChildren.addAll(PlayerStatusPanel.newStatusPane(controller), newBoardGrid(amount, controller, fieldIconSeq, garageFieldIconSeq))
      center = stackPane

    }
  }

  def generateFieldIconSeq(amount: Int, board: BoardTrait, controller: ControllerTrait): Seq[Button] = {
    var idx = 0
    Seq.fill(amount)(new Button("", new ImageView(
      stdPath +
        (if (board.cell(idx).isFilled)
          board.cell(idx).getColor
        else "field") + ".png") {
      fitWidth = 35
      fitHeight = 35
    }) {
      id = idx.toString
      //Padding of FieldButtons
      val stdStyle: String = "-fx-background-color: transparent;" +
        "-fx-min-width: 30px; " +
        "-fx-min-height: 30px; " +
        "-fx-max-width: 50px; " +
        "-fx-max-height: 40px;" +
        "-fx-padding:4;" +
        "-fx-border-radius:10 ;" +
        "-fx-border-width:2;"


      val blackStyle: String = stdStyle + "-fx-background-color:#000000;"
      val whiteStyle: String = stdStyle + "-fx-background-color:#ffffff;"

      val red: String = "-fx-border-color:#ff0000;"
      val white: String = "-fx-border-color:#ffffff;"
      val yellow: String = "-fx-border-color:#ffff00;"
      val green: String = "-fx-border-color:#00FF00;"
      var borderColor = ""
      var homeColor = ""

      controller.gameState.players._1.foreach(i => if (board.size == i.garage.size || i.homePosition == idx) homeColor = i.color)

      borderColor = homeColor match {
        case "green" => green
        case "white" => white
        case "yellow" => yellow
        case "red" => red
        case _ => "-fx-border-color:transparent;"
      }

      this.style <== when(pressed) choose blackStyle otherwise (when(hover) choose whiteStyle otherwise (
        if (SelectedState.ownFieldClicked == idx || SelectedState.otherFieldClicked == idx)
          whiteStyle
        else
          stdStyle + borderColor))

      idx = idx + 1
      //field OnClickListener
      onAction = _ => {
        println("pressed field = " + this.getId)
        controller.selectedField(this.getId.toInt)
      }
    })
  }

  def newBoardGrid(amount: Int, c: ControllerTrait, fieldIconSeq: Seq[Button], garageFieldIconSeq: Seq[Seq[Button]]): GridPane = {

    new GridPane {
      setAlignment(Center)

      //computes and displays Board on view, as an horizontal rectangle
      val leftAndRightEdge: Int = amount / 8
      val topAndBottomEdge: Int = (amount / 4) + leftAndRightEdge
      val garageSize: Int = c.gameState.actualPlayer.garage.size
      val homePos: List[Int] = List.empty
      c.gameState.players._1.foreach(x => homePos :+ x.homePosition)
      var fieldIdx = 0

      for (i <- 0 until topAndBottomEdge) {
        if (fieldIdx < garageSize) {
          add(garageFieldIconSeq.head(fieldIdx), i + 2, 0)
        }
        add(fieldIconSeq(fieldIdx), i + 2, 1); fieldIdx = fieldIdx + 1

      }
      for (i <- 0 until leftAndRightEdge) {
        if (fieldIdx < garageSize) add(garageFieldIconSeq.head(fieldIdx), i + 2, 0)
        add(fieldIconSeq(fieldIdx), topAndBottomEdge + 2, i + 2); fieldIdx = fieldIdx + 1
      }
      for (i <- topAndBottomEdge until 0 by -1) {
        add(fieldIconSeq(fieldIdx), i + 1, leftAndRightEdge + 2); fieldIdx = fieldIdx + 1
      }
      for (i <- leftAndRightEdge until 0 by -1) {
        add(fieldIconSeq(fieldIdx), 0, i + 1); fieldIdx = fieldIdx + 1
      }
    }
  }
}

