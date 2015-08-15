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

public class PrinterListener extends potigolBaseListener {
	private BufferedTokenStream tokens;
	private final ParseTreeProperty<String> values = new ParseTreeProperty<String>();
	private String saida = "<!DOCTYPE html>\n<html>\n<head>\n<meta charset=\"UTF-8\">"
			+ "<link rel=\"stylesheet\" href=\"potigol.css\">"
			+ "<head>\n<body><button onclick=\"javascript:window.print();\">Imprimir</button>\n<pre>";
	private String indentStr = "                                                                                     ";

	private void setValue(ParseTree node, String value) {
		values.put(node, value);
	}

	public PrinterListener(BufferedTokenStream tokens) {
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
		String s = id + "<span class='vetor'>[</span>" + indice
				+ "<span class='vetor'>]</span>";
		setValue(ctx, s);
	}

	@Override
	public void exitDcl1(Dcl1Context ctx) {
		final String s;
		if (ctx.ID() != null)
			s = getValue(ctx.ID());
		else if (ctx.expr2() != null)
			s = "<span class='parametros'>(</span>" + getValue(ctx.expr2())
					+ "<span class='parametros'>)</span>";
		else
			s = "<span class='parametros'>(</span>" + getValue(ctx.dcls())
					+ "<span class='parametros'>)</span>";
		setValue(ctx, s);
	}

	@Override
	public void exitSet_vetor(Set_vetorContext ctx) {
		String id = getValue(ctx.ID());
		String s = id;
		for (ExprContext e : ctx.expr().subList(0, ctx.expr().size() - 1))
			s += "<span class='colchete'>[</span>" + getValue(e)
					+ "<span class='colchete'>]</span>";
		String exp = getValue(ctx.expr(ctx.expr().size() - 1));
		s += "<span class='atribuicao'> := </span>" + exp;
		setValue(ctx, s);
	}

	@Override
	public void exitLambda(LambdaContext ctx) {
		String param = getValue(ctx.dcl1());

		String corpo = "";
		for (InstContext i : ctx.inst()) {
			corpo += "\n" + getValue(i);
		}
		String s = param + "<span class='lambda'> => </span>"
				+ corpo.substring(1);
		setValue(ctx, s);
	}

	@Override
	public void exitTipo_generico(Tipo_genericoContext ctx) {
		String id = getValue(ctx.ID());
		String tipo = getValue(ctx.tipo());
		String s = id + "<span class='generico'>[</span>" + tipo
				+ "<span class='generico'>]</span>";
		setValue(ctx, s);
	}

	@Override
	public void exitTipo_funcao(Tipo_funcaoContext ctx) {
		String esq = getValue(ctx.tipo(0));
		String dir = getValue(ctx.tipo(1));
		String s = esq + "<span class='lambda'> => </span>" + dir;
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
		String s = "<span class='tupla'>(</span>" + getValue(ctx.tipo2())
				+ "<span class='tupla'>)</span>";
		setValue(ctx, s);
	}

	@Override
	public void exitCons(ConsContext ctx) {
		String a = getValue(ctx.expr(0));
		String as = getValue(ctx.expr(1));
		String s = a + "<span class='cons'> :: </span>" + as;
		setValue(ctx, s);
	}

	@Override
	public void exitExpoente(ExpoenteContext ctx) {
		String base = getValue(ctx.expr(0));
		String exp = getValue(ctx.expr(1));
		String s = base + "<span class='expoente'> ^ </span>" + exp;
		setValue(ctx, s);
	}

	@Override
	public void exitCaso(CasoContext ctx) {
		final String exp = getValue(ctx.expr(0));
		final String cond;
		if (ctx.expr().size() > 1) {
			cond = "<span class='chave'> se </span>" + getValue(ctx.expr(1));
		} else {
			cond = "";

		}
		String exps = getValue(ctx.exprlist());
		String s = "<span class='chave'>caso </span>" + exp + cond
				+ "<span class='lambda'> => </span>";
		String r = exps.substring(1).trim()
				.replaceAll("\\n", "\n" + indentStr.substring(0, 8));

		setValue(ctx, s + r);
	}

	@Override
	public void exitEscolha(EscolhaContext ctx) {
		String exp = getValue(ctx.expr());
		String s = "<span class='chave'>escolha </span>" + exp + "\n";
		for (CasoContext caso : ctx.caso()) {
			s += "  " + getValue(caso) + "\n";
		}
		s += "<span class='fim'>fim</span>";
		setValue(ctx, s);
	}

	@Override
	public void exitFormato(FormatoContext ctx) {
		String exp1 = getValue(ctx.expr(0));
		String exp2 = getValue(ctx.expr(1));
		String s = exp1 + "<span class='operador'> formato </span>" + exp2;
		setValue(ctx, s);
	}

	@Override
	public void exitAlias(AliasContext ctx) {
		String id = ctx.ID().getText();
		String tipo = getValue(ctx.tipo());
		String s = "<span class='chave'>tipo </span>" + id
				+ "<span class='atribuicao'> = </span>" + tipo;
		setValue(ctx, s);
	}

	@Override
	public void exitClasse(ClasseContext ctx) {
		String id = ctx.ID().getText();
		String s = "<span class='chave'>tipo </span>" + id + "\n";
		for (int i = 2; i < ctx.children.size() - 1; i++) {
			ParseTree d = ctx.children.get(i);
			s += "  " + getValue(d) + "\n";
		}
		s += "<span class='fim'>fim</span>";
		setValue(ctx, s);
	}

	@Override
	public void exitAtrib_multipla(Atrib_multiplaContext ctx) {
		String id = getValue(ctx.id2());
		String exp = getValue(ctx.expr2());
		String s = id + "<span class='atribuicao'> := </span>" + exp;
		setValue(ctx, s);
	}

	@Override
	public void exitAtrib_simples(Atrib_simplesContext ctx) {
		String id = getValue(ctx.id1());
		String exp = getValue(ctx.expr());
		String s = id + "<span class='atribuicao'> := </span>" + exp;
		setValue(ctx, s);
	}

	@Override
	public void exitChamada_funcao(Chamada_funcaoContext ctx) {
		setValue(ctx, getValue(ctx.expr())
				+ "<span class='parametros'>(</span>" + getValue(ctx.expr1())
				+ "<span class='parametros'>)</span>");
	}

	@Override
	public void exitChamada_metodo(Chamada_metodoContext ctx) {
		String exp = getValue(ctx.expr());
		String id = getValue(ctx.ID());
		String exp1 = getValue(ctx.expr1());
		String s = exp + "<span class='ponto'>.</span>" + id;
		if (exp1 != null)
			s += "<span class='parametros'>(</span>" + exp1
					+ "<span class='parametros'>)</span>";
		setValue(ctx, s);
	}

	@Override
	public void exitComparacao(ComparacaoContext ctx) {
		String exp1 = getValue(ctx.expr(0));
		String exp2 = getValue(ctx.expr(1));
		String op = ctx.getChild(1).getText().replaceAll("<", "&lt;")
				.replaceAll(">", "&gt;");
		setValue(ctx, exp1 + " <span class='operador'>" + op + "</span> "
				+ exp2);
	}

	@Override
	public void exitDcl(DclContext ctx) {
		String id = getValue(ctx.id1());
		String tipo = getValue(ctx.tipo());
		String s = id + "<span class='tipo'>: </span>" + tipo;
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
		String s = "<span class='chave'>var </span>" + id
				+ "<span class='atribuicao'> := </span>" + exp;
		setValue(ctx, s);
	}

	@Override
	public void exitDecl_var_simples(Decl_var_simplesContext ctx) {
		String id = getValue(ctx.id1());
		String exp = getValue(ctx.expr());
		setValue(ctx, "<span class='chave'>var </span>" + id
				+ "<span class='atribuicao'> := </span>" + exp);
	}

	@Override
	public void exitDef_funcao(Def_funcaoContext ctx) {
		String s = getValue(ctx.ID()) + "<span class='parametros'>(</span>"
				+ getValue(ctx.dcls()) + "<span class='parametros'>)</span>";
		String tipo = getValue(ctx.tipo());
		if (tipo != null)
			s += "<span class='tipo'>: </span>" + tipo;
		s += "<span class='atribuicao'> = </span>" + getValue(ctx.expr());
		setValue(ctx, s);
	}

	@Override
	public void exitDef_funcao_corpo(Def_funcao_corpoContext ctx) {
		String s = getValue(ctx.ID()) + "<span class='parametros'>(</span>"
				+ getValue(ctx.dcls()) + "<span class='parametros'>)</span>";
		String tipo = getValue(ctx.tipo());
		if (tipo != null)
			s += "<span class='tipo'>: </span>" + tipo;
		s += getValue(ctx.exprlist()) + "\n<span class='fim'>fim</span>";
		setValue(ctx, s);
	}

	@Override
	public void exitE_logico(E_logicoContext ctx) {
		String exp1 = getValue(ctx.expr(0));
		String exp2 = getValue(ctx.expr(1));
		setValue(ctx, exp1 + "<span class='operando'> e </span>" + exp2);
	}

	@Override
	public void exitEnquanto(EnquantoContext ctx) {
		String exp = getValue(ctx.expr());
		String bloco = getValue(ctx.bloco());
		String s = "<span class='chave'>enquanto </span>" + exp + " " + bloco;
		setValue(ctx, s);
	}

	@Override
	public void exitBloco(BlocoContext ctx) {
		String exp = getValue(ctx.exprlist());
		String s = "<span class='chave'>faça</span>" + exp
				+ "\n<span class='fim'>fim</span>";
		setValue(ctx, s);
	}

	@Override
	public void exitEscreva(EscrevaContext ctx) {
		setValue(ctx,
				"<span class='chave'>escreva </span>" + getValue(ctx.expr()));
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
			s += "\n" + getValue(i);
		}
		setValue(ctx, s.replaceAll("\\n", "\n  "));
	}

	@Override
	public void exitFaixa(FaixaContext ctx) {
		final String s;
		String id = getValue(ctx.ID());
		switch (ctx.expr().size()) {
		case 1:
			s = "<span class='operador'> em </span>" + getValue(ctx.expr(0));
			break;
		case 2:
			s = "<span class='operador'> de </span>" + getValue(ctx.expr(0))
					+ "<span class='operador'> até </span>"
					+ getValue(ctx.expr(1));
			break;
		default:
			s = "<span class='operador'> de </span>" + getValue(ctx.expr(0))
					+ "<span class='operador'> até </span>"
					+ getValue(ctx.expr(1))
					+ "<span class='operador'> passo </span>"
					+ getValue(ctx.expr(2));
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
		String id = ctx.getText();
		if (id.charAt(0) >= 'A' && id.charAt(0) <= 'Z')
			id = "<span class='classe'>" + id + "</span>";
		setValue(ctx, id);
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
		setValue(ctx,
				"<span class='chave'>imprima </span>" + getValue(ctx.expr()));
	}

	@Override
	public void exitInst(InstContext ctx) {
		String s = getValue(ctx.getChild(0));
		setValue(ctx, s);
		int a = ctx.getStart().getTokenIndex();
		List<Token> tt = tokens.getHiddenTokensToLeft(a, 1);
		List<Token> ttnl = tokens.getHiddenTokensToLeft(a, 2);
		String ttt = "<span class=\"comentario\">";
		if (ttnl != null && ttnl.size() > 1)
			ttt = "\n\n";
		if (tt != null) {
			for (Token t : tt) {
				ttt = ttt + t.getText();
				// System.out.println(t.getType() + " - " + t.getChannel());
			}
			// System.out.println(ttt);
		}
		ttt = ttt + "</span>";
		setValue(ctx, ttt + getValue(ctx));

	}

	@Override
	public void exitInteiro(InteiroContext ctx) {
		setValue(ctx, "<span class=\"numero\">" + ctx.getText() + "</span>");
	}

	@Override
	public void exitLista(ListaContext ctx) {
		String exp = getValue(ctx.expr1());
		if (exp == null)
			exp = "";
		setValue(ctx, "<span class='colchete'>[</span>" + exp
				+ "<span class='colchete'>]</span>");
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
		setValue(ctx, exp1 + " <span class='operador'>" + op + "</span> "
				+ exp2);
	}

	@Override
	public void exitNao_logico(Nao_logicoContext ctx) {
		String exp = getValue(ctx.expr());
		setValue(ctx, "<span class='operador'>não </span>" + exp);
	}

	@Override
	public void exitOu_logico(Ou_logicoContext ctx) {
		String exp1 = getValue(ctx.expr(0));
		String exp2 = getValue(ctx.expr(1));
		setValue(ctx, exp1 + "<span class='operador'> ou </span>" + exp2);
	}

	@Override
	public void exitPara_faca(Para_facaContext ctx) {
		String faixas = getValue(ctx.faixas());
		String se = getValue(ctx.expr());
		String bloco = getValue(ctx.bloco());
		String s = "<span class='chave'>para </span>" + faixas + " ";
		if (se != null)
			s += "<span class='chave'>se </span> " + se + " ";
		s += bloco;
		setValue(ctx, s);
	}

	@Override
	public void exitPara_gere(Para_gereContext ctx) {
		String faixas = getValue(ctx.faixas());
		String se = getValue(ctx.expr());
		String faca = getValue(ctx.exprlist());
		String s = "<span class='chave'>para </span>" + faixas + " ";
		if (se != null)
			s += "<span class='chave'>se </span> " + se + " ";
		s += "<span class='chave'>gere</span>" + faca
				+ "\n<span class='fim'>fim</span>";
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
		saida += "</pre>\n</body></html>";
		setValue(ctx, saida);
	}

	@Override
	public void exitReal(RealContext ctx) {
		setValue(ctx, "<span class=\"numero\">" + ctx.getText() + "</span>");
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

		String s = "<span class='chave'>se </span>" + getValue(ctx.expr());
		s += entao + separador;
		for (String sns : senaose) {
			s += sns + separador;
		}
		// s = s.substring(0, s.length() - 1);
		if (senao != null) {
			s += senao + separador;
		}
		s += "\n<span class='fim'>fim</span>";
		setValue(ctx, s);
	}

	@Override
	public void exitEntao(EntaoContext ctx) {
		String exp = getValue(ctx.exprlist());
		String s;
		if (exp.contains("\n")) {
			s = "<span class='chave'> então </span>" + exp;
		} else {
			s = "<span class='chave'> então </span>" + exp + " ";
		}
		setValue(ctx, s);
	}

	@Override
	public void exitSenao(SenaoContext ctx) {
		String exp = getValue(ctx.exprlist());
		String s;
		if (exp.contains("\n")) {
			s = "\n<span class='chave'>senão</span>" + exp;
		} else {
			s = "\n<span class='chave'>senão</span>" + exp;
		}
		setValue(ctx, s);
	}

	@Override
	public void exitSenaose(SenaoseContext ctx) {
		String s = "\n<span class='chave'>senãose </span>"
				+ getValue(ctx.expr()) + getValue(ctx.entao());
		// + "\n";
		setValue(ctx, s);
	}

	@Override
	public void exitSoma_sub(Soma_subContext ctx) {
		String exp1 = getValue(ctx.expr(0));
		String exp2 = getValue(ctx.expr(1));
		String op = ctx.getChild(1).getText();
		setValue(ctx, exp1 + " <span class='operador'>" + op + "</span> "
				+ exp2);
	}

	@Override
	public void exitTexto(TextoContext ctx) {
		String s = "<span class='texto'>" + ctx.getText() + "</span>";
		setValue(ctx, s);
	}

	public void exitTexto_interpolacao(Texto_interpolacaoContext ctx) {
		String s = "<span class='texto'>" + ctx.BS().getText() + "</span>";
		s += getValue(ctx.expr(0));
		int i = 1;
		for (TerminalNode x : ctx.MS()) {
			s += "<span class='texto'>" + x.getText() + "</span>";
			s += getValue(ctx.expr(i));
			i++;
		}
		s += "<span class='texto'>" + ctx.ES().getText() + "</span>";
		setValue(ctx, s);
	}

	@Override
	public void exitDecl_uso(Decl_usoContext ctx) {
		String s = "<span class='chave'>use </span>" + getValue(ctx.STRING());
		setValue(ctx, s);
	}

	@Override
	public void exitTupla(TuplaContext ctx) {
		String exp = getValue(ctx.expr2());
		setValue(ctx, "(" + exp + ")");
	}

	@Override
	public void exitValor_multiplo(Valor_multiploContext ctx) {
		String id = getValue(ctx.id2());
		String exp = getValue(ctx.expr2());
		String s = id + "<span class='atribuicao'> = </span>" + exp;
		setValue(ctx, s);
	}

	@Override
	public void exitValor_simples(Valor_simplesContext ctx) {
		final String id = getValue(ctx.id1());
		final String exp = getValue(ctx.expr());
		setValue(ctx, id + "<span class='atribuicao'> = </span>" + exp);
	}

	@Override
	public void exitBooleano(BooleanoContext ctx) {
		final String valor = "<span class='logico'>" + ctx.BOOLEANO().getText()
				+ "</span>";
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
		String s = node.getText();
		if (s.charAt(0) >= 'A' && s.charAt(0) <= 'Z')
			s = "<span class='classe'>" + s + "</span>";
		setValue(node, s);
	}

	public String getSaida() {
		// while (saida.contains("\n\n\n") || saida.contains(" \n")) {
		saida = saida.replaceAll(" \n", "\n").replaceAll("\n\n\n", "\n\n");
		// }
		return saida;
	}
}
