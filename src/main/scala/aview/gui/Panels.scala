package aview.gui

import javafx.scene.layout.GridPane
import scalafx.geometry.Insets
import scalafx.scene.Node
import scalafx.scene.control.{Button, SplitPane}
import scalafx.scene.image._
import scalafx.scene.layout._

import scala.swing.Orientation

object CardPanel {

  def newCardViews(): BorderPane = {

    val cardViewAndPlayView: SplitPane = new SplitPane {
      Orientation.Horizontal
      style = "-fx-background-color:#383838"
      val view: GridPane = new GridPane {
        padding = Insets(100, 5, 10, 500)

        var b1, b2, b3, b4 = new Button("", new ImageView("file:ace.png") {
          fitHeight = 250
          fitWidth = 150
        }) {
          style = "-fx-padding:5;-fx-background-color:#383838"
          onAction = { _ =>
            graphic = new Button("used")
          }
        }

        add(b1, 0, 0)
        add(b2, 1, 0)
        add(b3, 2, 0)
      }
      items.add(view)
    }

    val cardPane = new BorderPane {
      center = cardViewAndPlayView
    }
    cardPane
  }
}

object BoardPanel {

  def newBoardView(): Node = {
    val b1, b2, b3, b4, b5 = new Button("", new ImageView("file:field.png")) {
      style = "-fx-padding:0;-fx-background-color:#383838"
    }

    b1.pressed onChange {
      println("I am a field")
    }

    val splitPane = new SplitPane {
      val view: GridPane = new GridPane {
        padding = Insets(100, 0, 10, 500)
        style = "-fx-background-color:#383838"


        add(b1, 5, 0)
        add(b2, 6, 0)
        add(b3, 7, 0)
      }
      items.add(view)
    }
    splitPane
  }
}
