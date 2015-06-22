package br.edu.ifrn.potigol.editor

import scala.Array.canBuildFrom

object Pretty {

  val map = Map(
    "( " -> "(",
//    " (" -> "(",
    " ," -> ",",
    "," -> ", ",
    "+" -> " + ",
    "*" -> " * ",
    "/" -> " / ",
    "::" -> " :: ",
    ">=" -> " >= ",
    "<" -> " <",
    "<>" -> " <> ",
    "==" -> " == ",
    "( " -> "(",
//    " (" -> "(",
    ")" -> ") ",
    " )" -> ")",
    ")=" -> ") =",

    "[ " -> "[",
//    " [" -> "[",
    "] " -> "]",
    " ]" -> "]")

  def print(s: String): String = {
    var linhas = s.split('\n').map { _.trim() }.mkString("\n")
    while (linhas.contains("\n\n\n"))
      linhas = linhas.replaceAll("\n\n\n", "\n\n")
    val lista = linhas.split('\n')
    var tab = 0
    var anterior = ""
    val resp = for (linha <- lista) yield {
      var l = linha
      while (l.contains("  ")) {
        l = l.replaceAll("  ", " ")
      }
      for (a <- map) {
        l = l.replace(a._1, a._2)
      }
      l = l.replaceAll("  ", " ")
      if (l.startsWith("|")) {
        val p = if (anterior.trim().startsWith("|"))
          anterior.indexOf("|")
        else
          anterior.lastIndexOf("\"")
        println(p)
        l = " " * p + l.trim
      }
      anterior = l
      if (l == "fim" && !l.startsWith("se")) { tab = tab - 2 }
      if (l.endsWith("senão")) { tab = tab - 2 }
      if (l.endsWith("senao")) { tab = tab - 2 }
      val x = (" " * tab) + l
      if (l.endsWith("faça")) { tab = tab + 2 }
      if (l.endsWith("faca")) { tab = tab + 2 }
      if (l.startsWith("escolha")) { tab = tab + 2 }
      if (l.endsWith("então") || l.endsWith("entao") || l.startsWith("se ")) { tab = tab + 2 }
      if (l.endsWith("senão")) { tab = tab + 2 }
      if (l.endsWith("senao")) { tab = tab + 2 }
      x
    }
    resp.mkString("\n")
  }

}