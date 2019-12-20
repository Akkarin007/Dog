package dog

import com.google.inject.AbstractModule
import com.google.inject.name.Names
import dog.controller.ControllerTrait
import dog.controller.controllerBaseImpl.Controller
import dog.model.BoardComponent.BoardTrait
import dog.model.BoardComponent.boardAdvancedImpl.Board
import net.codingwell.scalaguice.ScalaModule


class DogModule extends AbstractModule with ScalaModule {

  val defaultSize: Int = 20

  def configure(): Unit = {
    bindConstant().annotatedWith(Names.named("DefaultSize")).to(defaultSize)
    bind[BoardTrait].to[Board]
    bind[ControllerTrait].to[Controller]
    bind[BoardTrait].annotatedWithName(name = "nano").toInstance(new Board(1))
    bind[BoardTrait].annotatedWithName(name = "micro").toInstance(new Board(4))
    bind[BoardTrait].annotatedWithName(name = "milli").toInstance(new Board(9))
    bind[BoardTrait].annotatedWithName(name = "small").toInstance(new Board(20))
    bind[BoardTrait].annotatedWithName(name = " normal").toInstance(new Board(64))
  }

}
