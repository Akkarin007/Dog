package aview

import scala.io.StdIn.readLine

object Base {

  def main(args: Array[String]): Unit = {
    val tui = Tui()
    var input = ""
    //    tui.printMenu()
    do {
      print("say something ")
      System.out.print(">> ")
      input = readLine
      tui.print(input)
      println(f"ur commands result is: ${tui.input(input)}")
    } while (input != "exit")
  }
}