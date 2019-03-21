package br.edu.ifrn.potigol.editor

import org.antlr.v4.runtime.tree.TerminalNode
import org.antlr.v4.runtime.tree.ParseTreeListener;

import org.antlr.v4.runtime.Token
import org.antlr.v4.runtime.tree.ErrorNode
import org.antlr.v4.runtime.ParserRuleContext
import br.edu.ifrn.potigol.parser.potigolBaseListener

class HighLight extends ParseTreeListener {
  var tokens = List[Token]()
  override def visitErrorNode(node: ErrorNode) {}
  override def exitEveryRule(ctx: ParserRuleContext) {}
  override def enterEveryRule(ctx: ParserRuleContext) {}
  override def visitTerminal(node: TerminalNode) {
    tokens ::= node.getSymbol()
  }
  def getSaida() = tokens
}

