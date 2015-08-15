package br.edu.ifrn.potigol.editor

import org.antlr.v4.runtime.ANTLRInputStream
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.Token
import org.antlr.v4.runtime.tree.ParseTreeWalker
import br.edu.ifrn.potigol.parser.potigolLexer
import br.edu.ifrn.potigol.parser.potigolParser
import br.edu.ifrn.potigol.Compilador
import scala.util.Success

object ParserEditor {
  def parse(r: String, channel: Int = 0): List[Token] = {
    val input = new ANTLRInputStream(r)
    val lexer = new potigolLexer(input)
    val tokens = new CommonTokenStream(lexer)
    val parser = new potigolParser(tokens)
    val tree = parser.prog()
   
    val walker = new ParseTreeWalker()
    val listener = new HighLight()
    walker.walk(listener, tree)
    listener.getSaida
  }
  def pretty(r: String) = {
    val input = new ANTLRInputStream(r)
    val lexer = new potigolLexer(input)
    val tokens = new CommonTokenStream(lexer)
    val parser = new potigolParser(tokens)
    val tree = parser.prog()
    val walker = new ParseTreeWalker() 
    val listener = new PrettyListener(tokens)
    walker.walk(listener, tree)
    val saida = listener.getSaida
    if (parser.getNumberOfSyntaxErrors > 0) r else saida
  }
  
  def print(r: String)  = {
    val input = new ANTLRInputStream(r)
    val lexer = new potigolLexer(input)
    val tokens = new CommonTokenStream(lexer)
    val parser = new potigolParser(tokens)
    val tree = parser.prog()
    val walker = new ParseTreeWalker() 
    val listener = new PrinterListener(tokens)
    walker.walk(listener, tree)
    val saida = listener.getSaida
    saida
  }
}