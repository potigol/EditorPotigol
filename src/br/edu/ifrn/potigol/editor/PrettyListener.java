package br.edu.ifrn.potigol.editor;

/*
 *  Potigol
 *  Copyright (C) 2015 by Leonardo Lucena
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along
 *  with this program; if not, write to the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

/**
 *   _____      _   _             _ 
 *  |  __ \    | | (_)           | |
 *  | |__) |__ | |_ _  __ _  ___ | |
 *  |  ___/ _ \| __| |/ _` |/ _ \| |
 *  | |  | (_) | |_| | (_| | (_) | |
 *  |_|   \___/ \__|_|\__, |\___/|_|
 *                     __/ |        
 *                    |___/         
 *
 * @author Leonardo Lucena (leonardo.lucena@escolar.ifrn.edu.br)
 */

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.BufferedTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeProperty;
import org.antlr.v4.runtime.tree.TerminalNode;

import br.edu.ifrn.potigol.parser.potigolBaseListener;
import br.edu.ifrn.potigol.parser.potigolParser.*;

public class PrettyListener extends potigolBaseListener {
	private BufferedTokenStream tokens;
	private final ParseTreeProperty<String> values = new ParseTreeProperty<String>();
	private String saida = "";
	private String indentStr = "                                        ";

	private void setValue(ParseTree node, String value) {
		values.put(node, value);
	}

	public PrettyListener(BufferedTokenStream tokens) {
		this.tokens = tokens;
	}

	private String getValue(ParseTree node) {
		return values.get(node);
	}

	@Override
	public void exitChar(CharContext ctx) {
		String s = ctx.CHAR().getText();
		setValue(ctx, s);
	}

	@Override
	public void exitGet_vetor(Get_vetorContext ctx) {
		String id = getValue(ctx.expr(0));
		String indice = getValue(ctx.expr(1));
		String s = id + "[" + indice + "]";
		setValue(ctx, s);
	}

	@Override
	public void exitDcl1(Dcl1Context ctx) {
		final String s;
		if (ctx.ID() != null)
			s = getValue(ctx.ID());
		else if (ctx.expr2() != null)
			s = "(" + getValue(ctx.expr2()) + ")";
		else
			s = "(" + getValue(ctx.dcls()) + ")";
		setValue(ctx, s);
	}

	@Override
	public void exitSet_vetor(Set_vetorContext ctx) {
		String id = getValue(ctx.ID());
		String s = id;
		for (ExprContext e : ctx.expr().subList(0, ctx.expr().size() - 1))
			s += "[" + getValue(e) + "]";
		String exp = getValue(ctx.expr(ctx.expr().size() - 1));
		s += " := " + exp;
		setValue(ctx, s);
	}

	@Override
	public void exitLambda(LambdaContext ctx) {
		String param = getValue(ctx.dcl1());
		String corpo = getValue(ctx.inst());
		String s = param + " => " + corpo;
		setValue(ctx, s);
	}

	@Override
	public void exitTipo_generico(Tipo_genericoContext ctx) {
		String id = getValue(ctx.ID());
		String tipo = getValue(ctx.tipo());
		String s = id + "[" + tipo + "]";
		setValue(ctx, s);
	}

	@Override
	public void exitTipo_funcao(Tipo_funcaoContext ctx) {
		String esq = getValue(ctx.tipo(0));
		String dir = getValue(ctx.tipo(1));
		String s = esq + " => " + dir;
		setValue(ctx, s);
	}

	@Override
	public void exitTipo2(Tipo2Context ctx) {
		String s = "";
		for (TipoContext tipo : ctx.tipo()) {
			s += ", " + getValue(tipo);
		}
		s = s.replaceFirst(", ", "");
		setValue(ctx, s);
	}

	@Override
	public void exitTipo_tupla(Tipo_tuplaContext ctx) {
		String s = "Tupla(" + getValue(ctx.tipo2()) + ")";
		setValue(ctx, s);
	}

	@Override
	public void exitCons(ConsContext ctx) {
		String a = getValue(ctx.expr(0));
		String as = getValue(ctx.expr(1));
		String s = a + " :: " + as;
		setValue(ctx, s);
	}

	@Override
	public void exitExpoente(ExpoenteContext ctx) {
		String base = getValue(ctx.expr(0));
		String exp = getValue(ctx.expr(1));
		String s = base + " ^ " + exp;
		setValue(ctx, s);
	}

	@Override
	public void exitCaso(CasoContext ctx) {
		final String exp = getValue(ctx.expr(0));
		final String cond;
		if (ctx.expr().size() > 1) {
			cond = " se " + getValue(ctx.expr(1));
		} else {
			cond = "";

		}
		String exps = getValue(ctx.exprlist());
		String s = "caso " + exp + cond + " => ";
		String r = exps.substring(1).trim()
				.replaceAll("\\n", "\n" + indentStr.substring(0, s.length()));

		setValue(ctx, s + r);
	}

	@Override
	public void exitEscolha(EscolhaContext ctx) {
		String exp = getValue(ctx.expr());
		String s = "escolha " + exp + "\n";
		for (CasoContext caso : ctx.caso()) {
			s += "  " + getValue(caso) + "\n";
		}
		s += "fim";
		setValue(ctx, s);
	}

	@Override
	public void exitFormato(FormatoContext ctx) {
		String exp1 = getValue(ctx.expr(0));
		String exp2 = getValue(ctx.expr(1));
		String s = exp1 + " formato " + exp2;
		setValue(ctx, s);
	}

	@Override
	public void exitAlias(AliasContext ctx) {
		String id = ctx.ID().getText();
		String tipo = getValue(ctx.tipo());
		String s = "tipo " + id + " = " + tipo;
		setValue(ctx, s);
	}

	@Override
	public void exitClasse(ClasseContext ctx) {
		String id = ctx.ID().getText();
		String s = "tipo " + id + "\n";
		for (int i = 2; i < ctx.children.size() - 1; i++) {
			ParseTree d = ctx.children.get(i);
			s += "  " + getValue(d) + "\n";
		}
		s += "fim";
		setValue(ctx, s);
	}

	@Override
	public void exitAtrib_multipla(Atrib_multiplaContext ctx) {
		String id = getValue(ctx.id2());
		String exp = getValue(ctx.expr2());
		String s = id + " := " + exp;
		setValue(ctx, s);
	}

	@Override
	public void exitAtrib_simples(Atrib_simplesContext ctx) {
		String id = getValue(ctx.id1());
		String exp = getValue(ctx.expr());
		String s = id + " := " + exp;
		setValue(ctx, s);
	}

	@Override
	public void exitChamada_funcao(Chamada_funcaoContext ctx) {
		setValue(ctx, getValue(ctx.expr()) + "(" + getValue(ctx.expr1()) + ")");
	}

	@Override
	public void exitChamada_metodo(Chamada_metodoContext ctx) {
		String exp = getValue(ctx.expr());
		String id = getValue(ctx.ID());
		String exp1 = getValue(ctx.expr1());
		String s = exp + "." + id;
		if (exp1 != null)
			s += "(" + exp1 + ")";
		setValue(ctx, s);
	}

	@Override
	public void exitComparacao(ComparacaoContext ctx) {
		String exp1 = getValue(ctx.expr(0));
		String exp2 = getValue(ctx.expr(1));
		String op = ctx.getChild(1).getText();
		setValue(ctx, exp1 + " " + op + " " + exp2);
	}

	@Override
	public void exitDcl(DclContext ctx) {
		String id = getValue(ctx.id1());
		String tipo = getValue(ctx.tipo());
		String s = id + ": " + tipo;
		setValue(ctx, s);
	}

	@Override
	public void exitDcls(DclsContext ctx) {
		List<String> a = new ArrayList<String>();
		for (DclContext dcl : ctx.dcl()) {
			a.add(getValue(dcl));
		}
		ids2String(ctx, a);
	}

	@Override
	public void exitDecl(DeclContext ctx) {
		String s = "";
		for (ParseTree i : ctx.children) {
			s += getValue(i);
		}
		setValue(ctx, s);
	}

	@Override
	public void exitDecl_var_multipla(Decl_var_multiplaContext ctx) {
		String id = getValue(ctx.id2());
		String exp = getValue(ctx.expr2());
		String s = "var " + id + " := " + exp;
		setValue(ctx, s);
	}

	@Override
	public void exitDecl_var_simples(Decl_var_simplesContext ctx) {
		String id = getValue(ctx.id1());
		String exp = getValue(ctx.expr());
		setValue(ctx, "var " + id + " := " + exp);
	}

	@Override
	public void exitDef_funcao(Def_funcaoContext ctx) {
		String s = getValue(ctx.ID()) + "(" + getValue(ctx.dcls()) + ")";
		String tipo = getValue(ctx.tipo());
		if (tipo != null)
			s += ": " + tipo;
		s += " = " + getValue(ctx.expr());
		setValue(ctx, s);
	}

	@Override
	public void exitDef_funcao_corpo(Def_funcao_corpoContext ctx) {
		String s = getValue(ctx.ID()) + "(" + getValue(ctx.dcls()) + ")";
		String tipo = getValue(ctx.tipo());
		if (tipo != null)
			s += ": " + tipo;
		s += getValue(ctx.exprlist()) + "\nfim";
		setValue(ctx, s);
	}

	@Override
	public void exitE_logico(E_logicoContext ctx) {
		String exp1 = getValue(ctx.expr(0));
		String exp2 = getValue(ctx.expr(1));
		setValue(ctx, exp1 + " e " + exp2);
	}

	@Override
	public void exitEnquanto(EnquantoContext ctx) {
		String exp = getValue(ctx.expr());
		String bloco = getValue(ctx.bloco());
		String s = "enquanto " + exp + " " + bloco;
		setValue(ctx, s);
	}

	@Override
	public void exitBloco(BlocoContext ctx) {
		String exp = getValue(ctx.exprlist());
		String s = "faça" + exp + "\nfim";
		setValue(ctx, s);
	}

	@Override
	public void exitEscreva(EscrevaContext ctx) {
		setValue(ctx, "escreva " + getValue(ctx.expr()));
	}

	@Override
	public void exitEveryRule(ParserRuleContext ctx) {
		if (getValue(ctx) == null)
			setValue(ctx, getValue(ctx.getChild(0)));
	}

	@Override
	public void exitExpr1(Expr1Context ctx) {
		List<String> a = new ArrayList<String>();
		for (ExprContext exp : ctx.expr()) {
			a.add(getValue(exp));
		}
		ids2String(ctx, a);
	}

	@Override
	public void exitExpr2(Expr2Context ctx) {
		List<String> a = new ArrayList<String>();
		for (ExprContext exp : ctx.expr()) {
			a.add(getValue(exp));
		}
		ids2String(ctx, a);
	}

	@Override
	public void enterExprlist(ExprlistContext ctx) {
		// setIndent(ctx, getIndent(ctx.getParent())+2);
	}

	@Override
	public void exitExprlist(ExprlistContext ctx) {
		String s = "";
		for (InstContext i : ctx.inst()) {
			String r = getValue(i);
			r = "\n" + r;
			r = r.replaceAll("\\n", "\n  ");
			r = r.replaceAll("  \\|", "|");
			s += r;
		}
		setValue(ctx, s);
	}

	@Override
	public void exitFaixa(FaixaContext ctx) {
		final String s;
		String id = getValue(ctx.ID());
		switch (ctx.expr().size()) {
		case 1:
			s = " em " + getValue(ctx.expr(0));
			break;
		case 2:
			s = " de " + getValue(ctx.expr(0)) + " até "
					+ getValue(ctx.expr(1));
			break;
		default:
			s = " de " + getValue(ctx.expr(0)) + " até "
					+ getValue(ctx.expr(1)) + " passo " + getValue(ctx.expr(2));
			break;
		}
		setValue(ctx, id + s);
	}

	@Override
	public void exitFaixas(FaixasContext ctx) {
		String s = "";
		for (FaixaContext f : ctx.faixa()) {
			if (f != ctx.faixa(0))
				s += ",\n     " + getValue(f);
			else
				s += getValue(f);
		}
		setValue(ctx, s);
	}

	@Override
	public void exitId(IdContext ctx) {
		setValue(ctx, ctx.getText());
	}

	@Override
	public void exitId1(Id1Context ctx) {
		List<String> a = new ArrayList<String>();
		for (TerminalNode id : ctx.ID()) {
			a.add(getValue(id));
		}
		ids2String(ctx, a);
	}

	@Override
	public void exitId2(Id2Context ctx) {
		List<String> a = new ArrayList<String>();
		for (TerminalNode id : ctx.ID()) {
			a.add(getValue(id));
		}
		ids2String(ctx, a);
	}

	@Override
	public void exitImprima(ImprimaContext ctx) {
		setValue(ctx, "imprima " + getValue(ctx.expr()));
	}

	@Override
	public void exitInst(InstContext ctx) {
		String s = getValue(ctx.getChild(0));
		setValue(ctx, s);
		int a = ctx.getStart().getTokenIndex();
		List<Token> tt = tokens.getHiddenTokensToLeft(a, 1);
		List<Token> ttnl = tokens.getHiddenTokensToLeft(a, 2);
		String ttt = "";
		if (ttnl != null && ttnl.size() > 1)
			ttt = "\n\n";
		if (tt != null) {
			for (Token t : tt) {
				ttt = ttt + t.getText();
				// System.out.println(t.getType() + " - " + t.getChannel());
			}
			// System.out.println(ttt);
		}
		setValue(ctx, ttt + getValue(ctx));

	}

	@Override
	public void exitInteiro(InteiroContext ctx) {
		setValue(ctx, ctx.getText());
	}

	@Override
	public void exitLista(ListaContext ctx) {
		String exp = getValue(ctx.expr1());
		if (exp == null)
			exp = "";
		setValue(ctx, "[" + exp + "]");
	}

	@Override
	public void exitMais_menos_unario(Mais_menos_unarioContext ctx) {
		String exp1 = getValue(ctx.expr());
		String op = ctx.getChild(0).getText();
		setValue(ctx, op + exp1);
	}

	@Override
	public void exitMult_div(Mult_divContext ctx) {
		String exp1 = getValue(ctx.expr(0));
		String exp2 = getValue(ctx.expr(1));
		String op = ctx.getChild(1).getText();
		setValue(ctx, exp1 + " " + op + " " + exp2);
	}

	@Override
	public void exitNao_logico(Nao_logicoContext ctx) {
		String exp = getValue(ctx.expr());
		setValue(ctx, "não " + exp);
	}

	@Override
	public void exitOu_logico(Ou_logicoContext ctx) {
		String exp1 = getValue(ctx.expr(0));
		String exp2 = getValue(ctx.expr(1));
		setValue(ctx, exp1 + " ou " + exp2);
	}

	@Override
	public void exitPara_faca(Para_facaContext ctx) {
		String faixas = getValue(ctx.faixas());
		String se = getValue(ctx.expr());
		String bloco = getValue(ctx.bloco());
		String s = "para " + faixas + " ";
		if (se != null)
			s += "se " + se + " ";
		s += bloco;
		setValue(ctx, s);
	}

	@Override
	public void exitPara_gere(Para_gereContext ctx) {
		String faixas = getValue(ctx.faixas());
		String se = getValue(ctx.expr());
		String faca = getValue(ctx.exprlist());
		String s = "para " + faixas + " ";
		if (se != null)
			s += "se " + se + " ";
		s += "gere" + faca + "\nfim";
		setValue(ctx, s);

	}

	@Override
	public void exitParen(ParenContext ctx) {
		String exp = getValue(ctx.expr());
		setValue(ctx, "(" + exp + ")");
	}

	@Override
	public void enterProg(ProgContext ctx) {
		// setIndent(ctx, 0);
	}

	@Override
	public void exitProg(ProgContext ctx) {
		for (InstContext i : ctx.inst()) {
			saida += getValue(i) + "\n";
		}
		// saida += "\n";
		setValue(ctx, saida);
	}

	@Override
	public void exitReal(RealContext ctx) {
		setValue(ctx, ctx.getText());
	}

	@Override
	public void exitSe(SeContext ctx) {
		String entao = getValue(ctx.entao());
		List<String> senaose = new ArrayList<>();
		if (ctx.senaose() != null) {
			for (SenaoseContext sns : ctx.senaose()) {
				senaose.add(getValue(sns));
			}
		}
		String senao = getValue(ctx.senao());

		final String separador;
		if (entao.contains("\n") || !senaose.isEmpty()
				|| (senao != null && senao.contains("\n")))
			separador = " ";
		else
			separador = " ";

		String s = "se " + getValue(ctx.expr());
		s += entao + separador;
		for (String sns : senaose) {
			s += sns + separador;
		}
		// s = s.substring(0, s.length() - 1);
		if (senao != null) {
			s += senao + separador;
		}
		s += "\nfim";
		setValue(ctx, s);
	}

	@Override
	public void exitEntao(EntaoContext ctx) {
		String exp = getValue(ctx.exprlist());
		String s;
		if (exp.contains("\n")) {
			s = " então" + exp;
		} else {
			s = " então " + exp + " ";
		}
		setValue(ctx, s);
	}

	@Override
	public void exitSenao(SenaoContext ctx) {
		String exp = getValue(ctx.exprlist());
		String s;
		if (exp.contains("\n")) {
			s = "\nsenão" + exp;
		} else {
			s = "\nsenão" + exp;
		}
		setValue(ctx, s);
	}

	@Override
	public void exitSenaose(SenaoseContext ctx) {
		String s = "\nsenãose " + getValue(ctx.expr()) + getValue(ctx.entao());
		// + "\n";
		setValue(ctx, s);
	}

	@Override
	public void exitSoma_sub(Soma_subContext ctx) {
		String exp1 = getValue(ctx.expr(0));
		String exp2 = getValue(ctx.expr(1));
		String op = ctx.getChild(1).getText();
		setValue(ctx, exp1 + " " + op + " " + exp2);
	}

	@Override
	public void exitTexto(TextoContext ctx) {
		String s = ctx.getText();
		setValue(ctx, s);
	}

	public void exitTexto_interpolacao(Texto_interpolacaoContext ctx) {
		String s = ctx.BS().getText();
		s += getValue(ctx.expr(0));
		int i = 1;
		for (TerminalNode x : ctx.MS()) {
			s += x.getText();
			s += getValue(ctx.expr(i));
			i++;
		}
		s += ctx.ES().getText();
		setValue(ctx, s);
	}

	@Override
	public void exitDecl_uso(Decl_usoContext ctx) {
		String s = "use " + getValue(ctx.STRING());
		setValue(ctx, s);
	}

	@Override
	public void exitTupla(TuplaContext ctx) {
		String exp = getValue(ctx.expr2());
		setValue(ctx, "Tupla(" + exp + ")");
	}

	@Override
	public void exitValor_multiplo(Valor_multiploContext ctx) {
		String id = getValue(ctx.id2());
		String exp = getValue(ctx.expr2());
		String s = id + " = " + exp;
		setValue(ctx, s);
	}

	@Override
	public void exitValor_simples(Valor_simplesContext ctx) {
		final String id = getValue(ctx.id1());
		final String exp = getValue(ctx.expr());
		setValue(ctx, id + " = " + exp);
	}

	@Override
	public void exitBooleano(BooleanoContext ctx) {
		final String valor = ctx.BOOLEANO().getText();
		setValue(ctx, valor);
	}

	private void ids2String(RuleContext ctx, List<String> a) {
		String s = "";
		if (a.size() > 0) {
			s = a.get(0);
		}
		for (int i = 1; i < a.size(); i++) {
			s += ", " + a.get(i);
		}
		setValue(ctx, s);
	}

	@Override
	public void visitTerminal(TerminalNode node) {
		final String s = node.getText();
		setValue(node, s);
	}

	public String getSaida() {
		while (saida.contains("\n\n\n") || saida.contains(" \n")) {
			saida = saida.replaceAll(" \n", "\n").replaceAll("\n\n\n", "\n\n");
		}
		return saida;
	}
}
